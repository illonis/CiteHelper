package de.illonis.citehelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

import com.google.common.eventbus.Subscribe;

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

	private static MainLogic instance;

	public static MainLogic getInstance() {
		return instance;
	}

	private Project project;
	private final CiteTableModel tableData;
	private MainWindow window;
	private FolderWatcher watcher;

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
		helper.loadDataFromCurrentProject();
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

	private static void fillDemoData(CiteTableModel tableData) {

		List<String> authors = new LinkedList<>();
		authors.add("Yann LeCun");
		Paper paper = new Paper();
		paper.setTitle("Random first title");
		paper.setYear(1997);
		paper.setAuthors(authors);
		paper.setFilename("random91.pdf");
		tableData.add(paper);

		paper = new Paper();
		paper.setTitle("Random second title");
		paper.setYear(2006);
		paper.setAuthors(new LinkedList<>());
		paper.setFilename("random9131.pdf");
		tableData.add(paper);
	}

	public CiteHelper(Project project) {
		this.project = project;
		tableData = new CiteTableModel();
		fillDemoData(tableData);
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
			CiteEventBus.getInstance().getBus()
					.post(new ErrorEvent("Could not parse file: " + file.getAbsolutePath(), e));
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
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						tableData.addAll(papers);
					}
				});
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
						List<Paper> papers = tableData.findPapersFromSource(file);
						tableData.remove(papers);
						tableData.addAll(newPapers);
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
					List<Paper> papers = tableData.findPapersFromSource(file);
					tableData.remove(papers);
				}
			});

		}
		System.out.println(event.getChangeType().name() + " > " + event.getFile());
	}

	private void showPreview(List<Paper> newPapers) {
		newPapers.forEach(p -> tableData.add(p));
		tableData.fireTableDataChanged();
		// TODO: show prompt with preview
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
			startWatching();
		}
	}

	private void startWatching() {
		try {
			watcher.registerPath(project.getWorkingDirectory());
		} catch (IOException e) {
			e.printStackTrace();
			CiteEventBus.getInstance().getBus()
					.post(new ErrorEvent("Could not start watching " + project.getWorkingDirectory() + ".", e));
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
			CiteEventBus.getInstance().getBus()
					.post(new ErrorEvent("Could not create project at " + project.getWorkingDirectory(), e));
		}
		return p;
	}

	private void showNewProjectScreen() {
		NewProjectDialog dialog = new NewProjectDialog(window);
		dialog.setLocationRelativeTo(window);
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.setVisible(true);
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
}
