package fr.soe.a3s.dao.connection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.output.CountingOutputStream;

import fr.soe.a3s.constant.ProtocolType;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.HttpException;

public class MyHttpConnection {

	private URLConnection urLConnection;
	private final AbstractProtocole protocole;
	private final HttpDAO httpDAO;
	private String rangeRequest;
	private String boundary;
	private byte[] boundaryBytes;
	private long contLen;
	private static final int BUFFER_SIZE = 4096;// 4KB
	private int bufferSize = BUFFER_SIZE;
	private long elapsedTime = 0;
	private long allData = 0;

	public MyHttpConnection(AbstractProtocole protocole, HttpDAO httpDAO) {
		this.protocole = protocole;
		this.httpDAO = httpDAO;
	}

	/**
	 * opens URLConnection
	 * 
	 * @param the
	 *            full relativeUrl of the file
	 * @throws IOException
	 */
	public void openConnection(String relativeUrl) throws IOException {

		String hostname = protocole.getHostname();
		String port = protocole.getPort();
		String connectionTimeOut = protocole.getConnectionTimeOut();
		String readTimeOut = protocole.getReadTimeOut();
		String login = protocole.getLogin();
		String password = protocole.getPassword();

		/*
		 * See http://stackoverflow.com/questions/724043/http-url-address
		 * -encoding -in-java
		 * http://stackoverflow.com/questions/13022717/java-and
		 * -https-url-connection-without-downloading-certificate
		 */

		try {
			if (protocole.getProtocolType().equals(ProtocolType.HTTPS)) {

				URI uri = new URI("https", hostname, relativeUrl, null);
				URL url = uri.toURL();
				String file = url.getFile();

				URL url2 = new URL("https", hostname, Integer.parseInt(port),
						file);
				urLConnection = url2.openConnection();

				// Create a trust manager that does not validate certificate
				// chains
				TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
					@Override
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain,
							String authType) throws CertificateException {
					}
				} };

				// Install the all-trusting trust manager
				final SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
				// Create all-trusting host name verifier
				HostnameVerifier allHostsValid = new HostnameVerifier() {
					@Override
					public boolean verify(String hostname, SSLSession session) {
						return true;
					}
				};

				// Install the all-trusting host verifier
				HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
			} else {
				URI uri = new URI("http", hostname, relativeUrl, null);
				URL url = uri.toURL();
				String file = url.getFile();

				URL url2 = new URL("http", hostname, Integer.parseInt(port),
						file);
				urLConnection = url2.openConnection();
			}

			// Set connection and read time out
			int connectionTimeOutValue = Integer.parseInt(connectionTimeOut);
			if (connectionTimeOutValue != 0) {
				urLConnection.setConnectTimeout(connectionTimeOutValue);
			}
			int readTimeOutValue = Integer.parseInt(readTimeOut);
			if (readTimeOutValue != 0) {
				urLConnection.setReadTimeout(readTimeOutValue);
			}

			// Set User Agent
			urLConnection.setRequestProperty("User-Agent", "ArmA3Sync");

			// Login
			if (!(login.equalsIgnoreCase("anonymous"))) {
				String encoding = Base64Coder
						.encodeLines((login + ":" + password).getBytes());
				urLConnection.setRequestProperty("Authorization", "Basic "
						+ encoding.substring(0, encoding.length() - 1));
			}
		} catch (NumberFormatException | NoSuchAlgorithmException
				| KeyManagementException | URISyntaxException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Returns HTTP status code of response
	 * 
	 * @return HTTP code
	 * @throws IOException
	 */
	public int getHttpStatusCode() throws IOException {
		int code = ((HttpURLConnection) urLConnection).getResponseCode();
		return code;
	}

	public void setRequestHead() throws ProtocolException {
		((HttpURLConnection) urLConnection).setRequestMethod("HEAD");
	}

	/**
	 * Download and entire file, not cancelable
	 * 
	 * @param file
	 * @throws IOException
	 * @throws HttpException
	 */
	public void downloadFile(File file) throws IOException, HttpException {

		InputStream inputStream = null;
		FileOutputStream outputStream = null;

		try {
			int code = getHttpStatusCode();
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new HttpException(httpDAO.WRONG_LOGIN_PASSWORD);
			} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new FileNotFoundException(
						"File not found on repository: "
								+ urLConnection.getURL());
			}

			/*
			 * opens input stream from the HTTP httpURLConnection, throws
			 * FileNotFoundException if remote file does not exists
			 */
			inputStream = urLConnection.getInputStream();

			// opens an output stream to save into file
			outputStream = new FileOutputStream(file);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while (((bytesRead = inputStream.read(buffer)) != -1)) {
				outputStream.write(buffer, 0, bytesRead);
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	/**
	 * Download or resume a file, with record progress, cancelable
	 * 
	 * @param targetFile
	 * @return false if remote file does not exists
	 * @throws IOException
	 * @throws HttpException
	 */
	public void downloadFileWithRecordProgress(File targetFile)
			throws IOException, HttpException {

		/*
		 * Set range request for Resuming http://stackoverflow.com/questions
		 * /3414438/java-resume-download -in-urlconnection
		 */
		boolean resume = false;
		if (httpDAO.getOffset() > 0) {
			resume = true;
			System.out.println("Resuming file: " + targetFile.getAbsolutePath()
					+ " at offset " + httpDAO.getOffset());

			urLConnection.setRequestProperty(
					"Range",
					"bytes=" + httpDAO.getOffset() + "-"
							+ httpDAO.getExpectedFullSize());
		} else {
			resume = false;
			System.out.println("Downloading whole file: "
					+ targetFile.getAbsolutePath());
		}

		InputStream inputStream = null;
		FileOutputStream fos = null;
		CountingOutputStream dos = null;

		try {
			long startResponseTime = System.nanoTime();
			int code = getHttpStatusCode();
			long endResponseTime = System.nanoTime();
			httpDAO.setResponseTime(endResponseTime - startResponseTime);
			httpDAO.updateObserverDownloadResponseTime();

			if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new HttpException(httpDAO.WRONG_LOGIN_PASSWORD);
			} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new FileNotFoundException(
						"File not found on repository: "
								+ urLConnection.getURL());
			}

			/*
			 * opens input stream from the HTTP httpURLConnection, throws
			 * FileNotFoundException if remote file does not exists
			 */
			inputStream = urLConnection.getInputStream();

			/*
			 * opens an output stream to save into targetFile, throws
			 * FileNotFoundException target file can't be accessed/created
			 */
			fos = new FileOutputStream(targetFile, resume);

			final long startTime = System.nanoTime();
			this.elapsedTime = 0;
			this.httpDAO.setSpeed(0);
			dos = new CountingOutputStream(fos) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					long nbBytes = getByteCount();
					httpDAO.setCountFileSize(nbBytes);
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					long deltaTime = totalTime - elapsedTime;
					long speed = (long) (nbBytes / (totalTime * Math
							.pow(10, -9)));
					httpDAO.setSpeed(speed);
					if (httpDAO.getMaximumClientDownloadSpeed() != 0) {
						if (speed > httpDAO.getMaximumClientDownloadSpeed()) {
							bufferSize = bufferSize - 1;
							if (bufferSize < 0) {
								bufferSize = 0;
							}
						} else {
							bufferSize = bufferSize + 1;
							if (bufferSize > BUFFER_SIZE) {
								bufferSize = BUFFER_SIZE;
							}
						}
					}

					if (httpDAO.isAcquiredSemaphore()) {
						httpDAO.updateObserverDownloadSingleSizeProgress();
						if (deltaTime > Math.pow(10, 9) / 4) {
							httpDAO.updateObserverDownloadSpeed();
							elapsedTime = totalTime;
						}
					}
				}
			};

			int bytesRead = -1;
			byte[] buffer = new byte[bufferSize];
			while (((bytesRead = inputStream.read(buffer)) != -1)
					&& !httpDAO.isCanceled()) {
				dos.write(buffer, 0, bytesRead);
				buffer = new byte[bufferSize];
			}

			fos.close();
			dos.close();
			inputStream.close();
			httpDAO.setSpeed(0);

			if (!httpDAO.isCanceled()) {

				long actualSize = targetFile.length();
				long remoteSize = urLConnection.getContentLengthLong()
						+ httpDAO.getOffset();
				boolean checkSize = false;

				if (actualSize < remoteSize && remoteSize != -1) {
					String message = "WARNING: Incompete file size transfer. Remote size: "
							+ remoteSize
							+ " Bytes, "
							+ "Transfered size: "
							+ actualSize + " Bytes";
					System.out.println(message);
					String relativeUrl = urLConnection.getURL().getPath();
					closeConnection();
					openConnection(relativeUrl);
					boolean accept = checkAcceptRanges();
					closeConnection();
					if (accept) {
						System.out.println("Server supports resuming");
						openConnection(relativeUrl);
						httpDAO.setOffset(actualSize);
						downloadFileWithRecordProgress(targetFile);
					} else {
						System.out.println("Server does not supports resuming");
						throw new IOException(message + "/n"
								+ "Server does not supports resuming.");
					}
				}
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (dos != null) {
				dos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			httpDAO.setSpeed(0);
		}
	}

	/**
	 * Response body for downloading .zsync file
	 * 
	 * @return
	 * @throws IOException
	 * @throws HttpException
	 */
	public byte[] getResponseBody() throws IOException, HttpException {

		InputStream inputStream = null;
		byte[] bytes = null;

		try {
			int code = getHttpStatusCode();
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new HttpException(httpDAO.WRONG_LOGIN_PASSWORD);
			} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new FileNotFoundException(
						"File not found on repository: "
								+ urLConnection.getURL());
			}

			// opens input stream from the HTTP connection
			inputStream = urLConnection.getInputStream();

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			int bytesRead;
			byte[] temp = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(temp)) != -1
					&& !httpDAO.isCanceled()) {
				buffer.write(temp, 0, bytesRead);
			}

			bytes = buffer.toByteArray();
			buffer.close();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return bytes;
	}

	/**
	 * Downloads data block or ranges of blocks
	 * 
	 * @param blockSize
	 *            Length of a data block that we are downloading
	 * @param numberOfRanges
	 *            : number of downloaded blocks
	 * @param partFile
	 * @param partFile
	 * @return Content of body in byte array
	 * @throws IOException
	 * @throws HttpException
	 */
	public byte[] getResponseBody(List<DataRange> rangeList, File partFile,
			final int cumulatedBytesDownloaded) throws IOException,
			HttpException {

		InputStream inputStream = null;
		CountingOutputStream dos = null;

		try {
			long start = rangeList.get(0).getStart();
			long end = rangeList.get(rangeList.size() - 1).getEnd();
			final long transferSize = end - start;

			System.out.println("Performing range request [" + start + "-" + end
					+ "] for file: " + partFile.getAbsolutePath());

			((HttpURLConnection) urLConnection).setRequestProperty("Range",
					"bytes=" + start + "-" + end);

			long startResponseTime = System.nanoTime();
			int code = getHttpStatusCode();
			long endResponseTime = System.nanoTime();
			httpDAO.setResponseTime(endResponseTime - startResponseTime);
			httpDAO.updateObserverDownloadResponseTime();

			if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw new HttpException(httpDAO.WRONG_LOGIN_PASSWORD);
			} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
				throw new FileNotFoundException(
						"File not found on repository: "
								+ urLConnection.getURL());
			}

			inputStream = urLConnection.getInputStream();

			final long startTime = System.nanoTime();
			this.elapsedTime = 0;
			httpDAO.setSpeed(0);
			ByteArrayOutputStream byteArrayBuffer = new ByteArrayOutputStream();
			dos = new CountingOutputStream(byteArrayBuffer) {
				@Override
				protected void afterWrite(int n) throws IOException {
					super.afterWrite(n);
					long nbBytes = getByteCount();
					httpDAO.setCountFileSize(cumulatedBytesDownloaded + nbBytes);
					long endTime = System.nanoTime();
					long totalTime = endTime - startTime;
					long deltaTime = totalTime - elapsedTime;
					// System.out.println(deltaTime);
					long speed = (long) ((nbBytes * Math.pow(10, 9)) / totalTime);// B/s
					httpDAO.setSpeed(speed);
					if (httpDAO.getMaximumClientDownloadSpeed() != 0) {
						if (speed > httpDAO.getMaximumClientDownloadSpeed()) {
							bufferSize = bufferSize - 1;
							if (bufferSize < 0) {
								bufferSize = 0;
							}
						} else {
							bufferSize = bufferSize + 1;
							if (bufferSize > BUFFER_SIZE) {
								bufferSize = BUFFER_SIZE;
							}
						}
					}

					if (httpDAO.isAcquiredSemaphore()) {
						httpDAO.updateObserverDownloadSingleSizeProgress();
						if (deltaTime > Math.pow(10, 9) / 4) {
							httpDAO.updateObserverDownloadSpeed();
							elapsedTime = totalTime;
						}
					}
				}
			};

			int bytesRead = -1;
			byte[] temp = new byte[bufferSize];
			while ((bytesRead = inputStream.read(temp)) != -1
					&& !httpDAO.isCanceled()) {
				dos.write(temp, 0, bytesRead);
				temp = new byte[bufferSize];
			}

			byte[] bytes = byteArrayBuffer.toByteArray();
			contLen = bytes.length;
			allData += contLen;
			return bytes;
		} finally {
			httpDAO.setSpeed(0);
			if (inputStream != null) {
				inputStream.close();
			}
			if (dos != null) {
				dos.close();
			}
		}
	}

	/**
	 * Check header for Accept-Ranges
	 * 
	 * @return true if server accept Ranges request
	 * @throws IOException
	 */
	public boolean checkAcceptRanges() throws IOException {
		/*
		 * http://stackoverflow.com/questions/17643851/downloading-a-portion-of-a
		 * -file-using-http-requests
		 */
		// Note: IIS7 does not support range request with HEAD
		urLConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + 1);
		int statusCode = getHttpStatusCode();
		if (statusCode == HttpURLConnection.HTTP_PARTIAL) {
			return true;
		} else {
			return false;
		}
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
		Map responseHeader = urLConnection.getHeaderFields();

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
	 * Closes HTTP connection
	 */
	public void closeConnection() {
		try {
			((HttpURLConnection) urLConnection).disconnect();
		} catch (Exception e) {
		}
	}

	public HttpDAO getHttpDAO() {
		return this.httpDAO;
	}
}
