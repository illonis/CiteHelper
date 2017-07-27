package de.illonis.citehelper.views;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.jbibtex.BibTeXEntry;
import org.jbibtex.ParseException;

import de.illonis.citehelper.Messages;
import de.illonis.citehelper.bibtex.BibtexImporter;

public class TagModel extends AbstractTableModel {

	private final static class AttributeEntry {
		private final String key;
		private final String value;

		public AttributeEntry(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

	}

	private static final long serialVersionUID = 1L;
	private final static String[] columnNames = { Messages.getString("tableheader.key"), //$NON-NLS-1$
			Messages.getString("tableheader.value") }; //$NON-NLS-1$

	private final List<AttributeEntry> entries;

	public TagModel() {
		entries = new LinkedList<>();
	}

	@Override
	public int getRowCount() {
		return entries.size();
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (0 == columnIndex) {
			return entries.get(rowIndex).getKey();
		} else {
			return entries.get(rowIndex).getValue();
		}
	}

	public void setData(BibTeXEntry bibtexEntry) {
		entries.clear();
		bibtexEntry.getFields().entrySet().forEach(e -> {
			String plainValue = e.getValue().toUserString();
			if (BibtexImporter.hasLatex(plainValue)) {
				try {
					plainValue = BibtexImporter.latexToPlaintext(plainValue);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			entries.add(new AttributeEntry(e.getKey().getValue(), plainValue));
		});
		entries.add(new AttributeEntry(Messages.getString("tableheader.citekey"), bibtexEntry.getKey().getValue())); //$NON-NLS-1$
		fireTableDataChanged();
	}

}
