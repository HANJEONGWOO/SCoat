package com.gnukkuldak.kkuldak_v1;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
public class Val_Kkuldak {
	public static int basket_index = 0;
	public static String[] menu = { "�ܴ߰���", "��������������", "ġŲ ������", "�ݰ���", "ġ�ƽ",
			"ũ������", "�������", "�����巳��ƽ", "�����Ĵ�ġŲ", "�ݰ���", "�ܴ� ���̵�޴�" };
	public static String[] basket_menu = new String[20];
	/*
	 * 0: �ܴ� 1: �����ܴ� 2: Ŀ���ܴ� 3: ����ܴ� 4: �������������� 5: ũ������ 6: ������� 7: �����巳��ƽ 8:
	 * �����Ĵ�ġŲ 9: �ݰ��� 10: �ܴ� ���̵� �޴�
	 */
	public static String[] basket_taste = new String[20];
	/*
	 * 0: ���� 1: ���Ѹ� 2: �ſ�� 3: ũ�������������� 4: ġ�ƽ 5: ������ƽ 6: ����Ƣ�� 7: ī������� 8:
	 * �ѿ�����
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