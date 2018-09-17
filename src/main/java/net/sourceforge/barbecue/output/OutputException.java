package net.sourceforge.barbecue.output;

public class OutputException extends Exception {
	private static final long serialVersionUID = 9217976537464519000L;

	public OutputException(String msg) {
		super(msg);
	}

	public OutputException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public OutputException(Throwable cause) {
		super(cause);
	}
}
