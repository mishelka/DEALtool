package gui.generator.itask;

import gui.model.domain.DomainModel;
import gui.model.domain.Term;

import java.awt.MenuItem;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;

import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.Property;
import yajco.model.type.PrimitiveType;
import yajco.model.type.ReferenceType;
import yajco.model.type.SetType;
import yajco.model.type.Type;

public abstract class ITaskTemplateGenerator extends TemplateGenerator {
	public ITaskTemplateGenerator(Language language, DomainModel domainModel, String template) {
		super(language, domainModel, template);
	}
	
	@Override
    protected void generate(Writer writer, Map<String, Object> params) throws GeneratorException {
        params.put("package", getPackage(getTemplate()));
        super.generate(writer, params);
    }
	

	protected String getOutputDirectory() {
		String packageDir = getPackage(template).replace('.', '/');
		return getDestinationDir() + "/" + packageDir + "/";
	}
	
	/* Utility methods used in templates. */
    /* *********************************************************************************** */
    public String getPackage(String template) {
        return generatorProperties.getProperty(template + ".package");
    }

    public String toUCIdent(String ident) {
        return Character.toUpperCase(ident.charAt(0)) + ident.substring(1);
    }

    public String toLCIdent(String ident) {
        return Character.toLowerCase(ident.charAt(0)) + ident.substring(1);
    }

    public String coalesce(String... args) {
        for (String s : args) {
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    public String formatName(String name, String template) {
        String format = generatorProperties.getProperty(template + ".format");
        
        if(name == null)
        	name = template;
        
        Formatter f = new Formatter();
        f.format(format, name);
        
        return f.toString();
    }

    public String formatQualifiedName(String name, String template) {
        return getPackage(template) + "." + formatName(name, template);
    }

    public Class<?> getJavaClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
    
    public boolean isPrimitiveSubtype(Concept concept) {
    	return (getPrimitiveSubtype(concept) != null);
    }
    
    public PrimitiveType getPrimitiveSubtype(Concept concept) {
    	List<Property> props = concept.getAbstractSyntax();
    	
    	if(props.size() == 1) {
			Property prop = props.get(0);
			
			if(prop.getType() instanceof PrimitiveType) {
				return (PrimitiveType) prop.getType();
			}
    	}
    	
		return null;
    }
    
    public String toFirstLetterUppercase(String string) {
    	if(string == null || string.length() == 0) return string;
    	
    	return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
    
    private String toITaskType(String type) {
    	if(type.equals("STRING")) return "String";
    	if(type.equals("INTEGER")) return "Int";
    	return "String";
    }
    
    //String, Int, Note, Date, ...
    public String getStringTypeFromYajco(Property property) {
    	Type type = property.getType();
    	
    	if(type instanceof PrimitiveType) {
     		PrimitiveType pt = (PrimitiveType) type;
     		String stringType = pt.getPrimitiveTypeConst().toString();
     		return toITaskType(stringType);
     	} else if(type instanceof ReferenceType) {
    		ReferenceType rt = (ReferenceType)type;
    		Concept referencedConcept = rt.getConcept();

    		//tak je to mutually exclusive
    		if(referencedConcept.getConcreteSyntax().size() > 1) {
    			//do nothing
    		}
    		
    		if(isPrimitiveSubtype(referencedConcept)) {
    			PrimitiveType pt = getPrimitiveSubtype(referencedConcept);
    			return toITaskType(pt.getPrimitiveTypeConst().toString());
    		}
    		
    		return toFirstLetterUppercase(referencedConcept.getName());
    		
    	} else if(type instanceof SetType) {
    		SetType st = (SetType) type;
    		
    		Type t = st.getComponentType();
    		if(t instanceof ReferenceType) {
    			ReferenceType rt = (ReferenceType) t;
    			Concept setConcept = rt.getConcept();
    			
    			return setConcept.getConceptName();
    		}
    		
    		return st.toString();
    	}
    	
    	return "String";
    }

	public boolean isSet(Concept concept) {
		List<Property> props = concept.getAbstractSyntax();

		if (props.size() == 1) {
			Property p = props.get(0);
			Type t = p.getType();

			if (t instanceof SetType)
				return true;
		}

		List<Notation> notations = concept.getConcreteSyntax();

		if (notations.size() > 1)
			return true;
		
		return false;
	}
    
    public boolean isNotMutuallyExclusiveType(Property property) {
    	Type type = property.getType();
    	return type instanceof SetType;
    }
    
    public boolean isMutuallyExclusiveType(Property property) {
    	Type type = property.getType();
    	
    	if(type instanceof ReferenceType) {
    		ReferenceType rt = (ReferenceType) type;
    		Concept referencedConcept = rt.getConcept();
    		return referencedConcept.getConcreteSyntax().size() > 1;
    	}
    	
    	return false;
    }
    
    public String getMutuallyExclusiveElements(Property property) {
    	StringBuilder sb = new StringBuilder();
    	
    	Concept refConcept = getReferencedConcept(property);
    	
    	if(refConcept != null) {
     		if(refConcept.getConcreteSyntax().size() > 1) {
     			for(Notation n : refConcept.getConcreteSyntax()) {
     				sb.append(toFirstLetterUppercase(clearToken(n.toString())));
     				sb.append("|");
     			}
     			sb.deleteCharAt(sb.length()-1);
     		}
    	}
    	
    	return sb.toString();
    }
    
    //:: MyColors = {blue::Bool, white::Bool, yellow::Bool, green :: Bool}
    public String getNotMutuallyExclusiveElements(Property property) {
    	Type type = property.getType();
    	Concept refConcept = null;
    	
    	if (type instanceof SetType) {
    		SetType setType = (SetType) type;
    		Type componentType = setType.getComponentType();

    		if(componentType instanceof ReferenceType) {
    			ReferenceType refType = (ReferenceType) componentType;
    			
    			refConcept = refType.getConcept();
    		}
    		
			StringBuilder sb = new StringBuilder();
			
			for(Notation notation : refConcept.getConcreteSyntax()) {
				sb.append(clearToken(notation.toString()));
				sb.append("::Bool");
				sb.append(",");
			}
			//delete last ',' character
			sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
    	}
		
		return "";
    }
    
    private String clearToken(String string) {
    	return string.replace("Token: ", "");
    }
    
    private Concept getReferencedConcept(Property property) {
    	Type type = property.getType();
    	Concept refConcept = null;
    	
    	if(type instanceof ReferenceType) {
    		ReferenceType rt = (ReferenceType) type;
    		refConcept = rt.getConcept();
    	}
    	
    	return refConcept;
    }
    
    public List<Term> getMenuItemTerms() {
    	return getTermsOfComponentType(MenuItem.class);
    }
    
    public List<Term> getButtonTerms() {
    	return getTermsOfComponentType(JButton.class);
    }
    
    private List<Term> getTermsOfComponentType(Class type) {
    	List<Term> termsOfType = new ArrayList<Term>();
    	for(Term t : super.getDomainModel().getAllTerms()) {
    		if(t.getComponent() != null)
	    		if(type.isAssignableFrom(t.getComponent().getClass())) {
	    			termsOfType.add(t);
	    		}
    	}
    	
    	return termsOfType;
    }
}
