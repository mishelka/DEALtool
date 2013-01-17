package temp.feature.io;

import gui.analyzer.util.Logger;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import temp.feature.io.helpers.AbstractFeatureModelWriter;


/**
 * Prints a feature model in XML format.
 * 
 * @author Fabian Wielgorz -> OLD
 * @author Dariusz Krolikowski
 * @author Maik Lampe
 * 
 */
public class XmlFeatureModelWriter extends AbstractFeatureModelWriter {
	/**
	 * Creates a new writer and sets the feature model to write out.
	 * 
	 * @param featureModel
	 *            the structure to write
	 */
	public XmlFeatureModelWriter(DomainModel featureModel) {
		setFeatureModel(featureModel);
	}

	/**
	 * Creates XML-Document
	 * 
	 * @param doc
	 *            document to write
	 */
	private void createXmlDoc(Document doc) {
		System.out.println(">>> creating xml doc");
		Element root = doc.createElement("featureModel");
		Element struct = doc.createElement("struct");

		root.setAttribute("chosenLayoutAlgorithm", "" + 1);
		System.out.println(">>> chosenLayoutAlgorithm 1");

		doc.appendChild(root);
		root.appendChild(struct);
		System.out.println("appending children: struct, featureModel, chosenLayoutAlgorithm");
		createXmlDocRec(doc, struct, featureModel.getRoot());
	}

	/**
	 * Creates document based on feature model step by step
	 * 
	 * @param doc
	 *            document to write
	 * @param node
	 *            parent node
	 * @param feat
	 *            current feature
	 */
	private void createXmlDocRec(Document doc, Element node, Term feat) {
		System.out.println(">>> writing feature " + feat);
		if (feat == null)
			return;

		Element fnod;
		List<Term> children;

		children = feat.getChildren();
		if (children.isEmpty()) {
			fnod = doc.createElement("feature");
			fnod.setAttribute("name", feat.getName());
			fnod.setAttribute("description", feat.getDescription());

			node.appendChild(fnod);
		} else {
			if (feat.getRelation() == RelationType.AND) {
				fnod = doc.createElement("and");
			} else if (feat.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
				fnod = doc.createElement("or");
			} else if (feat.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				fnod = doc.createElement("alt");
			} else
				fnod = doc.createElement("unknown");// FMCorePlugin.getDefault().logInfo("creatXMlDockRec: Unexpected error!");

			fnod.setAttribute("name", feat.getName());

			node.appendChild(fnod);
			
			//iterator doesn't work - using foreach instead
			for(Term f : children) {
				createXmlDocRec(doc, fnod, f);
			}
		}
	}

	/**
	 * Inserts indentations into the text
	 * 
	 * @param text
	 * @return
	 */
	private String prettyPrint(String text) {
		StringBuilder result = new StringBuilder();
		String line;
		int indentLevel = 0;
		BufferedReader reader = new BufferedReader(new StringReader(text));
		try {
			line = reader.readLine();
			while (line != null) {
				if (line.startsWith("</")) {
					indentLevel--;
					for (int i = 0; i < indentLevel; i++) {
						result.append("\t");
					}
				} else if (line.startsWith("<")) {
					for (int i = 0; i < indentLevel; i++) {
						result.append("\t");
					}
					if (!line.contains("</")) {
						indentLevel++;
					}
				} else {
					for (int i = 0; i < indentLevel; i++) {
						result.append("\t");
					}
				}
				result.append(line + "\n");
				if (line.contains("/>")) {
					indentLevel--;
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			Logger.logError(e);
		}
		return result.toString();
	}

	public String writeToString() {
		// Create Empty DOM Document
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setIgnoringComments(true);
		dbf.setIgnoringElementContentWhitespace(false);
		dbf.setCoalescing(true);
		dbf.setExpandEntityReferences(true);
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			Logger.logError(pce);
		}
		Document doc = db.newDocument();
		// Create the Xml Representation
		createXmlDoc(doc);

		// Transform the Xml Representation into a String
		Transformer transfo = null;
		try {
			transfo = TransformerFactory.newInstance().newTransformer();
		} catch (TransformerConfigurationException e) {
			Logger.logError(e);
		} catch (TransformerFactoryConfigurationError e) {
			Logger.logError(e);
		}

		transfo.setOutputProperty(OutputKeys.METHOD, "xml");
		transfo.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(doc);
		try {
			transfo.transform(source, result);
		} catch (TransformerException e) {
			Logger.logError(e);
		}

		return prettyPrint(result.getWriter().toString());
	}
}
