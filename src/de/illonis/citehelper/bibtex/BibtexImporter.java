package de.illonis.citehelper.bibtex;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
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

	private static final String AUTHOR_DELIMITER = " and "; //$NON-NLS-1$
	private static final String FILE_SEPARATOR = "\n"; //$NON-NLS-1$

	public List<Paper> importFromFile(Path file) throws IOException, TokenMgrException, ParseException {
		String text = Files.readAllLines(file).stream().collect(Collectors.joining(FILE_SEPARATOR));
		List<Paper> papers = importFromString(text);
		papers.forEach(p -> p.setSource(file));
		return papers;
	}

	public List<Paper> importFromString(String text) throws TokenMgrException, ParseException {
		BibTeXParser bibtexParser = new org.jbibtex.BibTeXParser();
		Reader reader = new StringReader(text);
		BibTeXDatabase database = bibtexParser.parse(reader);
		List<Paper> papers = database.getEntries().entrySet().stream().map(m -> entryToPaper(m))
				.collect(Collectors.toList());
		return papers;
	}

	private static String latexToPlaintext(String latexString) throws ParseException {
		org.jbibtex.LaTeXParser latexParser = new org.jbibtex.LaTeXParser();
		List<org.jbibtex.LaTeXObject> latexObjects = latexParser.parse(latexString);
		org.jbibtex.LaTeXPrinter latexPrinter = new org.jbibtex.LaTeXPrinter();
		String plainTextString = latexPrinter.print(latexObjects);
		return plainTextString;
	}

	private static Paper entryToPaper(Entry<Key, BibTeXEntry> entry) {
		String key = entry.getKey().getValue();
		BibTeXEntry texEntry = entry.getValue();

		Paper paper = new Paper();
		paper.setKey(key);
		paper.setBibtexEntry(texEntry);

		Value title = texEntry.getField(BibTeXEntry.KEY_TITLE);
		if (null != title) {
			String titleString = title.toUserString();
			paper.setTitle(titleString);
		}

		Value year = texEntry.getField(BibTeXEntry.KEY_YEAR);
		if (null != year) {
			int yearVal = Integer.parseInt(year.toUserString());
			paper.setYear(yearVal);
		}

		Value authorValue = texEntry.getField(BibTeXEntry.KEY_AUTHOR);
		if (null != authorValue) {
			String authorString = authorValue.toUserString();
			String[] authors = authorString.split(AUTHOR_DELIMITER);
			List<String> authorList = new LinkedList<>();
			for (int i = 0; i < authors.length; i++) {
				String author = authors[i];
				if (author.indexOf('\\') > -1 || author.indexOf('{') > -1) {
					// LaTeX string that needs to be translated to plain text
					// string
					try {
						authorList.add(latexToPlaintext(author));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					// Plain text string
					authorList.add(author);
				}
			}
			paper.setAuthors(authorList);
		}

		Value url = texEntry.getField(BibTeXEntry.KEY_URL);
		if (null != url) {
			String urlString = url.toUserString();
			paper.setUrl(urlString);
		}
		return paper;
	}
}
