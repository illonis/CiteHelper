package de.illonis.citehelper.events;

import java.io.File;

public class ImportFileChosenEvent {

	private File fileImported;

	public ImportFileChosenEvent(File fileImported) {
		this.fileImported = fileImported;
	}

	public File getFileImported() {
		return fileImported;
	}

}
