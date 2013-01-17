package temp;

import gui.model.application.WindowScene;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XmlModelGenerator {
	private DomainModel model;

	public void generate(Window w) {
		generateModel(w);
		try {
			generateXml();
		} catch (Exception e) {
			System.out.println(">>> EXCEPTION DURING GENERATING FEATURE MODEL");
		}
	}

	public void generateModel(Window w) {
		model = new DomainModel("Model");
		model.setScene(new WindowScene(w));

		if (w instanceof JFrame) {
			JFrame f = (JFrame) w;
			model.setName(f.getTitle());
		} else if (w instanceof Dialog) {
			Dialog d = (Dialog) w;
			model.setName(d.getTitle());
		}

		generate(w.getComponents());

		System.out.println(model);
	}

	private void generate(Component[] components) {
		for (Component c : components) {
			Term f = new Term(model);
			f.setComponent(c);
			f.setComponentClass(c.getClass());

			model.addTerm(f);
			if (c instanceof Container) {
				generate(((Container) c).getComponents());
			}
		}
	}

	public void generateXml() throws FileNotFoundException,
			TransformerConfigurationException, SAXException {
		// PrintWriter from a Servlet
		PrintWriter out = new PrintWriter(new FileOutputStream(
				"outputModel.xml"));
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory
				.newInstance();
		// SAX2.0 ContentHandler.
		TransformerHandler hd = tf.newTransformerHandler();
		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "users.dtd");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		hd.setResult(streamResult);
		hd.startDocument();
		AttributesImpl atts = new AttributesImpl();
		// USERS tag.
		hd.startElement("", "", "USERS", atts);
		// USER tags.
		String[] id = { "PWD122", "MX787", "A4Q45" };
		String[] type = { "customer", "manager", "employee" };
		String[] desc = { "Tim@Home", "Jack&Moud", "John D'oé" };
		for (int i = 0; i < id.length; i++) {
			atts.clear();
			atts.addAttribute("", "", "ID", "CDATA", id[i]);
			atts.addAttribute("", "", "TYPE", "CDATA", type[i]);
			hd.startElement("", "", "USER", atts);
			hd.characters(desc[i].toCharArray(), 0, desc[i].length());
			hd.endElement("", "", "USER");
		}
		hd.endElement("", "", "USERS");
		hd.endDocument();

	}
}
