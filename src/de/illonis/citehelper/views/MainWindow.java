package de.illonis.citehelper.views;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.illonis.citehelper.FileMenu;
import de.illonis.citehelper.Paper;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final CiteTableModel tableModel;

	public MainWindow(CiteTableModel tableData) {
		super("CiteHelper");

		setJMenuBar(new FileMenu());

		tableModel = tableData;
		table = new JTable(tableModel);
		initTable();
		JScrollPane tableScroller = new JScrollPane(table);
		setLayout(new BorderLayout());
		add(tableScroller, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initTable() {
		table.setAutoCreateRowSorter(true);

		table.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int row = table.rowAtPoint(p);
				if (row != -1 && me.getClickCount() == 2) {
					// doubleclicked item
					int modelIndex = table.getRowSorter().convertRowIndexToModel(row);
					Paper paper = tableModel.getPaper(modelIndex);
					openFileFor(paper);
				}
			}
		});
	}

	protected void openFileFor(Paper paper) {
		// TODO implement
		JOptionPane.showMessageDialog(MainWindow.this, "Opened at " + paper.getTitle());

	}

}
