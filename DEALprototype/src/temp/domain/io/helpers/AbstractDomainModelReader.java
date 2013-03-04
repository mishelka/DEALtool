package temp.domain.io.helpers;

import gui.analyzer.util.Logger;
import gui.model.domain.DomainModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Default reader to be extended for each domain model format.
 */
public abstract class AbstractDomainModelReader implements IDomainModelReader {

	/**
	 * the structure to store the parsed data
	 */
	protected DomainModel domainModel;

	/**
	 * warnings occurred while parsing
	 */
	protected LinkedList<ModelWarning> warnings = new LinkedList<ModelWarning>();

	public void setDomainModel(DomainModel domainModel) {
		this.domainModel = domainModel;
	}

	public DomainModel getDomainModel() {
		return domainModel;
	}

	public void readFromFile(File file) throws UnsupportedModelException,
			FileNotFoundException {
		warnings.clear();
		String fileName = file.getPath();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(fileName);
			parseInputStream(inputStream);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Logger.logError(e);
				}
			}
		}
	}

	public void readFromString(String text) throws UnsupportedModelException {
		warnings.clear();
		InputStream inputStream = new ByteArrayInputStream(
				text.getBytes(Charset.availableCharsets().get("UTF-8")));
		parseInputStream(inputStream);
	}

	public List<ModelWarning> getWarnings() {
		return warnings;
	}

	/**
	 * Reads a domain model from an input stream.
	 * 
	 * @param inputStream
	 *            the textual representation of the domain model
	 * @throws UnsupportedModelException
	 */
	protected abstract void parseInputStream(InputStream inputStream)
			throws UnsupportedModelException;

}
