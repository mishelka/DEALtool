package gui.generator.itask;

import gui.generator.ConfigurationException;
import gui.generator.GeneratorException;
import gui.model.domain.DomainModel;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import yajco.model.Language;

//SEE ALSO: Templated Generation
//http://martinfowler.com/dslwip/TemplatedGeneration.html
public abstract class TemplateGenerator extends Generator {
    protected static final String VELOCITY_PROPERTIES_FILE = "velocity.properties";

    protected static final String TEMPLATE_PACKAGE = "templates";

    protected static VelocityEngine velocityEngine;

    protected final String template;

    static {
        try {
            Properties velocityProperties = new Properties();
            velocityProperties.load(ClassLoader.getSystemClassLoader().getResourceAsStream(VELOCITY_PROPERTIES_FILE));
            velocityEngine = new VelocityEngine(velocityProperties);
        } catch (Exception e) {
            throw new ConfigurationException("Failed during loading of the configuration file '" + VELOCITY_PROPERTIES_FILE + "'", e);
        }
    }

//    public TemplateGenerator(Language language, String template) {
//        this(language, null, template);
//    }
    
    public TemplateGenerator(Language language, DomainModel domainModel, String template) {
    	super(language, domainModel);
    	this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    protected void generate(Writer writer, Map<String, Object> params) throws GeneratorException {
        try {
            //Prepare context
            VelocityContext context = new VelocityContext(params);

            //Add model and generator
            context.put("language", getLanguage());
            context.put("domainModel", getDomainModel());
            context.put("generator", this);

            //Evaluate the template - generates output
            String path = TEMPLATE_PACKAGE + "/" + template + "." + getExtension() + ".vm";
            InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(path), "utf-8");
            velocityEngine.evaluate(context, writer, "", reader);
        } catch (IOException e) {
            throw new GeneratorException("Cannot generate output for template " + getTemplate(), e);
        }
    }

    public String getExtension() {
        return generatorProperties.getProperty(template + ".extension");
    }
}
