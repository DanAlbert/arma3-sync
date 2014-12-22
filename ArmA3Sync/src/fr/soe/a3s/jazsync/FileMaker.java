/* FileMaker.java

   FileMaker: File reading and making class
   Copyright (C) 2011 Tomáš Hlavni�?ka <hlavntom@fel.cvut.cz>

   This file is a part of Jazsync.

   Jazsync is free software; you can redistribute it and/or modify it
   under the terms of the GNU General Public License as published by the
   Free Software Foundation; either version 2 of the License, or (at
   your option) any later version.

   Jazsync is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Jazsync; if not, write to the

      Free Software Foundation, Inc.,
      59 Temple Place, Suite 330,
      Boston, MA  02111-1307
      USA
 */
package fr.soe.a3s.jazsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import fr.soe.a3s.dao.FileAccessMethods;

/**
 * Target file making class
 * 
 * @author Tomáš Hlavni�?ka
 */
public class FileMaker {

	private final MetaFileReader mfr;

	private final HttpConnection http;

	private final ChainingHash hashtable;

	private Configuration config;

	private int bufferOffset;

	private long fileOffset;

	private final long[] fileMap;

	private int missing;

	private boolean rangeQueue;

	private double complete;

	private final DecimalFormat df = new DecimalFormat("#.##");

	/** File existency and completion flag */
	public int FILE_FLAG = 0;

	public FileMaker(MetaFileReader mfr, HttpConnection http) {

		this.mfr = mfr;
		this.http = http;
		hashtable = mfr.getHashtable();
		fileMap = new long[mfr.getBlockCount()];
		Arrays.fill(fileMap, -1);
		fileOffset = 0;
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		df.setRoundingMode(RoundingMode.DOWN);
	}

	public void sync(File targetFile, String targetFileSha1,
			String relativeFileUrl) throws Exception, FileNotFoundException {

		System.out.println("");
		System.out.println("Processing file: " + targetFile.getAbsolutePath());

		if (targetFile.exists() && targetFileSha1 == null) {
			targetFileSha1 = FileAccessMethods.computeSHA1(targetFile);
		}

		SHA1check(targetFile, targetFileSha1);

		if (FILE_FLAG == 1) {
			if (targetFile.length() == 0) {
				getWholeFile(targetFile, relativeFileUrl);
			} else {
				mapMatcher(targetFile);
				if (complete > 0) {
					fileMaker(targetFile, relativeFileUrl, targetFileSha1);
				} else {
					getWholeFile(targetFile, relativeFileUrl);
				}
			}
		} else if (FILE_FLAG == -1) {
			getWholeFile(targetFile, relativeFileUrl);
		}
	}

	public double getCompletion(File targetFile, String targetFileSha1)
			throws Exception {

		if (targetFile.exists() && targetFileSha1 == null) {
			targetFileSha1 = FileAccessMethods.computeSHA1(targetFile);
		}

		SHA1check(targetFile, targetFileSha1);

		complete = 0;

		if (FILE_FLAG == 0) {
			complete = 100;
		} else if (FILE_FLAG == 1) {
			if (targetFile.length() != 0) {
				mapMatcher(targetFile);
			}
		}

		return complete;
	}

	/**
	 * Method for checking consistency of a file
	 * 
	 * @param file
	 *            File to check
	 */
	private void SHA1check(File targetFile, String targetFileSha1) {

		if (targetFile.exists()) {
			if (targetFileSha1.equals(mfr.getSha1())) {
				System.out.println("Read " + targetFile.getName()
						+ "verifying download...checksum matches OK\n"
						+ "used " + mfr.getLength() + " local, fetched 0");
			} else {
				FILE_FLAG = 1;
			}
		} else {
			FILE_FLAG = -1;
		}
	}

	/**
	 * Downloads a whole file in case that there were no relevant data found
	 * 
	 * @param httpURLConnection
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	private void getWholeFile(File targetFile, String relativeFileUrl)
			throws Exception, FileNotFoundException {

		try {
			http.openConnection(relativeFileUrl);
			System.out
					.println("No relevant data found, downloading whole file.");
			if (targetFile.exists()) {
				targetFile.delete();
			}
			boolean finished = http.getFile(mfr.getLength(), targetFile);
			http.closeConnection();
			if (!finished) {
				System.out.println("Download is not finished.");
				getResumedFile(targetFile, relativeFileUrl);
			} else {
				System.out.println("Target is 100.0% complete.");
			}
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			if (!http.getHttpDAO().isCanceled()) {
				e.printStackTrace();
				String message = "Failed to retrieve file "
						+ targetFile.getName();
				throw new Exception(message, e);
			}
		}
	}

	private void getResumedFile(File targetFile, String relativeFileUrl)
			throws Exception, FileNotFoundException {

		try {
			http.openConnection(relativeFileUrl);
			System.out.println("Resuming download.");
			boolean finished = http.getResumedFile(mfr.getLength(), targetFile);
			http.closeConnection();
			if (!finished) {
				System.out.println("Download is not finished.");
				getResumedFile(targetFile, relativeFileUrl);
			}
			System.out.println("Target is 100.0% complete.");
			http.closeConnection();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			String message = "Failed to retrieve file " + targetFile.getName();
			throw new Exception(message, e);
		}
	}

	/**
	 * URL parser, in case that metafile contains relative path
	 * 
	 * @param link
	 *            an URL to parse
	 * @return Absolute URL
	 */
	private String urlParser(String link) {
		String newUrl = null;
		try {
			URL url = new URL(link);
			String host = url.getHost().toString();
			String pathToFile = url.getPath().toString();
			pathToFile = pathToFile.substring(0, pathToFile.lastIndexOf("/"));
			newUrl = "http://" + host + pathToFile + "/" + mfr.getUrl();
		} catch (MalformedURLException ex) {
			System.out.println("URL in malformed format, make sure that"
					+ " metafile contains absolute URL or pass URL of metafile"
					+ " to jazsync by -u parameter.");
			System.exit(1);
		}
		return newUrl;
	}

	/**
	 * Method for completing file
	 * 
	 * @param targetFileSha1
	 * 
	 * @param httpURLConnection
	 * @param targetFileSha1
	 * @throws IOException
	 * @throws URISyntaxException
	 * @throws Exception
	 * @throws NoSuchAlgorithmException
	 */
	private void fileMaker(File targetFile, String relativeFileUrl,
			String targetFileSha1) throws Exception, FileNotFoundException {

		// try {
		boolean error = false;
		boolean resume = false;
		long allData = 0;
		double a = 10;
		int range = 0;
		int blockLength = 0;
		File partFile = new File(targetFile.getParentFile() + "/"
				+ targetFile.getName() + ".part");
		if (partFile.exists()) {
			partFile.delete();
		}

		ArrayList<DataRange> rangeList = null;
		byte[] data = null;
		partFile.createNewFile();
		ByteBuffer buffer = ByteBuffer.allocate(mfr.getBlocksize());
		FileChannel rChannel = new FileInputStream(targetFile).getChannel();
		FileOutputStream fos = new FileOutputStream(partFile, true);
		FileChannel wChannel = fos.getChannel();

		// http.getHttpDAO().setSize(mfr.getLength());

		System.out.println();
		System.out.print("File completion: ");
		System.out.print("|----------|");

		http.openConnection(relativeFileUrl);
		http.getResponseHeader();
		for (int i = 0; i < fileMap.length; i++) {
			if (http.getHttpDAO().isCanceled()) {
				break;
			}
			fileOffset = fileMap[i];
			if (fileOffset != -1) {
				rChannel.read(buffer, fileOffset);
				buffer.flip();
				wChannel.write(buffer);
				buffer.clear();
			} else {
				if (!rangeQueue) {
					rangeList = rangeLookUp(i);
					range = rangeList.size();
					http.openConnection(relativeFileUrl);
					http.setRangesRequest(rangeList);
					http.sendRequest();
					http.getResponseHeader();
					data = http.getResponseBody(mfr.getBlocksize(),
							mfr.getRangesNumber(), targetFile, partFile);
					if (data == null) {
						System.out.println("RESUME");
						resume = true;
						break;
					} else {
						allData += http.getAllTransferedDataLength();
					}

					if (http.getHttpDAO().isCanceled()) {
						break;
					}
				}

				if ((i * mfr.getBlocksize() + mfr.getBlocksize()) < mfr
						.getLength()) {
					blockLength = mfr.getBlocksize();
				} else {
					blockLength = (int) ((mfr.getBlocksize()) + (mfr
							.getLength() - (i * mfr.getBlocksize() + mfr
							.getBlocksize())));
				}

				try {
					buffer.put(data,
							(range - rangeList.size()) * mfr.getBlocksize(),
							blockLength);
					buffer.flip();
					wChannel.write(buffer);
					buffer.clear();
					rangeList.remove(0);
					if (rangeList.isEmpty()) {
						rangeQueue = false;
					}
				} catch (Exception e) {
					System.out
							.println("ERROR: .zsync file metadata don't match for target file "
									+ targetFile.getAbsolutePath());
					error = true;
					break;
				}
			}

			double value = (i / ((double) fileMap.length - 1)) * 100;

			if (value >= a) {
				progressBar(value);
				a += 10;
			}

			// int nbBytes = (int) (value * mfr.getLength() / 100);
		}
		rChannel.close();
		wChannel.close();
		fos.flush();
		fos.close();
		partFile.setLastModified(getMTime());

		if (error) {
			System.out.println("Deleting temporary file");
			partFile.delete();
			getWholeFile(targetFile, relativeFileUrl);
		}

		else {

			System.out.println("used "
					+ (mfr.getLength() - (mfr.getBlocksize() * missing)) + " "
					+ "local, fetched " + (mfr.getBlocksize() * missing));
			String name = targetFile.getName();
			File oldFile = new File(targetFile.getParentFile()
					.getAbsolutePath() + "/" + name + ".zs-old");
			File finalFile = new File(targetFile.getParentFile()
					.getAbsolutePath() + "/" + name);
			targetFile.renameTo(oldFile);
			partFile.renameTo(finalFile);
			oldFile.delete();

			allData += mfr.getLengthOfMetafile();
			System.out.println("really downloaded " + allData);
			double overhead = ((double) (allData - (mfr.getBlocksize() * missing)) / ((double) (mfr
					.getBlocksize() * missing))) * 100;
			System.out.println("overhead: " + df.format(overhead) + "%");

			if (resume) {
				getResumedFile(targetFile, relativeFileUrl);
			} else {
				targetFileSha1 = FileAccessMethods.computeSHA1(targetFile);
				if (targetFileSha1.equals(mfr.getSha1())) {
					System.out
							.println("\nverifying download...checksum matches OK");
				} else {
					System.out
							.println("\nverifying download...checksum don't match");
					getWholeFile(targetFile, relativeFileUrl);
				}
			}
		}
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// throw new Exception(ex.getMessage());
		// }
	}

	/**
	 * Instead of downloading single blocks, we can look into fieMap and collect
	 * amount of missing blocks or end of map accurs. Single ranges are stored
	 * in ArrayList
	 * 
	 * @param i
	 *            Offset in fileMap where to start looking
	 * @return ArrayList with ranges for requesting
	 */
	private ArrayList<DataRange> rangeLookUp(int i) {
		ArrayList<DataRange> ranges = new ArrayList<DataRange>();
		for (; i < fileMap.length; i++) {
			if (fileMap[i] == -1) {
				ranges.add(new DataRange(i * mfr.getBlocksize(), (i * mfr
						.getBlocksize()) + mfr.getBlocksize()));
			}
			if (ranges.size() == mfr.getRangesNumber()) {
				break;
			}
		}
		if (!ranges.isEmpty()) {
			rangeQueue = true;
		}
		return ranges;
	}

	/**
	 * Parsing out date from metafile into long value
	 * 
	 * @return Time as long value in milliseconds passed since 1.1.1970
	 */
	private long getMTime() {
		long mtime = 0;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"E, dd MMM yyyy HH:mm:ss Z", Locale.US);
			Date date = sdf.parse(mfr.getMtime());
			mtime = date.getTime();
		} catch (ParseException e) {
			System.out.println("Metafile is containing a wrong time format. "
					+ "Using today's date.");
			Date today = new Date();
			mtime = today.getTime();
		}
		return mtime;
	}

	/**
	 * Reads file and map it's data into the fileMap.
	 * 
	 * @throws Exception
	 */
	private void mapMatcher(File targetFile) throws Exception {
		InputStream is = null;
		try {
			Security.addProvider(new JarsyncProvider());
			config = new Configuration();
			config.strongSum = MessageDigest.getInstance("MD4");
			config.weakSum = new Rsum();
			config.blockLength = mfr.getBlocksize();
			config.strongSumLength = mfr.getChecksumBytes();
			Generator gen = new Generator(config);
			int weakSum;
			byte[] strongSum;
			byte[] backBuffer = new byte[mfr.getBlocksize()];
			byte[] blockBuffer = new byte[mfr.getBlocksize()];
			byte[] fileBuffer;
			int mebiByte = 1048576;
			if (mfr.getLength() < mebiByte
					&& mfr.getBlocksize() < mfr.getLength()) {
				fileBuffer = new byte[(int) mfr.getLength()];
			} else if (mfr.getBlocksize() > mfr.getLength()
					|| mfr.getBlocksize() > mebiByte) {
				fileBuffer = new byte[mfr.getBlocksize()];
			} else {
				fileBuffer = new byte[mebiByte];
			}
			is = new FileInputStream(targetFile);
			long fileLength = targetFile.length();
			int n;
			byte newByte;
			boolean firstBlock = true;
			int len = fileBuffer.length;
			boolean end = false;
			System.out.print("Reading " + targetFile.getName() + ": ");
			System.out.print("|----------|");
			double a = 10;
			boolean found = false;
			int skip = 0;
			while (true) {
				n = is.read(fileBuffer, 0, len);
				if (firstBlock) {
					weakSum = gen.generateWeakSum(fileBuffer, 0);
					bufferOffset = mfr.getBlocksize();
					if (hashLookUp(updateWeakSum(weakSum), null)) {
						strongSum = gen.generateStrongSum(fileBuffer, 0,
								mfr.getBlocksize());
						found = hashLookUp(updateWeakSum(weakSum), strongSum);
						if (found) {
							skip = mfr.getBlocksize() - 1;
							found = false;
						}
					}
					fileOffset++;
					firstBlock = false;
				}
				for (; bufferOffset < fileBuffer.length; bufferOffset++) {
					newByte = fileBuffer[bufferOffset];
					if (fileOffset + mfr.getBlocksize() > fileLength) {
						newByte = 0;
					}
					weakSum = gen.generateRollSum(newByte);
					if (skip == 0) {
						if (hashLookUp(updateWeakSum(weakSum), null)) {
							if (fileOffset + mfr.getBlocksize() > fileLength) {
								if (n > 0) {
									Arrays.fill(fileBuffer, n,
											fileBuffer.length, (byte) 0);
								} else {
									int offset = fileBuffer.length
											- mfr.getBlocksize() + bufferOffset
											+ 1;
									System.arraycopy(fileBuffer, offset,
											blockBuffer, 0, fileBuffer.length
													- offset);
									Arrays.fill(blockBuffer, fileBuffer.length
											- offset, blockBuffer.length,
											(byte) 0);
								}
							}
							if ((bufferOffset - mfr.getBlocksize() + 1) < 0) {
								if (n > 0) {
									System.arraycopy(backBuffer,
											backBuffer.length + bufferOffset
													- mfr.getBlocksize() + 1,
											blockBuffer, 0, mfr.getBlocksize()
													- bufferOffset - 1);
									System.arraycopy(fileBuffer, 0,
											blockBuffer, mfr.getBlocksize()
													- bufferOffset - 1,
											bufferOffset + 1);
								}
								strongSum = gen.generateStrongSum(blockBuffer,
										0, mfr.getBlocksize());
								found = hashLookUp(updateWeakSum(weakSum),
										strongSum);
							} else {
								strongSum = gen.generateStrongSum(fileBuffer,
										bufferOffset - mfr.getBlocksize() + 1,
										mfr.getBlocksize());
								found = hashLookUp(updateWeakSum(weakSum),
										strongSum);
							}

							if (found) {
								skip = mfr.getBlocksize() - 1;
								found = false;
							}
						}
					} else {
						skip--;
					}
					fileOffset++;
					if ((((double) fileOffset / (double) fileLength) * 100) >= a) {
						progressBar(((double) fileOffset / (double) fileLength) * 100);
						a += 10;
					}
					if (fileOffset == fileLength) {
						end = true;
						break;
					}
				}
				System.arraycopy(fileBuffer,
						fileBuffer.length - mfr.getBlocksize(), backBuffer, 0,
						mfr.getBlocksize());
				bufferOffset = 0;
				if (end) {
					break;
				}
			}

			complete = matchControl();
			System.out.println("Target " + df.format(complete) + "% complete.");
			fileMap[fileMap.length - 1] = -1;
			is.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			String message = "Can't read seed file, check your file access permissions.";
			throw new Exception(message, e1);
		} catch (Exception e2) {
			e2.printStackTrace();
			throw new Exception("Internal error.", e2);
		}
	}

	/**
	 * Shorten the calculated weakSum according to variable length of weaksum
	 * 
	 * @param weak
	 *            Generated full weakSum
	 * @return Shortened weakSum
	 */
	private int updateWeakSum(int weak) {
		byte[] rsum;
		switch (mfr.getRsumBytes()) {
		case 2:
			rsum = new byte[] { (byte) 0, (byte) 0, (byte) (weak >> 24), // 1
					(byte) ((weak << 8) >> 24) // 2
			};
			break;
		case 3:
			rsum = new byte[] { (byte) ((weak << 8) >> 24), // 2
					(byte) 0, // 3
					(byte) ((weak << 24) >> 24), // 0
					(byte) (weak >> 24) // 1
			};
			break;
		case 4:
			rsum = new byte[] { (byte) (weak >> 24), // 1
					(byte) ((weak << 8) >> 24), // 2
					(byte) ((weak << 16) >> 24), // 3
					(byte) ((weak << 24) >> 24) // 0
			};
			break;
		default:
			rsum = new byte[4];
		}
		int weakSum = 0;
		weakSum += (rsum[0] & 0x000000FF) << 24;
		weakSum += (rsum[1] & 0x000000FF) << 16;
		weakSum += (rsum[2] & 0x000000FF) << 8;
		weakSum += (rsum[3] & 0x000000FF);
		return weakSum;
	}

	/**
	 * Method is used to draw a progress bar of how far we are in file.
	 * 
	 * @param i
	 *            How much data we already progressed (value in percents)
	 */
	private void progressBar(double i) {
		if (i >= 10) {
			for (int b = 0; b < 11; b++) {
				System.out.print("\b");
			}
		}
		if (i >= 10 && i < 20) {
			System.out.print("#---------|");
		} else if (i >= 20 && i < 30) {
			System.out.print("##--------|");
		} else if (i >= 30 && i < 40) {
			System.out.print("###-------|");
		} else if (i >= 40 && i < 50) {
			System.out.print("####------|");
		} else if (i >= 50 && i < 60) {
			System.out.print("#####-----|");
		} else if (i >= 60 && i < 70) {
			System.out.print("######----|");
		} else if (i >= 70 && i < 80) {
			System.out.print("#######---|");
		} else if (i >= 80 && i < 90) {
			System.out.print("########--|");
		} else if (i >= 90 & i < 100) {
			System.out.print("#########-|");
		} else if (i >= 100) {
			System.out.print("##########|");
		}
	}

	/**
	 * Clears non-matching blocks and returns percentage value of how complete
	 * is our file
	 * 
	 * @return How many percent of file we have already
	 */
	private double matchControl() {
		missing = 0;
		for (int i = 0; i < fileMap.length; i++) {
			if (mfr.getSeqNum() == 2) { // pouze pokud kontrolujeme matching
										// continuation
				if (i > 0 && i < fileMap.length - 1) {
					if (fileMap[i - 1] == -1 && fileMap[i] != -1
							&& fileMap[i + 1] == -1) {
						fileMap[i] = -1;
					}
				} else if (i == 0) {
					if (fileMap[i] != -1 && fileMap[i + 1] == -1) {
						fileMap[i] = -1;
					}
				} else if (i == fileMap.length - 1) {
					if (fileMap[i] != -1 && fileMap[i - 1] == -1) {
						fileMap[i] = -1;
					}
				}
			}
			if (fileMap[i] == -1) {
				missing++;
			}
		}
		return ((((double) fileMap.length - missing) / fileMap.length) * 100);
	}

	/**
	 * Looks into hash table and check if got a hit
	 * 
	 * @param weakSum
	 *            Weak rolling checksum
	 * @param strongSum
	 *            Strong MD4 checksum
	 * @return True if we got a hit
	 */
	private boolean hashLookUp(int weakSum, byte[] strongSum) {
		ChecksumPair p;
		if (strongSum == null) {
			p = new ChecksumPair(weakSum);
			ChecksumPair link = hashtable.find(p);
			if (link != null) {
				return true;
			}
		} else {
			p = new ChecksumPair(weakSum, strongSum);
			ChecksumPair link = hashtable.findMatch(p);
			int seq;
			if (link != null) {
				/**
				 * V pripade, ze nalezneme shodu si zapiseme do file mapy offset
				 * bloku, kde muzeme dana data ziskat. Nasledne po sobe muzeme
				 * tento zaznam z hash tabulky vymazat.
				 */
				seq = link.getSequence();
				fileMap[seq] = fileOffset;
				hashtable.delete(new ChecksumPair(weakSum, strongSum, mfr
						.getBlocksize() * seq, mfr.getBlocksize(), seq));
				return true;
			}
		}
		return false;
	}

}
