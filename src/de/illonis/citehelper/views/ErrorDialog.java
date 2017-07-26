package de.illonis.citehelper.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public ErrorDialog(Frame parent, String message, Exception error) {
		super(parent);
		setLayout(new BorderLayout());
		JLabel label = new JLabel(message);
		label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(label, BorderLayout.NORTH);
		getRootPane().setBackground(Color.RED);
		setModal(true);
		setTitle("Error: " + message);
		StringWriter stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);
		error.printStackTrace(writer);
		String stackTrace = stringWriter.toString();
		JTextArea field = new JTextArea(stackTrace);
		field.setRows(8);
		field.setEditable(false);
		JScrollPane scroller = new JScrollPane(field);
		add(scroller, BorderLayout.CENTER);
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		add(closeButton, BorderLayout.SOUTH);
		pack();
		setSize((int) (parent.getWidth() * .8), getHeight());
		setLocationRelativeTo(parent);
	}

}
