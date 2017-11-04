package fr.soe.a3s.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class ConnectionExceptionFactory {

	private static final String CONNECTION_FAILED = "Connection failed";
	private static final String CONNECTION_TIME_OUT_REACHED = "Connection closed by ArmA3Sync."
			+ "\n" + "Server didn't respond, connection timeout reached";
	private static final String READ_TIME_OUT_REACHED = "Connection closed by ArmA3Sync"
			+ "\n" + "Server didn't respond, read timeout reached";
	private static final String TIME_OUT_REACHED = "Connection closed by ArmA3Sync"
			+ "\n"
			+ "Server didn't respond, connection or read timeout reached";
	public static final String WRONG_LOGIN_PASSWORD = "login or password wrong or missing";

	public static IOException Exception(String coreMessage, IOException e) {

		if (e instanceof UnknownHostException || e instanceof SocketException
				|| e.getCause() instanceof SocketException) {
			String message = coreMessage + "\n" + CONNECTION_FAILED;
			return new SocketException(message);
		} else if (e instanceof SocketTimeoutException
				|| e.getCause() instanceof SocketTimeoutException) {
			Throwable thr = null;
			if (e instanceof SocketTimeoutException) {
				thr = e;
			} else {
				thr = e.getCause();
			}
			String message = coreMessage + "\n" + TIME_OUT_REACHED;
			if (thr.getMessage() != null) {
				if (thr.getMessage().toLowerCase().contains("read")) {
					message = coreMessage + "\n" + READ_TIME_OUT_REACHED;
				} else if (thr.getMessage().toLowerCase().contains("connect")) {
					message = coreMessage + "\n" + CONNECTION_TIME_OUT_REACHED;
				}
			}
			return new SocketTimeoutException(message);
		} else if (e instanceof IncompleteFileTransferException) {
			return e;
		} else if (e instanceof FileNotFoundException) {
			String message = coreMessage;
			if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			return new FileNotFoundException(message);
		} else {
			String message = coreMessage;
			if (e.getMessage() != null) {
				message = message + "\n" + e.getMessage();
			}
			return new IOException(message);
		}
	}
}
