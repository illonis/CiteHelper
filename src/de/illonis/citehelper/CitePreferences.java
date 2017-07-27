package de.illonis.citehelper;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

public class CitePreferences {

	private final static String KEY_RECENT_PROJECT = "recent_project"; //$NON-NLS-1$

	private Path recentProjectPath;

	public void savePreferences() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs.put(KEY_RECENT_PROJECT, recentProjectPath.toAbsolutePath().normalize().toString());
	}

	public void loadPreferences() {
		Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
		String recentPath = prefs.get(KEY_RECENT_PROJECT, null);
		if (null == recentPath) {
			recentProjectPath = null;
		} else {
			recentProjectPath = Paths.get(recentPath);
		}
	}

	public Path getRecentProjectPath() {
		return recentProjectPath;
	}

	public void setRecentProjectPath(Path recentProjectPath) {
		this.recentProjectPath = recentProjectPath;
	}

}
