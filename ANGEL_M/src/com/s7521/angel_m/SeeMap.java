package com.s7521.angel_m;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

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
import com.google.android.maps.MapController;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.s7521.angel_m.*;
import com.s7521.marker.AngelMarker;
import com.s7521.marker.MarkerTree;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SeeMap extends Activity{
	MyLocationListener listener;
	GoogleMap map;
	MapController mapController;
	LocationManager manager;
	
	TextView HttpText;
	View WindowView;
	String provider,session,BtAnimal,ImagePath,getID,SessionID;
	double Lat,Lng;
	ArrayList<AngelMarker> AnimalMarkers;
	MarkerTree Markers;
	TextView title;
	TextView sinpper;
	ImageView image;
	ImageLoader loader;
	AngelMarker Marker;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seemap);
		SessionID = (String)MainActivity.context.getAttribute("ID");
		WindowView = getLayoutInflater().inflate(R.layout.info_window, null);
		image = (ImageView)WindowView.findViewById(R.id.imageView);
		title = (TextView)WindowView.findViewById(R.id.title);
		sinpper = (TextView)WindowView.findViewById(R.id.snippet);

		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setInfoWindowAdapter(new MyInfoWindowAdapter());	//??? - 일단안넣음
		//InfoWindowAdapter->Customizing

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
		.zoom(9)                   
		.bearing(0)                
		.tilt(0)                 
		.build();      

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		new MarkerSet().execute();	//	??? - 일단안넣음


		map.setMyLocationEnabled(true);
		//Marker�� Ŭ�������� ���� Ž��Ʈ���� �˻�
		map.setOnMarkerClickListener(new OnMarkerClickListener(){
			public boolean onMarkerClick(Marker marker) {
				image.setImageResource(R.drawable.animal);
				ImagePath = Markers.BSTSearch(marker.getPosition().latitude);
				Log.e("ImagePath :",ImagePath);
				return false;
			}
		});
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
			public void onInfoWindowClick(
					com.google.android.gms.maps.model.Marker marker){
				//marker.hideInfoWindow();
				getID=Markers.BSTSearchID(marker.getPosition().latitude);
				Log.e("marker"," "+marker.getPosition().latitude);
				Log.e("getID",getID);
				Log.e("SessionID",SessionID);
				if(getID.equals(SessionID))
				{
					Markers.BSTRemove(marker.getPosition().latitude);
				}//��Ŀ�� ������ ������ ���̵�� ���� session ���� ������ �ִ�
				//���̵� ������ ���� ����
			}
		});
		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				Lat = point.latitude;
				Lng = point.longitude;
				ImgBtDialog dialog= new ImgBtDialog(SeeMap.this);
				dialog.show();
			}
		});
		map.setOnMyLocationChangeListener((OnMyLocationChangeListener) listener);
		listener = new MyLocationListener();
		listener.onLocationChanged(location);

		loader = ImageLoader.getInstance();
		loader.init(ImageLoaderConfiguration.createDefault(SeeMap.this));

		manager.requestLocationUpdates(provider,0,0, listener);
	}
	public void onResume() {
		super.onResume();
		manager.requestLocationUpdates(provider,0,0, listener);
	}
	public void onPause() {
		super.onPause();
		manager.removeUpdates(listener);
	}
	public class MyInfoWindowAdapter implements InfoWindowAdapter
	{
		public View getInfoContents(Marker marker){
			setInfoWindowView(marker);
			return WindowView;
		}
		public View getInfoWindow(Marker marker) {
			return null;
		}
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

	public class MarkerSet extends AsyncTask<Void,Void,String>{	
		public MarkerSet()
		{
			Log.e("MarkerSet","Called!!!");
		}
		protected String doInBackground(Void... params) {
			String response = null;
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<BasicNameValuePair> parameter = new ArrayList();
			try{
				parameter.add(new BasicNameValuePair("CASE","GetAllMarkers"));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
				httpPost.setEntity(ent);
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0,j=0;
				Markers = new MarkerTree();
				while ((s = buffer.readLine()) != null) {
					if(i==8)
					{
						i=0;
						j++;
						Log.e("��Ŀ ������ ���"," "+j);
					}
					if(i==0)
					{
						Marker= new AngelMarker();
						Marker.setID(s);
					}
					if(i==1)
					{
						Marker.setNickName(s);
					}
					else if(i==2)
					{
						Marker.setTitle(s);
					}
					else if(i==3)
					{
						Marker.setSnippet(s);
					}
					else if(i==4)
					{
						Marker.setLatitude(Double.parseDouble(s));
					}
					else if(i==5)
					{
						Marker.setLongitude(Double.parseDouble(s));
					}
					else if(i==6)
					{
						Marker.setAnimalFlag(s);
					}
					else if(i==7)
					{
						Marker.setImagePath(s);
						Markers.BSTInsert(Marker);
					}
					i++;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String result)
		{
			Markers.SetMarkers(Markers.Root, map);
		}
	}

	public class ImgBtDialog extends Dialog implements android.view.View.OnClickListener{
		ImageButton Kitten,Puppy,Dontknow;
		public ImgBtDialog(Context context)
		{
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.radio_animal);
			Kitten=(ImageButton)findViewById(R.id.Kitten);
			Puppy=(ImageButton)findViewById(R.id.Puppy);
			Dontknow=(ImageButton)findViewById(R.id.Dontknow);

			Kitten.setOnClickListener(this);
			Puppy.setOnClickListener(this);
			Dontknow.setOnClickListener(this);
		}
		public void onClick(View v) {
			if(v==Kitten)
			{
				Log.e("Button","Kitten Clicked!!!");
				BtAnimal = "�����";
				Intent intent = new Intent(SeeMap.this,SetAnimalInfo.class);
				intent.putExtra("BtAnimal",BtAnimal);
				intent.putExtra("Lat",Lat);
				intent.putExtra("Lng",Lng);
				startActivity(intent);
				finish();
			}
			if(v==Puppy)
			{
				Log.e("Button","Puppy Clicked!!!");
				BtAnimal = "������";
				Intent intent = new Intent(SeeMap.this,SetAnimalInfo.class);
				intent.putExtra("BtAnimal",BtAnimal);
				intent.putExtra("Lat",Lat);
				intent.putExtra("Lng",Lng);
				startActivity(intent);
				finish();
			}
			if(v==Dontknow)
			{
				Log.e("Button","Dontknow Clicked!!!");
				BtAnimal = "�ٸ� ����";
				Intent intent = new Intent(SeeMap.this,SetAnimalInfo.class);
				intent.putExtra("BtAnimal",BtAnimal);
				intent.putExtra("Lat",Lat);
				intent.putExtra("Lng",Lng);
				startActivity(intent);
				finish();
			}
		}
	}

	private void setInfoWindowView(final Marker marker) {
		title.setText(marker.getTitle());		
		sinpper.setText(marker.getSnippet());
		loader.displayImage("http://192.168.42.77:8080/ANGEL_M_SERVER/PhotoUpload/"+ImagePath, image, new ImageLoadingListener() {
			public void onLoadingCancelled(String arg0, View arg1){}
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) 
			{ //�ε��� �Ϸ�Ǹ� �ٽ� �ѹ� marker�� showInfoWindow
				marker.showInfoWindow();
			}
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2){}
			public void onLoadingStarted(String arg0, View arg1){}
		});
	}
}