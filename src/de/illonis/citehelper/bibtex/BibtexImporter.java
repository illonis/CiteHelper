package de.illonis.citehelper.bibtex;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.Key;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.jbibtex.Value;

import de.illonis.citehelper.Paper;

public class BibtexImporter {

	public List<Paper> importFromString(String text) throws TokenMgrException, ParseException {
		BibTeXParser bibtexParser = new org.jbibtex.BibTeXParser();
		Reader reader = new StringReader(text);
		BibTeXDatabase database = bibtexParser.parse(reader);
		List<Paper> papers = database.getEntries().entrySet().stream().map(m -> entryToPaper(m))
				.collect(Collectors.toList());
		return papers;
	}

	private static Paper entryToPaper(Entry<Key, BibTeXEntry> entry) {
		String key = entry.getKey().getValue();
		BibTeXEntry texEntry = entry.getValue();

		Paper paper = new Paper();
		paper.setKey(key);

		Value title = texEntry.getField(BibTeXEntry.KEY_TITLE);
		String titleString = title.toUserString();
		paper.setTitle(titleString);

		return paper;
	}
}
