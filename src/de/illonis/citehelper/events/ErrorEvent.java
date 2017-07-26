package de.illonis.citehelper.events;

public class ErrorEvent {
	private final String message;
	private final Exception exception;

	public ErrorEvent(String message, Exception e) {
		this.message = message;
		this.exception = e;
	}

	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}

}
