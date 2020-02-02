package com.gnukkuldak.kkuldak_v1;

import android.util.Log;

public class MenuKkuldak_v1 {
	private String Menu_name;
	private String State;
	private String Menu_taste;
	private String Menu_size;
	private String Menu_money;
	private String Menu;//DB호출용 변수
	
	
	private int Menu_picture;
	private int Menu_select;

	// 생성자  생성
	public MenuKkuldak_v1(String Menu_name, String Menu_taste,
			String Menu_size, String Menu_money, int Menu_picture) {
		super();
		this.Menu_name = Menu_name;
		this.Menu_taste = Menu_taste;
		this.Menu_size = Menu_size;
		this.Menu_money = Menu_money;
		this.Menu_picture = Menu_picture;

	}
	
	public MenuKkuldak_v1( int Menu_picture) {
		super();
		this.Menu_picture = Menu_picture;

	}
	
	public MenuKkuldak_v1( String menu ,String state) {
		super();
		this.Menu = menu;
		this.State = state;
	}


	public int getMenu_picture() {
		return Menu_picture;
	}

	public int getMenu_select() {
		return Menu_select;
	}

	public String getMenu_money() {
		return Menu_money;
	}

	public void setMenu_picture(int Menu_picture) {
		this.Menu_picture = Menu_picture;
	}

	public void setMenu_select(int Menu_select) {
		this.Menu_select = Menu_select;
	}

	public String getMenu_name() {
		return Menu_name;
	}
	
	public String get_state() {
		return State;
	}

	public void setMenu_name(String Menu_name) {
		this.Menu_name = Menu_name;
	}

	public String getMenu_taste() {
		return Menu_taste;
	}
	
	public String get_menu() {
		return Menu;
	}
	
	public void setMenu_taste(String Menu_taste) {
		this.Menu_taste = Menu_taste;
	}

	public String getMenu_size() {
		return Menu_size;
	}

	public void setMenu_size(String Menu_size) {
		this.Menu_size = Menu_size;
	}

}
