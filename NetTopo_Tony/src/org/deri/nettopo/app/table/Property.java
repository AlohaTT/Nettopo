package org.deri.nettopo.app.table;

public class Property {
	private String key;
	private String value;
	
	public Property(String key, String value){
		setKey(key);
		setValue(value);
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	
	public String getKey(){
		return key;
	}
	
	public String getValue(){
		return value;
	}
}
