package com.s7521.marker;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.s7521.angel_m.R;
public class MarkerTree {
	public MarkerNode Root;
	public MarkerTree()
	{
		this.Root = null;
	}
	public double BSTGetNodeData()
	{
		return Root.Marker.getLatitude();
	}
	public void BSTInsert(AngelMarker Marker)
	{
		MarkerNode Parent = null;
		MarkerNode Current = this.Root;
		MarkerNode New = new MarkerNode(Marker);

		while(Current !=null)
		{
			if(Marker.getLatitude() == Current.GetLat())
				return;

			Parent = Current;

			if(Current.GetLat()>Marker.getLatitude())
			{
				Current = Current.GetLSubTree();
			}
			else
			{
				Current = Current.GetRSubTree();
			}
		}
		if(Parent != null)
		{
			if(Marker.getLatitude() < Parent.GetLat())
			{
				Parent.MakeLSubTree(New);
			}
			else
			{
				Parent.MakeRSubTree(New);
			}
		}
		else
		{
			this.Root = New;
		}
	}
	
	
	public String BSTSearch(double Target)
	{
		MarkerNode Current = this.Root;
		double CurrentData;

		while(Current != null)
		{
			CurrentData = Current.GetLat();

			if(Target==CurrentData)
				return Current.Marker.getImagePath();
			else if(Target <CurrentData)
				Current = Current.GetLSubTree();
			else
				Current = Current.GetRSubTree();
		}
		return null; // 탐색 대상이 저장되어 있지 않음
	}
	public String BSTSearchID(double Target)
	{
		MarkerNode Current = this.Root;
		double CurrentData;

		while(Current != null)
		{
			CurrentData = Current.Marker.getLatitude();
			
			if(Target==CurrentData){
				Log.e("ㅇㅇ",Current.Marker.getID());
				return Current.Marker.getID();
			}
			else if(Target <CurrentData)
				Current = Current.GetLSubTree();
			else
				Current = Current.GetRSubTree();
		}
		return null; // 탐색 대상이 저장되어 있지 않음
	}

	//왼쪽 자식노드를 트리에서 제거, 제거된 노드 반환
	
	
	public MarkerNode RemoveLSubTree(MarkerNode bt)
	{
		MarkerNode delNode = null;
		if(bt!=null)
		{
			delNode = bt.Left;
			bt.Left = null;
		}
		return delNode;
	}
	public MarkerNode RemoveRSubTree(MarkerNode bt)
	{
		MarkerNode delNode = null;
		if(bt!=null)
		{
			delNode = bt.Right;
			bt.Right = null;
		}
		return delNode;
	}
	
	public void ChangeLSubTree(MarkerNode main,MarkerNode sub)
	{
		main.Left = sub;
	}
	public void ChangeRSubTree(MarkerNode main,MarkerNode sub)
	{
		main.Right = sub;
	}
	
	public MarkerNode BSTRemove(double Target)
	{
		MarkerNode vRoot = new MarkerNode();
		
		MarkerNode Parent = this.Root;
		MarkerNode Current = this.Root;
		MarkerNode Del = null;

		ChangeRSubTree(vRoot,Root);
		
		while(Current!=null && Current.GetLat() !=Target)
		{
			Parent = Current;
			if(Target < Current.GetLat())
				Current = Current.GetLSubTree();
			else
				Current = Current.GetRSubTree();
		}
		if(Current == null)
			return null;

		Del = Current;

		if(Del.GetLSubTree()==null & Del.GetRSubTree()==null)
		{
			if(Parent.GetLSubTree() == Del)
				RemoveLSubTree(Parent);
			else
				RemoveRSubTree(Parent);
		}
		else if(Del.GetLSubTree()==null|Del.GetRSubTree()==null)
		{
			MarkerNode DelCurrent;

			if(Del.GetLSubTree() !=null)
				DelCurrent = Del.GetLSubTree();
			else
				DelCurrent = Del.GetRSubTree();

			if(Parent.GetLSubTree()==Del)
				ChangeLSubTree(Parent,DelCurrent);
			else
				ChangeRSubTree(Parent,DelCurrent);
		}
		else
		{
			MarkerNode mNode = Del.GetRSubTree();
			MarkerNode mpNode = Del;
			double DelData;

			while(mNode.GetLSubTree()!=null){
				mpNode = mNode;
				mNode = mNode.GetLSubTree();
			}
			DelData = Del.GetLat();
			Del.setLat(mNode.GetLat());

			if(mpNode.GetLSubTree() == mNode)
				ChangeLSubTree(mpNode,mNode.GetRSubTree());
			else
				ChangeRSubTree(mpNode,mNode.GetRSubTree());

			Del = mNode;
			Del.setLat(DelData);
		}
		if(vRoot.GetRSubTree() != Root)
			Root = vRoot.GetRSubTree();
		vRoot = null;
		return Del;
	}
	
	public void SetKittens(MarkerNode node,GoogleMap map)
	{
		if( node == null ) //bt 가 NULL 이라면 재귀 탈출! 
			return;
		SetMarkers(node.Left,map);
		map.addMarker(new MarkerOptions()
		.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
		.title(node.Marker.getTitle())
		.snippet(node.Marker.getSnippet()
				+"\n작성자 - "+node.Marker.getNickName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.kitten)));
		SetMarkers(node.Right,map);
		
	}
	public void SetPuppys(MarkerNode node,GoogleMap map)
	{
		if( node == null ) //bt 가 NULL 이라면 재귀 탈출! 
			return;
		SetMarkers(node.Left,map);
		map.addMarker(new MarkerOptions()
		.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
		.title(node.Marker.getTitle())
		.snippet(node.Marker.getSnippet()
				+"\n작성자 - "+node.Marker.getNickName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.puppy)));
		SetMarkers(node.Right,map);
		
	}
	public void SetDontkns(MarkerNode node,GoogleMap map)
	{
		if( node == null ) //bt 가 NULL 이라면 재귀 탈출! 
			return;
		SetMarkers(node.Left,map);
		map.addMarker(new MarkerOptions()
		.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
		.title(node.Marker.getTitle())
		.snippet(node.Marker.getSnippet()
				+"\n작성자 - "+node.Marker.getNickName())
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.dontknow)));
		SetMarkers(node.Right,map);
		
	}
	public void SetMarkers(MarkerNode node,GoogleMap map)
	{
		if( node == null ) //bt 가 NULL 이라면 재귀 탈출! 
			return;
		SetMarkers(node.Left,map);
		if(node.Marker.getAnimalFlag().equals("강아지"))
		{
			map.addMarker(new MarkerOptions()
			.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
			.title(node.Marker.getTitle())
			.snippet(node.Marker.getSnippet()
					+"\n작성자 - "+node.Marker.getNickName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.puppy)));
		}
		if(node.Marker.getAnimalFlag().equals("고양이"))
		{
			map.addMarker(new MarkerOptions()
			.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
			.title(node.Marker.getTitle())
			.snippet(node.Marker.getSnippet()
					+"\n작성자 - "+node.Marker.getNickName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.kitten)));
		}
		if(node.Marker.getAnimalFlag().equals("다른 동물"))
		{
			map.addMarker(new MarkerOptions()
			.position(new LatLng(node.Marker.getLatitude(),node.Marker.getLongitude()))
			.title(node.Marker.getTitle())
			.snippet(node.Marker.getSnippet()
					+"\n작성자 - "+node.Marker.getNickName())
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.dontknow)));
		}
		SetMarkers(node.Right,map);
	}
}
