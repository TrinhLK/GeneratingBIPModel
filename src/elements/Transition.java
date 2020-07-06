package elements;

public class Transition {

	private String target;
	private String source;
	private String action;
	
	public Transition() {
		// TODO Auto-generated constructor stub
	}
	
	public Transition(String _source, String _target, String _action) {
		target = _target;
		source = _source;
		action = _action;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
	
}
