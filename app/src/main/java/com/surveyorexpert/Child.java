package com.surveyorexpert;

import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.Toast;

public class Child implements OnClickListener
{
	private String name;
	private String text1;
	private String text2;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getText1()
	{
		return text1;
	}
	
	public void setText1(String text1)
	{
		this.text1 = text1;
	}
	
	public String getText2()
	{
		return text2;
	}
	
	public void setText2(String text2)
	{
		this.text2 = text2;
	}

	@Override
	public void onClick(View v) {
		//Toast.makeText(getApplicationContext(), "Child  Selected:",Toast.LENGTH_SHORT).show();
		
	}
}
