package de.illonis.citehelper.views;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.illonis.citehelper.CiteEventBus;
import de.illonis.citehelper.CiteHelper;
import de.illonis.citehelper.Messages;
import de.illonis.citehelper.Paper;
import de.illonis.citehelper.events.ErrorEvent;

public class SidePanel extends JPanel implements ActionListener {

	private static final String ACTION_PDF = "pdf"; //$NON-NLS-1$
	private static final String ACTION_DELETE = "delete"; //$NON-NLS-1$
	private static final String EXT_PDF = ".pdf"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	final static String CARD_EMPTY = "Empty card"; //$NON-NLS-1$
	final static String CARD_PAPER = "Paper card"; //$NON-NLS-1$
	private static final String PDF = "pdf"; //$NON-NLS-1$
	private final JTable tagTable;
	private final TagModel tagModel;
	private final JButton pdfButton;
	private final JButton deleteButton;
	private final JFileChooser pdfChooser;
	private Paper currentPaper;

	public SidePanel() {
		JPanel paperPanel = new JPanel(new BorderLayout());

		pdfButton = new JButton();
		paperPanel.add(pdfButton, BorderLayout.NORTH);
		pdfButton.setActionCommand(ACTION_PDF); // $NON-NLS-1$
		pdfButton.addActionListener(this);

		deleteButton = new JButton(Messages.getString("action.delete")); //$NON-NLS-1$
		deleteButton.setActionCommand(ACTION_DELETE);
		deleteButton.addActionListener(this);
		paperPanel.add(deleteButton, BorderLayout.SOUTH);

		tagModel = new TagModel();
		tagTable = new JTable(tagModel);
		tagTable.setAutoCreateRowSorter(true);

		JScrollPane scroller = new JScrollPane(tagTable);
		paperPanel.add(scroller, BorderLayout.CENTER);

		JPanel emptyPanel = new JPanel(new BorderLayout());
		JLabel emptyLabel = new JLabel(Messages.getString("label.emptysidebar")); //$NON-NLS-1$
		emptyLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		emptyPanel.add(emptyLabel, BorderLayout.CENTER);

		setLayout(new CardLayout());
		add(emptyPanel, CARD_EMPTY);
		add(paperPanel, CARD_PAPER);
		pdfChooser = new JFileChooser();
		pdfChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		pdfChooser.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Messages.getString("label.pdffiles"), //$NON-NLS-1$
				PDF);
		pdfChooser.setFileFilter(filter);

		setPreferredSize(new Dimension(200, 20));
		updateView();
	}

	private void updateView() {
		CardLayout cl = (CardLayout) (getLayout());
		cl.show(this, CARD_EMPTY);
		if (null == currentPaper) {
			cl.show(this, CARD_EMPTY);
		} else {
			cl.show(this, CARD_PAPER);
			if (hasPdf()) {
				pdfButton.setText(Messages.getString("action.openpdf")); //$NON-NLS-1$
			} else {
				pdfButton.setText(Messages.getString("action.assignpdf")); //$NON-NLS-1$
			}
			tagModel.setData(currentPaper.getBibtexEntry());
		}
	}

	private boolean hasPdf() {
		String filename = currentPaper.getKey() + EXT_PDF;
		Path filePath = CiteHelper.getInstance().getCurrentProject().getWorkingDirectory().resolve(filename);
		return Files.isRegularFile(filePath);
	}

	public void setPaper(Paper paper) {
		this.currentPaper = paper;
		updateView();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case ACTION_DELETE:
			showConfirmDelete();
			break;
		case ACTION_PDF:
			if (hasPdf()) {
				MainWindow.openFileFor(this, currentPaper);
			} else {
				showAssignPdfDialog();
			}
			break;
		}
	}

	private void showConfirmDelete() {
		// TODO Auto-generated method stub

	}

	private void showAssignPdfDialog() {
		pdfChooser.setDialogTitle(MessageFormat.format(Messages.getString("title.choosepdf"), currentPaper.getTitle())); //$NON-NLS-1$
		int result = pdfChooser.showDialog(this, Messages.getString("action.assign")); //$NON-NLS-1$
		if (JFileChooser.APPROVE_OPTION == result) {
			Path current = pdfChooser.getSelectedFile().toPath();
			if (Files.isRegularFile(current)) {
				Path target = CiteHelper.getInstance().getCurrentProject().getWorkingDirectory()
						.resolve(currentPaper.getKey() + EXT_PDF);

				try {
					Files.copy(current, target, StandardCopyOption.REPLACE_EXISTING);
					updateView();
				} catch (IOException e) {
					e.printStackTrace();
					CiteEventBus.getInstance().getBus()
							.post(new ErrorEvent(MessageFormat.format(Messages.getString("messages.error.pdfcopy"), //$NON-NLS-1$
									current.toAbsolutePath()), e));
				}
			}
		}
	}

}
