package gui.editor;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("serial")
public class DealFileChooser extends JFileChooser {
	public static final String OPEN_XML_DIALOG_NAME = "Open Ranorex xml file";
	public static final String OPEN_DEAL_DIALOG_NAME = "Open existing DEAL file";
	public static final String OWL_DIALOG_NAME = "Open OWL file";
	
	public static final String DEAL_FILE_EXT = "deal";
	public static final String XML_FILE_EXT = "xml";
	public static final String OWL_FILE_EXT = "owl";

	public DealFileChooser() {
		super();
	}
	
	public DealFileChooser(String filePath) {
		super(filePath);
	}
	
	public void setSettingsFor(DealFileChooserType type) {
		switch(type) {
			case DEAL: setDealSettings();
			case OWL: setOwlSettings();
			case XML: setXmlSettings();
		}
	}

	private void setXmlSettings() {
		setAccessibleInfo(OPEN_XML_DIALOG_NAME);
		XmlFileFilter filter = new XmlFileFilter();
		setFileFilter(filter);
	}

	private void setDealSettings() {
		setAccessibleInfo(OPEN_DEAL_DIALOG_NAME);
		DealFileFilter filter = new DealFileFilter();
		setFileFilter(filter);
	}

	private void setOwlSettings() {
		setAccessibleInfo(OWL_DIALOG_NAME);
		setAcceptAllFileFilterUsed(false);
		OwlFileFilter filter = new OwlFileFilter();
		setFileFilter(filter);
	}
	
	private void setAccessibleInfo(String name) {
		setName(name);
		setDialogTitle(name);
	}

	private class XmlFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			return f.isDirectory()
					|| FilenameUtils.getExtension(f.getPath())
					.equalsIgnoreCase(XML_FILE_EXT);
		}

		@Override
		public String getDescription() {
			return "XML files *." + XML_FILE_EXT;
		}
	}

	/**
	 * File filter for *.deal files.
	 */
	private class DealFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || 
					FilenameUtils.getExtension(f.getPath())
					.equalsIgnoreCase(DEAL_FILE_EXT); 
		}

		@Override
		public String getDescription() {
			return "DEAL files *." + DEAL_FILE_EXT;
		}
	}

	private class OwlFileFilter extends FileFilter {
		@Override
		public String getDescription() {
			return "Ontology files *." + OWL_FILE_EXT;
		}

		@Override
		public boolean accept(File f) {
			return (f.isDirectory() || 
				FilenameUtils.getExtension(f.getPath())
				.equalsIgnoreCase(OWL_FILE_EXT));
		}
	}
	
	public enum DealFileChooserType {
		OWL, XML, DEAL;
	}
}
