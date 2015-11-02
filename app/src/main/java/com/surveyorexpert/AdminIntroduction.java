package com.surveyorexpert;
//import com.example.demo.R;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
//import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
//import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AdminIntroduction extends Activity {

	String URL = "http://www.surveyorexpert.com/webservice/logos/"; // yes 

	private Button enter = null;	
	private Bitmap bitmap = null;
	private String /*resource, project,*/ company, logoName; 
	private TextView organisation;
	private ImageView logo;
	private Spinner Resource, Project;
	private String user_id, userName, domain;
	private int success = 0;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	
	private JSONArray mResource = null;
	private ArrayList<HashMap<String, String>> mResourceList;
	
	private JSONArray mProject = null;
	private ArrayList<HashMap<String, String>> mProjectList;
	
	private ArrayList<HashMap<String, String>> mTmpList;
		
	private JSONObject json  = null;
	List<NameValuePair> jsonParams = null;
	
	HashMap<String, String> mOut = null;

	private String state;
	
	private static final String GETRESOURCE_URL =
			 "http://www.surveyorexpert.com/webservice/getResource.php";
	private static final String GETPROJECT_URL =
			 "http://www.surveyorexpert.com/webservice/getProject.php";
	private static final String GETORGANISATION_URL =
			 "http://www.surveyorexpert.com/webservice/getOrganisation.php";

	// JSON element ids from response of php script:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_USERID = "user_id";
	private static final String TAG_SURNAME = "surname";
	private static final String TAG_PROJECT = "project_name";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_COMPANY = "company";
	private static final String TAG_LOGO = "logo_name";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_introduction);
	
		Bundle extras = getIntent().getExtras(); 
		
		if (extras != null) {
			user_id = extras.getString("user_id"); 
			userName = extras.getString("userName"); 
			domain = extras.getString("domain"); 
				
				}

		Log.d(".............ADMIN...........", "Instantiate data object");
		new GetResAndProj().execute();	
	
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String online = preferences.getString("ONLINE","");
		if(!online.equalsIgnoreCase(""))
		{
			state = online;
		}

		
		//setTitle(" User: " + userName + " User_id:  " + user_id 
		//		+  " Domain:  " + domain +  " ONLINE State =  " + state);
		
		setTitle(" OFF LINE SET UP");
		
		logo = (ImageView) findViewById(R.id.ivIntroLogo);

	}
	

	public void addItemsOnProject() {
	 
		Project = (Spinner) findViewById(R.id.spEditEstimate);
		List<String> list = new ArrayList<String>();
		
		//mProjectList. is a ArrayList of maps	
		for (int i = 0; i < mProjectList.size(); i++) {
		        mOut = mProjectList.get(i); 
		        for (String key: mOut.keySet()) {
			    list.add( mOut.get(key));        
		   }  
		}
 
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
			android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Project.setAdapter(dataAdapter);	
	  }
	
	public void addItemsOnResource() {
			
		//	Log.d("CALUM", "About to add items to Resource list" + Integer.toString(mResourceList.size()));
		//	Log.d("CALUM", "About to add TMPLIST to Resource list" + Integer.toString(mTmpList.size()));
			  
			Resource = (Spinner) findViewById(R.id.spEditSeverity);		
			List<String> list = new ArrayList<String>();
			
			//mResourceList. is a ArrayList of maps	
			/* CGM Check this out
			for (int i = 0; i < mResourceList.size(); i++) {
			   mOut = mResourceList.get(i); 
			   for (String key: mOut.keySet()) {
				    list.add( mOut.get(key));        
			   }  
			}*/
			
			for (int i = 0; i < mTmpList.size(); i++) {
				   mOut = mTmpList.get(i); 
				   for (String key: mOut.keySet()) {
					    list.add( mOut.get(key));        
				   }  
				}
					
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Resource.setAdapter(dataAdapter);
			
		  }	  
		  
	public void addListenerOnSpinnerItemSelection() {
		  
		Resource = (Spinner) findViewById(R.id.spEditSeverity);
		Resource.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	
	  }

	  // get the selected dropdown list value
	public void addListenerOnButton() {
		
		Log.d(".............ADMIN...........", "addListenerOnButton started");

		enter = (Button) findViewById(R.id.bAdminIntroEnter);

	    enter.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
					Log.d(".............ADMIN...........", "CLICK");
					Toast t = Toast.makeText(AdminIntroduction.this, "LETS GO!!!!!!!!!!", Toast.LENGTH_SHORT);
					t.show();
			  }
	   	  });		
	  }

	
	class GetResAndProj extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
		/*
			json = new JSONObject();
			jsonParams = new ArrayList<NameValuePair>();
			jsonParams.add(new BasicNameValuePair("user_id", user_id));
			
			
			populateResourceList();
			populateProjectList();
			populateOrganisation();
			populateLogo();
			*/
			return null;
		}

		private void populateLogo() {
			
			String imageURL = URL + logoName;			
			try {
				// Download Image from URL
				InputStream input = new java.net.URL(imageURL).openStream();
				// Decode Bitmap
				bitmap = BitmapFactory.decodeStream(input);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}

		protected void onPostExecute(String file_url) {
	
			/*
			addItemsOnResource();
			addItemsOnProject();
			*/
			addListenerOnButton();
			/*
	   	    addListenerOnSpinnerItemSelection(); 	
	   		organisation.setText(company);
	   		logo.setImageBitmap(bitmap);
	   		*/
			
	   		Toast t = Toast.makeText(AdminIntroduction.this, "Post execute", Toast.LENGTH_SHORT);
			t.show();
	   		
	   		
	   		// CGM Temp switch on company - get image from server
	   		/*
			if(company.contentEquals("HSBC"))
		   		logo.setImageResource(R.drawable.hsbclogo);
			if(company.contentEquals("FXInc"))
		   		logo.setImageResource(R.drawable.button_check);
			if(company.contentEquals("Balneaves"))
		   		logo.setImageResource(R.drawable.logo); 
		   		*/ 	
		}
	
		public void populateOrganisation() {
			
			try{	
				json = jsonParser.makeHttpRequest(GETORGANISATION_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);		
			} catch(Exception e){
				e.printStackTrace();
			}

			if (success == 1) {		
				mResourceList = new ArrayList<HashMap<String, String>>();
				try {
					mResource = json.getJSONArray(TAG_POSTS);
					for (int i = 0; i < mResource.length(); i++) {
					
						JSONObject c = mResource.getJSONObject(i);
				
						String user_id = c.getString(TAG_USERID);
						String comp = c.getString(TAG_COMPANY);
						String lName = c.getString(TAG_LOGO);
					
						company = comp;
						logoName = lName;
					}	   
			    json.getString(TAG_MESSAGE);   
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}	
		
		private void populateResourceList() {
			
			try{	
				json = jsonParser.makeHttpRequest(GETRESOURCE_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);		
			} catch(Exception e){
				e.printStackTrace();
			}

			if (success == 1) {		
				mResourceList = new ArrayList<HashMap<String, String>>();
				try {
					mResource = json.getJSONArray(TAG_POSTS);
					for (int i = 0; i < mResource.length(); i++) {
					JSONObject c = mResource.getJSONObject(i);
				
					String user_id = c.getString(TAG_USERID);
					String surname = c.getString(TAG_SURNAME);
				
					// creating new HashMap
					HashMap<String, String> map = new HashMap<String, String>();
					map.put(TAG_USERID, surname);
					
					// adding HashList to ArrayList
					mResourceList.add(map);	
				}
			   
			    json.getString(TAG_MESSAGE);   
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mTmpList = mResourceList;
			//	Log.d("CALUM", "populateResourceList OK " + Integer.toString(mResourceList.size()));
			}
		}

		private void populateProjectList() {
			
			try{	
				json = jsonParser.makeHttpRequest(GETPROJECT_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);		
			} catch(Exception e){
				e.printStackTrace();
			}

			if (success == 1) {
		
				mProjectList = new ArrayList<HashMap<String, String>>();
				try {
					mProject = json.getJSONArray(TAG_POSTS);
					for (int i = 0; i < mProject.length(); i++) 
					{
						JSONObject c = mProject.getJSONObject(i);
				
						String user_id = c.getString(TAG_USERID);
						String project_name = c.getString(TAG_PROJECT);
				
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_USERID, project_name);
					
						// adding HashList to ArrayList
						mProjectList.add(map);	
					}
			   
			    json.getString(TAG_MESSAGE);   
				} catch (JSONException e) {
					e.printStackTrace();
				}	
			}
		}		
	}
}
