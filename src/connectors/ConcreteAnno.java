package connectors;

public class ConcreteAnno {

	private String value;
	private String abstractAnno;
	
	public ConcreteAnno() {
		// TODO Auto-generated constructor stub
	}
	
	public ConcreteAnno(String _value, String _abstractAnno) {
		// TODO Auto-generated constructor stub
		value = _value;
		abstractAnno = _abstractAnno;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAbstractAnno() {
		return abstractAnno;
	}

	public void setAbstractAnno(String abstractAnno) {
		this.abstractAnno = abstractAnno;
	}
	
	
}
