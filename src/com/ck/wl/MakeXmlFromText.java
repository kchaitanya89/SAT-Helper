package com.ck.wl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MakeXmlFromText {

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

	static final String fileName = "squares";

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		SortedMap<String, StudyListEntry> completeDictionary = new TreeMap<String, StudyListEntry>();

		File inputTextFile = new File("D:\\MSat\\ck97982\\WL-Files\\"
				+ fileName + ".txt");

		BufferedReader bufferedReader = new BufferedReader(new FileReader(
				inputTextFile));

		String lineRead = null;
		while ((lineRead = bufferedReader.readLine()) != null) {
			StudyListEntry entry = new StudyListEntry();
			entry.answer = lineRead.substring(0, lineRead.indexOf('-'));
			entry.entry = lineRead.substring(lineRead.indexOf('-') + 1);
			entry.difficult = "1";
			entry.sentence = "";
			entry.favourite = false;

			completeDictionary.put(entry.entry, entry);
		}

		bufferedReader.close();

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
			hideWhenMinimizedElement.appendChild(doc.createTextNode("true"));
			settings.appendChild(hideWhenMinimizedElement);

			rootElement.appendChild(settings);

			Element wordList = doc.createElement(STUDY_LIST);
			rootElement.appendChild(wordList);
			int count = 0;
			for (String dictionaryEntry : completeDictionary.keySet()) {
				// if (completeDictionary.get(dictionaryEntry).sentence
				// .contains("\t")) {
				// count++;
				// String replacementSent = completeDictionary
				// .get(dictionaryEntry).sentence.replace('\t', ' ');
				// replacementSent = replacementSent.replace("  ", " ");
				// replacementSent = replacementSent.replace("   ", " ");
				//
				// String replacementMeaning = completeDictionary
				// .get(dictionaryEntry).answer.replace('\t', ' ');
				// replacementMeaning = replacementMeaning.replace("  ", " ");
				// replacementMeaning = replacementMeaning.replace("   ", " ");
				//
				// StudyListEntry value = completeDictionary
				// .get(dictionaryEntry);
				// value.sentence = replacementSent;
				// value.answer = replacementMeaning;
				// completeDictionary.put(dictionaryEntry, value);
				// }
				//
				// if (completeDictionary.get(dictionaryEntry).sentence
				// .contains("  ")) {
				// count++;
				// String replacementSent = completeDictionary
				// .get(dictionaryEntry).sentence.replace("  ", " ");
				// replacementSent = replacementSent.replace("   ", " ");
				//
				// String replacementMeaning = completeDictionary
				// .get(dictionaryEntry).answer.replace("  ", " ");
				// replacementMeaning = replacementMeaning.replace("   ", " ");
				//
				// StudyListEntry value = completeDictionary
				// .get(dictionaryEntry);
				// value.sentence = replacementSent;
				// value.answer = replacementMeaning;
				// completeDictionary.put(dictionaryEntry, value);
				// }

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
			OutputStream output = new FileOutputStream(new File(
					"D:\\MSat\\ck97982\\WL-Files\\" + fileName + ".xml"));
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
}
