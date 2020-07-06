package elements;

public class States {
	private String name;
	private boolean init;
	private boolean finalState;
	
	public States(String _name, boolean _init, boolean _final) {
		// TODO Auto-generated constructor stub
		name = _name;
		init = _init;
		finalState = _final;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isFinalState() {
		return finalState;
	}

	public void setFinalState(boolean finalState) {
		this.finalState = finalState;
	}
	
	
}
