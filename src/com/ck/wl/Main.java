package com.ck.wl;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import net.miginfocom.swing.MigLayout;

public class Main {

	JFileChooser fileChooser;

	private JFrame frame;
	private JTextField locationField;
	private File xmlFile;
	private JCheckBox chckbxPrintMetricsOn;
	private boolean showMetrics;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager
			.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Main window = new Main();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("SAT Helper");
		// frame.setBounds(50, 50, 800, 700);
		frame.setBounds(250, 150, 700, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		frame.getContentPane().setLayout(
				new MigLayout("", "[][][][grow][][][]", "[][][][][][][][]"));

		JLabel lblWelcome = new JLabel("Welcome!");
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblWelcome, "cell 3 2,alignx center");

		JLabel lblPleaseProvideThe = new JLabel(
				"Please provide the location of the  XML file.");
		frame.getContentPane().add(lblPleaseProvideThe,
				"cell 3 3,alignx center");

		locationField = new JTextField();
		frame.getContentPane().add(locationField, "cell 3 5,growx");
		locationField.setColumns(10);

		JButton btnBrowse = new JButton("Browse");
		frame.getContentPane().add(btnBrowse, "cell 4 5");

		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				xmlFile = null;
				int returnVal = fileChooser.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					xmlFile = fileChooser.getSelectedFile();
					locationField.setText(xmlFile.getPath());
				} else {
					locationField.setText("");
				}

			}
		});

		chckbxPrintMetricsOn = new JCheckBox("Print Metrics on Console?");
		frame.getContentPane().add(chckbxPrintMetricsOn, "cell 3 6");
		chckbxPrintMetricsOn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showMetrics = true;
			}
		});

		JButton btnStart = new JButton("START");
		btnStart.setHorizontalAlignment(SwingConstants.RIGHT);
		frame.getContentPane().add(btnStart, "cell 3 7,alignx center");

		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				WList window = new WList(xmlFile.getPath(), showMetrics);
				window.frame.setVisible(true);
				frame.setVisible(false);
			}
		});
	}

}
