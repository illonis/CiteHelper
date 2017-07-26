package de.illonis.citehelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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
import de.illonis.citehelper.views.CiteTableModel;
import de.illonis.citehelper.views.ErrorDialog;
import de.illonis.citehelper.views.MainWindow;

public class CiteHelper implements MainLogic {

	private Project project;
	private final CiteTableModel tableData;
	private MainWindow window;

	public static void main(String[] args) {

		CiteHelper helper = new CiteHelper();
		CiteEventBus.getInstance().getBus().register(helper);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				helper.startWindow();
			}
		});
		FolderWatcher watcher = new FolderWatcher();
		try {
			watcher.init();
			watcher.registerPath(Paths.get("/tmp/citetest"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		watcher.start();
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

	public CiteHelper() {
		project = new Project("Empty project");
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
			List<Paper> newPapers = importer.importFromFile(file);
			showPreview(newPapers);
		} catch (TokenMgrException | IOException | ParseException e) {
			e.printStackTrace();
			CiteEventBus.getInstance().getBus()
					.post(new ErrorEvent("Could not parse file: " + file.getAbsolutePath(), e));
		}
	}

	@Subscribe
	public void onFileChanged(FileChangeEvent event) {
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
	}

	@Override
	public Project getCurrentProject() {
		return project;
	}
}
