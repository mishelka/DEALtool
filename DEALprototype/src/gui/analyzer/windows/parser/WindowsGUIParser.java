package gui.analyzer.windows.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Pinko
 */
public class WindowsGUIParser {

    private Document doc;
    private Document newDoc;
    private String filePath;
    
    public Document parse(String filePath) {
    	this.filePath = filePath;
        doc = loadXMLFile();
        Node root = getAppRoot(doc);
        newDoc = createNewDocument(root);

//        Element rootElement = newDoc.getDocumentElement();
        removeAttrsFromAllElements(newDoc);
        removeAllUnnecessaryElements();
        
        //TODO: odstranit prazdne riadky vo vyslednom subore?
        
        //TODO: docasne zakomentovane, pouzivat len pre ucely testovania (necommitovat)
        //saveAsXML();
        
        return newDoc;
    }

    /**
     * returns element that represents the host computer in the XML file
     * structure
     *
     */
    public Element getHostElement() {
        Element root = doc.getDocumentElement();
        NodeList list = root.getElementsByTagName("element");
        Element host = (Element) list.item(0);
        return host;
    }

    /**
     * returns the root element of the application GUI this is the root that is
     * needed for domain model creation
     */
    public Element getAppRoot(Document doc) {
        Element host = getHostElement();
        NodeList list = host.getElementsByTagName("children");
        Element children = (Element) list.item(0);
        NodeList list2 = children.getElementsByTagName("element");
        Element rootElement = (Element) list2.item(0);

        return rootElement;
    }

    /**
     * loads an XML file into a DOM object without making any changes
     *
     */
    public Document loadXMLFile() {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            return doc;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * creates a new Document that starts with application GUI root
     */
    public Document createNewDocument(Node root) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Node newNode = doc.importNode(root, true);
            doc.appendChild(newNode);
            return doc;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * helping method for printing element attributes
     */
    public static void listAllAttributes(Element element) {

        System.out.println("List attributes for node: " + element.getNodeName());
        NamedNodeMap attributes = element.getAttributes();
        int numAttrs = attributes.getLength();

        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attributes.item(i);

            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();
            System.out.println("\t" + attrName + ": " + attrValue);

        }
    }

    /**
     * removes attributes of an element that are not needed for domain model
     * creation
     */
    public void removeElementAttributes(Element element) {
        ArrayList<String> neededAttrs = new ArrayList<String>();
        Collections.addAll(neededAttrs, "role", "capabilities", "visible",
                "enabled", "hasfocus", "index");

        NamedNodeMap realAttrs = element.getAttributes();
        ArrayList<Attr> attrsToDelete = new ArrayList<Attr>();

        for (int i = 0; i < realAttrs.getLength(); i++) {
            Attr temp = (Attr) realAttrs.item(i);
            if (!neededAttrs.contains(temp.getName())) {
                attrsToDelete.add(temp);
            }
        }

        for (Attr a : attrsToDelete) {
            element.removeAttributeNode(a);
        }
    }

    /**
     * all GUI objects are represented in XML file as elements called "element"
     * unnecessary attributes have to be deleted from all these elements
     */
    public void removeAttrsFromAllElements(Document document) {
        NodeList list = document.getElementsByTagName("element");
        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            removeElementAttributes(e);
        }
    }

    public void removeAllUnnecessaryElements() {
        ArrayList<String> neededElements = new ArrayList<String>();
        ArrayList<Element> elementsToDelete = new ArrayList<Element>();
        Collections.addAll(neededElements, "snapshot", "appicon", "AccessibleDescription", "AccessibleKeyboardShortcut",
                "AccessibleName", "AccessibleRole", "AccessibleState", "text", "class",
                "index", "title");

        NodeList elementList = newDoc.getElementsByTagName("attribute");
        for (int i = 0; i < elementList.getLength(); i++) {
            Element e = (Element) elementList.item(i);
            String nameAttr = e.getAttribute("name");
            if (!neededElements.contains(nameAttr)) {
                elementsToDelete.add(e);
            }
        }

        for (Element e : elementsToDelete) {
            e.getParentNode().removeChild(e);
        }
        elementsToDelete.clear();

        NodeList otherElements = newDoc.getElementsByTagName("dynamicattribute");
        for (int i = 0; i < otherElements.getLength(); i++) {
            Element e = (Element) otherElements.item(i);
            elementsToDelete.add(e);
        }

        for (Element e : elementsToDelete) {
            e.getParentNode().removeChild(e);
        }

    }

    public void saveAsXML() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            Result output = new StreamResult(new File("/windows/temp/output.xml"));
            Source input = new DOMSource(newDoc);
            transformer.transform(input, output);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(WindowsGUIParser.class.getName()).log(Level.SEVERE, null, ex);
        } 

    }

}
