package elements;

public class Variable {

	private String type;
	private String name;
	private String value;
	
	public Variable(String _type, String _name, String _value) {
		// TODO Auto-generated constructor stub
		type = _type;
		name = _name;
		value = _value;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
