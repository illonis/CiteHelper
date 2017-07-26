package de.illonis.citehelper;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;

import com.google.common.eventbus.EventBus;

import de.illonis.citehelper.events.FileChangeEvent;

public class FolderWatcher extends Thread {

	private WatchService watcher;
	private final HashMap<WatchKey, Path> keyPaths;

	public FolderWatcher() {
		super("FolderWatcher");
		setDaemon(true);
		keyPaths = new HashMap<>();
	}

	public void init() throws IOException {
		watcher = FileSystems.getDefault().newWatchService();
	}

	public void registerPath(Path path) throws IOException {
		WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keyPaths.put(key, path);
	}

	@Override
	public void run() {
		watch();
	}

	void watch() {
		for (;;) {

			// wait for key to be signaled
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind<?> kind = event.kind();

				// This key is registered only
				// for ENTRY_CREATE events,
				// but an OVERFLOW event can
				// occur regardless if events
				// are lost or discarded.
				if (kind == OVERFLOW) {
					continue;
				}

				// The filename is the
				// context of the event.
				WatchEvent<Path> ev = (WatchEvent<Path>) event;
				Path filename = ev.context();

				// Resolve the filename against the directory.
				// If the filename is "test" and the directory is "foo",
				// the resolved name is "test/foo".
				Path basePath = keyPaths.get(key);
				Path child = basePath.resolve(filename);
				String actualFileName = child.getFileName().toString();
				if (actualFileName.endsWith(".bib") || actualFileName.endsWith(".pdf")) {
					EventBus bus = CiteEventBus.getInstance().getBus();
					bus.post(new FileChangeEvent(ev.kind(), basePath, child));
				} else {
					System.out.println("irrelevant file change: " + actualFileName);
					continue;
				}

			}

			// Reset the key -- this step is critical if you want to
			// receive further watch events. If the key is no longer valid,
			// the directory is inaccessible so exit the loop.
			boolean valid = key.reset();
			if (!valid) {
				keyPaths.remove(key);
				break;
			}
		}
	}

}
