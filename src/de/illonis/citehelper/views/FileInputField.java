package de.illonis.citehelper.views;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.illonis.citehelper.Messages;

public class FileInputField extends JPanel {

	private static final long serialVersionUID = 1L;
	private final JTextField textField;
	private final JButton chooseButton;
	private final JFileChooser chooser;
	private Path currentFile;

	public FileInputField() {
		setLayout(new FlowLayout());
		textField = new JTextField(20);
		textField.setEditable(false);
		chooser = new JFileChooser();
		chooser.setDialogTitle(Messages.getString("title.selectworkingdir")); //$NON-NLS-1$
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooseButton = new JButton(Messages.getString("action.choosefolder")); //$NON-NLS-1$
		chooseButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showChooseDialog();
			}
		});
		add(textField);
		add(chooseButton);
	}

	protected void showChooseDialog() {
		if (null != currentFile) {
			chooser.setCurrentDirectory(currentFile.toFile());
		}
		int result = chooser.showOpenDialog(this);
		if (JFileChooser.APPROVE_OPTION == result) {
			setCurrentFile(chooser.getSelectedFile().toPath());
		}
	}

	public void setCurrentFile(Path currentFile) {
		this.currentFile = currentFile.toAbsolutePath().normalize();
		updateUi();
	}

	private void updateUi() {
		textField.setText(currentFile.toString());
	}

	public Path getCurrentFile() {
		return currentFile;
	}
}
