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
import de.illonis.citehelper.CiteHelper;
import de.illonis.citehelper.Messages;
import de.illonis.citehelper.events.ImportFileChosenEvent;

public class FileMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;
	private final JFileChooser importChooser;

	public FileMenu() {
		importChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Messages.getString("label.bibtexfiles"), "bib", //$NON-NLS-1$ //$NON-NLS-2$
				"txt"); //$NON-NLS-1$
		importChooser.setFileFilter(filter);
		JMenu file = createFileMenu();
		add(file);
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu(Messages.getString("menu.file")); //$NON-NLS-1$

		JMenuItem importItem = new JMenuItem(Messages.getString("menu.import")); //$NON-NLS-1$
		importItem.setActionCommand("import"); //$NON-NLS-1$
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

		JMenuItem export = new JMenuItem(Messages.getString("menu.export")); //$NON-NLS-1$
		file.add(export);

		file.addSeparator();
		JMenuItem exit = new JMenuItem(Messages.getString("gui.menu.exit")); //$NON-NLS-1$
		file.add(exit);
		exit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CiteHelper.getInstance().exit();
			}
		});
		return file;
	}
}
