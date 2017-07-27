package de.illonis.citehelper.tasks;

public interface ResultHandler<T> {

	void handleAsyncResult(T result);
}
