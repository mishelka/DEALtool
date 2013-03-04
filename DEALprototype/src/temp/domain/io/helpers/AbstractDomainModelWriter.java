package temp.domain.io.helpers;

import gui.analyzer.util.Logger;
import gui.model.domain.DomainModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Default writer to be extended for each domain model format.
 */
public abstract class AbstractDomainModelWriter implements IDomainModelWriter {

	/**
	 * the domain model to write out
	 */
	protected DomainModel domainModel;

	public void setDomainModel(DomainModel domainModel) {
		this.domainModel = domainModel;
	}

	public DomainModel getDomainModel() {
		return domainModel;
	}

	public void writeToFile(File file) {
		FileOutputStream output = null;
		try {
			if (!file.exists())
				file.createNewFile();
			output = new FileOutputStream(file);
			output.write(writeToString().getBytes());
			output.flush();
		} catch (IOException e) {
			Logger.logError(e);
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				Logger.logError(e);
			}
		}
	}
}
