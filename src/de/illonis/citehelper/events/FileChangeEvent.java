package de.illonis.citehelper.events;

import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;

public class FileChangeEvent {

	private final WatchEvent.Kind<Path> changeType;
	private final Path folder;
	private final Path file;

	public FileChangeEvent(Kind<Path> changeType, Path folder, Path file) {
		this.changeType = changeType;
		this.folder = folder;
		this.file = file;
	}

	public WatchEvent.Kind<Path> getChangeType() {
		return changeType;
	}

	public Path getFolder() {
		return folder;
	}

	public Path getFile() {
		return file;
	}

}
