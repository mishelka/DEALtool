package gui.analyzer.aspects.yajco;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yajco.generator.parsergen.JavaccCompilerGenerator;
import yajco.model.Concept;
import yajco.model.Language;
import yajco.model.LocalVariablePart;
import yajco.model.Notation;
import yajco.model.NotationPart;
import yajco.model.Property;
import yajco.model.PropertyReferencePart;
import yajco.model.SkipDef;
import yajco.model.TokenDef;
import yajco.model.TokenPart;
import yajco.model.pattern.NotationPartPattern;
import yajco.model.pattern.impl.Enum;
import yajco.model.pattern.impl.Token;
import yajco.model.type.ListType;
import yajco.model.type.PrimitiveType;
import yajco.model.type.PrimitiveTypeConst;
import yajco.model.type.ReferenceType;
import yajco.model.type.SetType;
import yajco.model.type.Type;
import yajco.parser.ParserHelper;
import yajco.printer.Printer;

import gui.model.domain.ComponentInfoType;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;
import gui.tools.DomainModelGenerator;

public aspect YajcoAspect {

	pointcut modelCreation(): call(public* DomainModelGenerator.createDomainModel(..));

	after() returning (DomainModel model): modelCreation() {
		Map<Term, Concept> map = new HashMap<Term, Concept>();
		List<Concept> specialHelpConcepts = new ArrayList<Concept>();

		getMap(model.getRoot(), map);

		for (Term term : map.keySet()) {
			Concept concept = map.get(term);
			// System.out.println(">>>> " + term.getName() + " ::: "
			// + term.getComponentInfoType());
			List<NotationPart> notations = new ArrayList<NotationPart>();
			if (term.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
				// createEnumConcept(term, concept, specialHelpConcepts, true);
			} else if (term.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
				// createEnumConcept(term, concept, specialHelpConcepts, false);
			} else {
				notations.add(new TokenPart(getConceptName(term)));
				for (Term childTerm : getUsableChildren(term)) {
//					if (childTerm.getComponentInfoType() == ComponentInfoType.FUNCTIONAL) {
//						continue;
//					}
					if (childTerm.getRelation() == RelationType.MUTUALLY_EXCLUSIVE) {
						createEnumConcept(childTerm, concept, notations, map,
								specialHelpConcepts, true);
					} else if (childTerm.getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE) {
						createEnumConcept(childTerm, concept, notations, map,
								specialHelpConcepts, false);
					} else {
						Property property = new Property(
								getPropertyName(childTerm), new ReferenceType(
										map.get(childTerm), null));
						concept.addProperty(property);
						notations
								.add(new PropertyReferencePart(property, null));
					}
				}
				if (term.getComponentInfoType() == ComponentInfoType.DESCRIPTIVE) {
					Property property = new Property("innerValue",
							new PrimitiveType(PrimitiveTypeConst.STRING));
					concept.addProperty(property);
					PropertyReferencePart part = new PropertyReferencePart(
							property, null);
					part.addPattern(new Token("stringValue"));
					notations.add(part);
				}
				concept.addNotation(new Notation(notations
						.toArray(new NotationPart[] {})));
			}
		}

		final Language language = new Language(null);
		language.setName("deal");
		language.setSkips(new ArrayList<SkipDef>());
		language.setTokens(new ArrayList<TokenDef>());
		language.getTokens().add(
				new TokenDef("stringValue", "\\\"([^\\\"]*)\\\""));
		language.getSkips().add(new SkipDef("[ \\n\\t\\r]"));

		language.addConcept(map.get(model.getRoot()));

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
		properties.put("yajco.generateTools", "class, text");
		ParserHelper helper = new ParserHelper(new StringReader(""), properties);

		helper.generate("." + File.separatorChar + "src", language);

	}

	private void createEnumConcept(Term term, Concept concept,
			List<NotationPart> notationParts, Map<Term, Concept> map,
			List<Concept> specialHelpConcepts, boolean mutuallyExclusiveChilds) {
		Concept childConcept = map.containsKey(term) ? map.get(term) : new Concept(getConceptName(term));
		childConcept.addPattern(new Enum());
		//if (mutuallyExclusiveChilds) {
			for (Term childTerm : term.getChildren()) {
				NotationPart part = new TokenPart(getConceptName(childTerm));
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
		if (mutuallyExclusiveChilds) {
			propertyType = new ReferenceType(childConcept, null);
		} else {
			propertyType = new SetType(new ReferenceType(childConcept, null));
		}
		Property property = new Property(getPropertyName(term), propertyType);
		concept.addProperty(property);

		PropertyReferencePart notationPart = new PropertyReferencePart(
				property, null);
		// concept.addNotation(new Notation(new NotationPart[] { notationPart
		// }));
		notationParts.add(notationPart);
		//specialHelpConcepts.add(childConcept);
	}

	private String getPropertyName(Term term) {
		String original = getConceptName(term);
		return Character.toLowerCase(original.charAt(0))
				+ original.substring(1);
	}

	private String getConceptName(Term term) {
		if (term.getName() != null && !term.getName().isEmpty()) {
			Pattern regex = Pattern.compile("[A-Za-z ][\\w ]*");
			Matcher matcher = regex.matcher(term.getName());
			if (matcher.find()) {
				return matcher.group().replaceAll("[ ]", "_");
			}
		}
		return "unknown" + term.hashCode();
	}

	private String getConceptValuesName(Term term) {
		return getConceptName(term) + "Values";
	}

	private void getMap(Term term, Map<Term, Concept> map) {
		if (map == null) {
			throw new IllegalArgumentException("map argument cannot be null");
		}
		map.put(term, new Concept(getConceptName(term)));
		for (Term childTerm : getUsableChildren(term)) {
			getMap(childTerm, map);
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
		return !((term.getParent() != null && (term.getParent().getRelation() == RelationType.MUTUALLY_EXCLUSIVE || term
				.getParent().getRelation() == RelationType.MUTUALLY_NOT_EXCLUSIVE)) || term
				.getComponentInfoType() == ComponentInfoType.FUNCTIONAL);
	}

	private <T> List<List<T>> getPermutations(List<T> list) {
		List<List<T>> pList = new ArrayList<List<T>>();
		if (list.size() == 1) {
			pList.add(list);
			//pList.add(new ArrayList<T>());
		} else {
			List<List<T>> tempList = getPermutations(list.subList(1,
					list.size()));
			for (List<T> innerList : tempList) {
				pList.add(innerList);

				List<T> temp = new ArrayList<T>(innerList.size() + 1);
				temp.addAll(innerList);
				temp.add(list.get(0));
				pList.add(temp);
			}
		}
		return pList;
	}

}
