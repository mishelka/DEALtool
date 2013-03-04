package temp.domain.io.helpers;

import gui.model.domain.DomainModel;

import java.io.File;


/**
 * Writes a domain model to a file or string.
 */
public interface IDomainModelWriter {

	/**
	 * Returns the domain model to write out.
	 * 
	 * @return the model to write
	 */
	public DomainModel getDomainModel();
	
	/**
	 * Sets the domain model to be saved in a textual representation.
	 * 
	 * @param domainModel the model to write
	 */
	public void setDomainModel(DomainModel domainModel);
	
	/**
	 * Saves a domain model to a file.
	 * 
	 * @param file
	 * @throws CoreException
	 */
	public abstract void writeToFile(File file);
	
	/**
	 * Converts a domain model to a textual representation.
	 * 
	 * @return
	 */
	public abstract String writeToString();

}
