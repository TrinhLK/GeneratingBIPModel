package elements;

import java.util.ArrayList;

public class Component {

	private String type;
	private String name;
	private ArrayList<Variable> variables;
	private ArrayList<String> actions;
	private ArrayList<States> states;
	private ArrayList<Transition> transitions;
	
	public Component() {
		// TODO Auto-generated constructor stub		
	}

	public Component(String _type, String _name, ArrayList<Variable> _variables, ArrayList<String> _actions, ArrayList<States> _states, ArrayList<Transition> _transitions) {
		// TODO Auto-generated constructor stub
		type = _type;
		name = _name;
		variables = _variables;
		actions = _actions;
		states = _states;
		transitions = _transitions;
	}
	
	public String generateComponent() {
		String rs = "\tatom type " + type + "()\n";
		
		//variables
		for (Variable data : variables) {
			rs += "\t\tdata " + data.getType() + " " + data.getName();
			if (!data.getValue().equals("")) {
				rs += " = " + data.getValue();
			}
			rs += "\n";
		}
		
		//actions
		for(String action : actions) {
			rs += "\t\texport port Port " + action + "()\n";
		}
		
		//states
		rs += "\n\t\tplace ";
		for(int i=0 ; i<states.size()-1 ; i++) {
			rs += states.get(i).getName() + ", ";
		}
		if (states.size() > 1) {
			rs += states.get(states.size()-1).getName();
		}
			rs += "\n";
		ArrayList<States> listInitialState = getListInitialStates();
		for (States initS : listInitialState) {
			rs += "\n\t\tinitial to " + initS.getName() + " do {}\n";
		}
		
		//transitions
		for (Transition tran : transitions) {
			rs += "\t\ton " + tran.getAction() + " from " + tran.getSource() + " to " + tran.getTarget() + " do {}\n";
		}
		rs += "\tend";
		return rs;
	}
	
	public void printComponent() {
		String rs = "atom type " + type + "()\n";
		
		//variables
		for (Variable data : variables) {
			rs += "\tdata " + data.getType() + " " + data.getName();
			if (!data.getValue().equals("")) {
				rs += " = " + data.getValue();
			}
			rs += "\n";
		}
		
		//actions
		for(String action : actions) {
			rs += "\texport port Port " + action + "()\n";
		}
		
		//states
		rs += "\n\tplace ";
		for(int i=0 ; i<states.size()-1 ; i++) {
			rs += states.get(i).getName() + ", ";
		}
		rs += states.get(states.size()-1).getName() + "\n";
		ArrayList<States> listInitialState = getListInitialStates();
		for (States initS : listInitialState) {
			rs += "\n\tinitial to " + initS.getName() + " do {}\n";
		}
		
		//transitions
		for (Transition tran : transitions) {
			rs += "\ton " + tran.getAction() + " from " + tran.getSource() + " to " + tran.getTarget() + " do {}\n";
		}
		rs += "end";
		System.out.println(rs);
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Variable> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<Variable> variables) {
		this.variables = variables;
	}

	public ArrayList<String> getActions() {
		return actions;
	}

	public void setActions(ArrayList<String> actions) {
		this.actions = actions;
	}

	public ArrayList<Transition> getListTransition() {
		return transitions;
	}

	public void setListTransition(ArrayList<Transition> listTransition) {
		this.transitions = listTransition;
	}

	public ArrayList<States> getStates() {
		return states;
	}

	public void setStates(ArrayList<States> states) {
		this.states = states;
	}
	
	public ArrayList<States> getListInitialStates(){
		ArrayList<States> rs = new ArrayList<States>();
		for (States s : states) {
			if (s.isInit()) {
				rs.add(s);
			}
		}
		return rs;
	}
}
