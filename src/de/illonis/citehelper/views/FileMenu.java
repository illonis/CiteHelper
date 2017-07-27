package de.illonis.citehelper.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.illonis.citehelper.CiteEventBus;
import de.illonis.citehelper.events.ImportFileChosenEvent;

public class FileMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private final JFileChooser importChooser;

	public FileMenu() {
		importChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("BibTeX files", "bib", "txt");
		importChooser.setFileFilter(filter);
		JMenu file = createFileMenu();
		add(file);
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu("File");

		JMenuItem importItem = new JMenuItem("Import");
		importItem.setActionCommand("import");
		importItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = importChooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = importChooser.getSelectedFile();
					CiteEventBus.getInstance().getBus().post(new ImportFileChosenEvent(file));
				}

			}
		});
		file.add(importItem);

		JMenuItem export = new JMenuItem("Export");
		file.add(export);

		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		return file;
	}
}
