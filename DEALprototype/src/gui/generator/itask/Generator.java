package gui.generator.itask;

import gui.generator.ConfigurationException;
import gui.generator.GeneratorException;
import gui.model.domain.DomainModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import yajco.model.Language;

public abstract class Generator {
    protected static final String PROPERTIES_FILE = "generator.properties";

    protected static Properties generatorProperties = new Properties();

    private final Language language;
    private final DomainModel domainModel;

    static {
        try {
            generatorProperties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (Exception e) {
            throw new ConfigurationException("Failed during loading of the configuration file '" + PROPERTIES_FILE + "'", e);
        }
    }

//    public Generator(Language language) {
//        this(language, null);
//    }
    
    public Generator(Language language, DomainModel domainModel) {
    	this.language = language;
    	this.domainModel = domainModel;
    }

    public Language getLanguage() {
        return language;
    }
    
    public DomainModel getDomainModel() {
    	return domainModel;
    }

    public String getDestinationDir() {
        return generatorProperties.getProperty("destDir");
    }

    protected PrintWriter createWriter(String fileName) throws GeneratorException {
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            return new PrintWriter(file);
        }
        catch (IOException e) {
            throw new GeneratorException("Cannot write generated output to file '" + fileName + "'", e);
        }
    }

    public abstract void generate() throws GeneratorException;
}
