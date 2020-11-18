package entity;

import java.io.Serializable;

public class Select2 implements Serializable{

	private String id;
	private String text;
	public Select2(String id, String text) {
		super();
		this.id = id;
		this.text = text;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
}
