package com.ck.wl;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.miginfocom.swing.MigLayout;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Chaitanya Kaalipati
 *
 */
public class WList {

	static String xmlFilePath = "";

	static final String HIDE_WHEN_MINIMIZED = "HideWhenMinimized";
	static final String ROOT = "Root";
	static final String SETTINGS = "Settings";
	static final String STUDY_LIST = "StudyList";
	static final String ENTRY = "Entry";
	static final String DIFFICULT = "difficult";
	static final String FAVOURITE = "favourite";
	static final String QUESTION = "Question";
	static final String ANSWER = "Answer";
	static final String SENTENCE = "Sentence";
	static final String TRUE_STRING = "true";

	DocumentBuilderFactory dbFactory;
	DocumentBuilder dBuilder;

	JFrame frame;
	JTextField searchField;
	JComboBox<String> alphabetList;
	JTable wlTable;
	JScrollPane scrollPane;
	WLTableModel model;
	WLTableModel mainModel;
	boolean practiceMode;
	boolean showMetrics;
	JLabel wordCountLabel = new JLabel("");
	static SortedMap<String, StudyListEntry> completeDictionary = new TreeMap<String, StudyListEntry>();
	static SortedMap<Character, int[]> aplhabetCount = new TreeMap<Character, int[]>();

	Document doc;
	NodeList nList;

	String[] columnNames = new String[] { "S.No", "Question", "Meaning",
			"Sentence", "Difficult", "Favourite" };

	WLTableModelListener tableModelListener = new WLTableModelListener();
	JCheckBox chckbxPracticeMode;
	JLabel lblDifficulty;
	JComboBox<String> difficultyList;

	static Boolean hideWhenMinimized = false;
	JCheckBox chckbxShowOnlyFavourites;
	boolean favouritesMode;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		xmlFilePath = "squares.xml";
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					WList window = new WList("D:\\MSat\\ck97982\\WL-Files\\"
							+ xmlFilePath, false);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 *
	 */
	private void makeTrayIcon() {
		final TrayIcon trayIcon;

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();
			Image image = new ImageIcon(WList.class.getResource("icon.png"), "")
			.getImage();

			MouseListener mouseListener = new MouseListener() {

				@Override
				public void mouseClicked(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}
			};

			PopupMenu popup = new PopupMenu();
			MenuItem exittItem = new MenuItem("Exit");
			ActionListener exitListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveAndClose();
					System.exit(0);
				}
			};
			exittItem.addActionListener(exitListener);

			MenuItem saveItem = new MenuItem("Save Changes");
			ActionListener saveListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					saveXml();
				}
			};
			saveItem.addActionListener(saveListener);

			MenuItem showWindowItem = new MenuItem("Open WL");
			showWindowItem.setFont(new Font("Calibri", Font.BOLD, 12));
			ActionListener showWindowListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!frame.isVisible()) {
						frame.setVisible(true);
						frame.setState(Frame.NORMAL);
					}
				}
			};
			showWindowItem.addActionListener(showWindowListener);

			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName(SETTINGS);

			if (getTagValue(HIDE_WHEN_MINIMIZED, (Element) nList.item(0))
					.equalsIgnoreCase(TRUE_STRING)) {
				hideWhenMinimized = true;
			}

			CheckboxMenuItem cb1 = new CheckboxMenuItem("Hide When Minimized");
			if (hideWhenMinimized) {
				cb1.setState(true);
			}
			cb1.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent e) {
					if (hideWhenMinimized) {
						hideWhenMinimized = false;
					} else {
						hideWhenMinimized = true;
					}
				}
			});

			popup.add(showWindowItem);
			popup.add(cb1);
			popup.add(saveItem);
			popup.add(exittItem);

			trayIcon = new TrayIcon(image, "WL App", popup);

			ActionListener actionListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (!frame.isVisible()) {
						frame.setVisible(true);
						frame.setState(Frame.NORMAL);
					}
					// trayIcon.displayMessage("Action Event",
					// "An Action Event Has Been Performed!",
					// TrayIcon.MessageType.INFO);
				}
			};

			trayIcon.setImageAutoSize(true);
			trayIcon.addActionListener(actionListener);
			trayIcon.addMouseListener(mouseListener);

			frame.setIconImage(image);
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("TrayIcon could not be added.");
			}

		} else {

			// System Tray is not supported

		}
	}

	/**
	 * Create the application.
	 */
	public WList(String path, boolean showMetrics) {
		xmlFilePath = path;
		this.showMetrics = showMetrics;
		try {
			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			// doc =
			// dBuilder.parse(this.getClass().getResourceAsStream(xmlFilePath));
			doc = dBuilder.parse(new File(xmlFilePath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			initialize();
		} catch (IOException e) {
			System.err.println("File server cannot be read");
			e.printStackTrace();
		} catch (ParserConfigurationException e) {

			e.printStackTrace();
		} catch (SAXException e) {

			e.printStackTrace();
		}
	}

	/**
	 * @param selectedAlphabet
	 */
	void updateModel() {
		model = null;
		model = new WLTableModel(columnNames);

		int i = 0;

		String selectedAlphabet = alphabetList.getSelectedItem().toString();
		for (String word : completeDictionary.keySet()) {
			if (favouritesMode) {
				if (!selectedAlphabet.equalsIgnoreCase("Select")) {
					if (word.toUpperCase().startsWith(
							selectedAlphabet.toUpperCase())) {
						if (completeDictionary.get(word).favourite) {
							i = insertToModel(i, completeDictionary.get(word));
						}
					}
				} else {
					if (completeDictionary.get(word).favourite) {
						i = insertToModel(i, completeDictionary.get(word));
					}
				}
			} else {
				if (!selectedAlphabet.equalsIgnoreCase("Select")) {
					if (word.toUpperCase().startsWith(
							selectedAlphabet.toUpperCase())) {
						i = insertToModel(i, completeDictionary.get(word));
					}
				} else {
					i = insertToModel(i, completeDictionary.get(word));
				}
			}
		}
		model.addTableModelListener(tableModelListener);
		updateWordCount();

		wlTable.setModel(model);
		setTableRenderOptions(wlTable);
	}

	private void updateWordCount() {
		wordCountLabel.setText("Words : " + model.getRowCount());
	}

	/**
	 * @param i
	 * @param word
	 * @return
	 */
	private int insertToModel(int i, StudyListEntry word) {
		if (Integer.parseInt(word.difficult) >= Integer.parseInt(difficultyList
				.getSelectedItem().toString())) {
			if (!practiceMode) {
				model.insertRow(i, new Object[] { i + 1, word.entry,
						word.answer, word.sentence, word.difficult,
						word.favourite });
			} else {
				model.insertRow(i, new Object[] { i + 1, word.entry, "", "",
						word.difficult, word.favourite });
			}
			i++;
		}
		return i;
	}

	/**
	 * @throws TransformerFactoryConfigurationError
	 */
	private static void saveAndClose()
			throws TransformerFactoryConfigurationError {
		saveXml();
		System.exit(1);
	}

	/**
	 * @throws TransformerFactoryConfigurationError
	 */
	private static void saveXml() throws TransformerFactoryConfigurationError {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder;
			docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(ROOT);
			doc.appendChild(rootElement);

			Element settings = doc.createElement(SETTINGS);

			Element hideWhenMinimizedElement = doc
					.createElement(HIDE_WHEN_MINIMIZED);
			hideWhenMinimizedElement.appendChild(doc
					.createTextNode(hideWhenMinimized.toString()));
			settings.appendChild(hideWhenMinimizedElement);

			rootElement.appendChild(settings);

			Element wordList = doc.createElement(STUDY_LIST);
			rootElement.appendChild(wordList);
			int count = 0;
			for (String dictionaryEntry : completeDictionary.keySet()) {
				if (completeDictionary.get(dictionaryEntry).sentence
						.contains("\t")) {
					count++;
					String replacementSent = completeDictionary
							.get(dictionaryEntry).sentence.replace('\t', ' ');
					replacementSent = replacementSent.replace("  ", " ");
					replacementSent = replacementSent.replace("   ", " ");

					String replacementMeaning = completeDictionary
							.get(dictionaryEntry).answer.replace('\t', ' ');
					replacementMeaning = replacementMeaning.replace("  ", " ");
					replacementMeaning = replacementMeaning.replace("   ", " ");

					StudyListEntry value = completeDictionary
							.get(dictionaryEntry);
					value.sentence = replacementSent;
					value.answer = replacementMeaning;
					completeDictionary.put(dictionaryEntry, value);
				}

				if (completeDictionary.get(dictionaryEntry).sentence
						.contains("  ")) {
					count++;
					String replacementSent = completeDictionary
							.get(dictionaryEntry).sentence.replace("  ", " ");
					replacementSent = replacementSent.replace("   ", " ");

					String replacementMeaning = completeDictionary
							.get(dictionaryEntry).answer.replace("  ", " ");
					replacementMeaning = replacementMeaning.replace("   ", " ");

					StudyListEntry value = completeDictionary
							.get(dictionaryEntry);
					value.sentence = replacementSent;
					value.answer = replacementMeaning;
					completeDictionary.put(dictionaryEntry, value);
				}

				Element entry = doc.createElement(ENTRY);
				wordList.appendChild(entry);

				Attr difficultAttr = doc.createAttribute(DIFFICULT);
				difficultAttr
				.setValue(completeDictionary.get(dictionaryEntry).difficult);

				Attr favAttr = doc.createAttribute(FAVOURITE);
				favAttr.setValue(completeDictionary.get(dictionaryEntry).favourite
						.toString());

				entry.setAttributeNode(difficultAttr);
				entry.setAttributeNode(favAttr);

				Element word = doc.createElement(QUESTION);
				word.appendChild(doc.createTextNode(dictionaryEntry));
				entry.appendChild(word);

				Element meaning = doc.createElement(ANSWER);
				meaning.appendChild(doc.createTextNode(completeDictionary
						.get(dictionaryEntry).answer));
				entry.appendChild(meaning);

				Element sentence = doc.createElement(SENTENCE);
				sentence.appendChild(doc.createTextNode(completeDictionary
						.get(dictionaryEntry).sentence));
				entry.appendChild(sentence);

			}

			System.out.println(count + " number of corrections made");

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			// URL resourceUrl = WLMain.class.getResource("wl.xml");
			// OutputStream output = new FileOutputStream(new File(
			// resourceUrl.toURI()));
			System.out.println("writing to : " + xmlFilePath);
			OutputStream output = new FileOutputStream(new File(xmlFilePath));
			StreamResult result = new StreamResult(output);

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	@SuppressWarnings("unchecked")
	private void initialize() throws IOException, ParserConfigurationException,
	SAXException {
		frame = new JFrame("My Dictionary");
		// frame.setBounds(50, 50, 800, 700);
		frame.setBounds(250, 150, 740, 400);
		// panel = new TablePanel();
		makeTrayIcon();

		WindowListener windowListener = new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
				if (hideWhenMinimized) {
					frame.setVisible(false);
				}
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				saveAndClose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		};

		frame.addWindowListener(windowListener);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().setLayout(
				new MigLayout("", "[][][grow]", "[][][][][][90,grow]"));

		JLabel selectLabel = new JLabel("Select");
		frame.getContentPane().add(selectLabel, "cell 0 0");

		frame.getContentPane().add(wordCountLabel, "cell 0 4");

		alphabetList = new JComboBox<String>(populateDropDownList());
		frame.getContentPane().add(alphabetList,
				"flowx,cell 2 0,alignx left,aligny center");

		alphabetList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateModel();
				wlTable.setModel(model);
				setTableRenderOptions(wlTable);
			}
		});

		alphabetList.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollListener(e, (JComboBox<String>) e.getSource());
			}

		});

		makeTable();

		chckbxPracticeMode = new JCheckBox("Practice Mode");
		chckbxPracticeMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (practiceMode) {
					practiceMode = false;
				} else {
					practiceMode = true;
				}

				updateModel();

			}
		});

		lblDifficulty = new JLabel("Difficulty > ");
		frame.getContentPane().add(lblDifficulty, "cell 0 1");

		difficultyList = new JComboBox<String>(new String[] { "1", "2", "3" });
		frame.getContentPane().add(difficultyList, "cell 2 1,alignx left");

		difficultyList.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateModel();
				wlTable.setModel(model);
				setTableRenderOptions(wlTable);
			}
		});

		difficultyList.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				scrollListener(e, (JComboBox<String>) e.getSource());
			}
		});

		JLabel searchLabel = new JLabel("Search");
		frame.getContentPane().add(searchLabel, "cell 0 2");

		searchField = new JTextField();
		frame.getContentPane().add(searchField, "cell 2 2,alignx left");
		searchField.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				String searchStrig = ((JTextField) e.getComponent()).getText();
				model = null;
				model = new WLTableModel(columnNames);

				for (int i = 0, j = 0; i < mainModel.getRowCount(); i++) {

					String currentWord = (String) mainModel.getValueAt(i, 1);
					String currentMeaning = (String) mainModel.getValueAt(i, 2);
					String currentSentence = (String) mainModel
							.getValueAt(i, 3);
					String currentDiff = (String) mainModel.getValueAt(i, 4);
					boolean currentFav = (Boolean) mainModel.getValueAt(i, 5);
					if (currentWord.toUpperCase().startsWith(
							searchStrig.toUpperCase())) {
						model.insertRow(j, new Object[] { j + 1, currentWord,
								currentMeaning, currentSentence, currentDiff,
								currentFav });
						j++;
					}
				}
				model.addTableModelListener(tableModelListener);
				wlTable.setModel(model);
				setTableRenderOptions(wlTable);

				practiceMode = false;
				chckbxPracticeMode.setSelected(false);
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		searchField.setColumns(10);
		frame.getContentPane().add(chckbxPracticeMode, "cell 2 0");

		chckbxShowOnlyFavourites = new JCheckBox("Show Only Favourites");
		chckbxShowOnlyFavourites.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (favouritesMode) {
					favouritesMode = false;
				} else {
					favouritesMode = true;
				}

				updateModel();

			}
		});
		frame.getContentPane().add(chckbxShowOnlyFavourites, "cell 2 0");
		scrollPane = new JScrollPane(wlTable);
		frame.getContentPane().add(scrollPane, "cell 2 4,grow");

	}

	private void scrollListener(MouseWheelEvent e, JComboBox<String> comboBox) {
		int newSelectedIndex;
		try {
			if (e.getWheelRotation() == -1) {
				newSelectedIndex = comboBox.getSelectedIndex() - 1;
			} else {
				newSelectedIndex = comboBox.getSelectedIndex() + 1;
			}

			if (newSelectedIndex != -1) {
				comboBox.setSelectedIndex(newSelectedIndex);
			}

		} catch (Exception exception) {
			// Commented the stack-trace as it will definitely occur
			// exception.printStackTrace();
		}
	}

	private void makeTable() throws ParserConfigurationException, SAXException,
	IOException {
		model = new WLTableModel(columnNames);

		nList = doc.getElementsByTagName(ENTRY);
		int[] metrics = new int[5];
		int countTotal = 0;
		int countSimple = 0;
		int countMedium = 0;
		int countDiff = 0;
		int countFav = 0;

		for (int temp = 0; temp < nList.getLength(); temp++) {

			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;

				StudyListEntry word = new StudyListEntry();

				word.entry = getTagValue(QUESTION, eElement);
				word.answer = getTagValue(ANSWER, eElement);
				// word.entry = getTagValue("Word", eElement);
				// word.answer = getTagValue("Meaning", eElement);
				word.difficult = getAttrValue(DIFFICULT, eElement);
				word.sentence = getTagValue(SENTENCE, eElement);
				if (TRUE_STRING.equalsIgnoreCase(getAttrValue(FAVOURITE,
						eElement))) {
					word.favourite = true;
				}

				if (!aplhabetCount.containsKey(word.entry.charAt(0))) {
					metrics = new int[5];
					countTotal = 0;
					countSimple = 0;
					countMedium = 0;
					countDiff = 0;
					countFav = 0;
				}
				countTotal++;
				if (word.difficult.equalsIgnoreCase("1")) {
					countSimple++;
				} else if (word.difficult.equalsIgnoreCase("2")) {
					countMedium++;
				} else if (word.difficult.equalsIgnoreCase("3")) {
					countDiff++;
				}
				if (word.favourite) {
					countFav++;
				}

				metrics[0] = countTotal;
				metrics[1] = countSimple;
				metrics[2] = countMedium;
				metrics[3] = countDiff;
				metrics[4] = countFav;

				aplhabetCount.put(word.entry.charAt(0), metrics);

				completeDictionary.put(word.entry, word);
			}
		}

		String header = "Letter" + "\t|\t" + "Tot" + "\t|\t" + "L1" + "\t|\t"
				+ "L2" + "\t|\t" + "L3" + "\t|\t" + "Fav";
		System.out.println(header);

		for (int i = 0; i < header.length(); i++) {
			if (header.charAt(i) == '\t') {
				// If '\t' the print 8 hypens
				System.out.print("--------");
			} else {
				System.out.print("-");
			}
		}
		System.out.println("");
		if (showMetrics) {
			for (Character c : aplhabetCount.keySet()) {
				System.out.println(c + "\t|\t" + aplhabetCount.get(c)[0]
						+ "\t|\t" + aplhabetCount.get(c)[1] + "\t|\t"
						+ aplhabetCount.get(c)[2] + "\t|\t"
						+ aplhabetCount.get(c)[3] + "\t|\t"
						+ aplhabetCount.get(c)[4]);
			}
		}

		int i = 0;
		for (String word : completeDictionary.keySet()) {
			// Insert first position
			model.insertRow(i,
					new Object[] { i + 1, word,
					completeDictionary.get(word).answer,
					completeDictionary.get(word).sentence,
					completeDictionary.get(word).difficult,
					completeDictionary.get(word).favourite });
			i++;
		}

		wlTable = new JTable(model) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				int column = columnAtPoint(e.getPoint());
				Object value = getValueAt(row, column);
				return value == null ? null : value.toString();
			}
		};

		wlTable.setAutoCreateRowSorter(true);

		setTableRenderOptions(wlTable);
		wlTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		wlTable.setRowHeight(20);
		mainModel = model;

		model.addTableModelListener(tableModelListener);

		updateWordCount();

	}

	static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();

		Node nValue = nlList.item(0);

		return nValue == null ? "" : nValue.getNodeValue().trim();
	}

	static String getAttrValue(String sTag, Element eElement) {
		return eElement.getAttribute(sTag);
	}

	public static void setTableRenderOptions(JTable table) {

		// Create a combo box to show that you can use one in a table.
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("1");
		comboBox.addItem("2");
		comboBox.addItem("3");

		TableColumn colorColumn = table.getColumn("Difficult");
		// Use the combo box as the editor in the "Favorite Color" column.
		colorColumn.setCellEditor(new DefaultCellEditor(comboBox));

		table.getColumnModel().getColumn(0).setMinWidth(35);
		table.getColumnModel().getColumn(1).setMinWidth(80);
		table.getColumnModel().getColumn(2).setMinWidth(125);
		table.getColumnModel().getColumn(4).setMinWidth(25);
		table.getColumnModel().getColumn(5).setMinWidth(25);
		table.getColumnModel().getColumn(0).setMaxWidth(35);
		table.getColumnModel().getColumn(1).setMaxWidth(80);
		table.getColumnModel().getColumn(2).setMaxWidth(125);
		// table.getColumnModel().getColumn(2).setMaxWidth(175);
		table.getColumnModel().getColumn(4).setMaxWidth(50);
		table.getColumnModel().getColumn(5).setMaxWidth(50);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
	}

	/**
	 * @return
	 */
	private String[] populateDropDownList() {
		String[] alphabets = new String[27];
		alphabets[0] = "Select";

		for (int i = 1, j = 65; j < 91; i++, j++) {
			Character c = (char) j;
			alphabets[i] = c.toString();
		}
		return alphabets;
	}

	class WLTableModelListener implements TableModelListener {
		@Override
		public void tableChanged(TableModelEvent e) {
			int row = e.getFirstRow();
			int column = e.getColumn();
			DefaultTableModel model = (DefaultTableModel) e.getSource();
			Object data = null;
			try {

				data = model.getValueAt(row, column);

				String keyModified = (String) model.getValueAt(row, 1);

				for (String key : completeDictionary.keySet()) {
					if (key.equalsIgnoreCase(keyModified)) {
						switch (column) {
						case 2: {
							StudyListEntry word = completeDictionary
									.get(keyModified);
							word.answer = data.toString();
							completeDictionary.put(key, word);
						}
						break;
						case 3: {
							StudyListEntry word = completeDictionary
									.get(keyModified);
							word.sentence = data.toString();
							completeDictionary.put(key, word);
						}
						break;
						case 4: {
							StudyListEntry word = completeDictionary
									.get(keyModified);
							word.difficult = data.toString();
							completeDictionary.put(key, word);
						}
						break;
						case 5: {
							StudyListEntry word = completeDictionary
									.get(keyModified);
							if (TRUE_STRING.equalsIgnoreCase(data.toString())) {
								word.favourite = true;
							} else {
								word.favourite = false;
							}
							completeDictionary.put(key, word);
						}
						break;
						default:
							break;
						}
					}
				}

			} catch (ArrayIndexOutOfBoundsException exception) {
			} catch (Exception exception) {
				System.err.println("Updation failed for row : " + row
						+ " column : " + column);
				exception.printStackTrace();
			}
		}
	}

	class WLTableModel extends DefaultTableModel {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		Object rowData[][] = new Object[][] { {} };
		String[] columns = columnNames;

		public WLTableModel(String[] columnNames) {
			super(null, columnNames);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			if (!practiceMode) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Class<? extends Object> getColumnClass(int column) {
			switch (column) {
			case 1:
				return String.class;
			case 2:
				return String.class;
			case 3:
				return String.class;
			case 4:
				return String.class;
			case 5:
				return Boolean.class;
			default:
				return String.class;
			}
		}
	}

}

class StudyListEntry {
	String entry;
	String answer;
	String sentence;
	String difficult;
	Boolean favourite = false;
}
