package connectors;

public class Annotation {
	private String id;
	private String value;
	
	public Annotation(String _id, String _value) {
		// TODO Auto-generated constructor stub
		id = _id;
		value = _value;
	}

	public void printAnno() {
		System.out.println(id + ": " + value);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
