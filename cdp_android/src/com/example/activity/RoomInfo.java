package com.example.activity;

import java.lang.reflect.Member;
import java.util.ArrayList;

public class RoomInfo {
	private int roomNumber;
	private String roomName;
	private int max;
	private int cur;
	private ArrayList<Member> list;
	public RoomInfo(int roomNumber, String roomName, int max, int cur,
			ArrayList<Member> list) {
		super();
		this.roomNumber = roomNumber;
		this.roomName = roomName;
		this.max = max;
		this.cur = cur;
		this.list = list;
	}
	public int getRoomNumber() {
		return roomNumber;
	}
	public void setRoomNumber(int roomNumber) {
		this.roomNumber = roomNumber;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getCur() {
		return cur;
	}
	public void setCur(int cur) {
		this.cur = cur;
	}
	public ArrayList<Member> getList() {
		return list;
	}
	public void setList(ArrayList<Member> list) {
		this.list = list;
	}
	
}
