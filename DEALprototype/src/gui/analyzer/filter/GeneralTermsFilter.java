package gui.analyzer.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GeneralTermsFilter {
	private List<GeneralTerm> generalTerms;
	private static final String fileName = "src" + File.separator + "gui"
			+ File.separator + "analyzer" + File.separator + "filter"
			+ File.separator + "generalTermsList.xml";

	public GeneralTermsFilter() {
	}

	public void serialize() {
		XStream xstream = new XStream();

		xstream.alias("generalTerm", GeneralTerm.class);
		xstream.alias("generalTerms", List.class);

		String xml = xstream.toXML(generalTerms);

		FileWriter fw = null;

		try {
			fw = new FileWriter(fileName);
			fw.write(xml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	public void deserialize() {
		XStream xstream = new XStream(new DomDriver());
		FileInputStream fis = null;
		xstream.alias("generalTerm", GeneralTerm.class);
		xstream.alias("generalTerms", List.class);

		/*
		 * if(file == null) --> initialize(); else --> deserialize();
		 */
		File f = new File(fileName);
		if (f.exists()) {

			try {
				fis = new FileInputStream(new File(fileName));

				if (fis != null) {
					generalTerms = (ArrayList<GeneralTerm>) xstream
							.fromXML(fis);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		
		if(!f.exists() || generalTerms == null) {
			System.out.println("ERROR: File " + fileName + " does not exist or it's empty. Initializing default general terms database.");
			defaultInit();
		}
	}
	
	private void defaultInit() {
		generalTerms = new ArrayList<GeneralTerm>();
		
		// starting point for general terms - this will be loaded only if the
		// generalTermsList.xml is empty or does not exist.
		GeneralTerm application = new GeneralTerm("application", null);
		GeneralTerm file = new GeneralTerm("file", application);
		GeneralTerm childOfFile = new GeneralTerm("open", file);
		childOfFile = new GeneralTerm("close", file);
		childOfFile = new GeneralTerm("exit", file);
		childOfFile = new GeneralTerm("save", file);
		childOfFile = new GeneralTerm("save as", file);
		childOfFile = new GeneralTerm("print", file);
		childOfFile = new GeneralTerm("new", file);
		childOfFile = new GeneralTerm("open", file);
		childOfFile = new GeneralTerm("close all", file);
		childOfFile = new GeneralTerm("restart", file);
		childOfFile = new GeneralTerm("import", file);
		childOfFile = new GeneralTerm("export", file);
		childOfFile = new GeneralTerm("properties", file);

		GeneralTerm edit = new GeneralTerm("edit", application);
		GeneralTerm childOfEdit = new GeneralTerm("undo", edit);
		childOfEdit = new GeneralTerm("redo", edit);
		childOfEdit = new GeneralTerm("copy", edit);
		childOfEdit = new GeneralTerm("paste", edit);
		childOfEdit = new GeneralTerm("cut", edit);
		childOfEdit = new GeneralTerm("remove", edit);
		childOfEdit = new GeneralTerm("delete", edit);
		childOfEdit = new GeneralTerm("replace", edit);
		childOfEdit = new GeneralTerm("select all", edit);
		childOfEdit = new GeneralTerm("find", edit);
		childOfEdit = new GeneralTerm("replace", edit);
		childOfEdit = new GeneralTerm("find next", edit);
		childOfEdit = new GeneralTerm("find previous", edit);
		childOfEdit = new GeneralTerm("search", edit);

		GeneralTerm window = new GeneralTerm("window", application);
		GeneralTerm childOfWindow = new GeneralTerm("new window", window);
		childOfWindow = new GeneralTerm("preferences", window);

		generalTerms.add(application);
	}

	public List<GeneralTerm> getGeneralTerms() {
		return generalTerms;
	}

	public void setGeneralTerms(List<GeneralTerm> generalTerms) {
		this.generalTerms = generalTerms;
	}

	public void addGeneralTerm(GeneralTerm generalTerm) {
		generalTerms.add(generalTerm);
	}

	public void removeGeneralTerm(int index) {
		generalTerms.remove(index);
	}

	public void removeGeneralTerm(GeneralTerm generalTerm) {
		generalTerms.remove(generalTerm);
	}
}
