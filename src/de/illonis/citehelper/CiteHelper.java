package de.illonis.citehelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

import com.google.common.eventbus.Subscribe;

import de.illonis.citehelper.bibtex.BibtexExporter;
import de.illonis.citehelper.bibtex.BibtexImporter;
import de.illonis.citehelper.events.ErrorEvent;
import de.illonis.citehelper.events.FileChangeEvent;
import de.illonis.citehelper.events.ImportFileChosenEvent;
import de.illonis.citehelper.tasks.FolderReader;
import de.illonis.citehelper.tasks.ResultHandler;
import de.illonis.citehelper.views.CiteTableModel;
import de.illonis.citehelper.views.ErrorDialog;
import de.illonis.citehelper.views.MainWindow;
import de.illonis.citehelper.views.NewProjectDialog;

public class CiteHelper implements MainLogic, ResultHandler<List<Paper>> {

	private static final String BIBTEX_FILE_SUFFIX = ".bib"; //$NON-NLS-1$
	private static MainLogic instance;

	public static MainLogic getInstance() {
		return instance;
	}

	private Project project;
	private final CiteTableModel tableData;
	private MainWindow window;
	private FolderWatcher watcher;
	private final JFileChooser openProjectChooser;

	public static void main(String[] args) {

		CitePreferences prefs = new CitePreferences();
		prefs.loadPreferences();
		Path recentProjectPath = prefs.getRecentProjectPath();
		Project recentProject = null;
		if (null != recentProjectPath) {
			recentProject = tryReadProjectFromPath(recentProjectPath);
		}
		CiteHelper helper = new CiteHelper(recentProject);
		instance = helper;
		CiteEventBus.getInstance().getBus().register(helper);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				helper.startWindow();
			}
		});
	}

	private static Project tryReadProjectFromPath(Path directory) {
		Path confFile = directory.resolve(Project.PROJECT_FILE_NAME);
		if (Files.isRegularFile(confFile)) {
			try {
				return Project.parse(confFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public CiteHelper(Project project) {
		this.project = project;
		tableData = new CiteTableModel();
		openProjectChooser = new JFileChooser();
		openProjectChooser.setDialogTitle(Messages.getString("title.openproject")); //$NON-NLS-1$
		openProjectChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		openProjectChooser.setAcceptAllFileFilterUsed(false);
	}

	@Subscribe
	public void onError(ErrorEvent e) {
		if (null != window) {
			new ErrorDialog(window, e.getMessage(), e.getException()).setVisible(true);
		}
	}

	@Subscribe
	public void onImportFileChosen(ImportFileChosenEvent event) {
		File file = event.getFileImported();
		BibtexImporter importer = new BibtexImporter();
		try {
			List<Paper> newPapers = importer.importFromFile(file.toPath());
			showPreview(newPapers);
		} catch (TokenMgrException | IOException | ParseException e) {
			e.printStackTrace();
			CiteEventBus.getInstance().getBus().post(new ErrorEvent(
					MessageFormat.format(Messages.getString("messages.error.fileparse"), file.getAbsolutePath()), e)); //$NON-NLS-1$
		}
	}

	@Subscribe
	public void onFileChanged(FileChangeEvent event) {
		// called on FolderWatcher thread!
		final Path file = event.getFile();
		if (StandardWatchEventKinds.ENTRY_CREATE == event.getChangeType()) {
			BibtexImporter importer = new BibtexImporter();
			try {
				List<Paper> papers = importer.importFromFile(file);
				if (papers.size() > 0) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							synchronized (tableData) {
								tableData.addAll(papers);
							}
						}
					});
				}
			} catch (TokenMgrException | IOException | ParseException e) {
				// TODO log error
				e.printStackTrace();
			}

		} else if (StandardWatchEventKinds.ENTRY_MODIFY == event.getChangeType()) {
			BibtexImporter importer = new BibtexImporter();
			try {
				List<Paper> newPapers = importer.importFromFile(file);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						synchronized (tableData) {
							List<Paper> papers = tableData.findPapersFromSource(file);
							tableData.remove(papers);
							tableData.addAll(newPapers);
						}
					}
				});
			} catch (TokenMgrException | IOException | ParseException e) {
				// TODO log error
				e.printStackTrace();
			}

		} else if (StandardWatchEventKinds.ENTRY_DELETE == event.getChangeType()) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					synchronized (tableData) {
						List<Paper> papers = tableData.findPapersFromSource(file);
						tableData.remove(papers);
					}
				}
			});

		}
		System.out.println(event.getChangeType().name() + " > " + event.getFile()); //$NON-NLS-1$
	}

	private void showPreview(List<Paper> newPapers) {
		// TODO: show prompt with preview
		importData(newPapers);
	}

	private void importData(List<Paper> newPapers) {
		newPapers.forEach(p -> {
			// import as file
			Path file = project.getWorkingDirectory().resolve(p.getKey() + BIBTEX_FILE_SUFFIX);
			try {
				BibtexExporter.exportToFile(file, p.getBibtexEntry());
			} catch (TokenMgrException | IOException | ParseException e) {
				// TODO log error
				e.printStackTrace();
			}
			p.setSource(file);
		});
	}

	private void startWindow() {
		window = new MainWindow(tableData, this);
		window.setSize(800, 600);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		onWindowReady();
	}

	private void onWindowReady() {
		startFileWatcher();
		if (null == project) {
			// ask for initial project
			showProjectSetupScreen();
		} else {
			setCurrentProject(project);
		}
	}

	private void startWatching() {
		try {
			watcher.registerPath(project.getWorkingDirectory());
		} catch (IOException e) {
			e.printStackTrace();
			CiteEventBus.getInstance().getBus().post(new ErrorEvent(MessageFormat
					.format(Messages.getString("messages.error.watchstart"), project.getWorkingDirectory()), e)); //$NON-NLS-1$
		}
	}

	private void showProjectSetupScreen() {
		// TODO Auto-generated method stub
		showNewProjectScreen();

	}

	@Override
	public Project createProject(String name, Path workingDir) {
		Project p = new Project(name);
		p.setWorkingDirectory(workingDir);
		try {
			Project.save(p);
		} catch (IOException e) {
			e.printStackTrace();
			CiteEventBus.getInstance().getBus().post(new ErrorEvent(MessageFormat
					.format(Messages.getString("messages.error.createproject"), project.getWorkingDirectory()), e)); //$NON-NLS-1$
		}
		return p;
	}

	@Override
	public void showNewProjectScreen() {
		NewProjectDialog dialog = new NewProjectDialog(window);
		dialog.setLocationRelativeTo(window);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setVisible(true);
	}

	@Override
	public void showOpenProjectScreen() {
		int result = openProjectChooser.showOpenDialog(window);
		if (JFileChooser.APPROVE_OPTION == result) {
			Path file = openProjectChooser.getSelectedFile().toPath();
			Project openProject = tryReadProjectFromPath(file);
			if (null != openProject) {
				setCurrentProject(openProject);
			} else {
				JOptionPane.showMessageDialog(window, Messages.getString("messages.error.noprojectfolder"), //$NON-NLS-1$
						Messages.getString("label.error"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			}
		}
	}

	private void startFileWatcher() {
		watcher = new FolderWatcher();
		try {
			watcher.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		watcher.start();
	}

	@Override
	public Project getCurrentProject() {
		return project;
	}

	@Override
	public void setCurrentProject(Project project) {
		this.project = project;
		startWatching();
		loadDataFromCurrentProject();
		CitePreferences prefs = new CitePreferences();
		prefs.setRecentProjectPath(project.getWorkingDirectory());
		prefs.savePreferences();
		window.setTitle(
				MessageFormat.format("{0} - {1}", project.getName(), Messages.getString("appname.windowtitle"))); //$NON-NLS-1$//$NON-NLS-2$
	}

	private void loadDataFromCurrentProject() {
		tableData.clear();
		if (null != project) {
			new FolderReader(this, project.getWorkingDirectory()).execute();
		}
	}

	@Override
	public void handleAsyncResult(List<Paper> result) {
		for (Paper paper : result) {
			tableData.add(paper);
		}

	}

	@Override
	public void exit() {
		window.dispose();
	}
}
