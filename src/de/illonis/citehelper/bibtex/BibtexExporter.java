package de.illonis.citehelper.bibtex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

public class BibtexExporter {

	public static void exportToFile(Path file, BibTeXEntry entry)
			throws IOException, TokenMgrException, ParseException {
		List<BibTeXEntry> entryList = new LinkedList<>();
		entryList.add(entry);
		exportToFile(file, entryList);
	}

	public static void exportToFile(Path file, List<BibTeXEntry> entries)
			throws IOException, TokenMgrException, ParseException {
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			BibTeXDatabase database = new BibTeXDatabase();
			for (BibTeXEntry bibTeXEntry : entries) {
				database.addObject(bibTeXEntry);
			}
			BibTeXFormatter bibtexFormatter = new BibTeXFormatter();
			bibtexFormatter.format(database, writer);
		}
	}

}
