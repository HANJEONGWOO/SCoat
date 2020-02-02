package com.gnukkuldak.kkuldak_v1;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
public class Val_Kkuldak {
	public static int basket_index = 0;
	public static String[] menu = { "²Ü´ß°­Á¤", "¶¥Äá¹ü¹÷°¡¶ó¾ÆÄÉ", "Ä¡Å² ÅÁ¼öÀ°", "Äİ°­Á¤", "Ä¡Áî½ºÆ½",
			"Å©¸®½ºÇÇ", "°ñµåÀ®½º", "°¥¸¯µå·³½ºÆ½", "¼ø»ìÆÄ´ßÄ¡Å²", "Äİ°­Á¤", "²Ü´ß »çÀÌµå¸Ş´º" };
	public static String[] basket_menu = new String[20];
	/*
	 * 0: ²Ü´ß 1: °¥¸¯²Ü´ß 2: Ä¿¸®²Ü´ß 3: ¼ø»ì²Ü´ß 4: ¶¥Äá¹ü¹÷°¡¶ó¾ÆÄÉ 5: Å©¸®½ºÇÇ 6: °ñµåÀ®½º 7: °¥¸¯µå·³½ºÆ½ 8:
	 * ¼ø»ìÆÄ´ßÄ¡Å² 9: Äİ°¥Á¤ 10: ²Ü´ß »çÀÌµå ¸Ş´º
	 */
	public static String[] basket_taste = new String[20];
	/*
	 * 0: ¾øÀ½ 1: ¼øÇÑ¸À 2: ¸Å¿î¸À 3: Å©·¹ÀÌÁöÆ÷Å×ÀÌÅä 4: Ä¡Áî½ºÆ½ 5: °í±¸¸¶½ºÆ½ 6: »õ¿ìÆ¢±è 7: Ä«·¹°í·ÎÄÉ 8:
	 * ÇÑ¿ì°í·ÎÄÉ
	 */
	public static String[] basket_money = new String[20];
	public static String phone;
	
	public static int[] img_id = new int[10];
	public static int[] basket_amount = new int[10];
	public static int[] menu_price = new int[10];
		public void setData(String basket_menu, String basket_taste,
			String basket_money, int img_id, int basket_amount) {
		this.basket_menu[this.basket_index] = basket_menu;
		this.basket_taste[this.basket_index] = basket_taste;
		this.basket_money[this.basket_index] = basket_money;
		this.basket_amount[this.basket_index] = basket_amount;
		this.img_id[this.basket_index] = img_id;
	}
	public void removeData(int position) {
		for (int i = position; i < this.basket_index - 1; i++) {
			basket_menu[i] = basket_menu[i + 1];
			basket_money[i] = basket_money[i + 1];
			basket_taste[i] = basket_taste[i + 1];
			basket_amount[i] = basket_amount[i + 1];
			img_id[i] = img_id[i + 1];
		}
	}
	public void printData() {
		for (int i = 0; i < this.basket_index; i++) {
			Log.d("menu" + i, basket_menu[i]);
			Log.d("taste" + i, basket_taste[i]);
			Log.d("menu" + i, basket_money[i]);
			Log.d("amount" + i, basket_amount[i] + "");
		}
	}
}