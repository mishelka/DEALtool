package gui.model.domain;

/**
 * This enumeration class represents the basic categories of components based on
 * what domain-relevant information can be extracted from them.
 */
public enum ComponentInfoType {
	/**
	 * A group including all functional components in the target application
	 * GUI. Functional components represent the basic functionalities of the
	 * application and they enable actions which change the application state.<br/>
	 * If we remove general functionalities such as "Save as..", "Open", "New"
	 * etc., we can extract application domain functionalities. <br/>
	 * Examples: buttons, web-links, menus, file choosers.
	 */
	FUNCTIONAL,
	/**
	 * A group including all informative and textual components in the target
	 * application GUI. <br/>
	 * Informative and textual components have the ability to display or acquire
	 * data or to determine a purpose of another components. <br/>
	 * Example: labels, spinner, text components (text areas, text fields,
	 * etc.), tree, graph, picture image
	 */
	INFORMATIVE_AND_TEXTUAL,
	/**
	 * A group including all logically grouping components. Logically grouping
	 * components are components which components or content related logically. <br/>
	 * Examples: combo box, list, radio button group, check box group, tree,
	 * tabbed pane
	 */
	LOGICALLY_GROUPING,
	/**
	 * A group including all graphically grouping components. Graphically
	 * grouping components related graphically (and/or logically). <br/>
	 * Examples: container, tabbed pane, menu, window, dialog, panels
	 */
	GRAPHICALLY_GROUPING,
	/**
	 * Custom components are components created by programmers. <br/>
	 * When a programmer creates a new custom component, he inserts new terms
	 * into the application language. <br/>
	 * The amount of domain information possible to extract from such a
	 * component depends on the component implementation. <br/>
	 * If the component is created incorrectly, then it is hard or impossible to
	 * extract domain information from it. To handle such components, new
	 * DomainIdentifiable handler has to be created.
	 */
	CUSTOM,
	/**
	 * If a component can not be included into any of these groups, then it is
	 * of information type UNKNOWN.
	 */
	UNKNOWN;
}
