package temp.domain.io;

import gui.analyzer.util.Logger;
import gui.model.domain.DomainModel;
import gui.model.domain.Term;
import gui.model.domain.relation.RelationType;

import java.awt.Point;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.soap.Node;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import temp.domain.io.helpers.AbstractDomainModelReader;
import temp.domain.io.helpers.UnsupportedModelException;


/**
 * TODO: not working yet
 * Toto je zle spravene, je potrebne spravit jedinecne cislo alebo identifikator pre kazdy term.
 * Tento identifikator bude do pamate ulozeny ako id, nasledne sa pomocou neho aj obnovi vsetko z pamate.
 * Ulozeny domenovy model sice bude mozne editovat, ale uz tam nebude referencia na komponenty.
 */
public class XmlDomainModelReader extends AbstractDomainModelReader {
	public XmlDomainModelReader(DomainModel domainModel) {
		setDomainModel(domainModel);
	}

	/**
	 * A kind of mind for the hirachy of the xml model
	 */
	private Stack<String[]> parentStack = new Stack<String[]>();
	/**
	 * A kind of mind for the hirachy of the xml contraint model
	 */
	private LinkedList<LinkedList<Node>> ruleTemp = new LinkedList<LinkedList<Node>>();
	/**
	 * A list which will be filled with the term names in their appropriate
	 * order
	 */
	private ArrayList<String> termOrderList = new ArrayList<String>();
	private static final String[] validTagsStruct = { "and", "or", "alt",
			"term", "direct-alt", "direct-or" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * temp.domain.io.helpers.AbstractDomainModelReader#parseInputStream
	 * (java.io.InputStream)
	 */
	protected boolean isInArray(String str, String[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(str))
				return true;
		}
		return false;
	}

	/*
	 * synchronized should prevent the
	 * "NullPointer when saving the domain model"
	 */
	@Override
	protected synchronized void parseInputStream(InputStream inputStream)
			throws UnsupportedModelException {
		termOrderList.clear();

		domainModel.reset();

		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory
					.createXMLEventReader(inputStream);

			// mode: 0 = start; 1 = struct; 2 = constraints; 3 = comments; 4 =
			// term`Order
			int mode = 0;
			ruleTemp.clear();
			ruleTemp.add(new LinkedList<Node>());
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement currentStartTag = event.asStartElement();
					String currentTag = currentStartTag.getName()
							.getLocalPart();

					if (mode == 1) {
						if (!isInArray(currentTag, validTagsStruct)) {
							throw new UnsupportedModelException(
									"'"
											+ currentTag
											+ "' is not a valid tag in struct-section.",
									event.getLocation().getLineNumber());
						}
						// BEGIN XML-reader is reading information about the
						// terms
						boolean isMandatory = false;
						Point termLocation = null;
						String attrName = "noname";
						String attrDesc = "nodesc";
						String parent = parentStack.peek()[1];

						@SuppressWarnings("unchecked")
						Iterator<Attribute> attributes = currentStartTag
								.getAttributes();

						// BEGIN read attributes from XML tag
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							String curName = attribute.getName().getLocalPart();
							String curValue = attribute.getValue();
							if (curName.equals("name"))
								attrName = curValue;
							else if (curName.equals("description"))
								attrDesc = curValue;
							else if (curName.equals("mandatory"))
								isMandatory = curValue.equals("true");
							else if (curName.equals("abstract")) {
							} else if (curName.equals("coordinates")) {
								String subStringX = curValue.substring(0,
										curValue.indexOf(", "));
								String subStringY = curValue.substring(curValue
										.indexOf(", ") + 2);
								try {
									termLocation = new Point(
											Integer.parseInt(subStringX),
											Integer.parseInt(subStringY));
								} catch (Exception e) {
									throw new UnsupportedModelException(
											e.getMessage()
													+ "is no valid Integer Value",
											event.getLocation().getLineNumber());
								}
							} else {
								throw new UnsupportedModelException("'"
										+ curName
										+ "' is not a valid attribute.", event
										.getLocation().getLineNumber());
							}
						}
						// END read attributes from XML tag

						if (!domainModel.getTermNames().contains(attrName))
							addTerm(attrName, attrDesc, isMandatory, parent,
									termLocation);
						else
							throw new UnsupportedModelException(
									"Cannot redefine '" + attrName + "'", event
											.getLocation().getLineNumber());

						if (!"ternm".equals(currentTag)) {
							parentStack.push(new String[] { currentTag,
									attrName });
							// END XML-reader is reading information about the
							// terms
						}

					} else if (mode == 2 || mode == 3) {
					} else if (mode == 4) {
						if (currentTag.equals("term")) {
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = currentStartTag
									.getAttributes();

							// BEGIN read attributes from XML tag
							while (attributes.hasNext()) {
								Attribute attribute = attributes.next();
								String curName = attribute.getName()
										.getLocalPart();
								String curValue = attribute.getValue();

								if (currentTag.equals("term")
										&& curName.equals("name")
										&& domainModel.getTermNames()
												.contains(curValue)) {
									termOrderList.add(curValue);
								}
							}
						} else {
							throw new UnsupportedModelException(
									"'"
											+ currentTag
											+ "' is not a valid tag in termOrder-section.",
									event.getLocation().getLineNumber());
						}
					} else {
						if (currentTag.equals("domainModel"))
							;
						else if (currentTag.equals("struct")) {
							parentStack
									.push(new String[] { currentTag, "root" });
							mode = 1;
						} else if (currentTag.equals("constraints"))
							;
						else if (currentTag.equals("comments"))
							;
						else if (currentTag.equals("termOrder"))
							mode = 4;
					}
				}
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();

					String currentTag = endElement.getName().getLocalPart();
					if (mode == 1) {
						if (!currentTag.equals("term"))
							if (parentStack.peek()[0].equals(currentTag))
								parentStack.pop();

						if (currentTag.equals("struct"))
							mode = 0;
					} else if (mode == 2) {
						if (currentTag.equals("constraints"))
							mode = 0;
						if (currentTag.equals("rule"))
							if (!ruleTemp.isEmpty())
								if (!ruleTemp.getFirst().isEmpty()) {
									ruleTemp.clear();
									ruleTemp.add(new LinkedList<Node>());
								}
						if (currentTag.equals("conj"))
							;
						if (currentTag.equals("atrmost1"))
							;
						if (currentTag.equals("disj"))
							;
						if (currentTag.equals("imp"))
							;
						if (currentTag.equals("eq"))
							;
						if (currentTag.equals("not"))
							;
					} else if (mode == 3)
						if (currentTag.equals("comments"))
							mode = 0;
						else if (mode == 4)
							if (currentTag.equals("termOrder"))
								// domainModel.setTermOrderList(termOrderList);
								mode = 0;
				}
			}
			eventReader.close();
		} catch (XMLStreamException e) {
			throw new UnsupportedModelException(e.getMessage(), e.getLocation()
					.getLineNumber());
		}
		// Update the DomainModel in Editor
//		domainModel.handleModelDataLoaded();
	}

	/**
	 * Create a new term and add it to the domainModel.
	 * 
	 * @param termName
	 *            String with the name of the term
	 * @param isMandatory
	 *            boolean, true it the term is mandatory
	 * @param isAbstract
	 *            boolean, true if the term is abstract
	 * @param parent
	 */
	private void addTerm(String termName, String termDescription,
			boolean isMandatory, String parent, Point location) {
		/*
		 * HOWTO: add a child to the DomainModel
		 * 
		 * first: create an Term
		 * second: set flags like mandatory and
		 * abstract third: add the Term to the TermModel 
		 * last: get the parent of the current Term and add the current Term as a child
		 * of this parent (Term)
		 * 
		 * Note: addChild DOESN'T ADD THE TERM!
		 */
		Term term = null;
		if (parent.equals(domainModel.getRoot())) {
			term = new Term(domainModel, termName);
			domainModel.setRoot(term);
		} else {
			term = new Term(domainModel, termName);
			term.setDescription(termDescription);
			domainModel.addTerm(term);
			String temp = parentStack.peek()[0];
			if(!domainModel.getTerm(parent).isEmpty()) {
			Term t = domainModel.getTerm(parent).get(0);
				if (temp.equals("and"))
					t.setRelation(RelationType.AND);
				if (temp.equals("or"))
					t.setRelation(RelationType.MUTUALLY_EXCLUSIVE);
				else {
					Logger.log(domainModel + " "
							+ t + " " + parent);
					t.setRelation(RelationType.MUTUALLY_NOT_EXCLUSIVE);
				}
				t.addChild(term);
			}
		}
		if (location != null && term != null) {
//			DomainModel.setTermLocation(location, term);
		}
	}
}
