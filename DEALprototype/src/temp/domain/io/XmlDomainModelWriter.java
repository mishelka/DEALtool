package temp.domain.io;

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

import temp.domain.io.helpers.AbstractDomainModelWriter;

/**
 * Prints a domain model in XML format.
 */
public class XmlDomainModelWriter extends AbstractDomainModelWriter {
	/**
	 * Creates a new writer and sets the domain model to write out.
	 * 
	 * @param domainModel  the structure to write
	 */
	public XmlDomainModelWriter(DomainModel domainModel) {
		setDomainModel(domainModel);
	}

	/**
	 * Creates XML-Document
	 * 
	 * @param doc  document to write
	 */
	private void createXmlDoc(Document doc) {
		Logger.log("creating xml doc");
		Element root = doc.createElement("domainModel");
		Element struct = doc.createElement("struct");

		root.setAttribute("chosenLayoutAlgorithm", "" + 1);
		Logger.log("chosenLayoutAlgorithm 1");

		doc.appendChild(root);
		root.appendChild(struct);
		Logger.log("appending children: struct, domainModel, chosenLayoutAlgorithm");
		createXmlDocRec(doc, struct, domainModel.getRoot());
	}

	/**
	 * Creates document based on domain model step by step
	 * 
	 * @param doc
	 *            document to write
	 * @param node
	 *            parent node
	 * @param term
	 *            current term
	 */
	private void createXmlDocRec(Document doc, Element node, Term term) {
		Logger.log("writing term " + term);
		if (term == null)
			return;

		Element fnod;
		List<Term> children;

		children = term.getChildren();
		if (children.isEmpty()) {
			fnod = doc.createElement("term");
			fnod.setAttribute("name", term.getName());
			fnod.setAttribute("description", term.getDescription());

			node.appendChild(fnod);
		} else {
			if (term.getRelation() == RelationType.AND) {
				fnod = doc.createElement("and");
			} else if (term.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
				fnod = doc.createElement("or");
			} else if (term.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				fnod = doc.createElement("alt");
			} else
				fnod = doc.createElement("unknown");// FMCorePlugin.getDefault().logInfo("creatXMlDockRec: Unexpected error!");

			fnod.setAttribute("name", term.getName());

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
