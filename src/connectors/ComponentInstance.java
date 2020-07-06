package connectors;

import java.util.ArrayList;
import java.util.HashMap;

public class ComponentInstance {

	private ArrayList<AnInstance> listInstance;
	private HashMap<String, ArrayList<String>> mapInstance;
	
	public ComponentInstance() {
		// TODO Auto-generated constructor stub
		listInstance = new ArrayList<AnInstance>();
		mapInstance = new HashMap<String, ArrayList<String>>();
	}
	
	public void add(String type, String value) {
		if (!isExist(type, value)) {
			listInstance.add(new AnInstance(type, value));
		}
		if (mapInstance.containsKey(type)) {
			boolean flag = false;
			ArrayList<String> myValues = mapInstance.get(type);
			for (String v : myValues) {
				if (v.equals(value)) {
					flag = true;
					break;
				}
			}
			if (!flag) myValues.add(value);
		} else {
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(value);
			mapInstance.put(type, temp);
		}
	}
	
	public void print() {
		for (AnInstance i : listInstance) {
			System.out.println(i.getType() + "\t" + i.getValue());
		}
		
		System.out.println("Hash map:\t" + mapInstance);
		for (HashMap.Entry<String, ArrayList<String>> entry : mapInstance.entrySet()) {
		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		}
	}
	
	public boolean isExist(String type, String value) {
		
		for (AnInstance i : listInstance) {
			if (type.equals(i.getType()) && value.equals(i.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<String> getAllInstancesOf(String name){
		ArrayList<String> rs = new ArrayList<String>();
		for (AnInstance ai : listInstance) {
			if (ai.getType().equals(name)) {
				rs.add(ai.getValue());
			}
		}
		return rs;
	}
	
//	public ArrayList<String> createListInstanceAnnotation(String singleAnno) {
//		ArrayList<String> listAnnotationInstance = new ArrayList<String>();
//		
//		ArrayList<String> result = new ArrayList<String>();
//		for (HashMap.Entry<String, ArrayList<String>> entry : mapInstance.entrySet()) {
////		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		    if (singleAnno.contains(entry.getKey())) {
//		    	if (listAnnotationInstance.size() == 0) {
//		    		for (String value : entry.getValue()) {
//		    			String temp = singleAnno.replaceAll(entry.getKey(), value);
//			    		listAnnotationInstance.add(temp);
//		    		}
//		    	} else {
//		    		for (String value : entry.getValue()) {
//		    			ArrayList<String> tempList = (ArrayList<String>) listAnnotationInstance.clone();
//		    			for (String elem : tempList) {
//		    				String temp = elem.replaceAll(entry.getKey(), value);
//		    				result.add(temp);
//		    			}
//		    		}
//		    	}
//		    }
//		}
//		return result;
//	}
		
	public ArrayList<String> createListInstanceAnnotation(String singleAnno) {
		ArrayList<String> listAnnotationInstance = new ArrayList<String>();
		boolean shouldRun = false;
		ArrayList<String> result = new ArrayList<String>();
		
		//If size == 0
		if (listAnnotationInstance.size() == 0) {
			for (HashMap.Entry<String, ArrayList<String>> entry : mapInstance.entrySet()) {
				if (singleAnno.contains(entry.getKey())) {
					for (String value : entry.getValue()) {
						String temp = singleAnno.replaceAll(entry.getKey(), value);
//						System.out.println("1st: " + entry.getKey() + "\t" + value);
						listAnnotationInstance.add(temp);
					}
					break;
				}
			}
			shouldRun = true;
		}
		
		while (shouldRun) {

			for (int i=0 ; i<listAnnotationInstance.size() ; i++) {
				for (HashMap.Entry<String, ArrayList<String>> entry : mapInstance.entrySet()) {
					if (listAnnotationInstance.get(i).contains(entry.getKey())) {
						if (entry.getValue().size() == 1) {
							String temp = listAnnotationInstance.get(i).replaceAll(entry.getKey(), entry.getValue().get(0));
							listAnnotationInstance.set(i, temp);
						}else {
							String temp = listAnnotationInstance.get(i).replaceAll(entry.getKey(), entry.getValue().get(0));
							listAnnotationInstance.set(i, temp);
							for (int j=1 ; j<entry.getValue().size() ; j++) {
								String temp_j = listAnnotationInstance.get(i).replaceAll(entry.getValue().get(0), entry.getValue().get(j));
								listAnnotationInstance.add(temp_j);
							}
						}
					}
				}
			}

			boolean completed = true;
			for (HashMap.Entry<String, ArrayList<String>> entry : mapInstance.entrySet()) {
				if (listAnnotationInstance.get(0).contains(entry.getKey())) {

					completed = false;
					break;
				}
			}
			if (completed) shouldRun = false;
		}
		result = (ArrayList<String>) listAnnotationInstance.clone();
		return result;
	}
	
	public ArrayList<AnInstance> getListInstance() {
		return listInstance;
	}

	public void setListInstance(ArrayList<AnInstance> listInstance) {
		this.listInstance = listInstance;
	}

	public HashMap<String, ArrayList<String>> getMapInstance() {
		return mapInstance;
	}

	public void setMapInstance(HashMap<String, ArrayList<String>> mapInstance) {
		this.mapInstance = mapInstance;
	}

}

class AnInstance{
	private String type;
	private String value;
	
	public AnInstance(String _type, String _value) {
		// TODO Auto-generated constructor stub
		type = _type;
		value = _value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
