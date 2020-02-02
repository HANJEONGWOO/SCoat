package com.gnukkuldak.kkuldak_v1;

import java.util.ArrayList;

import com.gnukkul.kkuldak_v1.R;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsoluteLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Menu_AdaptKkuldak_v1 extends ArrayAdapter<MenuKkuldak_v1> {
	private int layout;
	private ArrayList<MenuKkuldak_v1> MenuList;
	private LayoutInflater layoutInflater;
	int type;
	
	
	

	public Menu_AdaptKkuldak_v1(Context menuListKkuldak_v1,
			int textViewResourceId, ArrayList<MenuKkuldak_v1> objects, int type) {
		super(menuListKkuldak_v1, textViewResourceId, objects);
		this.layout = textViewResourceId;
		this.MenuList = objects;
		this.layoutInflater = (LayoutInflater) menuListKkuldak_v1
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.type=type;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = layoutInflater.inflate(layout, null);
		}
	

		MenuKkuldak_v1 person = MenuList.get(position);

		if (person != null) {
			if(type==1){				
				TextView Name = (TextView) convertView.findViewById(R.id.name);
				TextView Size = (TextView) convertView.findViewById(R.id.size);
				TextView Taste = (TextView) convertView.findViewById(R.id.taste);
				TextView Money = (TextView) convertView.findViewById(R.id.money);
				Name.setText(person.getMenu_name());
				Size.setText(person.getMenu_size());
				Taste.setText(person.getMenu_taste());
				Money.setText(person.getMenu_money());
				
					((ImageView) convertView.findViewById(R.id.icon))
							.setImageResource(person.getMenu_picture());
			}
			
			else if( type==2)
				((ImageView) convertView.findViewById(R.id.icon))
				.setImageResource(person.getMenu_picture());
			
			else {
				
				TextView Menu = (TextView) convertView.findViewById(R.id.menu);
				Menu.setText(person.get_menu());
				
				TextView State = (TextView) convertView.findViewById(R.id.state);
				State.setText(person.get_state());
			}
	
	
		}

		return convertView;
	}
}