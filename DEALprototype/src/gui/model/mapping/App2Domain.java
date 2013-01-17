package gui.model.mapping;

import gui.model.application.Application;
import gui.model.domain.DomainModel;

import java.util.HashMap;

public class App2Domain {
	//TODO: VYMYSLIET TOTO!!!
	//1. mapovanie domainModel <-> appModel
	//2. mapovanie term <-> component
	//3. mapovanie postupnosti
	//	UiEvent <-> Term
	//	Sequence <-> Scene (vsetky alebo len startovacia scena?)
	//mozno by bolo dobre pouzit nieco ako HashMap alebo nieco co lahko udrziava dvojice komponentov
	HashMap<DomainModel, Application> models;
	
	//TODO dorobit a NEJDE CONTENT ASSIST V ECLIPSE!!!
}
