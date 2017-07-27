package de.illonis.citehelper.views;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

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

	private static final String EXT_TXT = "txt"; //$NON-NLS-1$
	private static final String EXT_BIBTEX = "bib"; //$NON-NLS-1$
	private static final long serialVersionUID = 1L;
	private final JFileChooser importChooser;

	public FileMenu() {
		importChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(Messages.getString("label.bibtexfiles"), //$NON-NLS-1$
				EXT_BIBTEX, EXT_TXT);
		importChooser.setFileFilter(filter);
		JMenu file = createFileMenu();
		add(file);
		JMenu project = createDataMenu();
		add(project);
	}

	private JMenu createDataMenu() {
		JMenu dataMenu = new JMenu(Messages.getString("menu.data")); //$NON-NLS-1$

		JMenuItem importClipboard = new JMenuItem(Messages.getString("menu.importclipboard")); //$NON-NLS-1$
		importClipboard.setActionCommand("importclipboard"); //$NON-NLS-1$
		importClipboard.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CiteHelper.getInstance().importFromClipboard();
			}
		});
		dataMenu.add(importClipboard);

		JMenuItem importItem = new JMenuItem(Messages.getString("menu.import")); //$NON-NLS-1$
		importItem.setActionCommand("import"); //$NON-NLS-1$
		importItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				importChooser.setDialogTitle(Messages.getString("title.import")); //$NON-NLS-1$
				int returnVal = importChooser.showDialog(getParent(), Messages.getString("action.import")); //$NON-NLS-1$
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = importChooser.getSelectedFile();
					CiteEventBus.getInstance().getBus().post(new ImportFileChosenEvent(file));
				}

			}
		});
		dataMenu.add(importItem);
		JMenuItem exportSelection = new JMenuItem(Messages.getString("menu.exportselection")); //$NON-NLS-1$
		exportSelection.setEnabled(false);
		dataMenu.add(exportSelection);

		JMenuItem export = new JMenuItem(Messages.getString("menu.export")); //$NON-NLS-1$
		export.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CiteHelper.getInstance().exportLibrary();
			}
		});
		dataMenu.add(export);
		return dataMenu;
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu(Messages.getString("menu.file")); //$NON-NLS-1$
		JMenuItem newProject = new JMenuItem(Messages.getString("menu.newproject")); //$NON-NLS-1$
		file.add(newProject);
		newProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CiteHelper.getInstance().showNewProjectScreen();
			}
		});
		JMenuItem openProject = new JMenuItem(Messages.getString("menu.openproject")); //$NON-NLS-1$
		file.add(openProject);
		openProject.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				CiteHelper.getInstance().showOpenProjectScreen();
			}
		});
		file.addSeparator();
		JMenuItem openWorkDir = new JMenuItem(Messages.getString("menu.openworkdir")); //$NON-NLS-1$
		file.add(openWorkDir);
		openWorkDir.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop()
							.open(CiteHelper.getInstance().getCurrentProject().getWorkingDirectory().toFile());
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});
		JMenuItem projectProperties = new JMenuItem(Messages.getString("menu.projectproperties")); //$NON-NLS-1$
		file.add(projectProperties);
		projectProperties.setEnabled(false);

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
