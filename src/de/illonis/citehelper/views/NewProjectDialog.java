package de.illonis.citehelper.views;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.illonis.citehelper.CiteHelper;
import de.illonis.citehelper.Project;

public class NewProjectDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private final JTextField nameInput;
	private final FileInputField workingDirInput;
	private final JButton createButton;

	public NewProjectDialog(Frame parent) {
		super(parent, "New project");
		nameInput = new JTextField();
		workingDirInput = new FileInputField();
		createButton = new JButton("Create");
		createButton.addActionListener(this);

		JPanel contentPane = (JPanel) getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

		contentPane.add(Box.createRigidArea(new Dimension(10, 10)));
		contentPane.add(nameInput);
		contentPane.add(Box.createRigidArea(new Dimension(10, 10)));
		contentPane.add(workingDirInput);
		contentPane.add(Box.createRigidArea(new Dimension(10, 10)));
		contentPane.add(createButton);
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (null != workingDirInput.getCurrentFile()) {
			Project p = CiteHelper.getInstance().createProject(nameInput.getText().trim(),
					workingDirInput.getCurrentFile());
			CiteHelper.getInstance().setCurrentProject(p);
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Working dir must not be empty.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
