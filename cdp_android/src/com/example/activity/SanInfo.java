package com.example.activity;

public class SanInfo 
{
	private String name;
	private String when;
	private int image;
	private int level;
	public SanInfo(String name, String when, int image, int level) {
		super();
		this.name = name;
		this.when = when;
		this.image = image;
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWhen() {
		return when;
	}
	public void setWhen(String when) {
		this.when = when;
	}
	public int getImage() {
		return image;
	}
	public void setImage(int image) {
		this.image = image;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
}
