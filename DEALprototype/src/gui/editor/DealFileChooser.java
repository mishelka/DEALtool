package gui.editor;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("serial")
public class DealFileChooser extends JFileChooser {
	public static final String OPEN_XML_DIALOG_NAME = "Open Ranorex file";
	public static final String OPEN_DEAL_DIALOG_NAME = "Open existing DEAL file";
	public static final String OWL_DIALOG_NAME = "Open OWL file";
	
	public static final String DEAL_FILE_EXT = "deal";
	public static final String XML_FILE_EXT = "xml";
	public static final String OWL_FILE_EXT = "owl";
	public static final String RANOREX_FILE_EXT = "rxsnp";

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
			case RANOREX: setRanorexSettings();
		}
	}

	private void setRanorexSettings() {
		setAccessibleInfo(OPEN_XML_DIALOG_NAME);
		RanorexFileFilter filter = new RanorexFileFilter();
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

	private class RanorexFileFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			String extension = FilenameUtils.getExtension(f.getPath());
			return f.isDirectory()
					|| extension
					.equalsIgnoreCase(XML_FILE_EXT)
					|| extension.equalsIgnoreCase(RANOREX_FILE_EXT);
		}

		@Override
		public String getDescription() {
			return "RANOREX files *." + XML_FILE_EXT + ", *." + RANOREX_FILE_EXT;
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
		OWL, DEAL, RANOREX;
	}
}
