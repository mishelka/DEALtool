package gui.generator.itask;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import yajco.model.Language;

public class ITaskGenerator extends ITaskTemplateGenerator {
	public static final String ITASK_DIR = "iTask/iTask/";
	
    public ITaskGenerator(Language language) {
        super(language, "iTask"); //iTask template name
    }

    @Override
    public void generate() throws GeneratorException {
        PrintWriter writer = createWriter(getOutputFileName());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", formatName(null, getTemplate()));
        
        generate(writer, params);
        writer.close();
    }

    protected String getOutputFileName() {
    	String languageName = super.getLanguage().getName();
        return getOutputDirectory() + languageName + "." + getExtension();
    }
}
