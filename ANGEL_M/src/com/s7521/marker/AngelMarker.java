package com.s7521.marker;

import android.util.Log;

public class AngelMarker{
	private String ID;
	String NickName;
	String Title; 
	String Snippet;
	double Latitude;
	double Longitude;
	String AnimalFlag;
	String ImagePath;
	public AngelMarker()
	{
		setID("NoID");	
		setNickName("NoNickName");
		setTitle(null);
		setSnippet(null);
		setLatitude(0.00);
		setLongitude(0.00);
	}
	public void setNickName(String NickName)
	{
		this.NickName = NickName;
	}
	public void setTitle(String Title)
	{
		this.Title = Title;
	}
	public void setSnippet(String Snippet)
	{
		this.Snippet = Snippet;
	}
	public void setLatitude(double Latitude)
	{
		this.Latitude = Latitude;
	}
	public void setLongitude(double Longitude)
	{
		this.Longitude = Longitude;
	}
	public String getNickName()
	{
		return this.NickName;
	}
	public String getTitle()
	{
		return this.Title;
	}
	public String getSnippet()
	{
		return this.Snippet;
	}
	public double getLatitude()
	{
		return this.Latitude;
	}
	public double getLongitude()
	{
		return this.Longitude;
	}
	public void setAnimalFlag(String AnimalFlag) {
		this.AnimalFlag = AnimalFlag;
	}
	public void setImagePath(String ImagePath) {
		this.ImagePath = ImagePath;
	}
	public String getAnimalFlag()
	{
		return this.AnimalFlag;
	}
	public String getImagePath()
	{
		return this.ImagePath;
	}
	public String getID()
	{
		return this.ID;
	}
	public void setID(String ID) {
		this.ID = ID;
	}
}
