package properties;

import java.util.ArrayList;
import java.util.HashMap;

import connectors.Element;

public class ListElement {

	private ArrayList<Element> listElements;
	
	public ListElement(ArrayList<Element> _listElements) {
		// TODO Auto-generated constructor stub
		listElements = _listElements;
	}

	/**
	 * get all blocks At the given Level
	 * */
	public ArrayList<String> getAllBlocksAtLevel(int lv){
		ArrayList<Element> elemsAtLv = getElementsAtLevel(lv);
		ArrayList<String> blocksName = new ArrayList<String>();
		HashMap<String, String> blocks = new HashMap<String, String>();
		
		for (int i=0 ; i<elemsAtLv.size() ; i++) {
			blocks.put(elemsAtLv.get(i).getLocation(), elemsAtLv.get(i).getContent());
		}
		
		for (HashMap.Entry<String, String> entry : blocks.entrySet()) {
			blocksName.add(entry.getKey());
		}
		return blocksName;
	}
	
	/**
	 * get all elements in the same block with given block's name
	 * */
	public ArrayList<Element> getElementsInSameBlock(String blocksName){
		
		ArrayList<Element> rs = new ArrayList<Element>();
		
		for (Element e : listElements) {
			if (e.getLocation().equals(blocksName)) {
				rs.add(e);
			}
		}
		
		return rs;
	}
	
	/**
	 * get all elements At Level
	 * */
	public ArrayList<Element> getElementsAtLevel(int lv) {
		
		ArrayList<Element> rs = new ArrayList<Element>();
		
		for (Element e : listElements) {
			if (e.getLevel() == lv) {
				rs.add(e);
			}
		}
		return rs;
	}
	
	/**
	 * get max level of this list
	 * */
	public int getMaxLevel() {
		int maxlv = 0;
		for (Element e : listElements) {
			if (maxlv < e.getLevel()) {
				maxlv = e.getLevel();
			}
		}
		return maxlv;
	}
	
	public ArrayList<Element> getListElements() {
		return listElements;
	}

	public void setListElements(ArrayList<Element> listElements) {
		this.listElements = listElements;
	}
	
}
