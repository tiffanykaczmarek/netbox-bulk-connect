package application;


public class Port {
	private int id;
	private String name;
	private Type type;
	
	public Port(int id, String name, Type type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}

public int getID(){
	return this.id;
	}

public String getName(){
	return this.name;
	}

public Type getType() {
		return this.type;
}

public String toString(){ 
    return this.id + " " + this.name + " " + this.type;
}

public enum Type {
		REARPORT("dcim.rearport"),
		FRONTPORT("dcim.frontport"),
		INTERFACE("dcim.interface");

		private final String apiName;

		Type(String apiName) {
			this.apiName = apiName;
		}

		public String getApiName() {
			return apiName;
		}
	}
}


