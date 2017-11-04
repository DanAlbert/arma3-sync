package fr.soe.a3s.exception;

import java.io.IOException;

public class IncompleteFileTransferException extends IOException {

	private String message;

	public IncompleteFileTransferException(String fileName, long actualSize,
			long remoteSize) {

		this.message = "Incompete file size transfer: " + fileName
				+ " Remote size: " + remoteSize + " Bytes, " + "Actual size: "
				+ actualSize + " Bytes";
	}

	@Override
	public String getMessage() {
		return this.message;
	}
}
