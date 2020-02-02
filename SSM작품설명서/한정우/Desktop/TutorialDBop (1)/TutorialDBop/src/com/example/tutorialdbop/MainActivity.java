package com.example.tutorialdbop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.os.AsyncTask;//비동기 쓰레드 작업 사용
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
	//이름, 성별, 생년월일을 담아둘 String(input_Name,sex,birthyear) | php로부터 받아온 결과값을 저장할 String(output_Result)
	private Button input, search, show;//input버튼, search버튼, show버튼 담을 객체
	private String error;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//버튼 Inflate
		input = (Button)findViewById(R.id.input_btn);
		search = (Button)findViewById(R.id.search_btn);
		show = (Button)findViewById(R.id.show_btn);
		
		//각 버튼들의 리스너 정의함.
		//1. input_btn 버튼 클릭 리스너 정의
		input.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//입력할 내용을 String에 담아놓고.
				input_Name = ((EditText)(findViewById(R.id.name))).getText().toString();
				input_Sex = ((EditText)(findViewById(R.id.sex))).getText().toString();
				input_Birth = ((EditText)(findViewById(R.id.birth))).getText().toString();
				new inputDB().execute();//입력 스레드 시작.
				
			}
		});
		//2. search_btn 버튼 클릭 리스너 정의
		search.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//DB의 이름을 검색
				input_Name = ((EditText)(findViewById(R.id.searchName))).getText().toString();
				new searchDB().execute();//검색 스레드 시작
			}
			
		});
		//3. show_btn 버튼 클릭 리스너 정의
		show.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				new showDB().execute();
				((TextView)(findViewById(R.id.DB_result))).setText(output_Result); 
			}
		});
		
	}
	
	//스레드 정의 input, search, show버튼에서 사용할 스레드 3가지.
	//스레드를 쓰는 이유는 소켓프로그래밍을 할 때 데이터가 언제 다운완료될지 모르기 떄문에
	//백그라운드에서 계속 작업을 진행해야한다고 한다. (메인 스레드에서 작업하면 프로그램 걍 뒤짐)
	//그러므로 AsyncTask 메소드를 이용하여 스레드를 돌리겠음.
	//1. input_btn용 스레드 정의
    private class inputDB extends AsyncTask<Void, Void, String>{
		@Override
		//백그라운드에서 작업할 작업을 지시함.
		protected String doInBackground(Void... params) {
			String temp;
			temp = inputData();
			return temp;
		}
		//백그라운드 작업 끝나고(여기서는 inputData가 정상적으로 끝나고)
		//실행할 작업.
		//왜 이걸 쓰냐면 doInBackground에서 ui thread(onCreate)부분 건들면 뒈져버린다.
		//그래서 onPostExecute에서 작업 결과를 표시하든지 한다.
		protected void onPostExecute(String temp){
    		((TextView)(findViewById(R.id.DB_result))).setText(temp); 
    	}
    }
    //2. search_btn용 스레드 정의
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
    //3. show_btn용 스레드 정의
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
    
    //각 스레드에서 사용할 각 함수들을 정의
    //1. input_btn용 스레드에 탑재될 함수
    public String inputData() { 
        try { 
             //URL 설정하고 접속하기 
             URL url = new URL("http://203.255.39.254/insert_menu.php");       // URL 설정 
             HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속 
             //   전송 모드 설정 - 기본적인 설정이다 
             http.setDefaultUseCaches(false);                                            
             http.setDoInput(true);// 서버에서 읽기 모드 지정 
             http.setDoOutput(true);// 서버로 쓰기 모드 지정  
             http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..

             //서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
             http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
             
             //실제로 건드릴 부분은 여기서 부터다.(위에는 기본 설정이니 이해하려고하지말고 걍 Ctrl+C -> Ctrl+V해라)
             //서버로 값 전송
             //전송에는 StringBuffer로 패킷으로 만들어서 보내야한다.
             //모든 보낼 String을 패킷으로 만들어서 밑의 형식으로 보내면 서버에있는 php파일에 변수가 저장되고 처리된다.
             StringBuffer buffer = new StringBuffer(); 
             buffer.append("name").append("=").append(input_Name).append("&");// php 변수에 값 대입 
             buffer.append("sex").append("=").append(input_Sex).append("&");// php 변수 앞에 '$' 붙이지 않는다 
             buffer.append("birthyear").append("=").append(input_Birth);// 변수 구분은 '&' 사용
                       
             OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8"); 
             
             PrintWriter writer = new PrintWriter(outStream); 
             writer.write(buffer.toString()); 
             writer.flush();
             
             //-------------------------- 
             //   서버에서 전송받기 
             //-------------------------- 
             InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
             
             BufferedReader reader = new BufferedReader(tmp); 
             StringBuilder builder = new StringBuilder(); 
             String str; 
             while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다 
                  builder.append(str + "\n");
             } 
             output_Result = builder.toString();// 전송결과를 전역 변수에 저장
             
        } catch (MalformedURLException e) { 
               // 
        } catch (IOException e) { 
               error = e.toString();
        } // try 
        return output_Result;//작업 결과 넘겨버려야지 ㅇㅅㅇ?
    } // HttpPostData 
    //2. search_btn용 스레드에 탑재될 함수
	public String searchData() { 
	    try { 
	         //URL 설정하고 접속하기 
	         URL url = new URL("http://203.255.39.254/search_data.php");       // URL 설정 
	         HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속 
	         //   전송 모드 설정 - 기본적인 설정이다 
	         http.setDefaultUseCaches(false);                                            
	         http.setDoInput(true);// 서버에서 읽기 모드 지정 
	         http.setDoOutput(true);// 서버로 쓰기 모드 지정  
	         http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..
	
	         //서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
	         http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
	         
	         //실제로 건드릴 부분은 여기서 부터다.(위에는 기본 설정이니 이해하려고하지말고 걍 Ctrl+C -> Ctrl+V해라)
	         //서버로 값 전송
	         //전송에는 StringBuffer로 패킷으로 만들어서 보내야한다.
	         //모든 보낼 String을 패킷으로 만들어서 밑의 형식으로 보내면 서버에있는 php파일에 변수가 저장되고 처리된다.
	         StringBuffer buffer = new StringBuffer(); 
	         buffer.append("name").append("=").append(input_Name);
	                   
	         OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF8"); 
	         
	         PrintWriter writer = new PrintWriter(outStream); 
	         writer.write(buffer.toString()); 
	         writer.flush();
	         
	         //-------------------------- 
	         //   서버에서 전송받기 
	         //-------------------------- 
	         InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
	         
	         BufferedReader reader = new BufferedReader(tmp); 
	         StringBuilder builder = new StringBuilder(); 
	         String str; 
	         while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다 
	              builder.append(str + "\n");
	         } 
	         output_Result = builder.toString();//검색 결과를 전역 변수에 저장
	         
	    } catch (MalformedURLException e) { 
	           // 
	    } catch (IOException e) { 
	           error = e.toString();
	    } // try
	    return output_Result;
	} // HttpPostData 
	//3. show_btn용 스레드에 탑재될 함수
	public String showData() { 
	    try { 
	         //URL 설정하고 접속하기 
	         URL url = new URL("http://203.255.39.254/show_data.php");       // URL 설정 
	         HttpURLConnection http = (HttpURLConnection) url.openConnection();   // 접속 
	         //   전송 모드 설정 - 기본적인 설정이다 
	         http.setDefaultUseCaches(false);                                            
	         http.setDoInput(true);// 서버에서 읽기 모드 지정 
	         http.setDoOutput(true);// 서버로 쓰기 모드 지정  
	         http.setRequestMethod("POST");// 전송 방식은 POST <- 보안때문에 이거 쓴단다..
	
	         //서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
	         http.setRequestProperty("content-type", "application/x-www-form-urlencoded"); 
	         
	         //-------------------------- 
	         //   서버에서 전송받기 
	         //-------------------------- 
	         InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF8");  
	         
	         BufferedReader reader = new BufferedReader(tmp); 
	         StringBuilder builder = new StringBuilder(); 
	         String str; 
	         while ((str = reader.readLine()) != null) {// 서버에서 라인단위로 보내줄 것이므로 라인단위로 읽는다 
	              builder.append(str + "\n");
	         } 
	         output_Result = builder.toString();//검색 결과를 전역 변수에 저장
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
