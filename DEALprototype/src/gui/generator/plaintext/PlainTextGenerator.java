package gui.generator.plaintext;

import gui.generator.GeneratorException;
import gui.generator.itask.Generator;
import gui.model.application.Application;
import gui.model.domain.DomainModel;

import java.io.File;
import java.io.PrintWriter;

public class PlainTextGenerator extends Generator {
	public static final String PLAIN_TEXT_DIR = "text" + File.separator;
	public static final String EXTENSION = "txt";
	
	protected static final String PROPERTIES_FILE = "plaintextgenerator.properties";
	private Application application;

	public PlainTextGenerator(Application application) {
		super(null, null);
		this.application = application;
	}
	
	@Override
    public void generate() throws GeneratorException {
        PrintWriter writer = createWriter(getOutputFileName());
        
        generate(writer);
        writer.close();
    }

    private void generate(PrintWriter writer) {
    	for(DomainModel dm : application.getDomainModels()) {
    		writer.write(dm.toString());
    	}
	}

	protected String getOutputFileName() {
    	String name = application.getName();
        return PLAIN_TEXT_DIR + name + "." + EXTENSION;
    }
}
