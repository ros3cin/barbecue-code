package net.sourceforge.barbecue.formatter;

public class FormattingException extends Exception {
	private static final long serialVersionUID = 7724920686248226885L;

	public FormattingException(String s) {
		this(s, null);
	}

	public FormattingException(String s, Throwable cause) {
		super(s, cause);
	}

}
