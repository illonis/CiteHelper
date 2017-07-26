package de.illonis.citehelper;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Project {

	private String name;
	private Path workingDirectory;
	private Path exportBibfile;

	public Project(String name) {
		this.name = name;
		this.workingDirectory = Paths.get(".");
		this.exportBibfile = Paths.get("library.bib");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Path getWorkingDirectory() {
		return workingDirectory;
	}

	public void setWorkingDirectory(Path workingDirectory) {
		this.workingDirectory = workingDirectory;
	}

	public Path getExportBibfile() {
		return exportBibfile;
	}

	public void setExportBibfile(Path exportBibfile) {
		this.exportBibfile = exportBibfile;
	}

}
