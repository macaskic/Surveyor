package com.surveyorexpert;

//import com.example.demo.R;

//import com.androidexample.listview.R;
//import com.androidexample.listview.R;
//import com.example.darsh.popup.R;
//import com.example.darsh.popup.R;
//import com.surveyorexpert.Editor.GetSeverityAndCost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ExpandableListActivity;
//import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
//import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
import android.view.MenuItem;
//import android.view.MotionEvent;
import android.view.View;
//import android.view.WindowManager;
import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
//import android.widget.ListView;
//import android.widget.PopupWindow;
//import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ExpandableListMain extends ExpandableListActivity implements OnClickListener
{
	//Initialize variables
	private int ParentClickStatus=-1;
	private int ChildClickStatus=-1;
	private ArrayList<Parent> parents;
	private String resource, project, userName;
	private String domain, user_id, ONLINE;
	private int success = 0;
	
	private static final String READ_ELEMENT_URL = "http://www.surveyorexpert.com/webservice/getElement2.php";
	private static final String READ_SECTION_URL = "http://www.surveyorexpert.com/webservice/getSection2.php";
	
	// JSON IDS:
	
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_ELEMENT = "element";
	private static final String TAG_ELEMENT_ID = "element_id";
	private static final String TAG_SECTION = "section";
	private static final String TAG_SECTION_ID = "section_id";

	private JSONObject json  = null;
	private List<NameValuePair> jsonParams = null;
	private JSONParser jsonParser = new JSONParser();
	
	private JSONArray mElement = null;
	private ArrayList<HashMap<String, String>> mElementList;
	private JSONArray mSection = null;
	private ArrayList<HashMap<String, String>> mSectionList;
	
	private HashMap<String, String> mOut = null;
	private HashMap<String, String> map = null;
	
	List<String> elemList = new ArrayList<String>();
	List<String> sectList = new ArrayList<String>();
	
	//List<String> compList = new ArrayList<String>();
//	HashMap<String, String> mapComponentIndex = new HashMap<String, String>();
//	private String state;

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	
		Bundle extras = getIntent().getExtras(); 

		if (extras != null) {
			project = extras.getString("project");
			resource = extras.getString("resource");  
			userName = extras.getString("userName"); 
			user_id = extras.getString("user_id"); 
			domain = extras.getString("domain"); 
			ONLINE  =  extras.getString("ONLINE"); 
		}
		
		/*
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String online = preferences.getString("ONLINE","");
		if(!online.equalsIgnoreCase(""))
		{
			ONLINE = online;
		}

		
		 setTitle("u/n " + userName
				 + "  user_id:  " + user_id 	
				 + "  resource:  " + resource 
				 + "  project:  " + project 
				 + "  domain: " + domain
				 + "    ONLINE = " + ONLINE);
				 */
			
		setTitle(" Select Item");
		
		Resources res = this.getResources();
	    Drawable divider = res.getDrawable(R.drawable.line);
		
	    // Set ExpandableListView values
	    getExpandableListView().setGroupIndicator(null);
		getExpandableListView().setDivider(divider);
		getExpandableListView().setChildDivider(divider);
		getExpandableListView().setDividerHeight(1);
		registerForContextMenu(getExpandableListView());
		
		Log.d("ExpandableListMain","Start Domain" );	
		new GetDomainData().execute();	
		
	}
	
	@Override
	public boolean   onOptionsItemSelected(MenuItem item) {
		/*
		switch (item.getItemId()) {
		   case R.id.camera:
		     break;
		   default:
		     break;
		   }
		   */
		return true;
	} 
	
	private ArrayList<Parent> buildData() {
		// Creating ArrayList of type parent class to store parent class objects
		final ArrayList<Parent> list = new ArrayList<Parent>();
	//	Log.d("ExpandableListMain.........IMPORTANT num elements = ", Integer.toString(elemList.size()));
	//	Log.d("ExpandableListMain.........IMPORTANT num sections = ", Integer.toString(sectList.size()));
			
		int countElement = 0;
		int countSection = 0;
		int countComponent = 0;
				
		for (int i = 0; i < elemList.size(); i++)
		{
			countElement++;
			countSection = 0;
	//		Log.d("ExpandableListMain Element loop  countSection = " , Integer.toString(countElement) + " " +
	//						Integer.toString(countSection));
				
			//Create parent class object
			final Parent parent = new Parent();
			parent.setName("" + i);
			parent.setText1(elemList.get(i));
					
	//		Log.d("ExpandableListMain..........SECTION", "mSectionList = " + Integer.toString(mSectionList.size()));
					
			parent.setChildren(new ArrayList<Child>());
					
			for (int  x = 0; x < mSectionList.size(); x++) 
			{	
				countSection++;
				mOut = mSectionList.get(x); 
		  
		///		Log.d("ExpandableListMain  mOut count = " , Integer.toString(mOut.size()));
			
				for (Map.Entry<String,String> entry : mOut.entrySet()) {
					//countSection =0; 
					
		//			Log.d("ExpandableListMain..........PROBLEM", Integer.toString(i+1));
					
					if (entry.getKey().contentEquals(Integer.toString(i+1)))
					{
						
						countSection++;
					//	Log.d("ExpandableListMain Section loop  countSection = " , 
					//			Integer.toString(countElement) + " " +
					//			Integer.toString(countSection)+ " " +
					//			Integer.toString(countComponent));
						
					//	 mapComponentIndex.put(Integer.toString(countElement) + "|" +
					//				Integer.toString(countSection), 
					//				Integer.toString(countComponent));
						 	   
						final Child child = new Child();
						child.setName("" + i);
						child.setText1(entry.getValue());
						parent.getChildren().add(child);   
							  
						countComponent++;
								  
			//			Log.d("ExpandableListMain..........NUMBERS", 
			//				"countElement = " + Integer.toString(countElement) +
			//				" countSection = " +  Integer.toString(countSection) +
			//				" countComponent = " +  Integer.toString(countComponent));	
					
					}
				}		
			}	
			list.add(parent);
		}
		return list;
	}
	
	
	
	private void loadHosts(final ArrayList<Parent> newParents)
	{
		if (newParents == null)
			return;
		
		parents = newParents;
		
		// Check for ExpandableListAdapter object
		if (this.getExpandableListAdapter() == null)
		{
			 //Create ExpandableListAdapter Object
			final MyExpandableListAdapter mAdapter = new MyExpandableListAdapter();
			
			// Set Adapter to ExpandableList Adapter
			this.setListAdapter(mAdapter);
		}
		else
		{
			 // Refresh ExpandableListView data 
			((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
		}	
		
	}

	/**
	 * A Custom adapter to create Parent view (Used grouprow.xml) and Child View((Used childrow.xml).
	 */
	private class MyExpandableListAdapter extends BaseExpandableListAdapter
	{
		

		private LayoutInflater inflater;

		public MyExpandableListAdapter()
		{
			// Create Layout Inflator
			inflater = LayoutInflater.from(ExpandableListMain.this);
		}
    
		
		// This Function used to inflate parent rows view
		
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, 
				View convertView, ViewGroup parentView)
		{
			final Parent parent = parents.get(groupPosition);
			
			// Inflate grouprow.xml file for parent rows
			convertView = inflater.inflate(R.layout.grouprow, parentView, false); 
			
			// Get grouprow.xml file elements and set values
			((TextView) convertView.findViewById(R.id.text1)).setText(parent.getText1());
			((TextView) convertView.findViewById(R.id.text)).setText(parent.getText2());
			ImageView image=(ImageView)convertView.findViewById(R.id.image);
			image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"+parent.getName(),null,null));
			ImageView rightcheck=(ImageView)convertView.findViewById(R.id.rightcheck);
			
			//Log.i("ExpandableListMain", "isChecked: "+parent.isChecked());
			
			// Change right check image on parent at runtime
			if(parent.isChecked()==true){
				rightcheck.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/rightcheck",null,null));
			}	
			else{
				rightcheck.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/button_check",null,null));
			}	
			
			// Get grouprow.xml file checkbox elements
			CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
			checkbox.setChecked(parent.isChecked());
			
			// Set CheckUpdateListener for CheckBox (see below CheckUpdateListener class)
			checkbox.setOnCheckedChangeListener(new CheckUpdateListener(parent));
			
			return convertView;
		}

		
		// This Function used to inflate child rows view
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, 
				View convertView, ViewGroup parentView)
		{
			final Parent parent = parents.get(groupPosition);
			final Child child = parent.getChildren().get(childPosition);
			
			// Inflate childrow.xml file for child rows
			convertView = inflater.inflate(R.layout.childrow, parentView, false);
			
			// Get childrow.xml file elements and set values
			((TextView) convertView.findViewById(R.id.text1)).setText(child.getText1());
			ImageView image=(ImageView)convertView.findViewById(R.id.image);
			image.setImageResource(getResources().getIdentifier("com.androidexample.customexpandablelist:drawable/setting"+parent.getName(),null,null));
			
			return convertView;
		}

		
		@Override
		public Object getChild(int groupPosition, int childPosition)
		{
			//Log.i("ExpandableListMain", groupPosition+"=  getChild =="+childPosition);
			return parents.get(groupPosition).getChildren().get(childPosition);
		}

		//Call when child row clicked
		@Override
		public long getChildId(int groupPosition, int childPosition)
		{
			/****** When Child row clicked then this function call *******/
			
			if( ChildClickStatus!=childPosition)
			{
			   ChildClickStatus = childPosition;
			   
		//	   Toast.makeText(getApplicationContext(), "Parent :"+groupPosition + " Child :"+childPosition , 
		//				Toast.LENGTH_LONG).show();
			}  
			
			return childPosition;
		}

		@Override
		public int getChildrenCount(int groupPosition)
		{
			int size=0;
			if(parents.get(groupPosition).getChildren()!=null)
				size = parents.get(groupPosition).getChildren().size();
			return size;
		}
     
		
		@Override
		public Object getGroup(int groupPosition)
		{		
			return parents.get(groupPosition);
		}

		@Override
		public int getGroupCount()
		{
			return parents.size();
		}

		//Call when parent row clicked
		@Override
		public long getGroupId(int groupPosition)
		{
		//	Log.i("ExpandableListMain", groupPosition+"=  getGroupId "+ParentClickStatus);
			
			if(groupPosition==2 && ParentClickStatus!=groupPosition){
				
				//Alert to user
			//	Toast.makeText(getApplicationContext(), "Parent :"+groupPosition , 
			//			Toast.LENGTH_LONG).show();
			}
			
			ParentClickStatus=groupPosition;
			if(ParentClickStatus==0)
				ParentClickStatus=-1;
			
			return groupPosition;
		}

		@Override
		public void notifyDataSetChanged()
		{
			// Refresh List rows
			super.notifyDataSetChanged();
		}

		@Override
		public boolean isEmpty()
		{
			return ((parents == null) || parents.isEmpty());
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition)
		{
			return true;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return true;
		}
		
		
		
		/******************* Checkbox Checked Change Listener ********************/
		
		private final class CheckUpdateListener implements OnCheckedChangeListener
		{
			private final Parent parent;
			
			private CheckUpdateListener(Parent parent)
			{
				this.parent = parent;
			}
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
			//	Log.i("ExpandableListMain", "isChecked: "+isChecked);
				parent.setChecked(isChecked);
				
				((MyExpandableListAdapter)getExpandableListAdapter()).notifyDataSetChanged();
				
				final Boolean checked = parent.isChecked();
			//	Toast.makeText(getApplicationContext(), "Parent : "+parent.getName() + " " + (checked ? STR_CHECKED : STR_UNCHECKED), 
			//			Toast.LENGTH_LONG).show();
			}
		}
		/***********************************************************************/	
	}

	
	
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		
		String element = null;
		String section = null;	
		
		element = elemList.get(groupPosition);		
	    Parent p  = (Parent) parents.get(groupPosition);
	    
	    String parentName = p.getText1();
	    element = p.getText1();
	    
	    ArrayList<Child> c = p.getChildren();
	    Child s =  c.get(childPosition);
	    String childName =  s.getText1();
	    section = s.getText1();
	 
		/*
		Toast.makeText(getApplicationContext(),
				"\nParent name =  " + parentName 
				+ "\nChild name" + childName, 
				Toast.LENGTH_LONG).show();
		*/
			

		Class myClass;
		try {
			Log.d("ExpandableListMain", "Try to get Editor");
			myClass = Class.forName("com.surveyorexpert.DetailListView");
			Intent ourIntent = new Intent(getApplicationContext(), myClass);
			ourIntent.putExtra("project", project);
			ourIntent.putExtra("resource", resource);	
			ourIntent.putExtra("userName", userName);  	
			ourIntent.putExtra("domain", domain);  	
			ourIntent.putExtra("user_id", user_id);  	
			ourIntent.putExtra("childPos", Integer.toString(childPosition+1));
			ourIntent.putExtra("parentPos", Integer.toString(groupPosition+1) );	
			ourIntent.putExtra("section", section);
			ourIntent.putExtra("element", element );	
			ourIntent.putExtra("ONLINE", ONLINE);  	
								
			startActivity(ourIntent);
			
			/*
	   		Toast.makeText(this, 
						"ExpandableListMain \n userName = " + userName +
						"\n user_id = " + user_id +
						"\n domain = " + domain + 
						"\n project = " + project + 
						"\n resource = " + resource + 	
						"\n parentPos = " + Integer.toString(groupPosition+1) + 	
						"\n childPos = " + Integer.toString(childPosition+1) + 	
						"\n ONLINE = " + ONLINE , 
						Toast.LENGTH_LONG).show();
			*/
			
			
		//	finish();
		} catch (ClassNotFoundException e) {
		//	Toast t = Toast.makeText(ExpandableListMain.this, "Class not found", Toast.LENGTH_SHORT);
		//	t.show();
			Log.d("ExpandableListMain", "Failing here");
			e.printStackTrace();
		}
		
		
		return super.onChildClick(parent, v, groupPosition, childPosition, id);
		
	}
	@Override
	public void onClick(View arg0) {
		// not used
	}
	
	
	/************************************* Get DATA  *********************************/
	class GetDomainData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		//	Toast.makeText(getApplicationContext(), "GetDomainData  onPreExecute", Toast.LENGTH_LONG).show();
		}

		@Override
		protected String doInBackground(String... args) {
		
			json = new JSONObject();
			jsonParams = new ArrayList<NameValuePair>();
			jsonParams.add(new BasicNameValuePair("domain", domain));
			jsonParams.add(new BasicNameValuePair("user_id", user_id));
				
			Log.d("ExpandableListMain ", "domain = " + domain);
			
			addItemsOnElement();
			addItemsOnSection();
				
			return null;
		}

	
		
		protected void onPostExecute(String file_url) {

			Log.d("ExpandableListMain.........elemList", Integer.toString(elemList.size()));
			Log.d("ExpandableListMain.........sectList", Integer.toString(sectList.size()));
			Log.d("ExpandableListMain.........mSectionList", Integer.toString(mSectionList.size()));

			final ArrayList<Parent> dataList = buildData();	
			loadHosts(dataList);
			
		//	Toast.makeText(getBaseContext(),"GetDomainData onPostExecute called" , Toast.LENGTH_LONG).show();
		}
		
	

		private void addItemsOnElement() {
	
			try{	
				//getElement2.php
				json = jsonParser.makeHttpRequest(READ_ELEMENT_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);	
			//	Log.d("ExpandableListMain ", "Element Good Call success = " + Integer.toString(success));
			} catch(Exception e){
			//	Log.d("ExpandableListMain ","Element Exception Success = " + Integer.toString(success));
				e.printStackTrace();
			}

			if (success == 1) {	
				Log.d("ExpandableListMain", "Good Call success = " + Integer.toString(success));
				
				mElementList = new ArrayList<HashMap<String, String>>();
			
				try {
					mElement = json.getJSONArray(TAG_POSTS);
					Log.d("ExpandableListMain", "mElement = " + Integer.toString(mElement.length()));
					
					for (int i = 0; i < mElement.length(); i++) {
						
						JSONObject c = mElement.getJSONObject(i);
				
						String element_id = c.getString(TAG_ELEMENT_ID);
						String element = c.getString(TAG_ELEMENT);
				
						// creating new HashMap
						map = new HashMap<String, String>();
						map.put(TAG_ELEMENT_ID, element);
					
						// adding HashList to ArrayList
						mElementList.add(map);	
						elemList.add(element);
					}  
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("ExpandableListMain", "mElementList = " + Integer.toString(mElementList.size()));
			}			
			
				/*	
			for (int i = 0; i < mSeverityList.size(); i++) {
				   mOut = mSeverityList.get(i); 
				   for (String key: mOut.keySet()) {
					    sevList.add( mOut.get(key));        
				   }  
				}
			Log.d("ExpandableListMain", "Created Severity pop = " + Integer.toString(sevList.size()));
			*/
		}
		
		private void addItemsOnSection() {
			try{	
				//getSection2.php
				json = jsonParser.makeHttpRequest(READ_SECTION_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);	
				Log.d("ExpandableListMain", "Section Good Call success = " + Integer.toString(success));
			} catch(Exception e){
				Log.d("ExpandableListMain","Section Exception Success = " + Integer.toString(success));
				e.printStackTrace();
			}


			if (success == 1) {	
				Log.d("ExpandableListMain", "Good Call success = " + Integer.toString(success));
				
				mSectionList = new ArrayList<HashMap<String, String>>();
			
				try {
					mSection  = json.getJSONArray(TAG_POSTS);
					Log.d("ExpandableListMain", "mSection = " + Integer.toString(mSection .length()));
					
					for (int i = 0; i < mSection .length(); i++) {
						
						JSONObject c = mSection .getJSONObject(i);
				
						String element_id = c.getString(TAG_ELEMENT_ID);
						String section_id = c.getString(TAG_SECTION_ID);
						String section = c.getString(TAG_SECTION);
				
						// creating new HashMap
						//HashMap<String, String>
						map = new HashMap<String, String>();
						//map.put(TAG_SECTION_ID, section);
						map.put(element_id, section);
						//Log.d("ExpandableListMain", "element_id =" + element_id + "  section = " + section);
						// adding HashList to ArrayList
						mSectionList.add(map);	
						sectList.add(section);
					}  
				} catch (JSONException e) {
	
					Log.d("ExpandableListMain   ", e.getMessage());
					e.printStackTrace();
				}
				Log.d("ExpandableListMain ", "mSectionList = " + Integer.toString(mSectionList.size()));
			}			
				
			//Log.d("ExpandableListMain", "Created Cost pop = " + Integer.toString(costList.size()));	
			
		}
	}
}

