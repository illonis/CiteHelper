package de.illonis.citehelper.views;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileView;

import de.illonis.citehelper.CiteHelper;
import de.illonis.citehelper.Project;

public class ProjectFileView extends FileView {

	private final ImageIcon projectIcon;

	public ProjectFileView() {
		projectIcon = new ImageIcon(getClass().getResource("/icons/project.png")); //$NON-NLS-1$
	}

	@Override
	public Icon getIcon(File f) {
		if (f.isDirectory()) {
			Project p = CiteHelper.tryReadProjectFromPath(f.toPath());
			if (null != p) {
				return projectIcon;
			}
		}
		return super.getIcon(f);
	}
}
