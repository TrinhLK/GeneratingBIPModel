package properties;

import java.util.ArrayList;
import connectors.Element;

public class Interaction {

	private ArrayList<Element> listElement;
	private int id;
	
	public Interaction(ArrayList<Element> _listElement) {
		// TODO Auto-generated constructor stub
		listElement = _listElement;
	}

	public ArrayList<Element> getListElement() {
		return listElement;
	}

	public void setListElement(ArrayList<Element> listElement) {
		this.listElement = listElement;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void print() {
		String rs = "(>= (+";
		for (int i=0 ; i<listElement.size() ; i++) {
			rs += " " + listElement.get(i).getInstanceName() + "_" + listElement.get(i).getAction();
		}
		rs += ") " + listElement.size() + ")";
		System.out.println(rs);
	}
	
	public String printForProperties() {
		String rs = "\t\t\t(>= (+";
		for (int i=0 ; i<listElement.size() ; i++) {
			rs += " " + listElement.get(i).getInstanceName() + "_" + listElement.get(i).getListStates().get(0);
		}
		rs += ") " + listElement.size() + ")";
		return rs;
	}
}
