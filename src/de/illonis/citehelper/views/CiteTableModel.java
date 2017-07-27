package de.illonis.citehelper.views;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import de.illonis.citehelper.Messages;
import de.illonis.citehelper.Paper;

public class CiteTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private final static String[] columnNames = { Messages.getString("tableheader.title"), Messages.getString("tableheader.author"), Messages.getString("tableheader.year"), Messages.getString("tableheader.citekey") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
		fireTableRowsInserted(paperList.size() - 1, paperList.size() - 1);
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
				return Messages.getString("label.na"); //$NON-NLS-1$
			}
		case 2:
			return paper.getYear();
		case 3:
			return paper.getKey();
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	public void clear() {
		paperList.clear();
		fireTableDataChanged();
	}

	public void addAll(List<Paper> papers) {
		if (papers.size() == 0) {
			return;
		}
		int oldSize = paperList.size();
		paperList.addAll(papers);
		fireTableRowsInserted(oldSize, paperList.size() - 1);
	}

	public List<Paper> findPapersFromSource(Path source) {
		return paperList.stream().filter(p -> p.getSource().equals(source)).collect(Collectors.toList());
	}

	public void remove(List<Paper> papers) {
		paperList.removeAll(papers);
		fireTableDataChanged();
	}
}
