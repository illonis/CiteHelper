package de.illonis.citehelper.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import de.illonis.citehelper.CiteEventBus;
import de.illonis.citehelper.CiteHelper;
import de.illonis.citehelper.GoogleScholar;
import de.illonis.citehelper.Messages;
import de.illonis.citehelper.Paper;
import de.illonis.citehelper.events.ErrorEvent;

public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private final JTable table;
	private final CiteTableModel tableModel;
	private final SidePanel sidePanel;

	public MainWindow(CiteTableModel tableData) {
		super(Messages.getString("appname.windowtitle")); //$NON-NLS-1$
		setJMenuBar(new FileMenu());

		tableModel = tableData;
		table = new JTable(tableModel);
		initTable();
		JScrollPane tableScroller = new JScrollPane(table);
		setLayout(new BorderLayout());
		sidePanel = new SidePanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScroller, sidePanel);
		splitPane.setResizeWeight(1);
		splitPane.setDividerLocation(.8d);
		add(splitPane, BorderLayout.CENTER);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initTable() {
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				handleSelectionEvent(e);
			}
		});
	}

	protected void handleSelectionEvent(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		int row = table.getSelectedRow();
		if (-1 != row) {
			int modelRow = table.getRowSorter().convertRowIndexToModel(row);
			sidePanel.setPaper(tableModel.getPaper(modelRow));
		} else {
			sidePanel.setPaper(null);
		}
	}

	public static void openFileFor(Component parent, Paper paper) {
		if (null != paper.getKey()) {
			String filename = paper.getKey() + ".pdf"; //$NON-NLS-1$
			Path filePath = CiteHelper.getInstance().getCurrentProject().getWorkingDirectory().resolve(filename);
			if (Files.isRegularFile(filePath)) {
				try {
					Desktop.getDesktop().open(filePath.toFile());
				} catch (IOException e) {
					CiteEventBus.getInstance().getBus().post(
							new ErrorEvent(Messages.getString("messages.error.openfailed") + paper.getTitle(), e)); //$NON-NLS-1$
					e.printStackTrace();
				}
			} else {
				int result = JOptionPane.showConfirmDialog(parent, Messages.getString("messages.error.nofile"), //$NON-NLS-1$
						paper.getTitle(), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (JOptionPane.OK_OPTION == result) {
					try {
						Desktop.getDesktop().browse(GoogleScholar.getSearchUri(paper));
					} catch (IOException e) {
						CiteEventBus.getInstance().getBus()
								.post(new ErrorEvent(Messages.getString("messages.error.urlopen") + paper.getUrl(), e)); //$NON-NLS-1$
						e.printStackTrace();
					}
				}
			}
		} else {
			JOptionPane.showMessageDialog(parent, Messages.getString("messages.error.missingkey")); //$NON-NLS-1$
		}
	}

}
