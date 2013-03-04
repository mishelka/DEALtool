package temp.domain.io.helpers;

import gui.model.domain.DomainModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;


/**
 * Parses a domain model from a given file or string.
 */
public interface IDomainModelReader {
	
	/**
	 * Returns the domain model where the read data is stored.
	 * 
	 * @return the model to fill
	 */
	public DomainModel getDomainModel();
	
	/**
	 * Sets the domain model where the read data is stored.
	 * 
	 * @param domainModel the model to fill
	 */
	public void setDomainModel(DomainModel domainModel);

	/**
	 * Parses a specific domain model file.
	 * 
	 * @param  file  the domain model file
	 * @throws UnsupportedModelException
	 * @throws FileNotFoundException
	 */
	public void readFromFile(File file)
			throws UnsupportedModelException, FileNotFoundException;

	/**
	 * Parses a textual representation of a domain model.
	 * 
	 * @param text
	 * @throws UnsupportedModelException
	 */
	public void readFromString(String text)
			throws UnsupportedModelException;

	/**
	 * Returns warnings occurred while last parsing.
	 * 
	 * @return
	 */
	public List<ModelWarning> getWarnings();

}
