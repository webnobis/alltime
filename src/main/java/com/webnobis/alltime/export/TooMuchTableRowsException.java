package com.webnobis.alltime.export;

public class TooMuchTableRowsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TooMuchTableRowsException(int rows, int maxRows) {
		super(String.format("%d rows given, up to %d rows expected", rows, maxRows));
	}

}
