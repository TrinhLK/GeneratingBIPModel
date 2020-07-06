package readxml;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import javax.xml.parsers.*;
import org.w3c.dom.*;

import connectors.Annotation;
import connectors.ComponentInstance;
import connectors.Element;
import connectors.SingleConnector;
import elements.Component;
import elements.States;
import elements.Transition;
import elements.Variable;
import properties.Interaction;
import properties.ListElement;
import properties.ListInteraction;

public class BIPModelGeneration {

	
	public static void main(String[] args) {
		BIPModelGeneration test = new BIPModelGeneration();
//		String fileXml = "input/mlamp.xml";
//		String fileConfigure = "input/lamp.configure";
		test.generateBIPModel(args[0], args[1]);
		test.genPropertyFile(args[0], args[1]);
	  }

	/**
	 * ====================================================================================================================================
	 * Deadlock properties
	 * */
	public void genPropertyFile(String fileXML, String fileConfigure) {

		ArrayList<Annotation> listAnnotation = getListPropertiesAnnotations(fileXML);	//List anno has "prop:" string
		ComponentInstance ci = getAllInstancesFromConfigurationFile(fileConfigure);
		StringBuilder sb = new StringBuilder();
		String bipModelName = getBIPModelNameFromXML(fileXML);
		
		if (listAnnotation.size() > 1) {
			sb.append("(and");
		}
		
		for (Annotation a : listAnnotation) {
			String newAnno = createConcreteData(a.getValue().replaceAll("prop: ", "").trim(), ci.getMapInstance());
			sb.append("\t" + newAnno + "\n");
		}
		/*
		for (Annotation a : listAnnotation) {
			ArrayList<SingleConnector> tempConcrete = genAllConcreteConnector(a.getValue(), a.getId(), fileConfigure);
			
			/**
			 * Get list Single Connector
			 * Add information of states into elements
			 * */
		/*
			for (int i=0 ; i<tempConcrete.size() ; i++) {
				ArrayList<Element> listElements = tempConcrete.get(i).getListStoredElements();
				for (Element e : listElements) {
					addStateInforToAnElement(e, listComponent, ci);
				}
			}

			sb.append("\t(or \n");
			for (int i=0 ; i<tempConcrete.size() ; i++) {
				
				//System.out.println("------------");
				ArrayList<Interaction> temp = makeInteractionFromConnector(tempConcrete.get(i));
				sb.append("\t\t(or \n");
				for (Interaction interaction : temp) {
					sb.append(interaction.printForProperties() + "\n");
				}
				sb.append("\t\t)\n");
			}
			sb.append("\t)\n");
		}
		*/
		if (listAnnotation.size() > 1) {
			sb.append(")\n");
		}
		System.out.println(sb);

		/**
		 * Write to file
		 * -------------------------------------------------------------------------------------------
		 * */
		//Creating a File object
		File file = new File("output");
		//Creating the directory
		file.mkdir();
		//Create BIP model
		createFiles("output/" + bipModelName + "-deadlock.pro", sb.toString());
	}
	
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
	
	public String createConcreteData(String inputStr, HashMap<String, ArrayList<String>> listComponent) {
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
//    		    	System.out.println(input + "\t->\t" + tempRs);
    		    	oldAndNew.put(input, tempRs);
    		    }
    		}
    	}
    	
    	for (HashMap.Entry<String, String> entry : oldAndNew.entrySet()) {
    		if (inputStr.contains(entry.getKey())) {
    			inputStr = inputStr.replaceAll(entry.getKey(), entry.getValue());
    		}
    	}
    	inputStr = inputStr.replaceAll("\\.", "_");
    	System.out.println(inputStr);
    	return inputStr;
    }
	
	//for concrete abstract connector
	public void addStateInforToAnElement(Element element, ArrayList<Component> listComponents, ComponentInstance ci) {
		
		try {
			String content = element.getContent().replaceAll("\\(|\\)|`", "").trim();
			String[] basicInfor = content.split("\\.");
			ArrayList<String> listState = new ArrayList<String>();
			String abstractName = "";
			//lay abstract ty basic[0]
			
			for (HashMap.Entry<String, ArrayList<String>> entry : ci.getMapInstance().entrySet()) {
				ArrayList<String> listConcrete = entry.getValue();
				for (String concrete : listConcrete) {
					if (basicInfor[0].equals(concrete)) {
						abstractName = entry.getKey();
					}
				}
			}
			
			for (Component c : listComponents) {
				if (c.getType().equals(abstractName)) {
					ArrayList<Transition> cTrans = c.getListTransition();
					for (Transition tran : cTrans) {
						if (tran.getAction().equalsIgnoreCase(basicInfor[1])) {
							listState.add(tran.getTarget());
						}
					}
				}
			}
			element.setListStates(listState);
		}catch (Exception e) {
			System.out.println("Error in split Element's content.");
		}
		
	}
	
	/**
	 * Generate an interaction from a connector
	 * */
	public ArrayList<Interaction> makeInteractionFromConnector(SingleConnector connector) {
		int maxLv = connector.getMaxLevel();
		
		ArrayList<Interaction> result = new ArrayList<Interaction>();
//		int count = 0;
//		ArrayList<ArrayList<Element>> listTriggerInteraction
		if (maxLv == 0) {
			ArrayList<ArrayList<Element>> triggerSets = removeEmpty(getAllSubsets(connector.getTriggers()));
			ArrayList<ArrayList<Element>> synchSets = getAllSubsets(connector.getSynchs());
			ArrayList<ArrayList<Element>> listInteractionsOfConnectors = mergeTwoList(triggerSets, synchSets);
			
			for (int i=0 ; i<listInteractionsOfConnectors.size() ; i++) {
				result.add(new Interaction(listInteractionsOfConnectors.get(i)));
			}
		}else {
			ListElement listElems = new ListElement(connector.getListStoredElements());
			ArrayList<ListInteraction> listInteraction = new ArrayList<ListInteraction>();
			
			for (int lv = maxLv ; lv >= 0 ; lv--) {
				//Get all blocks (compounds) at this level
				ArrayList<String> blocksName = listElems.getAllBlocksAtLevel(lv);
				ArrayList<Element> elemsAtlv = listElems.getElementsAtLevel(lv);
				
				for (int i=0 ; i < blocksName.size() ; i++) {
					ArrayList<Element> triggers = new ArrayList<Element>();
					ArrayList<Element> synchs = new ArrayList<Element>();
					int shouldBeTrigger = 1;
					for (Element e : elemsAtlv) {
						if (e.getLocation().equals(blocksName.get(i))) {
							if (e.getKind() == 0) triggers.add(e);
							else {
								synchs.add(e);
								if (e.getKind() == 2) shouldBeTrigger = 0;
							}
								
						}
					}
					ArrayList<ArrayList<Element>> tempTriggerSets = removeEmpty(getAllSubsets(triggers));
					ArrayList<ArrayList<Element>> tempSynchSets = getAllSubsets(synchs);
					
					if (lv == maxLv) {
						ArrayList<ArrayList<Element>> listInteractionsOfConnectors = mergeTwoList(tempTriggerSets, tempSynchSets);
						listInteraction.add(new ListInteraction(listInteractionsOfConnectors, shouldBeTrigger, lv-1));
					}else {
						ArrayList<ArrayList<Element>> triggerSets = new ArrayList<ArrayList<Element>>();
						ArrayList<ArrayList<Element>> synchSets = new ArrayList<ArrayList<Element>>();
						
						for (ListInteraction li : listInteraction) {
							if (li.getLevel() == lv) {
								if (li.getKind() == 0) triggerSets.addAll(li.getListOfInteraction());
								else
									synchSets.addAll(li.getListOfInteraction());
							}
						}
						triggerSets.addAll(tempTriggerSets);
						synchSets.addAll(tempSynchSets);
						ArrayList<ArrayList<Element>> listInteractionsOfConnectors = mergeTwoList(triggerSets, synchSets);
						ListInteraction temp = new ListInteraction(listInteractionsOfConnectors, shouldBeTrigger, lv-1);
						listInteraction.add(temp);
					}
				}
			}
			
			ArrayList<ArrayList<Element>> listInteractionsOfConnectors = new ArrayList<ArrayList<Element>>();
			for (ListInteraction li : listInteraction) {
//				System.out.println("--");
				if (li.getLevel() == -1) {
					listInteractionsOfConnectors = li.getListOfInteraction();
//					li.print();
				}
			}
			
			for (int i=0 ; i<listInteractionsOfConnectors.size() ; i++) {
				result.add(new Interaction(listInteractionsOfConnectors.get(i)));
			}
		}
		return result;
	}
	
	/**
	 * Get all sub-lists of a given arraylist
	 * */
    public ArrayList<ArrayList<Element>> getAllSubsets(ArrayList<Element> example) { 
    	
    	ArrayList<ArrayList<Element>> result = new ArrayList<ArrayList<Element>>();
    	int n = example.size(); 
    	
    	// Run a loop for printing all 2^n 
    	// subsets one by one 
		for (int i = 0; i < (1<<n); i++) { 
			ArrayList<Element> subset = new ArrayList<Element>();
			// Print current subset 
			for (int j = 0; j < n; j++) {
				// (1<<j) is a number with jth bit 1 
				// so when we 'and' them with the 
				// subset number we get which numbers 
				// are present in the subset and which 
				// are not 
				if ((i & (1 << j)) > 0) {
					subset.add(example.get(j));
				}
			}
			
			result.add(subset);
		} 
		
		return result;
	} 
    
    /**
	 * Remove the empty list from the arraylist
	 * */
    public ArrayList<ArrayList<Element>> removeEmpty(ArrayList<ArrayList<Element>> _trigger){
    	ArrayList<ArrayList<Element>> result = new ArrayList<ArrayList<Element>>();
    	
    	for (int i=0 ; i<_trigger.size() ; i++) {
    		if (_trigger.get(i).size() != 0) {
    			result.add(_trigger.get(i));
    		}
    	}
    	
    	return result;
    }
    
    /**
	 * Merge two array lists from triggers and synchronizes
	 * */
	public ArrayList<ArrayList<Element>> mergeTwoList(ArrayList<ArrayList<Element>> _trigger, ArrayList<ArrayList<Element>> _synch) {
    	ArrayList<ArrayList<Element>> result = new ArrayList<ArrayList<Element>>();
    	
//    	System.out.println(_synch.size() + "\t" + _trigger.size());
    	if (_trigger.size() > 0) {
    		for (int i=0 ; i<_synch.size() ; i++) {
        		
        		ArrayList<Element> synch_i = _synch.get(i);
        		for (int j=0 ; j<_trigger.size() ; j++) {
        			ArrayList<Element> mergeList = new ArrayList<Element>();
        			ArrayList<Element> trigger_j = _trigger.get(j);
        			
        			for(Element t : trigger_j) {
        				mergeList.add(t);
        			}
        			
        			for(Element s : synch_i) {
        				mergeList.add(s);
        			}
        			result.add(mergeList);
        		}	
        	}
    	} else {
    		
    		ArrayList<Element> mergeList = new ArrayList<Element>();
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
	/**
	 * ====================================================================================================================================
	 * */
	
	/**
	 * Generate full BIP model
	 * */
	public void generateBIPModel(String fileXML, String fileConfigure) {
		ArrayList<Component> listComponent = getBIPComponentsFromXML(fileXML);
		ArrayList<Annotation> listAnnotation = getListAnnotations(fileXML);
		ArrayList<SingleConnector> listConcreteSingleConnectors = new ArrayList<SingleConnector>();
		ArrayList<SingleConnector> listSingleConnectors = new ArrayList<SingleConnector>();
		StringBuilder sb = new StringBuilder();
		StringBuilder sbInvariants = new StringBuilder();
		String bipModelName = getBIPModelNameFromXML(fileXML);
		
		for (Annotation a : listAnnotation) {
			ArrayList<SingleConnector> tempConcrete = genAllConcreteConnector(a.getValue(), a.getId(), fileConfigure);
			ArrayList<SingleConnector> temp = genAllConnector(a.getValue(), a.getId());
			listConcreteSingleConnectors.addAll(tempConcrete);
			listSingleConnectors.addAll(temp);
		}
		
		//generate package infor
		sb.append("package " + bipModelName + "\n");
		sb.append("\n\tport type Port()\n\n");
		//Generate the components in detail
		for (Component c : listComponent) {
			sb.append(c.generateComponent() + "\n\n");
		}
		
		//Generate definitions of general connector 
		for (SingleConnector sc : listSingleConnectors) {
			sb.append(sc.genBIPConnectorDefination() + "\n");
		}
		
		/**
		 * COMPOUND PART
		 * -------------
		 * */
		//Create Compound
		sb.append("\tcompound type " + bipModelName + "Compound()\n");
		
		//Generate all Instances
		sb.append(generateComponentsInstances(fileConfigure) + "\n");
		
		//Generate all concrete connectors
		for (SingleConnector sc : listConcreteSingleConnectors) {
			sb.append(sc.genBIPConnector() + "\n");
		}
		sb.append("\tend\nend");
//		System.out.println(sb);
		/**
		 * -------------------------------------------------------------------------------------------
		 * */
		
		/**
		 * Invariants content
		 * -------------------------------------------------------------------------------------------
		 * */
		sbInvariants.append("# atom control invariants\n");
		//Generate the components in detail
		for (Component c : listComponent) {
			//-at Type -a atom-control
			sbInvariants.append("-at " + c.getType() + " -a atom-control\n");
		}
		sbInvariants.append("\n# compound control reachability\n");
		//-ct Track2Peer -a control-reachability
		sbInvariants.append("-ct " + bipModelName + "Compound -a control-reachability\n");
		System.out.println(sbInvariants);
		/**
		 * Write to file
		 * -------------------------------------------------------------------------------------------
		 * */
		//Creating a File object
		File file = new File("output");
		//Creating the directory
		file.mkdir();
		//Create BIP model
		createFiles("output/" + bipModelName + ".bip", sb.toString());
		createFiles("output/" + bipModelName + "-scheme.inv", sbInvariants.toString());
		
	}
	
	public void createFiles(String fileName, String content) {
		try (FileWriter writer = new FileWriter(fileName);
			BufferedWriter bw = new BufferedWriter(writer)) {
			bw.write(content);
		} catch (IOException e) {
			System.err.format("IOException: %s%n", e);
		}
	}
	/**
	 * Generate component's instances from the configuration File
	 * */
	public String generateComponentsInstances(String fileConfigure) {
		String rs = "";
		ComponentInstance ci = getAllInstancesFromConfigurationFile(fileConfigure);
		
		for (HashMap.Entry<String, ArrayList<String>> entry : ci.getMapInstance().entrySet()) {
//		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
		    for (String value : entry.getValue()) {
		    	rs += "\t\tcomponent " + entry.getKey() + " " + value + "()\n";
		    }
		}
		return rs;
	}
	
	/**
	 * Get list annotations from the XML file
	 * */
	public ArrayList<Annotation> getListAnnotations(String fileName) {
		ArrayList<Annotation> listAnnotation = new ArrayList<Annotation>();
		
	    try {
	    	File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			NodeList nListAnnos = doc.getElementsByTagName("annotations");
			for (int iAnnos=0 ; iAnnos<nListAnnos.getLength() ; iAnnos++) {
				Node curAnnos = nListAnnos.item(iAnnos);
				if (curAnnos.getNodeType() == Node.ELEMENT_NODE) {
					if (curAnnos.hasAttributes()) {
						NamedNodeMap nodeMapAnnos = curAnnos.getAttributes();
						for (int jAnnos = 0; jAnnos < nodeMapAnnos.getLength(); jAnnos++) {
							
							Node nodeAnnos = nodeMapAnnos.item(jAnnos);
							if (nodeAnnos.getNodeName().equals("name")) {
								if (nodeAnnos.getNodeValue().equalsIgnoreCase("Specification")) {
									
									NodeList nList = curAnnos.getChildNodes();
									System.out.println("list Anno: " + nList.getLength());
									for (int i=0 ; i<nList.getLength() ; i++) {
										
										Node curAnno = nList.item(i);
										if (curAnno.getNodeType() == Node.ELEMENT_NODE) {
											if (curAnno.hasAttributes()) {
											
												NamedNodeMap nodeMap = curAnno.getAttributes();
												for (int j = 0; j < nodeMap.getLength(); j++) {	
													Node node = nodeMap.item(j);
													if (node.getNodeName().equals("id")) {
														System.out.println("Normal anno: " + node.getNodeValue().replaceAll("-", "_") + "\t" + curAnno.getTextContent());
														listAnnotation.add(new Annotation(node.getNodeValue().replaceAll("-", "_"), curAnno.getTextContent()));
													}
												}
											}
										}								
									}
								}
							}
						}
					}
				}
			}
			
//			NodeList nList = doc.getElementsByTagName("annotation");
//			for (int i=0 ; i<nList.getLength() ; i++) {
//				Node curAnno = nList.item(i);
//				String id, value;
//				id = value = "";
//				if (curAnno.getNodeType() == Node.ELEMENT_NODE) {
//					if (curAnno.hasAttributes()) {
//						NamedNodeMap nodeMap = curAnno.getAttributes();
//						for (int j = 0; j < nodeMap.getLength(); j++) {
//							
//							Node node = nodeMap.item(j);
//							if (node.getNodeName().equals("id")) {
//								id = node.getNodeValue();
//							}
//						}
//					}
//				}
//				value = curAnno.getTextContent();
//				
//				if (!value.contains("prop:")) {
//					System.out.println("Normal anno: " + value);
//					listAnnotation.add(new Annotation(id, value));
//				}
//					
//			}
	    }catch (Exception e) {
			// TODO: handle exception
		}
	    return listAnnotation;
	}
	
	/**
	 * Get list annotations from the XML file
	 * */
	public ArrayList<Annotation> getListPropertiesAnnotations(String fileName) {
		ArrayList<Annotation> listAnnotation = new ArrayList<Annotation>();
		
	    try {
	    	File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			
			NodeList nListAnnos = doc.getElementsByTagName("annotations");
			for (int iAnnos=0 ; iAnnos<nListAnnos.getLength() ; iAnnos++) {
				Node curAnnos = nListAnnos.item(iAnnos);
				if (curAnnos.getNodeType() == Node.ELEMENT_NODE) {
					if (curAnnos.hasAttributes()) {
						NamedNodeMap nodeMapAnnos = curAnnos.getAttributes();
						for (int jAnnos = 0; jAnnos < nodeMapAnnos.getLength(); jAnnos++) {
							
							Node nodeAnnos = nodeMapAnnos.item(jAnnos);
							if (nodeAnnos.getNodeName().equals("name")) {
								if (nodeAnnos.getNodeValue().equalsIgnoreCase("Specification")) {
									NodeList nList = curAnnos.getChildNodes();
									//System.out.println("Check properties: " + nList.getLength());

									for (int i=0 ; i<nList.getLength() ; i++) {
										Node curAnno = nList.item(i);
										//System.out.println(curAnno.getNodeName() + "\t" + curAnno.getNodeValue());
										String id, value;
										id = value = "";
										if (curAnno.getNodeType() == Node.ELEMENT_NODE) {
											if (curAnno.hasAttributes()) {
												NamedNodeMap nodeMap = curAnno.getAttributes();
												for (int j = 0; j < nodeMap.getLength(); j++) {
													
													Node node = nodeMap.item(j);
													if (node.getNodeName().equals("id")) {
														id = node.getNodeValue();
													}
												}
											}
										}
										value = curAnno.getTextContent();
										
										if (value.contains("prop:")) {
											listAnnotation.add(new Annotation(id, value));
											System.out.println(value);
										}
											
									}
								}
							}
						}
					}
				}
			}
	    }catch (Exception e) {
			// TODO: handle exception
		}
	    return listAnnotation;
	}
	
	
	/**
	 * Get BIP Model name
	 * */
	public String getBIPModelNameFromXML(String fileName) {
		String rs = "";
		
		try {

			File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nList = doc.getElementsByTagName("extension");
			
			for (int i=0 ; i<nList.getLength() ; i++) {
				Node curExtension = nList.item(i);
				if (curExtension.getNodeType() == Node.ELEMENT_NODE) {
					if (curExtension.hasAttributes()) {
						NamedNodeMap nodeMap = curExtension.getAttributes();
						for (int j = 0; j < nodeMap.getLength(); j++) {
							Node node = nodeMap.item(j);
							if (node.getNodeName().equals("name")) {
								rs = node.getNodeValue();
							}
						}
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
		return rs;
	}
	
	/**
	 * Get BIP Components From the XML file
	 * */
	public ArrayList<Component> getBIPComponentsFromXML(String fileName) {
		ArrayList<Component> listComponent = new ArrayList<Component>();
	    try {

			File file = new File(fileName);
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
			                         .newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			NodeList nList = doc.getElementsByTagName("class");
			
			
			for (int i=0 ; i<nList.getLength() ; i++) {
				boolean shouldAdd = true;
				Component tempComponent = getComponentsInfor(nList.item(i).getChildNodes());
				Node curClass = nList.item(i);
				
				if (curClass.getNodeType() == Node.ELEMENT_NODE) {
					if (curClass.hasAttributes()) {
						NamedNodeMap nodeMap = curClass.getAttributes();
						for (int j = 0; j < nodeMap.getLength(); j++) {
							
							Node node = nodeMap.item(j);
							if (node.getNodeName().equals("parent") && node.getNodeValue().equalsIgnoreCase("link")) {
								shouldAdd = false;
								break;
							}
							
							if (node.getNodeName().equals("name")) {
//								System.out.println("class name: " + node.getNodeValue());
								tempComponent.setType(node.getNodeValue());
							}
						}
					}
				}
				
//				tempComponent.printComponent();
				if (shouldAdd) listComponent.add(tempComponent);
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	    
	    return listComponent;
	}
	
	
	public Component getComponentsInfor(NodeList nodeList) {

//		Component component = new Component();
		ArrayList<Variable> variables = new ArrayList<Variable>();
		ArrayList<String> actions = new ArrayList<String>();
		ArrayList<States> states = new ArrayList<States>();
		ArrayList<Transition> listTrans = new ArrayList<Transition>();
		
		for (int count = 0; count < nodeList.getLength(); count++) {
		
			Node tempNode = nodeList.item(count);
			
			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				
				if (tempNode.getNodeName().equals("variable")) {
					String type, name, value;
					type = name = value = "";
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("type"))
								type = node.getNodeValue().toLowerCase();
							if (node.getNodeName().equals("name"))
								name = node.getNodeValue().replaceAll("\\.", "_");
							if (node.getNodeName().equals("value"))
								value = node.getNodeValue();
						}
					}
					variables.add(new Variable(type, name, value));
				}
				
				if (tempNode.getNodeName().equals("action")) {
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("name"))
								actions.add(node.getNodeValue());
						}
					}
				}
				
				if (tempNode.getNodeName().equals("place")) {
					String name = "";
					boolean init = false, finalS = false;
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("name")) {
								name = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("initial")) {
								init = Boolean.parseBoolean(node.getNodeValue());
							}
							
							if (node.getNodeName().equals("final")) {
								finalS = Boolean.parseBoolean(node.getNodeValue());
							}
							
						}
					}
					states.add(new States(name, init, finalS));
				}
				
				if (tempNode.getNodeName().equals("transition")) {
					String from, to, action;
					from = to = action = "";
					
					if (tempNode.hasAttributes()) {
						NamedNodeMap nodeMap = tempNode.getAttributes();
						for (int i = 0; i < nodeMap.getLength(); i++) {
							
							Node node = nodeMap.item(i);
							if (node.getNodeName().equals("from")) {
								from = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("to")) {
								to = node.getNodeValue();
							}
							
							if (node.getNodeName().equals("action")) {
								action = node.getNodeValue();
							}
							
						}
					}
					listTrans.add(new Transition(from, to, action));
				}
				
//				System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
			}
		}
		return new Component("", "", variables, actions, states, listTrans);
	}
	
	/**
	 * -----------------------------------------------------------------------------------------------------
	 * */
	/**
	 * Get List Elements of a Connector from Annotations
	 * Standardize them
	 * */
	public ArrayList<SingleConnector> genAllConcreteConnector(String annotation, String annoId, String fileName) {
		
		ArrayList<String> listConnectorString = getListConnectorStringFromAnAnnotation(annotation);
		ArrayList<SingleConnector> listConcreteSingleConnector = new ArrayList<SingleConnector>();
		
		ComponentInstance ci = getAllInstancesFromConfigurationFile(fileName);
		
		//Create list concrete annotations
		for (int i=0 ; i<listConnectorString.size() ; i++) {
			ArrayList<String> listGeneratedConreteString_i = ci.createListInstanceAnnotation(listConnectorString.get(i));
			
			SingleConnector generalSingleConnector_i = createSingleConnector(listConnectorString.get(i));
			generalSingleConnector_i.setName("Connector" + annoId + (i+1));
			generalSingleConnector_i.standardizeConnector();
			
			for (int j=0 ; j<listGeneratedConreteString_i.size() ; j++) {
				SingleConnector concreteSingleConnector_j = createSingleConnector(listGeneratedConreteString_i.get(j));
				concreteSingleConnector_j.setName("Connector" + annoId + (i+1) + (j+1));
				concreteSingleConnector_j.setAbsName(generalSingleConnector_i);
				concreteSingleConnector_j.standardizeConnector();
				setListCompoundName(concreteSingleConnector_j, generalSingleConnector_i);
				listConcreteSingleConnector.add(concreteSingleConnector_j);
			}
			
		}
				
		return listConcreteSingleConnector;
	}
	
	public void setListCompoundName(SingleConnector concrete, SingleConnector abst) {
		for (int i=0 ; i<concrete.getListCompound().size() ; i++) {
			concrete.getListCompound().get(i).setType(abst.getListCompound().get(i).getName());
		}
	}
	
	/**
	 * Get List Elements of a Connector from Annotations
	 * Standardize them
	 * */
	public ArrayList<SingleConnector> genAllConnector(String annotation, String annoId) {
		
		ArrayList<String> listConnectorString = getListConnectorStringFromAnAnnotation(annotation);
		ArrayList<SingleConnector> listSingleConnector = new ArrayList<SingleConnector>();
		
		for (int i=0 ; i<listConnectorString.size() ; i++) {
			
			SingleConnector generalSingleConnector_i = createSingleConnector(listConnectorString.get(i));
			generalSingleConnector_i.setName("Connector" + annoId + (i+1));
			generalSingleConnector_i.standardizeConnector();
			listSingleConnector.add(generalSingleConnector_i);
		}
		
		return listSingleConnector;
	}
	
	public SingleConnector createSingleConnector(String connectorString) {
		
		SingleConnector aConnector = new SingleConnector();
		ArrayList<Element> listElements = new ArrayList<Element>();
		getListElements(listElements, connectorString, 0, "block");
		aConnector.setListElements(listElements);
		return aConnector;
		
	}
	
	/**
	 * Get List Elements of a Connector in Annotations
	 * */
	public void getListElements(ArrayList<Element> aConnector, String connector, int level, String location){
		if (aConnector == null)
			aConnector = new ArrayList<Element>();
		
		if (connector.length() == 0)
			return ;

		int pos = connector.indexOf('[');
		if (pos == -1) {
			String[] elems = connector.split("-");
			for(String e : elems) {
				String temp = e.trim();
				int kind = -1;
				if (temp.contains("`")) {
					kind = 0;
				}else if (temp.contains("*")) {
					//System.out.println(e.indexOf("*"));
					temp = temp.substring(0, temp.indexOf("*")) + temp.substring(temp.indexOf("*") + 1);
					kind = 2;
				}else {
					kind = 1;
				}
				aConnector.add(new Element(temp, level, kind, location));
			}
			return ;

		}
	
		Stack<Character> stack = new Stack<>();
		stack.push(connector.charAt(pos));
		int q = pos + 1;
		while (q < connector.length()) {
			if (connector.charAt(q) == ']') {
				if (!stack.empty())
					stack.pop();
			} else if (connector.charAt(q) == '[') {
				stack.push(connector.charAt(q));
			}
			q++;
			if (stack.empty())
				break;
		}
		
		String baseLevelConnector = connector.substring(0, pos);
		getListElements(aConnector, baseLevelConnector, level, location+"_0-"+pos);
		
		String nextLevelConnector = connector.substring(pos + 1, q - 1);
		int nextLv = level + 1;
		getListElements(aConnector, nextLevelConnector, nextLv, location+"_"+(pos+1)+"-"+(q-1));
		
		if (q + 1 < connector.length()) {
			getListElements(aConnector, connector.substring(q + 1, connector.length() - 1), level, location+"_"+(q+1)+"-"+(connector.length() - 1));
		}
		
		return ;
	}
	
	/**
	 * Read file contains the list of connectors
	 * Split and store connectors into an arraylist
	 * */
	public ArrayList<String> getListConnectorStringFromAnAnnotation(String annotation) {

        String readLine = annotation.toString();
        ArrayList<String> listConnectorString = new ArrayList<String>();
//        System.out.println("Anno: " + annotation);


    	if(readLine.contains("data:")) {
    		
    	}else {
    		if (readLine.contains("+")) {
           	 String[] subConnectors = readLine.split("\\+");
           	 for (String con : subConnectors) {
           		 String standardCon = con.trim();
           		 if (standardCon.contains("]`")) {
           			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
           			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
           			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
           			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
           			 standardCon = standCon1 + standCon2;
           		 }
           		 listConnectorString.add(standardCon);
           	 }
                	
           } else {
           	String standardCon = readLine.trim();
           	if (standardCon.contains("]`")) {
       			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
       			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
       			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
       			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
       			 standardCon = standCon1 + standCon2;
       		 }
           	listConnectorString.add(standardCon);
           }
    	}
        	
    	return listConnectorString;
	}
	
	/**
	 * Read file contains the list of connectors
	 * Split and store connectors into an arraylist
	 * */
	public ArrayList<String> readAnnotations(String annotation, String fileName) {

        String readLine = annotation.toString();
        ArrayList<String> listConnectorString = new ArrayList<String>();
        
        System.out.println("Anno: " + annotation);


    	if(readLine.contains("data:")) {
    		
    	}else {
    		if (readLine.contains("+")) {
           	 String[] subConnectors = readLine.split("\\+");
           	 for (String con : subConnectors) {
           		 String standardCon = con.trim();
           		 if (standardCon.contains("]`")) {
           			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
           			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
           			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
           			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
           			 standardCon = standCon1 + standCon2;
           		 }
           		 listConnectorString.add(standardCon);
           	 }
                	
           } else {
           	String standardCon = readLine.trim();
           	if (standardCon.contains("]`")) {
       			 String standCon1 = standardCon.substring(standardCon.indexOf("["), standardCon.indexOf("]`"));
       			 String standCon2 = standardCon.substring(standardCon.indexOf("]`"));
       			 standCon1 = standCon1.replaceAll("\\)", "\\)*");
       			 standCon2 = standCon2.replaceAll("\\]`", "\\]");
       			 standardCon = standCon1 + standCon2;
       		 }
           	listConnectorString.add(standardCon);
           }
    	}
        
    	
    	
    	return listConnectorString;
	}
	
	/**
	 * Get all instances of each class from the configuration file
	 * @input: configuration file's name
	 * */
	public ComponentInstance getAllInstancesFromConfigurationFile(String fileName) {
		ComponentInstance ci = new ComponentInstance();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

            // read line by line
            String line;
            while ((line = br.readLine()) != null) {

                if (line.contains("resource")) {
                	String rs = line.substring(line.lastIndexOf(":")+1).trim();
                    String className = rs.substring(rs.indexOf(".")+1, rs.indexOf("title")).trim();
                    String objName = rs.substring(rs.indexOf("\"")+1, rs.lastIndexOf("\"")).trim();
                    ci.add(className, objName);
                }
            }

        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
        }
        return ci;
	}
}
