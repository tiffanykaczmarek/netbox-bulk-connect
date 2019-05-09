package application;


public class Rearport {
	private int id;
	private String name;
	
	public Rearport (int id, String name) {
		this.id = id;
		this.name = name;
	}

public int getID(){
	return this.id;
	}

public String getName(){
	return this.name;
	}

public String toString(){ 
    return this.id + " " + this.name; 
} 

}


