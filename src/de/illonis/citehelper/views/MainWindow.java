package de.illonis.citehelper.views;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import de.illonis.citehelper.CiteEventBus;
import de.illonis.citehelper.GoogleScholar;
import de.illonis.citehelper.MainLogic;
import de.illonis.citehelper.Paper;
import de.illonis.citehelper.events.ErrorEvent;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final CiteTableModel tableModel;
	private final MainLogic logic;

	public MainWindow(CiteTableModel tableData, MainLogic logic) {
		super("CiteHelper");
		this.logic = logic;
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
		if (null != paper.getKey()) {
			String filename = paper.getKey() + ".pdf";
			Path filePath = logic.getCurrentProject().getWorkingDirectory().resolve(filename);
			if (Files.isRegularFile(filePath)) {
				try {
					Desktop.getDesktop().open(filePath.toFile());
				} catch (IOException e) {
					CiteEventBus.getInstance().getBus()
							.post(new ErrorEvent("Could not open associated file for paper " + paper.getTitle(), e));
					e.printStackTrace();
				}
			} else {
				int result = JOptionPane.showConfirmDialog(this, "No file attached, search on Google Scholar instead?",
						paper.getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (JOptionPane.OK_OPTION == result) {
					try {
						Desktop.getDesktop().browse(GoogleScholar.getSearchUri(paper));
					} catch (IOException e) {
						CiteEventBus.getInstance().getBus()
								.post(new ErrorEvent("Could not open url: " + paper.getUrl(), e));
						e.printStackTrace();
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "No Key provided.");
		}
	}

}
