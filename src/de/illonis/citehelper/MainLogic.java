package de.illonis.citehelper;

import java.nio.file.Path;

public interface MainLogic {

	Project getCurrentProject();

	void setCurrentProject(Project project);

	void showNewProjectScreen();

	Project createProject(String name, Path workingDir);

	void exit();

	void showOpenProjectScreen();

}
