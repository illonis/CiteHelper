package de.illonis.citehelper.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import de.illonis.citehelper.Paper;

public class CiteTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final static String[] columnNames = { "Title", "Author", "Year", "Citekey" };
	private final List<Paper> paperList;

	public CiteTableModel() {
		paperList = new ArrayList<>();
	}

	@Override
	public int getRowCount() {
		return paperList.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	public void add(Paper paper) {
		paperList.add(paper);
	}

	public Paper getPaper(int row) {
		return paperList.get(row);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Paper paper = paperList.get(rowIndex);

		switch (columnIndex) {
		case 0:
			return paper.getTitle();
		case 1:
			if (paper.getAuthors().size() > 0) {
				return paper.getAuthors().get(0);
			} else {
				return "N/A";
			}
		case 2:
			return paper.getYear();
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

}
