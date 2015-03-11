package gui.analyzer.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathHelper {
	public static XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
    }

    public static NodeList getNodeList(String xpath, Node parent) throws XPathExpressionException {
        return (NodeList) getXPath().evaluate(xpath, parent, XPathConstants.NODESET);
    }

    public static String getString(String xpath, Node parent) throws XPathExpressionException {
        return (String) getXPath().evaluate(xpath, parent, XPathConstants.STRING);
    }

    public static Node getNode(String xpath, Node parent) throws XPathExpressionException {
        return (Node) getXPath().evaluate(xpath, parent, XPathConstants.NODE);
    }
}
