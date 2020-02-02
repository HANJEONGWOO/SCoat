package com.s7521.angel_m;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.s7521.angel_m.MainActivity.BeAngelDialog;
import com.s7521.angel_m.MainActivity.LoginThread;
import com.s7521.marker.AngelMarker;
import com.s7521.marker.MarkerNode;
import com.s7521.marker.MarkerTree;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SeeAnimal extends Activity{
	LocationManager manager;
	MyLocationListener listener;
	String provider,ImagePath;
	private GoogleMap map;
	ImageView Puppy,Kitten,Dontknow,SeeAll,SeeLocal;
	ArrayList<AngelMarker> AnimalMarkers;
	MarkerTree Markers;
	ImageLoader loader;
	TextView title;
	TextView sinpper;
	ImageView image;
	View WindowView;
	LatLng target;
	int zoomin;
	AngelMarker Marker;
	private static final LatLng SEEALL_KOR =  new LatLng(37.978845,127.2);
	private static final LatLng KGLat = new LatLng(37.71125738622972, 128.27911376953125);
	private static final LatLng KKLat = new LatLng(37.44569832966899, 127.39471435546875);
	private static final LatLng CBLat = new LatLng(36.807772869302966, 127.77786254882812);
	private static final LatLng CNLat = new LatLng(36.45359844696101, 126.97174072265625);
	private static final LatLng GBLat = new LatLng(36.32978568078551, 128.94378662109375);
	private static final LatLng GNLat = new LatLng(35.24842291350237, 128.2708740234375);
	private static final LatLng JBLat = new LatLng(35.72561121496228, 127.20245361328125);
	private static final LatLng JNLat = new LatLng(34.90676854979741, 127.0074462890625);
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.see_animal);
		
		Puppy = (ImageView)findViewById(R.id.PuppyBt);
		Kitten = (ImageView)findViewById(R.id.KittenBt);
		Dontknow = (ImageView)findViewById(R.id.DontknBt);
		SeeAll = (ImageView)findViewById(R.id.SeeAll);
		SeeLocal = (ImageView)findViewById(R.id.SeeLocal);
		AngelMarker Marker;
		
		WindowView = getLayoutInflater().inflate(R.layout.info_window, null);
		image = (ImageView)WindowView.findViewById(R.id.imageView);
		title = (TextView)WindowView.findViewById(R.id.title);
		sinpper = (TextView)WindowView.findViewById(R.id.snippet);
		
		
		
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_animal)).getMap();
		LatLng target=SEEALL_KOR;
		int zoomin = 6;
//		target=SEEALL_KOR;
//		zoomin = 6;
//		
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(target)
		.zoom(zoomin)                   
		.bearing(0)                
		.tilt(0)                 
		.build(); 
		
		
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.setInfoWindowAdapter(new MyInfoWindowAdapter());
		map.setMyLocationEnabled(true);

		
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
				marker.hideInfoWindow();
			}
		});
		
		
		SeeAll.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				map.clear();
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(SEEALL_KOR)
				.zoom(6)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				new MarkerSet().execute();
			}
		});
		
		Puppy.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				map.clear();
				new PuppySet().execute();
			}
		});
		Kitten.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				map.clear();
				new KittenSet().execute();
			}
		});
		Dontknow.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				map.clear();
				new DontknowSet().execute();
			}
		});
		SeeLocal.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				SeeLocalD dialog= new SeeLocalD(SeeAnimal.this);
				dialog.show();
			}
		});
		loader = ImageLoader.getInstance();
		loader.init(ImageLoaderConfiguration.createDefault(SeeAnimal.this));
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
		updateLocation(location);
		listener = new MyLocationListener();
		listener.onLocationChanged(location);
	}
	public void onResume(){
		super.onResume();
		manager.requestLocationUpdates(provider,1000,0, listener);
	}
	public void onPause(){
		super.onPause();
		manager.removeUpdates(listener);
	}
	
	private void updateLocation(Location location) {
		if(location !=null){
			double Latitude = location.getLatitude();
			Log.e("Latitude",Double.toString(Latitude));
		}
	}

	public class SeeLocalD extends Dialog implements android.view.View.OnClickListener{
		ImageButton KK,KG,CN,CB,GN,GB,JN,JB;
		public SeeLocalD(Context context)
		{
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.localmap);
			KK=(ImageButton)findViewById(R.id.KK);
			KK.setOnClickListener(this);
			KG=(ImageButton)findViewById(R.id.KG);
			KG.setOnClickListener(this);
			CN=(ImageButton)findViewById(R.id.CN);
			CN.setOnClickListener(this);
			CB=(ImageButton)findViewById(R.id.CB);
			CB.setOnClickListener(this);
			GB=(ImageButton)findViewById(R.id.GB);
			GB.setOnClickListener(this);
			GN=(ImageButton)findViewById(R.id.GN);
			GN.setOnClickListener(this);
			JN=(ImageButton)findViewById(R.id.JN);
			JN.setOnClickListener(this);
			JB=(ImageButton)findViewById(R.id.JB);
			JB.setOnClickListener(this);
		}
		public void onClick(View v) {
			if(v==KK)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(KKLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==KG)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(KGLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==CN)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(CNLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==CB)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(CBLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==GB)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(GBLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==GN)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(GNLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==JN)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(JNLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			if(v==JB)
			{
				CameraPosition cameraPo = new CameraPosition.Builder()
				.target(JBLat)
				.zoom(8)                   
				.bearing(0)                
				.tilt(0)                 
				.build(); 	
				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPo));
				this.cancel();
			}
			
		}
	}
	public class MyLocationListener implements LocationListener{
		public MyLocationListener()
		{
			Log.e("LocationListener","LocationListener Called!!!");
		}
		public void onLocationChanged(Location location) {
			if(location!=null){
				System.out.println("움직임");
			}
			Log.e("LocationListener","onLocationChanged");
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
				
				//FileInputStream fcon = new FileInputStream(content);
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0,j=0;
				Markers = new MarkerTree();
				while ((s = buffer.readLine()) != null) {
					if(i==8)
					{
						i=0;
						j++;
						Log.e("마커 갯수가 몇개닝"," "+j);
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
	public class PuppySet extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {
			String response = null;
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<BasicNameValuePair> parameter = new ArrayList();
			try{
				parameter.add(new BasicNameValuePair("CASE","GetPuppyMarkers"));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
				httpPost.setEntity(ent);
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0,j=0;
				Markers = new MarkerTree();
				while ((s = buffer.readLine()) != null) {
					if(i==7)
					{
						i=0;
						j++;
						Log.e("마커 갯수가 몇개닝"," "+j);
					}
					if(i==0)
					{
						Marker= new AngelMarker();
						Marker.setNickName(s);
					}
					else if(i==1)
					{
						Marker.setTitle(s);
					}
					else if(i==2)
					{
						Marker.setSnippet(s);
					}
					else if(i==3)
					{
						Marker.setLatitude(Double.parseDouble(s));
					}
					else if(i==4)
					{
						Marker.setLongitude(Double.parseDouble(s));
					}
					else if(i==5)
					{
						Marker.setAnimalFlag(s);
					}
					else if(i==6)
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
			Markers.SetPuppys(Markers.Root, map);
		}
	}
	public class KittenSet extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {
			String response = null;
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<BasicNameValuePair> parameter = new ArrayList();
			try{
				parameter.add(new BasicNameValuePair("CASE","GetKittenMarkers"));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
				httpPost.setEntity(ent);
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0,j=0;
				Markers = new MarkerTree();
				while ((s = buffer.readLine()) != null) {
					if(i==7)
					{
						i=0;
						j++;
						Log.e("마커 갯수가 몇개닝"," "+j);
					}
					if(i==0)
					{
						Marker= new AngelMarker();
						Marker.setNickName(s);
					}
					else if(i==1)
					{
						Marker.setTitle(s);
					}
					else if(i==2)
					{
						Marker.setSnippet(s);
					}
					else if(i==3)
					{
						Marker.setLatitude(Double.parseDouble(s));
					}
					else if(i==4)
					{
						Marker.setLongitude(Double.parseDouble(s));
					}
					else if(i==5)
					{
						Marker.setAnimalFlag(s);
					}
					else if(i==6)
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
			Markers.SetKittens(Markers.Root, map);
		}
	}
	public class DontknowSet extends AsyncTask<Void,Void,String>{

		@Override
		protected String doInBackground(Void... arg0) {
			String response = null;
			String url = "http://192.168.42.77:8080/ANGEL_M_SERVER/Service";
			HttpClient client = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			List<BasicNameValuePair> parameter = new ArrayList();
			try{
				parameter.add(new BasicNameValuePair("CASE","GetDontknMarkers"));
				UrlEncodedFormEntity ent = new UrlEncodedFormEntity(parameter,HTTP.UTF_8);
				httpPost.setEntity(ent);
				HttpResponse execute = client.execute(httpPost);
				InputStream content = execute.getEntity().getContent();
				BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
				String s = "";
				int i = 0,j=0;
				Markers = new MarkerTree();
				while ((s = buffer.readLine()) != null) {
					if(i==7)
					{
						i=0;
						j++;
						Log.e("마커 갯수가 몇개닝"," "+j);
					}
					if(i==0)
					{
						Marker= new AngelMarker();
						Marker.setNickName(s);
					}
					else if(i==1)
					{
						Marker.setTitle(s);
					}
					else if(i==2)
					{
						Marker.setSnippet(s);
					}
					else if(i==3)
					{
						Marker.setLatitude(Double.parseDouble(s));
					}
					else if(i==4)
					{
						Marker.setLongitude(Double.parseDouble(s));
					}
					else if(i==5)
					{
						Marker.setAnimalFlag(s);
					}
					else if(i==6)
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
			Markers.SetDontkns(Markers.Root, map);
		}
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
	public void setInfoWindowView(final Marker marker) {
		title.setText(marker.getTitle());		
		sinpper.setText(marker.getSnippet());
		loader.displayImage("http://192.168.42.77:8080/ANGEL_M_SERVER/PhotoUpload/"+ImagePath, image, new ImageLoadingListener() {
			public void onLoadingCancelled(String arg0, View arg1){}
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) 
			{ //로딩이 완료되면 다시 한번 marker로 showInfoWindow
				marker.showInfoWindow();
			}
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2){}
			public void onLoadingStarted(String arg0, View arg1){}
		});
		
	}
}
