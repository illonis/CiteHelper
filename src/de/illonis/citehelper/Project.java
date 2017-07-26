package de.illonis.citehelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Project {

	public final static String PROJECT_FILE_NAME = ".citeproject";

	private static final String KEY_PROJECT_NAME = "projectname";
	private static final String KEY_BIBFILE = "exporttarget";
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

	public static Project parse(Path file) throws IOException {
		List<String> lines = Files.readAllLines(file);
		Project p = new Project("");
		for (String line : lines) {
			String[] parts = line.split("=", 2);
			String key = parts[0].trim();
			String val = parts[1].trim();
			switch (key) {
			case KEY_PROJECT_NAME:
				p.name = val;
				break;
			case KEY_BIBFILE:
				p.exportBibfile = Paths.get(val);
				break;
			default:
				break;
			}
			p.workingDirectory = file.getParent();
		}

		return p;
	}
}
