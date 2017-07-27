package de.illonis.citehelper;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class GoogleScholar {
	private final static String SEARCH_URL = "https://scholar.google.com/scholar?q=";

	public static URI getSearchUri(Paper paper) throws UnsupportedEncodingException {
		String term = paper.getTitle();
		if (paper.getAuthors().size() > 0) {
			term += " " + paper.getAuthors().get(0);
		}
		return URI.create(SEARCH_URL + URLEncoder.encode(term, "UTF-8"));
	}

}
