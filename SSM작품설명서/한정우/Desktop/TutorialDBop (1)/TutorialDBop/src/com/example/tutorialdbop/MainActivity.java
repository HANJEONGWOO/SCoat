package com.example.tutorialdbop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;//�񵿱� ������ �۾� ���
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private String input_Name, input_Sex, input_Birth, output_Result; 
	//�̸�, ����, ��������� ��Ƶ� String(input_Name,sex,birthyear) | php�κ��� �޾ƿ� ������� ������ String(output_Result)
	private Button input, search, show;//input��ư, search��ư, show��ư ���� ��ü
	private String error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//��ư Inflate
		input = (Button)findViewById(R.id.input_btn);
		search = (Button)findViewById(R.id.search_btn);
		show = (Button)findViewById(R.id.show_btn);
		
		//�� ��ư���� ������ ������.
		//1. input_btn ��ư Ŭ�� ������ ����
		input.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//�Է��� ������ String�� ��Ƴ���.
				input_Name = ((EditText)(findViewById(R.id.name))).getText().toString();
				input_Sex = ((EditText)(findViewById(R.id.sex))).getText().toString();
				input_Birth = ((EditText)(findViewById(R.id.birth))).getText().toString();
				new inputDB().execute();//�Է� ������ ����.
				
			}
		});
		//2. search_btn ��ư Ŭ�� ������ ����
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//DB�� �̸��� �˻�
				input_Name = ((EditText)(findViewById(R.id.searchName))).getText().toString();
				new searchDB().execute();//�˻� ������ ����
			}
			
		});
		//3. show_btn ��ư Ŭ�� ������ ����
		show.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				new showDB().execute();
				((TextView)(findViewById(R.id.DB_result))).setText(output_Result); 
			}
		});
		
	}
	
	//������ ���� input, search, show��ư���� ����� ������ 3����.
	//�����带 ���� ������ �������α׷����� �� �� �����Ͱ� ���� �ٿ�Ϸ���� �𸣱� ������
	//��׶��忡�� ��� �۾��� �����ؾ��Ѵٰ� �Ѵ�. (���� �����忡�� �۾��ϸ� ���α׷� �� ����)
	//�׷��Ƿ� AsyncTask �޼ҵ带 �̿��Ͽ� �����带 ��������.
	//1. input_btn�� ������ ����
    private class inputDB extends AsyncTask<Void, Void, String>{
		@Override
		//��׶��忡�� �۾��� �۾��� ������.
		protected String doInBackground(Void... params) {
			String temp;
			temp = inputData();
			return temp;
		}
		//��׶��� �۾� ������(���⼭�� inputData�� ���������� ������)
		//������ �۾�.
		//�� �̰� ���ĸ� doInBackground���� ui thread(onCreate)�κ� �ǵ�� ����������.
		//�׷��� onPostExecute���� �۾� ����� ǥ���ϵ��� �Ѵ�.
		protected void onPostExecute(String temp){
    		((TextView)(findViewById(R.id.DB_result))).setText(temp); 
    	}
    }
    //2. search_btn�� ������ ����
    private class searchDB extends AsyncTask<Void, Void, String>{
    	@Override
		protected String doInBackground(Void... params) {
			String temp;
			temp = searchData();
			return temp;
		}    
    	protected void onPostExecute(String temp){
    		((TextView)(findViewById(R.id.DB_result))).setText(temp); 
    	}
    }
    //3. show_btn�� ������ ����
    private class showDB extends AsyncTask<Void, Void, String>{
    	@Override
		protected String doInBackground(Void... params) {
    		String temp;
    		temp = showData();
			return temp;
		}
    	protected void onPostExecute(String temp){
    		((TextView)(findViewById(R.id.DB_result))).setText(temp); 
    	}
    }
    
    //�� �����忡�� ����� �� �Լ����� ����
    //1. input_btn�� �����忡 ž��� �Լ�
    public String inputData() { 
        try { 
             //URL �����ϰ� �����ϱ� 
             URL url = new URL("http://203.255.39.254/insert_menu.php");       // URL ���� 
             HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ���� 
             //   ���� ��� ���� - �⺻���� �����̴� 
             http.setDefaultUseCaches(false);                                            
             http.setDoInput(true);// �������� �б� ��� ���� 
             http.setDoOutput(true);// ������ ���� ��� ����  
             http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..

             //�������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش� 
             http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
             
             //������ �ǵ帱 �κ��� ���⼭ ���ʹ�.(������ �⺻ �����̴� �����Ϸ����������� �� Ctrl+C -> Ctrl+V�ض�)
             //������ �� ����
             //���ۿ��� StringBuffer�� ��Ŷ���� ���� �������Ѵ�.
             //��� ���� String�� ��Ŷ���� ���� ���� �������� ������ �������ִ� php���Ͽ� ������ ����ǰ� ó���ȴ�.
             StringBuffer buffer = new StringBuffer(); 
             buffer.append("name").append("=").append(input_Name).append("&");// php ������ �� ���� 
             buffer.append("sex").append("=").append(input_Sex).append("&");// php ���� �տ� '$' ������ �ʴ´� 
             buffer.append("birthyear").append("=").append(input_Birth);// ���� ������ '&' ���
                       
             OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8"); 
             
             PrintWriter writer = new PrintWriter(outStream); 
             writer.write(buffer.toString()); 
             writer.flush();
             
             //-------------------------- 
             //   �������� ���۹ޱ� 
             //-------------------------- 
             InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
             
             BufferedReader reader = new BufferedReader(tmp); 
             StringBuilder builder = new StringBuilder(); 
             String str; 
             while ((str = reader.readLine()) != null) {// �������� ���δ����� ������ ���̹Ƿ� ���δ����� �д´� 
                  builder.append(str + "\n");
             } 
             output_Result = builder.toString();// ���۰���� ���� ������ ����
             
        } catch (MalformedURLException e) { 
               // 
        } catch (IOException e) { 
               error = e.toString();
        } // try 
        return output_Result;//�۾� ��� �Ѱܹ������� ������?
    } // HttpPostData 
    //2. search_btn�� �����忡 ž��� �Լ�
	public String searchData() { 
	    try { 
	         //URL �����ϰ� �����ϱ� 
	         URL url = new URL("http://203.255.39.254/search_data.php");       // URL ���� 
	         HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ���� 
	         //   ���� ��� ���� - �⺻���� �����̴� 
	         http.setDefaultUseCaches(false);                                            
	         http.setDoInput(true);// �������� �б� ��� ���� 
	         http.setDoOutput(true);// ������ ���� ��� ����  
	         http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..
	
	         //�������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش� 
	         http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
	         
	         //������ �ǵ帱 �κ��� ���⼭ ���ʹ�.(������ �⺻ �����̴� �����Ϸ����������� �� Ctrl+C -> Ctrl+V�ض�)
	         //������ �� ����
	         //���ۿ��� StringBuffer�� ��Ŷ���� ���� �������Ѵ�.
	         //��� ���� String�� ��Ŷ���� ���� ���� �������� ������ �������ִ� php���Ͽ� ������ ����ǰ� ó���ȴ�.
	         StringBuffer buffer = new StringBuffer(); 
	         buffer.append("name").append("=").append(input_Name);
	                   
	         OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8"); 
	         
	         PrintWriter writer = new PrintWriter(outStream); 
	         writer.write(buffer.toString()); 
	         writer.flush();
	         
	         //-------------------------- 
	         //   �������� ���۹ޱ� 
	         //-------------------------- 
	         InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
	         
	         BufferedReader reader = new BufferedReader(tmp); 
	         StringBuilder builder = new StringBuilder(); 
	         String str; 
	         while ((str = reader.readLine()) != null) {// �������� ���δ����� ������ ���̹Ƿ� ���δ����� �д´� 
	              builder.append(str + "\n");
	         } 
	         output_Result = builder.toString();//�˻� ����� ���� ������ ����
	         
	    } catch (MalformedURLException e) { 
	           // 
	    } catch (IOException e) { 
	           error = e.toString();
	    } // try
	    return output_Result;
	} // HttpPostData 
	//3. show_btn�� �����忡 ž��� �Լ�
	public String showData() { 
	    try { 
	         //URL �����ϰ� �����ϱ� 
	         URL url = new URL("http://203.255.39.254/show_data.php");       // URL ���� 
	         HttpURLConnection http = (HttpURLConnection) url.openConnection();   // ���� 
	         //   ���� ��� ���� - �⺻���� �����̴� 
	         http.setDefaultUseCaches(false);                                            
	         http.setDoInput(true);// �������� �б� ��� ���� 
	         http.setDoOutput(true);// ������ ���� ��� ����  
	         http.setRequestMethod("POST");// ���� ����� POST <- ���ȶ����� �̰� ���ܴ�..
	
	         //�������� ������ <Form>���� ���� �Ѿ�� �Ͱ� ���� ������� ó���϶�� �� �˷��ش� 
	         http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
	         
	         //-------------------------- 
	         //   �������� ���۹ޱ� 
	         //-------------------------- 
	         InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
	         
	         BufferedReader reader = new BufferedReader(tmp); 
	         StringBuilder builder = new StringBuilder(); 
	         String str; 
	         while ((str = reader.readLine()) != null) {// �������� ���δ����� ������ ���̹Ƿ� ���δ����� �д´� 
	              builder.append(str + "\n");
	         } 
	         output_Result = builder.toString();//�˻� ����� ���� ������ ����
	    } catch (MalformedURLException e) { 
	           // 
	    } catch (IOException e) { 
	           error = e.toString();
	    } // try
		return output_Result;
	} // HttpPostData 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
