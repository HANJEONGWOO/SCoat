package com.s7521.marker;


public class MarkerNode {
	public AngelMarker Marker;
	MarkerNode Left;
	MarkerNode Right;
	public MarkerNode()
	{
		Marker=null;
		this.Left=null;
		this.Right=null;
	}
	public MarkerNode(AngelMarker Marker)
	{
		this.Marker = Marker;
		this.Left = null;
		this.Right = null;
	}
	double GetLat()
	{
		return this.Marker.getLatitude();
	}
	public void setLat(double Latitude)
	{
		this.Marker.Latitude=Latitude;
	}
	MarkerNode GetLSubTree()
	{
		return this.Left;
	}
	MarkerNode GetRSubTree()
	{
		return this.Right;
	}
	void MakeLSubTree(MarkerNode LSub)
	{
		this.Left = LSub;
	}
	void MakeRSubTree(MarkerNode RSub)
	{
		this.Right = RSub;
	}
}
