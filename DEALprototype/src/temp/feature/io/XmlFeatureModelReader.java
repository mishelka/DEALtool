package temp.feature.io;

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

import temp.feature.io.helpers.AbstractFeatureModelReader;
import temp.feature.io.helpers.UnsupportedModelException;


public class XmlFeatureModelReader extends AbstractFeatureModelReader {
	public XmlFeatureModelReader(DomainModel featureModel) {
		setFeatureModel(featureModel);
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
	 * A list which will be filled with the featureNames in their appropriate
	 * order
	 */
	private ArrayList<String> featureOrderList = new ArrayList<String>();
	private static final String[] validTagsStruct = { "and", "or", "alt",
			"feature", "direct-alt", "direct-or" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.ovgu.featureide.fm.core.io.AbstractFeatureModelReader#parseInputStream
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
	 * "NullPointer when saving the feature model" see ticket: #277
	 */
	@Override
	protected synchronized void parseInputStream(InputStream inputStream)
			throws UnsupportedModelException {
		featureOrderList.clear();

		featureModel.reset();

		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = inputFactory
					.createXMLEventReader(inputStream);

			// mode: 0 = start; 1 = struct; 2 = constraints; 3 = comments; 4 =
			// featureOrder
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
						// features
						boolean isMandatory = false;
						Point featureLocation = null;
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
									featureLocation = new Point(
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

						if (!featureModel.getTermNames().contains(attrName))
							addFeature(attrName, attrDesc, isMandatory, parent,
									featureLocation);
						else
							throw new UnsupportedModelException(
									"Cannot redefine '" + attrName + "'", event
											.getLocation().getLineNumber());

						if (!"feature".equals(currentTag)) {
							parentStack.push(new String[] { currentTag,
									attrName });
							// END XML-reader is reading information about the
							// features
						}

					} else if (mode == 2 || mode == 3) {
					} else if (mode == 4) {
						if (currentTag.equals("feature")) {
							@SuppressWarnings("unchecked")
							Iterator<Attribute> attributes = currentStartTag
									.getAttributes();

							// BEGIN read attributes from XML tag
							while (attributes.hasNext()) {
								Attribute attribute = attributes.next();
								String curName = attribute.getName()
										.getLocalPart();
								String curValue = attribute.getValue();

								if (currentTag.equals("feature")
										&& curName.equals("name")
										&& featureModel.getTermNames()
												.contains(curValue)) {
									featureOrderList.add(curValue);
								}
							}
						} else {
							throw new UnsupportedModelException(
									"'"
											+ currentTag
											+ "' is not a valid tag in featureOrder-section.",
									event.getLocation().getLineNumber());
						}
					} else {
						if (currentTag.equals("featureModel"))
							;
						else if (currentTag.equals("struct")) {
							parentStack
									.push(new String[] { currentTag, "root" });
							mode = 1;
						} else if (currentTag.equals("constraints"))
							;
						else if (currentTag.equals("comments"))
							;
						else if (currentTag.equals("featureOrder"))
							mode = 4;
					}
				}
				if (event.isEndElement()) {
					EndElement endElement = event.asEndElement();

					String currentTag = endElement.getName().getLocalPart();
					if (mode == 1) {
						if (!currentTag.equals("feature"))
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
							if (currentTag.equals("featureOrder"))
								// featureModel.setFeatureOrderList(featureOrderList);
								mode = 0;
				}
			}
			eventReader.close();
		} catch (XMLStreamException e) {
			throw new UnsupportedModelException(e.getMessage(), e.getLocation()
					.getLineNumber());
		}
		// Update the FeatureModel in Editor
//		featureModel.handleModelDataLoaded();
	}

	/**
	 * Create a new feature and add it to the featureModel.
	 * 
	 * @param featureName
	 *            String with the name of the feature
	 * @param isMandatory
	 *            boolean, true it the feature is mandatory
	 * @param isAbstract
	 *            boolean, true if the feature is abstract
	 * @param parent
	 *            String with the name of the parent feature
	 */
	private void addFeature(String featureName, String featureDescription,
			boolean isMandatory, String parent, Point location) {
		/*
		 * HOWTO: add a child to the FeaturModel
		 * 
		 * first: create an Feature second: set flags like mandatory and
		 * abstract third: add the Feature to the FeatureModel last: get the
		 * parent of the current Feature and add the current Feature as a child
		 * of this parent (Feature)
		 * 
		 * Note: addChild DOESN'T ADD THE FEATURE!
		 */
		Term feature = null;
		if (parent.equals(featureModel.getRoot())) {
			feature = featureModel.getTerm(featureName);
		} else {
			feature = new Term(featureModel, featureName);
			feature.setDescription(featureDescription);
			featureModel.addTerm(feature);
			String temp = parentStack.peek()[0];
			if (temp.equals("and"))
				featureModel.getTerm(parent).setRelation(RelationType.AND);
			if (temp.equals("or"))
				featureModel.getTerm(parent).setRelation(RelationType.MUTUALLY_EXCLUSIVE);
			else {
				Logger.log(featureModel + " "
						+ featureModel.getTerm(parent) + " " + parent);
				featureModel.getTerm(parent)
						.setRelation(RelationType.MUTUALLY_NOT_EXCLUSIVE);
			}
			featureModel.getTerm(parent).addChild(feature);
		}
		if (location != null && feature != null) {
//			DomainModel.setTermLocation(location, feature);
		}
	}
}
