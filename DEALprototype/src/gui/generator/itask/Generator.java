package gui.generator.itask;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import yajco.model.Language;

public abstract class Generator {
    protected static final String PROPERTIES_FILE = "generator.properties";

    protected static Properties generatorProperties = new Properties();

    private final Language language;

    static {
        try {
            generatorProperties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(PROPERTIES_FILE));
        } catch (Exception e) {
            throw new ConfigurationException("Failed during loading of the configuration file '" + PROPERTIES_FILE + "'", e);
        }
    }

    public Generator(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
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
