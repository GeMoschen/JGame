package de.gemo.gameengine.units;

import java.io.File;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class XMLTree {

	private Element rootNode = null;

	public XMLTree() {
	}

	public XMLTree(String fileName) {
		this.load(fileName);
	}

	public void load(String fileName) {
		SAXBuilder builder = new SAXBuilder();

		File xmlFile = new File(fileName);

		if (!xmlFile.exists()) {
			System.out.println("ERROR: File '" + fileName + "' does not exist!");
			this.rootNode = null;
			return;
		}
		try {
			Document document = (Document) builder.build(xmlFile);
			this.rootNode = document.getRootElement();
			if (this.rootNode == null) {
				System.out.println("ERROR: Rootnode does not exist!");
				this.rootNode = null;
				return;
			}
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
		}
	}

	public String getString(String path) {
		if (this.rootNode == null) {
			System.out.println("WARNING: Rootnode does not exist!");
			return null;
		}

		Element currentElement = this.rootNode;
		String[] split = path.split("\\.");
		String nodeString = "";
		for (int i = 0; i < split.length - 1; i++) {
			nodeString += split[i] + ".";
			currentElement = currentElement.getChild(split[i]);
			if (currentElement == null) {
				System.out.println("WARNING: Node '" + nodeString.substring(0, nodeString.length() - 1) + "' does not exist!");
				return null;
			}
		}
		return currentElement.getAttributeValue(split[split.length - 1]);
	}

	public int getInt(String path) {
		return this.getInt(path, 0);
	}

	public Element getRootNode() {
		return rootNode;
	}

	public int getInt(String path, int defaultValue) {
		String string = this.getString(path);
		if (string == null) {
			return defaultValue;
		}
		try {
			return Integer.valueOf(string);
		} catch (Exception exception) {
			return defaultValue;
		}
	}
}
