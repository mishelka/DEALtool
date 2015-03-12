package gui.generator.dsl;

import gui.analyzer.util.Logger;
import gui.analyzer.util.Util;
import gui.editor.DomainModelEditor;
import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.constraint.Constraint;
import gui.model.domain.constraint.DataType;
import gui.model.domain.constraint.DataTypeConstraint;
import gui.model.domain.relation.RelationType;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.Notation;
import yajco.model.NotationPart;
import yajco.model.Property;
import yajco.model.PropertyReferencePart;
import yajco.model.SkipDef;
import yajco.model.TokenDef;
import yajco.model.TokenPart;
import yajco.model.pattern.impl.Enum;
import yajco.model.pattern.impl.Token;
import yajco.model.type.PrimitiveType;
import yajco.model.type.PrimitiveTypeConst;
import yajco.model.type.ReferenceType;
import yajco.model.type.SetType;
import yajco.model.type.Type;
import yajco.parser.ParserHelper;
import yajco.printer.Printer;

public class YajcoGenerator {

	public static final String SRC_DIR = "src";// + File.separatorChar + "dsl";
	public static final String DSL_DIR = "src" + File.separatorChar + "dsl";
	private static Map<String,String> tokens = new HashMap<String,String>();
	
	private Language language;
	
	//static block
	{
		tokens.put("stringValue", "\\\"([^\\\"]*)\\\"");
		tokens.put("intValue", "([-]?[0-9]+)");
		tokens.put("realValue", "([-]?[0-9]+([.][0-9]+)?)");
//		tokens.put("dateValue", "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$");
	}

	public Language generateDSL(DomainModel model) {
		if(model.getRoot().isHidden()) {
			Logger.log("Model " + model.getName() + " hidden, doing nothing");
			return null; //hidden, do nothing with this model
		}
		
		Map<Term, Concept> map = new HashMap<Term, Concept>();
		HashSet<String> termNames = new HashSet<String>();
		List<Concept> specialHelpConcepts = new ArrayList<Concept>();

		getMap(model.getRoot(), map, termNames);

		for (Term term : map.keySet()) {
			Concept concept = map.get(term);
			List<NotationPart> notations = new ArrayList<NotationPart>();
			
			if (term.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
				// createEnumConcept(term, concept, specialHelpConcepts, true);
			} else if (term.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				// createEnumConcept(term, concept, specialHelpConcepts, false);
			} else {
				notations.add(new TokenPart(getConceptName(term, termNames)));
				for (Term childTerm : getUsableChildren(term)) {
					if(!childTerm.isHidden()) {
						if (childTerm.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
							createEnumConcept(childTerm, concept, notations, map,
									specialHelpConcepts, true, termNames);
						} else if (childTerm.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
							createEnumConcept(childTerm, concept, notations, map,
									specialHelpConcepts, false, termNames);
						} else {
							Property property = new Property(
									getPropertyName(childTerm, termNames), new ReferenceType(
											map.get(childTerm), null));
							concept.addProperty(property);
							notations
									.add(new PropertyReferencePart(property, null));
						}
					}
				}
				List<Constraint> constraints = term.getConstraints();
				DataType dataType = null;
				for(Constraint c : constraints) {
					if(c instanceof DataTypeConstraint) {
						DataTypeConstraint dtc = (DataTypeConstraint) c;
						dataType = dtc.getType();
					}
				}
				
				if(dataType != null) {
//				if (term.getComponentInfoType() == ComponentInfoType.TEXTUAL) {
					Property property;
					Token token = null;
					switch(dataType) {
//						case STRING:
//							Logger.log("STRING!!!!!!!!!!!!!!!!!!!!!");
//							property = new Property("innerValue",
//								new PrimitiveType(PrimitiveTypeConst.STRING));
//							token = new Token("stringValue"); 
//						break;
						case NUMERIC:
//							Logger.log("NUMERIC!!!!!!!!!!!!!!!!!!!!!");
							property = new Property("innerValue",
								new PrimitiveType(PrimitiveTypeConst.INTEGER));
							token = new Token("intValue");
						break;
						case REAL: property = new Property("innerValue",
								new PrimitiveType(PrimitiveTypeConst.REAL));
							token = new Token("realValue");
						break;
//						case DATE: property = new Property("innerValue",
//								new PrimitiveType(PrimitiveTypeConst.DATE));
						default: // STRING or anything else
							property = new Property("innerValue",
								new PrimitiveType(PrimitiveTypeConst.STRING));
							token = new Token("stringValue");
					}
					concept.addProperty(property);
					PropertyReferencePart part = new PropertyReferencePart(
							property, null);
					part.addPattern(token);
					notations.add(part);
				}
				concept.addNotation(new Notation(notations
						.toArray(new NotationPart[] {})));
			}
		}

		language = new Language(null);
		String languageName = getLanguageName(model);
		
		language.setName("dsl." + languageName);
		
		language.setSkips(new ArrayList<SkipDef>());
		language.setTokens(new ArrayList<TokenDef>());
		List<TokenDef> tokenDefs = language.getTokens(); 
		for (Entry<String, String> token : tokens.entrySet()) {
			tokenDefs.add(new TokenDef(token.getKey(), token.getValue()));
		}
		language.getSkips().add(new SkipDef("[ \\n\\t\\r]"));

		language.addConcept(map.get(model.getRoot()));
		language.getSettingsProperties().put("yajco.mainNode", map.get(model.getRoot()).getName());

		for (Concept concept : map.values()) {
			if (!concept.equals(map.get(model.getRoot()))) {
				language.addConcept(concept);
			}
		}
		for (Concept concept : specialHelpConcepts) {
			language.addConcept(concept);
		}

		final Printer printer = new Printer();
		printer.printLanguage(new PrintWriter(System.out), language);
		
		Properties properties = new Properties();
		properties.put("yajco.generateTools", "class");//, text"); //if I want to generate *.lang, uncomment this: "class, text"
		ParserHelper helper = new ParserHelper(new StringReader(""), properties);

		helper.generate("." + File.separatorChar + SRC_DIR, language);
		
		return language;
	}//end advice
	
	private String getLanguageName(DomainModel model) {
		String languageName = model.getName();
		
		if(languageName == null) {
			languageName = "DealLanguage" + DomainModelEditor.getInstance().getDomainModels().indexOf(model);
		}
		
		Logger.logError(languageName);
		
		languageName = Util.removeBadCharacters(languageName);
		languageName = languageName.replaceAll("-", "");
		languageName = languageName.replaceAll("\\s+", "");
		
		return languageName;
	}

	private void createEnumConcept(Term term, Concept concept,
			List<NotationPart> notationParts, Map<Term, Concept> map,
			List<Concept> specialHelpConcepts, boolean mutuallyExclusiveChilds, HashSet<String> termNames) {
		Concept childConcept = map.containsKey(term) ? map.get(term) : new Concept(getConceptName(term, termNames));
		childConcept.addPattern(new Enum());
		//if (mutuallyExclusiveChilds) {
			for (Term childTerm : term.getChildren()) {
				NotationPart part = new TokenPart(getConceptName(childTerm, termNames));
				childConcept.addNotation(new Notation(
						new NotationPart[] { part }));
			}
//		} else {
//			for (List<Term> terms : getPermutations(term.getChildren())) {
//				Notation notation = new Notation(new NotationPart[]{});
//				for (Term childTerm : terms) {
//					NotationPart part = new TokenPart(getConceptName(childTerm));
//					notation.addPart(part);
//				}
//				childConcept.addNotation(notation);
//			}
//		}

		// main
		Type propertyType;
		if(mutuallyExclusiveChilds) {
			propertyType = new ReferenceType(childConcept, null);
		} else {
			propertyType = new SetType(new ReferenceType(childConcept, null));
		}
		Property property = new Property(getPropertyName(term, termNames), propertyType);
		concept.addProperty(property);

		PropertyReferencePart notationPart = new PropertyReferencePart(
				property, null);
		// concept.addNotation(new Notation(new NotationPart[] { notationPart
		// }));
		notationParts.add(notationPart);
		//specialHelpConcepts.add(childConcept);
	}

	private String getPropertyName(Term term, HashSet<String> termNames) {
		String original = getConceptName(term, termNames);
		return Character.toLowerCase(original.charAt(0))
				+ original.substring(1);
	}

	private String getConceptName(Term term, HashSet<String> termNames) {
		String name = term.getName();
		if (!Util.isEmpty(name)) {
			name = name.trim();
			name = name.replaceAll("-", "");
			if (!name.isEmpty()) {
				Pattern regex = Pattern.compile("[A-Za-z ][\\w ]*");
				Matcher matcher = regex.matcher(name);
				if (matcher.find()) {
					name = matcher.group().replaceAll("[ ]", "_");
					if(JavaKeywords.isJavaKeyword(name)) {
						name = name + "_";
					}
					while(!termNames.add(name)) {
						name = name + "_";
					}
					return name;
				}
			}
		}
		return "Unknown" + term.hashCode();
	}

	private void getMap(Term term, Map<Term, Concept> map, HashSet<String> termNames) {
		if (map == null) {
			throw new IllegalArgumentException("Map argument cannot be null.");
		}
		if(!term.isHidden()) {
			map.put(term, new Concept(getConceptName(term, termNames)));
		}
		for (Term childTerm : getUsableChildren(term)) {
			getMap(childTerm, map, termNames);
		}
	}

	private List<Term> getUsableChildren(Term term) {
		List<Term> list = new ArrayList<Term>();
		for (Term childTerm : term.getChildren()) {
			if (testTermUsage(childTerm)) {
				list.add(childTerm);
			}
		}
		return list;
	}

	private boolean testTermUsage(Term term) {
		return !(
					term.getParent() != null &&
					(
					term.getParent().getRelation() == RelationType.MUTUALLY_EXCLUSIVE 
					|| term.getParent().getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE
					)
				) && (
						!DomainModelEditor.getInstance().getSetting().isExtractFunctionalComponents() &&
						term.getComponentInfoType() != ComponentInfoType.FUNCTIONAL
				);
	}	

	public Language getLanguage() {
		return language;
	}
}
