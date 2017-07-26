package de.illonis.citehelper;

import java.util.List;

public class Paper {

	private String key;
	private String title;
	private int year;
	private List<String> authors;
	private String filename;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getTitle() {
		return title;
	}

	public int getYear() {
		return year;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public String getFilename() {
		return filename;
	}

}
