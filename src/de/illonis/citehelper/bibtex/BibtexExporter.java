package de.illonis.citehelper.bibtex;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXFormatter;
import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

public class BibtexExporter {

	public static void exportToFile(Path file, BibTeXEntry entry)
			throws IOException, TokenMgrException, ParseException {
		try (BufferedWriter writer = Files.newBufferedWriter(file)) {
			BibTeXDatabase database = new BibTeXDatabase();
			database.addObject(entry);
			BibTeXFormatter bibtexFormatter = new BibTeXFormatter();
			bibtexFormatter.format(database, writer);
		}
	}

}
