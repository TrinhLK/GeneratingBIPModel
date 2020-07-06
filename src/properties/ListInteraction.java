package properties;

import java.util.ArrayList;

import connectors.Element;

public class ListInteraction {
	
	private ArrayList<Interaction> listInteraction;
	private ArrayList<ArrayList<Element>> listOfInteraction;
	private int kind;
	private int level;
	
	public ListInteraction() {
		// TODO Auto-generated constructor stub
		listInteraction = new ArrayList<Interaction>();
	}
	
//	public ListInteraction(ArrayList<Interaction> _listInteraction, int _kind) {
//		// TODO Auto-generated constructor stub
//		listInteraction = _listInteraction;
//		kind = _kind;
//	}
	
	public ListInteraction(ArrayList<ArrayList<Element>> _listOfInteraction, int _kind, int _level) {
		// TODO Auto-generated constructor stub
		listOfInteraction = _listOfInteraction;
		kind = _kind;
		level = _level; 
	}

	public ArrayList<Interaction> getListInteraction() {
		return listInteraction;
	}

	public void setListInteraction(ArrayList<Interaction> listInteraction) {
		this.listInteraction = listInteraction;
	}

	public int getKind() {
		return kind;
	}

	public void setKind(int kind) {
		this.kind = kind;
	}

	public ArrayList<ArrayList<Element>> getListOfInteraction() {
		return listOfInteraction;
	}

	public void setListOfInteraction(ArrayList<ArrayList<Element>> listOfInteraction) {
		this.listOfInteraction = listOfInteraction;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void print() {
		for (int i=0 ; i<listOfInteraction.size() ; i++) {
			System.out.println("lv: " + level + "\t" + listOfInteraction.get(i));
		}
	}
}
