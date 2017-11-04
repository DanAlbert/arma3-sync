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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
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
import java.util.List;
import java.util.Locale;

import fr.soe.a3s.dao.DataAccessConstants;
import fr.soe.a3s.dao.FileAccessMethods;
import fr.soe.a3s.dao.connection.RemoteFile;
import fr.soe.a3s.dao.connection.http.DataRange;
import fr.soe.a3s.dao.connection.http.HttpDAO;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.IncompleteFileTransferException;

/**
 * Target file making class
 * 
 * @author Tomáš Hlavni�?ka
 */
public class FileMaker {

	private final MetaFileReader mfr;
	private final HttpDAO httpDAO;
	private ChainingHash hashtable;
	private long fileOffset;
	private long[] fileMap;
	private int missing;
	private boolean rangeQueue;

	private final DecimalFormat df = new DecimalFormat("#.##");

	/** File existency and completion flag */
	// public int FILE_FLAG = 0;

	public FileMaker(MetaFileReader mfr, HttpDAO httpDAO) {

		this.mfr = mfr;
		this.httpDAO = httpDAO;
		hashtable = mfr.getHashtable();
		fileMap = new long[mfr.getBlockCount()];
		Arrays.fill(fileMap, -1);
		fileOffset = 0;
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		df.setRoundingMode(RoundingMode.DOWN);
	}

	/**
	 * Method for completing file
	 * 
	 * @param targetFile
	 * @param targetFileSha1
	 * @throws Exception
	 */
	public void fileMaker(File targetFile, RemoteFile remoteFile,
			AbstractProtocole protocol) throws IOException {

		File partFile = new File(targetFile.getParentFile() + "/"
				+ targetFile.getName() + DataAccessConstants.PART_EXTENSION);

		int range = 0;
		int blockLength = 0;
		List<DataRange> rangeList = new ArrayList<DataRange>();
		byte[] data = null;
		long cumulatedDataTransfered = 0;

		ByteBuffer buffer = ByteBuffer.allocate(mfr.getBlocksize());
		FileInputStream fis = null;
		FileOutputStream fos = null;
		FileChannel rChannel = null;
		FileChannel wChannel = null;

		try {
			fis = new FileInputStream(targetFile);
			rChannel = fis.getChannel();
			fos = new FileOutputStream(partFile);
			wChannel = fos.getChannel();

			for (int i = 0; i < fileMap.length; i++) {
				if (httpDAO.isCanceled()) {
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

						long startOffset = rangeList.get(0).getStart();
						long endOffset = rangeList.get(rangeList.size() - 1)
								.getEnd();

						System.out.println("Downloading partial file: "
								+ partFile.getAbsolutePath() + " at range ["
								+ startOffset + "-" + endOffset + "]");

						httpDAO.connect(protocol, remoteFile, startOffset,
								endOffset);
						data = httpDAO.getResponseBody(remoteFile,
								cumulatedDataTransfered, true, true);
						httpDAO.disconnect();

						if (data == null) {
							break;
						}

						cumulatedDataTransfered += data.length;
					}

					if (httpDAO.isCanceled()) {
						break;
					}

					blockLength = calcBlockLength(i, mfr.getBlocksize(),
							(int) mfr.getLength());
					int offset = (range - rangeList.size())
							* mfr.getBlocksize();

					if (offset >= 0 && offset <= data.length
							&& blockLength >= 0
							&& blockLength <= (data.length - offset)) {
						buffer.put(data, offset, blockLength);
						buffer.flip();
						wChannel.write(buffer);
					}
					buffer.clear();
					rangeList.remove(0);
					if (rangeList.isEmpty()) {
						rangeQueue = false;
					}
				}
			}

			rChannel.close();
			wChannel.close();

			if (!httpDAO.isCanceled()) {
				FileAccessMethods.deleteFile(targetFile);
				partFile.renameTo(targetFile);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
			FileAccessMethods.deleteFile(partFile);
			buffer = null;
		}
	}

	private int calcBlockLength(int i, int blockSize, int length) {
		if ((i + 1) * blockSize < length) {
			return blockSize;
		} else {
			return calcBlockLength_b(i, blockSize, length);
		}
	}

	private int calcBlockLength_b(int i, int blockSize, int length) {
		return blockSize + (length - (i * blockSize + blockSize));
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
	private List<DataRange> rangeLookUp(int i) {
		List<DataRange> ranges = new ArrayList<DataRange>();
		for (; i < fileMap.length; i++) {
			if (ranges.size() >= mfr.getRangesNumber()) {
				break;
			} else if (fileMap[i] == -1) {
				ranges.add(new DataRange((long) i * mfr.getBlocksize(),
						(i * mfr.getBlocksize()) + (long) mfr.getBlocksize()));
			} else {
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
	 */
	public double mapMatcher(File targetFile) throws IOException {

		int bufferOffset = 0;
		InputStream is = null;
		InputStream inBuf = null;
		long fileLength = targetFile.length();

		int weakSum = 0;
		byte[] strongSum = null;
		byte[] backBuffer = null;
		byte[] blockBuffer = null;
		byte[] fileBuffer = null;
		int mebiByte = 1048576;
		Configuration config = null;

		try {
			is = new FileInputStream(targetFile);
			inBuf = new BufferedInputStream(is);
			Security.addProvider(new JarsyncProvider());

			config = new Configuration();
			config.strongSum = MessageDigest.getInstance("MD4");
			config.weakSum = new Rsum();
			config.blockLength = mfr.getBlocksize();
			config.strongSumLength = mfr.getChecksumBytes();
			Generator gen = new Generator(config);

			backBuffer = new byte[mfr.getBlocksize()];
			blockBuffer = new byte[mfr.getBlocksize()];

			if (mfr.getLength() < mebiByte
					&& mfr.getBlocksize() < mfr.getLength()) {
				fileBuffer = new byte[(int) mfr.getLength()];
			} else if (mfr.getBlocksize() > mfr.getLength()
					|| mfr.getBlocksize() > mebiByte) {
				fileBuffer = new byte[mfr.getBlocksize()];
			} else {
				fileBuffer = new byte[mebiByte];
			}
			int n; // number of bytes read from input stream
			byte newByte;
			boolean firstBlock = true;
			int len = fileBuffer.length;
			boolean end = false;
			int blocksize = mfr.getBlocksize();

			//
			long lastMatch = 0;
			//

			while (fileOffset != fileLength) {

				if (httpDAO.isCanceled()) {
					break;
				}

				// System.out.println("Outer loop: " + mc.fileOffset);
				n = inBuf.read(fileBuffer, 0, len);
				if (firstBlock) {
					weakSum = gen.generateWeakSum(fileBuffer, 0);
					bufferOffset = mfr.getBlocksize();
					int weak = updateWeakSum(weakSum);
					if (hashLookUp(weak, null)) {
						strongSum = gen.generateStrongSum(fileBuffer, 0,
								blocksize);
						boolean match = hashLookUp(updateWeakSum(weakSum),
								strongSum);
						if (match) {
							lastMatch = fileOffset;
							// System.out.println("Last match: " + lastMatch);
						}
					}
					fileOffset++;
					firstBlock = false;
				}

				for (; bufferOffset < fileBuffer.length; bufferOffset++) {

					if (httpDAO.isCanceled()) {
						break;
					}

					newByte = fileBuffer[bufferOffset];
					if (fileOffset + mfr.getBlocksize() > fileLength) {
						newByte = 0;
					}
					weakSum = gen.generateRollSum(newByte);
					// System.out.println("Innner Loop: bufferOffset: " +
					// bufferOffset + " - fileBuffer.length: " +
					// fileBuffer.length + " weakSum: " + weakSum +
					// " mc.fileOffset: " + mc.fileOffset + " - lastMatch: " +
					// lastMatch);
					boolean found = false;
					if (fileOffset >= lastMatch + blocksize) {
						int wSum = updateWeakSum(weakSum);
						if (hashLookUp(wSum, null)) {
							found = true;
						} else {
							// System.out.println("Not found, weaksum: " +
							// wSum);
						}
					} else {
						// System.out.println("Not looking for match because fileOffset not far enough: "
						// + mc.fileOffset + " lastMatch: " + lastMatch +
						// " blockSize: " + blocksize);
					}
					if (found) {
						if (fileOffset + mfr.getBlocksize() > fileLength) {
							if (n > 0) {
								Arrays.fill(fileBuffer, n, fileBuffer.length,
										(byte) 0);
							} else {
								int offset = fileBuffer.length
										- mfr.getBlocksize() + bufferOffset + 1;
								System.arraycopy(fileBuffer, offset,
										blockBuffer, 0, fileBuffer.length
												- offset);
								Arrays.fill(blockBuffer, fileBuffer.length
										- offset, blockBuffer.length, (byte) 0);
							}
						}
						if ((bufferOffset - mfr.getBlocksize() + 1) < 0) {
							if (n > 0) {
								System.arraycopy(
										backBuffer,
										backBuffer.length + bufferOffset
												- mfr.getBlocksize() + 1,
										blockBuffer, 0, mfr.getBlocksize()
												- bufferOffset - 1);
								System.arraycopy(fileBuffer, 0, blockBuffer,
										mfr.getBlocksize() - bufferOffset - 1,
										bufferOffset + 1);
							}
							strongSum = gen.generateStrongSum(blockBuffer, 0,
									blocksize);
							// System.out.println("Look for match: " + new
							// String(blockBuffer));
							boolean match = hashLookUp(updateWeakSum(weakSum),
									strongSum);
							if (match)
								lastMatch = fileOffset;
						} else {
							strongSum = gen.generateStrongSum(fileBuffer,
									bufferOffset - blocksize + 1, blocksize);
							boolean match = hashLookUp(updateWeakSum(weakSum),
									strongSum);
							if (match)
								lastMatch = fileOffset;
						}
					}

					fileOffset++;
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

			double complete = matchControl();
			return complete;
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		} finally {
			if (is != null) {
				is.close();
			}
			if (inBuf != null) {
				inBuf.close();
			}
			hashtable = null;
			strongSum = null;
			backBuffer = null;
			blockBuffer = null;
			fileBuffer = null;
			config = null;
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

		if (fileMap.length > 0) {
			return ((((double) fileMap.length - missing) / fileMap.length) * 100);
		} else {
			return 0;
		}
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
