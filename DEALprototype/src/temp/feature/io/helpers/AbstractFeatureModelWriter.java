package temp.feature.io.helpers;

import gui.analyzer.util.Logger;
import gui.model.domain.DomainModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Default writer to be extended for each feature model format.
 * 
 * @author Thomas Thuem
 */
public abstract class AbstractFeatureModelWriter implements IFeatureModelWriter {

	/**
	 * the feature model to write out
	 */
	protected DomainModel featureModel;

	public void setFeatureModel(DomainModel featureModel) {
		this.featureModel = featureModel;
	}

	public DomainModel getFeatureModel() {
		return featureModel;
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
