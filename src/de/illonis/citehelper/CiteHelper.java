package de.illonis.citehelper;

import java.util.LinkedList;
import java.util.List;

import de.illonis.citehelper.views.CiteTableModel;
import de.illonis.citehelper.views.MainWindow;

public class CiteHelper {

	public static void main(String[] args) {

		CiteTableModel tableData = new CiteTableModel();

		fillDemoData(tableData);

		MainWindow window = new MainWindow(tableData);

		window.setSize(800, 600);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}

	private static void fillDemoData(CiteTableModel tableData) {

		List<String> authors = new LinkedList<>();
		authors.add("Yann LeCun");
		Paper paper = new Paper();
		paper.setTitle("Random first title");
		paper.setYear(1997);
		paper.setAuthors(authors);
		paper.setFilename("random91.pdf");
		tableData.add(paper);

		paper = new Paper();
		paper.setTitle("Random second title");
		paper.setYear(2006);
		paper.setAuthors(new LinkedList<>());
		paper.setFilename("random9131.pdf");
		tableData.add(paper);

	}
}
