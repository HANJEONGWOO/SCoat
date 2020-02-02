package com.example.blinder_ver01;

import android.location.*;
import android.os.Bundle;
import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;

import com.google.android.gms.internal.v;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;

import com.example.blinder_ver01.*;
import com.example.*;

public class MainActivity extends Activity {
	View mPage1, mPage2, mPage3, mPage4;
	
	GoogleMap map;	//현재 위칙로 맵을 가져오는 변수
	LocationManager manager;
	
	LocationManager mLocMan;
	TextView mStatus;
	TextView mResult;
	String mProvider;
	int mCount;
	
	String provider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();	//1)맵을 읽어옴
		
		manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			provider = LocationManager.GPS_PROVIDER;
		}
		else
		{
			provider = LocationManager.NETWORK_PROVIDER;
		}
		Location location = manager.getLastKnownLocation(provider);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(location.getLatitude(),location.getLongitude()))
		.zoom(13)                   
		.bearing(0)                
		.tilt(0)                 
		.build();      
		
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setMyLocationEnabled(true);	//1)현재 위치로 맵을 띄움
		
		mLocMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		mStatus = (TextView)findViewById(R.id.status);
		mResult = (TextView)findViewById(R.id.result);
		
		mProvider = mLocMan.getBestProvider(new Criteria(), true);
		// 1)상태 및 결과를 띄우기 위한 텍스트 뷰 설정
		
		/*각 페이지 아이디 설정 및 클릭 리스너 설정 */
		mPage1 = findViewById(R.id.page1);
		mPage2 = findViewById(R.id.page2);
		mPage3 = findViewById(R.id.page3);
		mPage4 = findViewById(R.id.page4);
		
		findViewById(R.id.btnpage1).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage2).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage3).setOnClickListener(mClickListener);
		findViewById(R.id.btnpage4).setOnClickListener(mClickListener);
	}
	
	Button.OnClickListener mClickListener = new Button.OnClickListener()	{
		
		public void onClick(View v)	{
			mPage1.setVisibility(View.VISIBLE);
			mPage2.setVisibility(View.INVISIBLE);
			mPage3.setVisibility(View.INVISIBLE);
			mPage4.setVisibility(View.INVISIBLE);
			
			switch(v.getId())	{
			case R.id.btnpage1:
				mPage1.setVisibility(View.VISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage2:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.VISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage3:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.VISIBLE);
				mPage4.setVisibility(View.INVISIBLE);
				break;
			case R.id.btnpage4:
				mPage1.setVisibility(View.INVISIBLE);
				mPage2.setVisibility(View.INVISIBLE);
				mPage3.setVisibility(View.INVISIBLE);
				mPage4.setVisibility(View.VISIBLE);
				break;
			}
		}
	};

	
		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
		
		
		public class MyLocationListener implements LocationListener{
			public MyLocationListener()
			{
				Log.e("LocationListener","LocationListener Called!!!");
			}
			public void onLocationChanged(Location location)
			{
				Log.e("onLocationChanged","Changing");
			}
			public void onProviderDisabled(String provider){}
			public void onProviderEnabled(String provider) {}
			public void onStatusChanged(String provider, int status, Bundle extras){}
		}
		
		public void onResume() {
			super.onResume();
			mCount = 0;
			mLocMan.requestLocationUpdates(mProvider,  3000,  10,  mListener);
			mStatus.setText("현재 상태 : 서비스 시작");
			
		}
		public void onPause() {
			super.onPause();
			mCount = 0;
			mStatus.setText("현재 상태 : 서비스 정지");
			
		}
		
		LocationListener mListener = new LocationListener()	{
			
			public void onLocationChanged(Location location) {
				mCount++;
				String sloc = String.format("위도 : %f\n 경도 : %f\n", location.getLatitude(), location.getLongitude());
				mResult.setText(sloc);
			}
			
			public void onProviderDisabled(String provider)	{
				mStatus.setText("현재 상태 : 서비스 사용 불가");
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				mStatus.setText("현재 상태 : 서비스 사용 가능");
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				String sStatus = "";
				switch(status)	{
				case LocationProvider.OUT_OF_SERVICE:
					sStatus = "범위 벗어남";
					break;
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					sStatus = "일시적 불능";
					break;
				case LocationProvider.AVAILABLE:
					sStatus = "사용 가능";
					break;
				}
			}	
		};
}