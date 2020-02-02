package com.gnukkuldak.kkuldak_v1;

import com.gnukkul.kkuldak_v1.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ToggleButton;

public class Order_Kkuldak_v1 extends Activity {

	ArrayAdapter<CharSequence> menu_spi;
	ArrayAdapter<CharSequence> taste_spi;
	ArrayAdapter<CharSequence> amount_spi;
	private int menu_num;
	public Val_Kkuldak glo_val = new Val_Kkuldak();
	public Intent viewPage;

	String menu_item;
	String taste_item;
	String money_item;
	String amount_item;

	int img_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_kkuldak_v1);

		Spinner taste = (Spinner) findViewById(R.id.tastespiner);
		Spinner menu = (Spinner) findViewById(R.id.menuspiner);
		Spinner amount = (Spinner) findViewById(R.id.amountspiner);
		ImageView img = (ImageView) findViewById(R.id.bigmenuimg);
		final Button order_btn = (Button) findViewById(R.id.order_real_btn);

		final Button back_btn = (Button) findViewById(R.id.back_btn);

		back_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}

		});

		back_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					back_btn.setBackgroundResource(R.drawable.barbtn_tablet_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					back_btn.setBackgroundResource(R.drawable.barbtn_tablet_b);
				}
				return false;
			}
		});

		order_btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				glo_val.setData(menu_item, taste_item, money_item, img_id,
						Integer.parseInt(amount_item));
				glo_val.basket_index++;
				finish();
			}

		});

		order_btn.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
					order_btn.setBackgroundResource(R.drawable.order_btn_a);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					order_btn.setBackgroundResource(R.drawable.order_btn);
				}
				return false;
			}
		});

		Resources res = getResources(); // 자원을 관리하기위한 리소스 클래스 생성
		BitmapDrawable bitmap;

		Intent intent = getIntent();
		menu_num = intent.getExtras().getInt("menu");

		switch (menu_num) {
		case 0:

			img_id = R.drawable.menu1_order;
			Log.d("numberimge1", R.drawable.menu1_order + "");
			bitmap = (BitmapDrawable) res.getDrawable(R.drawable.menu1_order);
			img.setImageDrawable(bitmap);

			amount.setPrompt("수량을 선택하세요.");
			amount_spi = ArrayAdapter.createFromResource(this,
					R.array.amount_list, android.R.layout.simple_spinner_item);
			amount_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			amount.setAdapter(amount_spi);

			menu.setPrompt("메뉴를 선택하세요.");
			menu_spi = ArrayAdapter.createFromResource(this,
					R.array.menu1_list, android.R.layout.simple_spinner_item);
			menu_spi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			menu.setAdapter(menu_spi);

			taste.setPrompt("맛을 선택하세요.");

			taste_spi = ArrayAdapter.createFromResource(this,
					R.array.taste1_list, android.R.layout.simple_spinner_item);
			taste_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			taste.setAdapter(taste_spi);

			menu_item = glo_val.menu[menu_num];

			menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					money_item = (String) parent.getItemAtPosition(position)
							.toString();

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			amount.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amount_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			taste.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					taste_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			break;

		case 1:
			img_id = R.drawable.menu2_order;
			Log.d("numberimge2", R.drawable.menu2_order + "");
			bitmap = (BitmapDrawable) res.getDrawable(R.drawable.menu2_order);
			img.setImageDrawable(bitmap);
			amount.setPrompt("수량을 선택하세요.");
			amount_spi = ArrayAdapter.createFromResource(this,
					R.array.amount_list, android.R.layout.simple_spinner_item);
			amount_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			amount.setAdapter(amount_spi);
			menu.setPrompt("메뉴를 선택하세요.");
			menu_spi = ArrayAdapter.createFromResource(this,
					R.array.menu2_list, android.R.layout.simple_spinner_item);
			menu_spi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			menu.setAdapter(menu_spi);

			taste.setPrompt("맛을 선택하세요.");

			taste_spi = ArrayAdapter.createFromResource(this,
					R.array.taste2_list, android.R.layout.simple_spinner_item);
			taste_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			taste.setAdapter(taste_spi);
			menu_item = glo_val.menu[menu_num];
			menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					money_item = (String) parent.getItemAtPosition(position)
							.toString();

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			taste.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					taste_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			amount.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amount_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			break;

		case 2:
			img_id = R.drawable.menu3_order;
			bitmap = (BitmapDrawable) res.getDrawable(R.drawable.menu3_order);
			img.setImageDrawable(bitmap);
			amount.setPrompt("수량을 선택하세요.");
			amount_spi = ArrayAdapter.createFromResource(this,
					R.array.amount_list, android.R.layout.simple_spinner_item);
			amount_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			amount.setAdapter(amount_spi);
			menu.setPrompt("메뉴를 선택하세요.");
			menu_spi = ArrayAdapter.createFromResource(this,
					R.array.menu3_list, android.R.layout.simple_spinner_item);
			menu_spi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			menu.setAdapter(menu_spi);

			taste.setPrompt("맛을 선택하세요.");

			taste_spi = ArrayAdapter.createFromResource(this,
					R.array.taste3_list, android.R.layout.simple_spinner_item);
			taste_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			taste.setAdapter(taste_spi);
			menu_item = glo_val.menu[menu_num];

			menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					money_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			taste.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					taste_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			amount.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amount_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case 3:
			img_id = R.drawable.menu4_order;
			bitmap = (BitmapDrawable) res.getDrawable(R.drawable.menu4_order);
			img.setImageDrawable(bitmap);
			amount.setPrompt("수량을 선택하세요.");
			amount_spi = ArrayAdapter.createFromResource(this,
					R.array.amount_list, android.R.layout.simple_spinner_item);
			amount_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			amount.setAdapter(amount_spi);
			menu.setPrompt("메뉴를 선택하세요.");
			menu_spi = ArrayAdapter.createFromResource(this,
					R.array.menu4_list, android.R.layout.simple_spinner_item);
			menu_spi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			menu.setAdapter(menu_spi);

			taste.setPrompt("맛을 선택하세요.");

			taste_spi = ArrayAdapter.createFromResource(this,
					R.array.taste4_list, android.R.layout.simple_spinner_item);
			taste_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			taste.setAdapter(taste_spi);
			menu_item = glo_val.menu[menu_num];
			menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					money_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			taste.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					taste_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			amount.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amount_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			break;
		case 4:
			img_id = R.drawable.menu5_order;
			bitmap = (BitmapDrawable) res.getDrawable(R.drawable.menu5_order);
			img.setImageDrawable(bitmap);
			amount.setPrompt("수량을 선택하세요.");
			amount_spi = ArrayAdapter.createFromResource(this,
					R.array.amount_list, android.R.layout.simple_spinner_item);
			amount_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			amount.setAdapter(amount_spi);
			menu.setPrompt("메뉴를 선택하세요.");
			menu_spi = ArrayAdapter.createFromResource(this,
					R.array.menu5_list, android.R.layout.simple_spinner_item);
			menu_spi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			menu.setAdapter(menu_spi);

			taste.setPrompt("맛을 선택하세요.");

			taste_spi = ArrayAdapter.createFromResource(this,
					R.array.taste5_list, android.R.layout.simple_spinner_item);
			taste_spi
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			taste.setAdapter(taste_spi);
			menu_item = glo_val.menu[menu_num];
			menu.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					money_item = (String) parent.getItemAtPosition(position)
							.toString();

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});

			taste.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					taste_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			amount.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					amount_item = (String) parent.getItemAtPosition(position)
							.toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
			break;
		default:
			// img=null;
			break;

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.kkuldak_v1, menu);
		return true;
	}

}
