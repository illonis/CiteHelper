package de.illonis.citehelper.bibtex;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;
import org.junit.Test;

import de.illonis.citehelper.Paper;

public class BibtexParseTest {

	private String readFile(String filename) throws IOException, URISyntaxException {
		URL url = getClass().getResource(filename);
		return Files.readAllLines(Paths.get(url.toURI())).stream().collect(Collectors.joining());
	}

	@Test
	public void testSingleSimpleEntry() throws IOException, TokenMgrException, ParseException, URISyntaxException {
		String text = readFile("single.bib");
		BibtexImporter importer = new BibtexImporter();
		List<Paper> papers = importer.importFromString(text);
		assertEquals(1, papers.size());
		Paper paper = papers.get(0);
		assertEquals(1988, paper.getYear());
		assertEquals("Designing BIBTEX styles", paper.getTitle());
		assertEquals(1, paper.getAuthors().size());
		assertEquals("Patashnik, Oren", paper.getAuthors().get(0));
		assertEquals(paper.getKey(), "patashnik1988designing");
	}

	@Test
	public void testSingleComplexEntry() throws IOException, TokenMgrException, ParseException, URISyntaxException {
		String text = readFile("single2.bib");
		BibtexImporter importer = new BibtexImporter();
		List<Paper> papers = importer.importFromString(text);
		assertEquals(1, papers.size());
		Paper paper = papers.get(0);
		assertEquals(1999, paper.getYear());
		assertEquals("Emergence of scaling in random networks", paper.getTitle());
		assertEquals(2, paper.getAuthors().size());
		assertEquals("Barabási, Albert-László", paper.getAuthors().get(0));
		assertEquals("Albert, Réka", paper.getAuthors().get(1));
		assertEquals(paper.getKey(), "barabasi1999emergence");
	}

	@Test
	public void testMultipleEntries() throws IOException, URISyntaxException, TokenMgrException, ParseException {
		String text = readFile("multi.bib");
		BibtexImporter importer = new BibtexImporter();
		List<Paper> papers = importer.importFromString(text);
		assertEquals(54, papers.size());

	}
}
