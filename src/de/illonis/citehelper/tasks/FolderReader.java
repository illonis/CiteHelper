package de.illonis.citehelper.tasks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import javax.swing.SwingWorker;

import org.jbibtex.ParseException;
import org.jbibtex.TokenMgrException;

import de.illonis.citehelper.Paper;
import de.illonis.citehelper.bibtex.BibtexImporter;

public class FolderReader extends SwingWorker<List<Paper>, Integer> {
	private static final String EXT_BIBTEX = ".bib"; //$NON-NLS-1$
	private final Path path;
	private final ResultHandler<List<Paper>> handleResult;
	private final Predicate<? super Path> bibFilter;

	public FolderReader(ResultHandler<List<Paper>> handler, Path path) {
		this.handleResult = handler;
		this.path = path;
		bibFilter = new Predicate<Path>() {

			@Override
			public boolean test(Path t) {
				return t.toString().toLowerCase().endsWith(EXT_BIBTEX);
			}
		};
	}

	@Override
	protected List<Paper> doInBackground() throws Exception {
		List<Paper> papers = new LinkedList<>();
		BibtexImporter importer = new BibtexImporter();
		Files.list(path).filter(bibFilter).forEach(p -> {
			try {
				List<Paper> filePapers = importer.importFromFile(p);
				papers.addAll(filePapers);
			} catch (TokenMgrException | IOException | ParseException e) {
				e.printStackTrace();
				// TODO log
			}
		});
		return papers;
	}

	@Override
	protected void done() {
		try {
			handleResult.handleAsyncResult(get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}
}
