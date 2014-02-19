/* HttpConnection.java

   HttpConnection: HTTP connection and parsing methods for Range requests
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.CountingOutputStream;

import fr.soe.a3s.dao.HttpDAO;

/**
 * HTTP connection with Range support class
 * 
 * @author Tomáš Hlavni�?ka
 */
public class HttpConnection {

	private String rangeRequest;

	private final String hostname;

	private final String login;

	private final String password;

	private final String port;

	private HttpURLConnection connection;

	private String boundary;

	private byte[] boundaryBytes;

	private long contLen;

	private static final int BUFFER_SIZE = 4096;

	private long allData = 0;

	private final HttpDAO httpDAO;

	public HttpConnection(String hostname, String login, String password,
			String port, HttpDAO httpDAO) {
		this.hostname = hostname;
		this.login = login;
		this.password = password;
		this.port = port;
		this.httpDAO = httpDAO;
	}

	/**
	 * Opens HTTP connection
	 * 
	 * @param relativeUrl
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void openConnection(String relativeUrl) throws IOException,
			URISyntaxException {

		// See
		// http://stackoverflow.com/questions/724043/http-url-address-encoding-in-java

		URI uri = new URI("http", hostname, relativeUrl, null);
		URL url = uri.toURL();
		String file = url.getFile();

		URL url2 = new URL("http", hostname, Integer.parseInt(port), file);
		connection = (HttpURLConnection) url2.openConnection();
		String encoding = Base64Coder.encodeLines((login + ":" + password)
				.getBytes());
		connection.setRequestProperty("Authorization",
				"Basic " + encoding.substring(0, encoding.length() - 1));
		httpDAO.seConnexion(connection);
	}

	/**
	 * Returns HTTP status code of response
	 * 
	 * @return HTTP code
	 * @throws IOException
	 */
	private int getHttpStatusCode() throws IOException {
		int code = connection.getResponseCode();
		return code;
	}

	/**
	 * Sends HTTP GET request
	 * 
	 * @throws ProtocolException
	 */
	public void sendRequest() throws ProtocolException {

		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "jazsync");
		if (login != null && password != null) {
			String encoding = Base64Coder.encodeLines((login + ":" + password)
					.getBytes());
			connection.setRequestProperty("Authorization",
					"Basic " + encoding.substring(0, encoding.length() - 1));
		}
		if (rangeRequest != null) {
			connection.setRequestProperty("Range", "bytes=" + rangeRequest);
		}
	}

	/**
	 * Sets ranges for http request
	 * 
	 * @param ranges
	 *            ArrayList of DataRange objects containing block ranges
	 */
	public void setRangesRequest(ArrayList<DataRange> ranges) {
		StringBuilder sb = new StringBuilder();
		for (DataRange d : ranges) {
			sb.append(d.getRange()).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());
		rangeRequest = sb.toString();
	}

	/**
	 * Comparing to find boundaries in byte stream
	 * 
	 * @param src
	 *            Byte array with data
	 * @param srcOff
	 *            Offset in byte array with data
	 * @param bound
	 *            Byte array with boundary value
	 * @return
	 */
	private boolean boundaryCompare(byte[] src, int srcOff, byte[] bound) {
		int j = srcOff;
		for (int i = 0; i < bound.length; i++) {
			if (src[j] != bound[i]) {
				return false;
			}
			j++;
		}
		return true;
	}

	/**
	 * Method that looks through byte array and figure out where boundaries are
	 * and where relevant data starts
	 * 
	 * @param src
	 *            Array where we are trying to find data boundaries
	 * @param i
	 *            Offset of src array where we are starting the look up
	 * @return Offset where the data starts
	 */
	private int dataBegin(byte[] src, int i) {
		int newLine = 0;
		int offset = i;
		for (; offset < src.length; offset++) {
			if (src[offset] == 13 && src[offset + 1] == 10) {
				newLine++;
				if (newLine == 4) {
					offset += 2;
					break;
				}
			}
		}
		return offset;
	}

	/**
	 * Downloads data block or ranges of blocks
	 * 
	 * @param blockLength
	 *            Length of a data block that we are downloading
	 * @return Content of body in byte array
	 * @throws IOException
	 */
	public byte[] getResponseBody(int blockLength) throws IOException {

		// opens input stream from the HTTP connection
		InputStream inputStream = connection.getInputStream();

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		httpDAO.setStartTime(System.nanoTime());

		CountingOutputStream dos = new CountingOutputStream(buffer) {
			@Override
			protected void afterWrite(int n) throws IOException {
				super.afterWrite(n);
				// System.out.println(getCount());
				int nbBytes = getCount();
				httpDAO.setEndTime(System.nanoTime());
				httpDAO.updateObserverSpeed(nbBytes);
			}
		};

		int read;
		// byte[] temp = new byte[(int) contLen];//out of memory >256M
		byte[] temp = new byte[BUFFER_SIZE];
		while ((read = inputStream.read(temp)) != -1 && !httpDAO.isCanceled()) {
			dos.write(temp, 0, read);
		}
		byte[] bytes = buffer.toByteArray();
		contLen = bytes.length;

		allData += contLen;

		// pripad, kdy data obsahuji hranice (code 206 - partial content)
		if (boundary != null) {
			int range = 0;
			byte[] rangeBytes = new byte[(int) contLen + blockLength];
			for (int i = 0; i < bytes.length; i++) {
				// jestlize jsou ve streamu "--"
				if (bytes[i] == 45 && bytes[i + 1] == 45) {
					// zkontrolujeme jestli za "--" je boundary hodnota
					if (boundaryCompare(bytes, i + 2, boundaryBytes)) {
						i += 2 + boundaryBytes.length; // presuneme se za
														// boundary
						/*
						 * pokud je za boundary dalsi "--" jde o konec streamu v
						 * opacnem pripade si data zkopirujeme
						 */
						if (bytes[i] != 45 && bytes[i + 1] != 45) {
							try {
								System.arraycopy(bytes, dataBegin(bytes, i),
										rangeBytes, range, blockLength);
							} catch (ArrayIndexOutOfBoundsException e) {
								// e.printStackTrace();
								/*
								 * osetreni vyjimky v pripade kopirovani
								 * kratsiho bloku dat
								 */
								System.arraycopy(bytes, dataBegin(bytes, i),
										rangeBytes, range, bytes.length
												- dataBegin(bytes, i));
							}
							range += blockLength;
						}
					}
				}
			}
			byte[] ranges = new byte[range];
			System.arraycopy(rangeBytes, 0, ranges, 0, ranges.length);
			return ranges;
		}

		return bytes;
	}

	/**
	 * Downloads whole file
	 * 
	 * @param length
	 *            Length of the file
	 * @param filename
	 *            Name of the downloaded and saved file
	 * @throws IOException
	 */
	public void getFile(long length, File targetFile) throws IOException {

		// opens input stream from the HTTP connection
		InputStream inputStream = connection.getInputStream();

		// opens an output stream to save into file
		FileOutputStream outputStream = new FileOutputStream(targetFile, false);

		httpDAO.setSize(length);
		httpDAO.setStartTime(System.nanoTime());
		httpDAO.setOffset(0);
		CountingOutputStream dos = new CountingOutputStream(outputStream) {
			@Override
			protected void afterWrite(int n) throws IOException {
				super.afterWrite(n);
				// System.out.println(getCount());
				int nbBytes = getCount();
				httpDAO.setCountFileSize(nbBytes);
				httpDAO.setEndTime(System.nanoTime());
				httpDAO.updateFileSizeObserver();
				httpDAO.updateObserverSpeed(nbBytes);
			}
		};

		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = inputStream.read(buffer)) != -1
				&& !httpDAO.isCanceled()) {
			dos.write(buffer, 0, bytesRead);
		}
		inputStream.close();
		outputStream.close();
		dos.close();
	}

	public void getResumedFile(long length, File targetFile) throws IOException {

		// Specify what portion of file to download.
		connection.setRequestProperty("Range", "bytes=" + targetFile.length()
				+ "-");

		// opens input stream from the HTTP connection
		InputStream inputStream = connection.getInputStream();

		// Check for valid content length.
		int contentLength = connection.getContentLength();

		// opens an output stream to save into file
		FileOutputStream outputStream = new FileOutputStream(targetFile, true);

		httpDAO.setSize(length);
		httpDAO.setStartTime(System.nanoTime());
		long offset = targetFile.length();
		httpDAO.setOffset(offset);
		CountingOutputStream dos = new CountingOutputStream(outputStream) {
			@Override
			protected void afterWrite(int n) throws IOException {
				super.afterWrite(n);
				// System.out.println(getCount());
				int nbBytes = getCount();
				httpDAO.setCountFileSize(getCount());
				httpDAO.setEndTime(System.nanoTime());
				httpDAO.updateFileSizeObserver();
				httpDAO.updateObserverSpeed(nbBytes);
			}
		};

		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = inputStream.read(buffer)) != -1
				&& !httpDAO.isCanceled()) {
			dos.write(buffer, 0, bytesRead);
		}
		inputStream.close();
		outputStream.close();
		dos.close();
	}

	/**
	 * Returns http response header and looks up for a boundary and length keys,
	 * saving their values into the variables
	 * 
	 * @return Returns header in String format
	 * @throws IOException
	 */
	public String getResponseHeader() throws IOException {
		String header = "";
		Map responseHeader = connection.getHeaderFields();

		for (Iterator iterator = responseHeader.keySet().iterator(); iterator
				.hasNext();) {
			String key = (String) iterator.next();
			if (key != null) {
				header += key + " = ";
			}
			List values = (List) responseHeader.get(key);
			for (int i = 0; i < values.size(); i++) {
				Object o = values.get(i);
				header += o.toString();
				parseBoundary(key, o.toString());
				parseLength(key, o.toString());
			}
			header += "\n";
		}
		allData += header.length();
		return header;
	}

	/**
	 * Parse the length of content send in body
	 * 
	 * @param key
	 *            Key name of header line
	 * @param values
	 *            Values of key header line
	 */
	private void parseLength(String key, String values) {
		if (key != null && key.equals("Content-Length") == true) {
			contLen = Integer.valueOf(values);
		}
	}

	/**
	 * Gets boundary sequence from response header for identificating the range
	 * boundaries
	 * 
	 * @param key
	 *            Key name of header line
	 * @param values
	 *            Values of key header line
	 * @throws IOException
	 */
	private void parseBoundary(String key, String values) throws IOException {
		if (getHttpStatusCode() == 206 && key != null
				&& key.equals("Content-Type") == true) {
			int index = values.indexOf("boundary");
			if (index != -1) {
				boundary = values.substring(index + "boundary=".length());
				boundaryBytes = boundary.getBytes();
			}
		}
	}

	/**
	 * Closes HTTP connection
	 */
	public void closeConnection() {
		connection.disconnect();
	}

	public long getAllTransferedDataLength() {
		return allData;
	}

	public HttpDAO getHttpDAO() {
		return this.httpDAO;
	}

}
