package com.surveyorexpert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import com.surveyorexpert.ExpandableListMain.GetDomainData;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
//import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class DetailListView extends Activity {
	ListView listView ;
	private String resource, project, userName;
	private String  retMessage, user_id, domain, itemValue, mbcode, description;
	private String childPos, parentPos, nbcMarker, nbcDescription, ONLINE, section, element;
	private int success = 0;
	private String[] mbcodes = null;
	private String[] descriptions = null;
	private String[] values = null;
	
	ArrayAdapter<String> adapter = null;
	
	private static final String READ_COMPONENT_URL 
			= "http://www.surveyorexpert.com/webservice/getComponent2.php";
	
	// JSON IDS:
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_POSTS = "posts";
	private static final String TAG_ELEMENT = "element";
	private static final String TAG_ELEMENT_ID = "element_id";
	private static final String TAG_SECTION = "section";
	private static final String TAG_SECTION_ID = "section_id";
	private static final String TAG_MBCODE= "mbcode";
	private static final String TAG_DESCRIPTION = "description";
	
	private JSONObject json  = null;
	private List<NameValuePair> jsonParams = null;
	private JSONParser jsonParser = new JSONParser();
	
	private JSONArray mComponent = null;
	private ArrayList<HashMap<String, String>> mComponentList;
	
	private HashMap<String, String> mOut = null;
	List<String> elemList = new ArrayList<String>();
	List<String> sectList = new ArrayList<String>();
	List<String> compList = new ArrayList<String>();

	HashMap<String, String> mapComponentIndex = new HashMap<String, String>();
	private String state;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);
		
		Bundle extras = getIntent().getExtras(); 

		if (extras != null) {
			project = extras.getString("project");
			resource = extras.getString("resource");  
			userName = extras.getString("userName"); 
			user_id = extras.getString("user_id"); 
			domain = extras.getString("domain"); 
			childPos  = extras.getString("childPos"); 
			parentPos = extras.getString("parentPos"); 
			ONLINE = extras.getString("ONLINE"); 
			section  = extras.getString("section"); 
			element = extras.getString("element"); 
		}
		
		/*
		Toast.makeText(this, 
				"DetailListView \n userName = " + userName +
				"\n user_id = " + user_id +
				"\n domain = " + domain + 
				"\n project = " + project + 
				"\n resource = " + resource + 	
				"\n parentPos = " + childPos + 	
				"\n childPos = " + parentPos + 	
				"\n ONLINE = " + ONLINE , 
				Toast.LENGTH_LONG).show();
		*/
		
		
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String online = preferences.getString("ONLINE","");
		if(!online.equalsIgnoreCase(""))
		{
			state = online;
		}

		/*
		 setTitle("u/n " + userName + 
				 " user_id:  " + user_id +	
				 " resource:  " + resource +	 
				 " project:  " + project + " domain: " + domain +
				 " ONLINE = " + state);
				 
		*/
		setTitle("Detail List View - Select details");
		
		new GetDomainData().execute();	
	
	}
	/************************************* Get DATA  *********************************/
	class GetDomainData extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			Toast.makeText(getApplicationContext(),
					"GetDomainData  onPreExecute \nDomain = " + domain 
					+ "\n user_id = " + user_id
					+ "\n parentPos = " + parentPos
					+ "\n childPos = " + childPos,
					Toast.LENGTH_LONG).show();
					*/
		}

		@Override
		protected String doInBackground(String... args) {
			
			json = new JSONObject();
			jsonParams = new ArrayList<NameValuePair>();
			jsonParams.add(new BasicNameValuePair("domain", domain));
			jsonParams.add(new BasicNameValuePair("parentPos", parentPos));
			
			Log.d("Detail List View - do in back ground ", 
					"domain = " + domain + " parentPos:  " + parentPos);
			
			addItemsOnComponent();
					
			return null;
		}

	
		private void addItemsOnComponent() {

			try{	
				json = jsonParser.makeHttpRequest(READ_COMPONENT_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);	
				Log.d("DetailListView", "Component Good Call success = " + Integer.toString(success));
			} catch(Exception e){
				Log.d("DetailListView","Component Exception Success = " + Integer.toString(success));
				e.printStackTrace();
			}
			

			if (success == 1) {	
				descriptions = new String[] { "", "", "", "", "", "", "", "",
						 "", "", "", "", "", "", "" };
				mbcodes = new String[] { "", "", "", "", "", "", "", "",
						 "", "", "", "", "", "", "" };
				values = new String[] { "", "", "", "", "", "", "", "",
						 "", "", "", "", "", "", "" };
				
				
			//	values = new String[20];
				mComponentList = new ArrayList<HashMap<String, String>>();
				try {
					
					mComponent = json.getJSONArray(TAG_POSTS);
	
					Log.d("DetailListView DATA","Component got data size = " + Integer.toString(mComponent.length()));
										
					for (int i = 0; i < mComponent.length(); i++) {		
						JSONObject c = mComponent.getJSONObject(i);	
						mbcodes[i] = c.getString(TAG_MBCODE);
						descriptions[i] = c.getString(TAG_DESCRIPTION);
						values[i] = descriptions[i] + " NBC = " + mbcodes[i];				
					}	   
				    json.getString(TAG_SUCCESS); 
				    
				} catch (JSONException e) {
					e.printStackTrace();
				}		
			}						
		}
		
		
		protected void onPostExecute(String file_url) {
	
			
			Log.d("DetailListView.........IMPORTANT", Integer.toString(compList.size()));

			listView = (ListView) findViewById(R.id.list);
	
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetailListView.this,
			  android.R.layout.simple_list_item_1, android.R.id.text1, values);

			// Assign adapter to ListView
			listView.setAdapter(adapter); 
			
			listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
				    int position, long id) 
			{
					
				// ListView Clicked item index
				int itemPosition     = position;  
				// ListView Clicked item value
				String  itemValue    = (String) listView.getItemAtPosition(position);
					  
				nbcMarker = mbcodes[itemPosition];
				nbcDescription = descriptions[itemPosition];
					// Show Alert 
				/*
				Toast.makeText(getApplicationContext(),
				      "DETAIL INPUT Position :" + itemPosition +
				      "  \nData : " + values[itemPosition] +
				      "  \nDescription = " +  nbcDescription +  
				      "  \nCode = " +  nbcMarker, Toast.LENGTH_LONG)
				      .show();
				*/
				     
				Class myClass;
				try {
					Log.d("DetailListView", "Try to get Editor");
					myClass = Class.forName("com.surveyorexpert.Editor");
					Intent ourIntent = new Intent(getApplicationContext(), myClass);
						
					ourIntent.putExtra("domain", domain);
					ourIntent.putExtra("userName", userName);  	
					ourIntent.putExtra("user_id", user_id);  	
					ourIntent.putExtra("project", project);
					ourIntent.putExtra("resource", resource);	
					ourIntent.putExtra("nbcMarker", nbcMarker);
					ourIntent.putExtra("nbcDescription", nbcDescription);
					ourIntent.putExtra("childPos", childPos);
					ourIntent.putExtra("parentPos", parentPos );	
					ourIntent.putExtra("ONLINE", ONLINE);  
					ourIntent.putExtra("section", section);
					ourIntent.putExtra("element", element );
				//		ourIntent.putExtra("parentPos", Integer.toString(position+1) );	
 					
					startActivity(ourIntent);
			
					
					/*
					Toast.makeText(getApplicationContext(),
								"DetailListView \nDomain = " + domain 
								+ "\n user_id = " + user_id
								+ "\n userName = " + userName		
								+ "\n project = " + project
								+ "\n resource = " + resource
								+ "\n nbcMarker = " + nbcMarker,
								Toast.LENGTH_LONG).show();
					*/
										
					//	finish();
				} catch (ClassNotFoundException e) {
					//	Toast t = Toast.makeText(ExpandableListMain.this, "Class not found", Toast.LENGTH_SHORT);
					//	t.show();
					Log.d("DetailListView", "Failing here");
					e.printStackTrace();
				}		
					    
			}		  		
			}); 
		}
	}
}
