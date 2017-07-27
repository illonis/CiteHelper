package de.illonis.citehelper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Project {

	private static final String KEYVALUE_SEPARATOR = "="; //$NON-NLS-1$

	private static final String DEFAULT_EXPORT_FILENAME = "library.bib"; //$NON-NLS-1$

	public final static String PROJECT_FILE_NAME = ".citeproject"; //$NON-NLS-1$

	private static final String KEY_PROJECT_NAME = "projectname"; //$NON-NLS-1$
	private static final String KEY_BIBFILE = "exporttarget"; //$NON-NLS-1$
	private String name;
	private Path workingDirectory;
	private Path exportBibfile;

	public Project(String name) {
		this.name = name;
		this.workingDirectory = Paths.get("."); //$NON-NLS-1$
		this.exportBibfile = Paths.get(DEFAULT_EXPORT_FILENAME);
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
		Project p = new Project(""); //$NON-NLS-1$
		for (String line : lines) {
			String[] parts = line.split(KEYVALUE_SEPARATOR, 2);
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

	public static void save(Project project) throws IOException {
		List<String> lines = new LinkedList<>();
		lines.add(KEY_PROJECT_NAME + KEYVALUE_SEPARATOR + project.getName());
		lines.add(KEY_BIBFILE + KEYVALUE_SEPARATOR + project.getExportBibfile().toString());
		Path confFile = project.getWorkingDirectory().resolve(Project.PROJECT_FILE_NAME);
		Files.write(confFile, lines);
	}
}
