package de.illonis.citehelper;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

public class GoogleScholar {
	private final static String SEARCH_URL = "https://scholar.google.com/scholar?q="; //$NON-NLS-1$

	public static URI getSearchUri(Paper paper) throws UnsupportedEncodingException {
		StringBuilder stringBuilder = new StringBuilder(paper.getTitle());
		if (paper.getAuthors().size() > 0) {
			stringBuilder.append(" "); //$NON-NLS-1$
			stringBuilder.append(paper.getAuthors().get(0));
		}
		return URI.create(SEARCH_URL + URLEncoder.encode(stringBuilder.toString(), "UTF-8")); //$NON-NLS-1$
	}

}
