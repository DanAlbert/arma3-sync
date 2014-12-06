/* MetafileReader.java

   MetafileReader: Metafile reader class
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;

/**
 * Class used to read metafile
 * 
 * @author Tomáš Hlavni�?ka
 */
public class MetaFileReader {

	private File metafile;;
	private ChainingHash hashtable;
	private int fileOffset;
	private int blockNum;

	/** Authentication variables */
	private String username;
	private String passwd;
	private final boolean authing = false;

	/** Variables for header information from .zsync metafile */
	// ------------------------------
	private String mf_version;
	private String mf_filename;
	private String mf_mtime;
	private int mf_blocksize;
	private long mf_length;
	private int mf_seq_num;
	private int mf_rsum_bytes;
	private int mf_checksum_bytes;
	private String mf_url;
	private String mf_sha1;
	private String auth;
	// ------------------------------
	private String url;
	private String localMetafile;
	private final boolean downMetaFile = false;
	private String extraInputFile;
	private final int ranges = 100;
	private long downloadedMetafile = 0;

	/**
	 * Metafile constructor
	 * 
	 * @param httpURLConnection
	 * 
	 * @param args
	 *            Arguments
	 * @throws Exception
	 */
	public MetaFileReader(String relativeZsyncFileUrl, HttpConnection http)
			throws Exception {

		try {
			http.openConnection(relativeZsyncFileUrl);
			http.sendRequest();
			http.getResponseHeader();
			byte[] mfBytes = http.getResponseBodyForZsyncFile();
			downloadedMetafile = mfBytes.length;
			http.closeConnection();
			readMetaFile(convertBytesToString(mfBytes));
			blockNum = (int) Math.ceil((double) mf_length
					/ (double) mf_blocksize);
			fillHashTable(mfBytes);
		} catch (MalformedURLException e) {
			String message = "Wrong .zsync file url " + relativeZsyncFileUrl;
			throw new Exception(message, e);
		} catch (Exception e) {
			String message = "Failed to retrieve .zsync file for url "
					+ relativeZsyncFileUrl + "\n" + e.getMessage();
			throw new Exception(message, e);
		}
	}

	/**
	 * Parsing method for metafile headers, saving each value into separate
	 * variable.
	 * 
	 * @param s
	 *            String containing metafile
	 * @return Boolean value notifying whether header ended or not (true = end
	 *         of header)
	 */
	private boolean parseHeader(String s) {
		String subs;
		int colonIndex;
		if (s.equals("")) {
			return true;
		}
		colonIndex = s.indexOf(":");
		subs = s.substring(0, colonIndex);
		if (subs.equalsIgnoreCase("zsync")) {
			mf_version = s.substring(colonIndex + 2);
			if (mf_version.equals("0.0.4") || mf_version.equals("0.0.2")) {
				System.out
						.println("This version is not compatible with zsync streams in versions up to 0.0.4");
				System.exit(1);
			}
		} else if (subs.equalsIgnoreCase("Filename")) {
			mf_filename = s.substring(colonIndex + 2);
		} else if (subs.equalsIgnoreCase("MTime")) {
			mf_mtime = s.substring(colonIndex + 2);
		} else if (subs.equalsIgnoreCase("Blocksize")) {
			mf_blocksize = Integer.parseInt(s.substring(colonIndex + 2));
		} else if (subs.equalsIgnoreCase("Length")) {
			mf_length = Long.parseLong(s.substring(colonIndex + 2));
		} else if (subs.equalsIgnoreCase("Hash-Lengths")) {
			int comma = s.indexOf(",");
			mf_seq_num = Integer.parseInt(s.substring((colonIndex + 2), comma));
			int nextComma = s.indexOf(",", comma + 1);
			mf_rsum_bytes = Integer.parseInt(s.substring(comma + 1, nextComma));
			mf_checksum_bytes = Integer.parseInt(s.substring(nextComma + 1));
			// zkontrolujeme validni hash-lengths
			if ((mf_seq_num < 1 || mf_seq_num > 2)
					|| (mf_rsum_bytes < 1 || mf_rsum_bytes > 4)
					|| (mf_checksum_bytes < 3 || mf_checksum_bytes > 16)) {
				System.out.println("Nonsensical hash lengths line "
						+ s.substring(colonIndex + 2));
				System.exit(1);
			}

		} else if (subs.equalsIgnoreCase("URL")) {
			mf_url = s.substring(colonIndex + 2);
		} else if (subs.equalsIgnoreCase("Z-URL")) {
			// not implemented yet
		} else if (subs.equalsIgnoreCase("SHA-1")) {
			mf_sha1 = s.substring(colonIndex + 2);
		} else if (subs.equalsIgnoreCase("Z-Map2")) {
			// not implemented yet
		}
		return false;
	}

	/**
	 * Method reads metafile from file and reads it line by line, sending line
	 * String to parser.
	 */
	private void readMetaFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(metafile));
			String s;
			while ((s = in.readLine()) != null) {
				if (parseHeader(s)) {
					break;
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("IO problem in metafile header reading");
		}
	}

	/**
	 * Method reads metafile from String and reads it line by line, sending line
	 * String to parser.
	 * 
	 * @param s
	 *            Metafile in String form
	 */
	private void readMetaFile(String s) {
		try {
			BufferedReader in = new BufferedReader(new StringReader(s));
			while ((s = in.readLine()) != null) {
				if (parseHeader(s)) {
					break;
				}
			}
			in.close();
		} catch (IOException e) {
			System.out.println("IO problem in metafile header reading");
		}
	}

	/**
	 * Method converts downloaded metafile from byte array into String and saves
	 * offset where headers end and blocksums starts.
	 * 
	 * @param bytes
	 * @return
	 */
	private String convertBytesToString(byte[] bytes) {
		for (int i = 2; i < bytes.length; i++) {
			if (bytes[i - 2] == 10 && bytes[i - 1] == 10) {
				fileOffset = i;
				break;
			}
		}
		String header = new String(bytes);
		return header;
	}

	/**
	 * Method that reads metafile from file and stores its content into byte
	 * array and saves offset where headers end and blocksums starts.
	 */
	private void readChecksums() {
		long length = metafile.length();
		if (metafile.length() > Integer.MAX_VALUE) {
			System.out.println("Metafile is too large");
			System.exit(1);
		}
		byte[] bytes = new byte[(int) length];

		try {
			InputStream is = new FileInputStream(metafile);
			int offset = 0;
			int n = 0;
			while (offset < bytes.length
					&& (n = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += n;
			}

			// Presvedcime se, ze jsme precetli cely soubor
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "
						+ metafile.getName());
			}

			// Zavre stream
			is.close();
		} catch (IOException e) {
			System.out.println("IO problem in metafile reading");
		}
		// urci offset, kde konci hlavicka a zacinaji kontrolni soucty
		fileOffset = 0;
		for (int i = 2; i < bytes.length; i++) {
			if (bytes[i - 2] == 10 && bytes[i - 1] == 10) {
				fileOffset = i;
				break;
			}
		}
		fillHashTable(bytes);
	}

	/**
	 * Fills a chaining hash table with ChecksumPairs
	 * 
	 * @param checksums
	 *            Byte array with bytes of whole metafile
	 */
	private void fillHashTable(byte[] checksums) {
		int i = 16;
		// spocteme velikost hashtable podle poctu bloku dat
		while ((2 << (i - 1)) > blockNum && i > 4) {
			i--;
		}
		// vytvorime hashtable o velikosti 2^i (max. 2^16, min. 2^4)
		hashtable = new ChainingHash(2 << (i - 1));
		ChecksumPair p = null;
		// Link item;
		int offset = 0;
		int weakSum = 0;
		int seq = 0;
		int off = fileOffset;

		byte[] weak = new byte[4];
		byte[] strongSum = new byte[mf_checksum_bytes];

		while (seq < blockNum) {

			for (int w = 0; w < mf_rsum_bytes; w++) {
				weak[w] = checksums[off];
				off++;
			}

			for (int s = 0; s < strongSum.length; s++) {
				strongSum[s] = checksums[off];
				off++;
			}

			weakSum = 0;
			weakSum += (weak[2] & 0x000000FF) << 24;
			weakSum += (weak[3] & 0x000000FF) << 16;
			weakSum += (weak[0] & 0x000000FF) << 8;
			weakSum += (weak[1] & 0x000000FF);

			p = new ChecksumPair(weakSum, strongSum.clone(), offset,
					mf_blocksize, seq);
			offset += mf_blocksize;
			seq++;
			// item = new Link(p);
			hashtable.insert(p);
		}
	}

	/**
	 * Returns value indicating whetever the authentication is neccessary
	 * 
	 * @return Boolean value
	 */
	public boolean getAuthentication() {
		return authing;
	}

	/**
	 * Authentication username
	 * 
	 * @return Username used for authentication
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Authentication password
	 * 
	 * @return Password used for authentication
	 */
	public String getPassword() {
		return passwd;
	}

	/**
	 * Returns hash table cotaining block checksums
	 * 
	 * @return Hash table
	 */
	public ChainingHash getHashtable() {
		return hashtable;
	}

	/**
	 * Returns number of blocks in complete file
	 * 
	 * @return Number of blocks
	 */
	public int getBlockCount() {
		return blockNum;
	}

	/**
	 * Returns size of block
	 * 
	 * @return Size of the data block
	 */
	public int getBlocksize() {
		return mf_blocksize;
	}

	/**
	 * Length of used strong sum
	 * 
	 * @return Length of strong sum
	 */
	public int getChecksumBytes() {
		return mf_checksum_bytes;
	}

	/**
	 * Returns name of the file that we are trying to synchronize
	 * 
	 * @return Name of the file
	 */
	public String getFilename() {
		return mf_filename;
	}

	/**
	 * Returns length of complete file
	 * 
	 * @return Length of the file
	 */
	public long getLength() {
		return mf_length;
	}

	/**
	 * Last modified time of file stored in metafile stored in string format
	 * ("EEE, dd MMM yyyy HH:mm:ss Z")
	 * 
	 * @return String form of mtime
	 */
	public String getMtime() {
		return mf_mtime;
	}

	/**
	 * Length of used weak sum
	 * 
	 * @return Length of weak sum
	 */
	public int getRsumBytes() {
		return mf_rsum_bytes;
	}

	/**
	 * Number of consequence blocks
	 * 
	 * @return Number of consequence blocks
	 */
	public int getSeqNum() {
		return mf_seq_num;
	}

	/**
	 * Returns SHA1sum of complete file
	 * 
	 * @return String containing SHA1 sum of complete file
	 */
	public String getSha1() {
		return mf_sha1;
	}

	/**
	 * Return URL of complete file
	 * 
	 * @return URL address in String format
	 */
	public String getUrl() {
		return mf_url;
	}

	/**
	 * Return URL as origin of local metafile (in case that metafile contains
	 * relative URL to a file)
	 * 
	 * @return URL address in String format
	 */
	public String getRelativeURL() {
		return url;
	}

	/**
	 * Maximum number of simultaneously downloaded blocks
	 * 
	 * @return Max. number of blocks downloaded in one piece
	 */
	public int getRangesNumber() {
		return ranges;
	}

	/**
	 * Returns filename of seeding file
	 * 
	 * @return Filename of extra seeding file
	 */
	public String getInputFile() {
		return extraInputFile;
	}

	/**
	 * Length of DOWNLOADED metafile
	 * 
	 * @return Length of metafile
	 */
	public long getLengthOfMetafile() {
		return downloadedMetafile;
	}

}