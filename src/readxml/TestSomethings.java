package readxml;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

public class TestSomethings {
    
	public ArrayList<String> splitBrackets(String input) {
		String result[] = input.split("[\\(||\\)]");
		ArrayList<String> rs = new ArrayList<String>();
		for (String s: result) {
			s = s.trim();
			if (s.length() > 0)
				rs.add(s);
		}
		
		return rs;
	}
    // Print all subsets of given list
    public ArrayList<ArrayList<String>> getAllSubsets(ArrayList<String> example) { 
    	
    	ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    	int n = example.size(); 
    	
    	// Run a loop for printing all 2^n 
    	// subsets one by one 
		for (int i = 0; i < (1<<n); i++) { 
			ArrayList<String> subset = new ArrayList<String>();
			String temp = "";
//			System.out.print("{ ");  
			// Print current subset 
			for (int j = 0; j < n; j++) {
				// (1<<j) is a number with jth bit 1 
				// so when we 'and' them with the 
				// subset number we get which numbers 
				// are present in the subset and which 
				// are not 
				if ((i & (1 << j)) > 0) {
					subset.add(example.get(j));
					temp += example.get(j);
//					System.out.print(temp + " "); 
				}
			}
			
			result.add(subset);
//            System.out.println("}"); 
		} 
		
		return result;
	} 
    
    public ArrayList<ArrayList<String>> mergeTwoList(ArrayList<ArrayList<String>> _trigger, ArrayList<ArrayList<String>> _synch) {
    	ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    	
    	System.out.println(_synch.size() + "\t" + _trigger.size());
    	if (_trigger.size() > 0) {
    		for (int i=0 ; i<_synch.size() ; i++) {
        		
        		ArrayList<String> synch_i = _synch.get(i);
        		for (int j=0 ; j<_trigger.size() ; j++) {
        			ArrayList<String> mergeList = new ArrayList<String>();
        			ArrayList<String> trigger_j = _trigger.get(j);
        			
        			for(String t : trigger_j) {
        				mergeList.add(t);
        			}
        			
        			for(String s : synch_i) {
        				mergeList.add(s);
        			}
        			result.add(mergeList);
        		}      		
        	}
    		
    	}else {
    		ArrayList<String> mergeList = new ArrayList<String>();
    		int max = _synch.get(0).size();
    		int indexMax = 0;
    		
    		for (int i=0 ; i<_synch.size() ; i++) {
    			if (max < _synch.get(i).size()) {
    				max = _synch.get(i).size();
    				indexMax = i;
    			}
    		}
    		
    		for (int i=0 ; i<_synch.get(indexMax).size() ; i++) {
    			mergeList.add(_synch.get(indexMax).get(i));
    		}
    		
    		result.add(mergeList);
    	}
    	
    	
    	return result;
    }
    
    public ArrayList<ArrayList<String>> removeEmpty(ArrayList<ArrayList<String>> _trigger){
    	ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
    	
    	for (int i=0 ; i<_trigger.size() ; i++) {
    		if (_trigger.get(i).size() != 0) {
    			result.add(_trigger.get(i));
    		}
    	}
    	
    	return result;
    }
    
    public HashMap<String, ArrayList<String>> createTestHashMapData() {
    	HashMap<String, ArrayList<String>> listComponent = new HashMap<String, ArrayList<String>>();
    	ArrayList<String> tomcats = new ArrayList<String>();
    	tomcats.add("t0");
    	tomcats.add("t1");
    	tomcats.add("t2");
    	
    	ArrayList<String> apaches = new ArrayList<String>();
    	apaches.add("as0");
    	apaches.add("as1");
    	
    	ArrayList<String> mysql = new ArrayList<String>();
    	mysql.add("m0");
    	
    	ArrayList<String> apps = new ArrayList<String>();
    	apps.add("la0");
    	apps.add("la1");
    	apps.add("la2");
    	apps.add("la3");
    	
    	listComponent.put("Tomcat", tomcats);
    	listComponent.put("Apache", apaches);
    	listComponent.put("MySQL", mysql);
    	listComponent.put("Apps", apps);
    	
    	return listComponent;
    }
    
    public void createConcreteData(String inputStr, HashMap<String, ArrayList<String>> listComponent) {
    	HashMap<String, String> oldAndNew = new HashMap<String, String>();
    	
    	ArrayList<String> listElems = splitBrackets(inputStr);
    	
    	for (String elems : listElems) {
    		String tempSplit[] = elems.split(" ");
        	String input = "";
        	for (String s : tempSplit) {
        		if (s.contains(".")) {
        			input = s;
        		}
        	}
        	//find key of listComponent in input, if existed, replaced by list value, save them into oldAndNew
        	for (HashMap.Entry<String, ArrayList<String>> entry : listComponent.entrySet()) {
//    		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
    		    if (input.contains(entry.getKey())){
    		    	String tempRs = "";
    		    	if (entry.getValue().size() == 1) {
    		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(entry.getValue().size()-1));
    		    	} else {
    		    		tempRs += "(+ ";
    		    		for (int i=0 ; i<entry.getValue().size() ; i++) {
        		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(i)) + " ";
        		    	}
    		    		tempRs += ")";
    		    	}
//    		    	for (int i=0 ; i<entry.getValue().size()-1 ; i++) {
//    		    		tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(i)) + " + ";
//    		    	}
//    		    	tempRs += input.replaceAll(entry.getKey(), entry.getValue().get(entry.getValue().size()-1));
//    		    	
    		    	System.out.println(input + "\t->\t" + tempRs);
    		    	oldAndNew.put(input, tempRs);
    		    }
    		}
    	}
    	
    	for (HashMap.Entry<String, String> entry : oldAndNew.entrySet()) {
    		if (inputStr.contains(entry.getKey())) {
    			inputStr = inputStr.replaceAll(entry.getKey(), entry.getValue());
    		}
    	}
    	System.out.println(inputStr);
    }
    // Driver code 
    public static void main(String[] args) 
    { 
    	TestSomethings test = new TestSomethings();
    	HashMap<String, ArrayList<String>> listComponent = test.createTestHashMapData();
    	test.createConcreteData("or (!(>= Apache.Active 1) (>= Tomcat.Active 1))", listComponent);
//    	ArrayList<String> example1 = new ArrayList<String>();
//    	ArrayList<String> example2 = new ArrayList<String>();
//    	ArrayList<ArrayList<String>> result1 = new ArrayList<ArrayList<String>>();
//    	ArrayList<ArrayList<String>> result2 = new ArrayList<ArrayList<String>>();
//    	ArrayList<ArrayList<String>> merge = new ArrayList<ArrayList<String>>();
//    	example1.add("p");
//    	example2.add("q");
//    	example2.add("r");
//    	example2.add("t");
//    	result1 = test.removeEmpty(test.getAllSubsets(example1));
//    	result2 = test.getAllSubsets(example2);
//    	merge = test.mergeTwoList(result1, result2);
//    	
//        System.out.println(result1);
//        System.out.println(result2);
//        System.out.println(merge);
    	test.splitBrackets("or (!(>= Apache.Active 1) (>= Tomcat.Active 1))");
    	
    } 
}
