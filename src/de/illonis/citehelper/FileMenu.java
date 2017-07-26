package de.illonis.citehelper;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class FileMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	public FileMenu() {
		JMenu file = createFileMenu();
		add(file);
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu("File");

		JMenuItem importItem = new JMenuItem("Import");
		file.add(importItem);

		JMenuItem export = new JMenuItem("Export");
		file.add(export);

		file.addSeparator();
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		return file;
	}
}
