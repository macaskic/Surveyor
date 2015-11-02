package com.surveyorexpert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Editor extends Activity implements OnClickListener, LocationListener{

	private LocationManager locationManager;

	final int MIN_TIME_BW_UPDATES = 3000;
    final int MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    
    private Intent i = null;
	private EditText message;
	private TextView title, position, parameters;
	private Button btConfirm, btTalk;
	
	private String project, resource, childPos, parentPos, userName, domain, ONLINE, section, element;
	private String  retMessage, user_id, params, nbcMarker, nbcDescription, retString, severity, costEst;
	private String strLongitude, strLatitude;
	
	/****************/
	private Bundle testBundle;
	private Intent intent = null;
	private TalkToMe testTTM;
	private int SPEECH_REQUEST_CODE = 1234;
    private TextView result;
    private TextToSpeech tts;
	/****************/
	
	private int version = 1;
	private Map<String, String> masterMap = new HashMap<String, String>();
	private Map<String, String> detailMap = new HashMap<String, String>();

	private int success = 0;
	private Spinner spCostEst, spSeverity;
	// Progress Dialog
    private ProgressDialog pDialog;
      
    //testing from a real server:
    private static final String POST_COMMENT_URL = "http://www.surveyorexpert.com/webservice/addleafdata.php";
	private static final String READ_COMMENTS_URL = "http://www.surveyorexpert.com/webservice/getleafdata.php";
	private static final String READ_SEVERITY_URL = "http://www.surveyorexpert.com/webservice/getSeverity.php";
	private static final String READ_COSTESTIMATE_URL = "http://www.surveyorexpert.com/webservice/getCostEstimate.php";

	// JSON IDS:
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERNAME = "username";	
	private static final String TAG_POSTS = "posts";
	private static final String TAG_USERID = "user_id";
	private static final String TAG_DEGREE = "degree";
	private static final String TAG_ESTIMATEDCOST = "estimatedcost";
	

	// JSON parser class
	private JSONParser jParser = new JSONParser(); // sort this
	private JSONParser jsonParser = new JSONParser();
	private JSONObject json  = null;
	private List<NameValuePair> jsonParams = null;
    
	private JSONArray mSeverity = null;
	private ArrayList<HashMap<String, String>> mSeverityList;
	private JSONArray mEstCost = null;
	private ArrayList<HashMap<String, String>> mEstCostList;
	
	private HashMap<String, String> mOut = null;
	
	// An array of all of our comments
	private JSONArray mComments = null;
	// manages all of our comments in a list.
	private ArrayList<HashMap<String, String>> mCommentList;
	
	List<String> sevList = new ArrayList<String>();
	List<String> costList = new ArrayList<String>();

	private boolean canGetLocation;
	private Location location;
	private double latitude;
	private double longitude;     
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.editor1);
		
		//tvEditSentParams
		parameters = (TextView) findViewById(R.id.tvEditSentParams);
	//	message = (EditText) findViewById(R.id.etEditDescription);
		title = (TextView) findViewById(R.id.tvEditEstCostLabel);
		position = (TextView) findViewById(R.id.tvEditLatLong);
		
		
	//	btConfirm = (Button) findViewById(R.id.btConfirm);	
	//	btConfirm.setOnClickListener(this);
		
	//	btTalk= (Button) findViewById(R.id.btTalk);	
	//	btTalk.setOnClickListener(this);
		/*******************************************************/

		Bundle extras = getIntent().getExtras(); 

		if (extras != null) {
			domain = extras.getString("domain");
			project = extras.getString("project");
			resource = extras.getString("resource");   
			nbcMarker = extras.getString("nbcMarker");   
			nbcDescription = extras.getString("nbcDescription");   
			userName = extras.getString("userName"); 
			user_id  = extras.getString("user_id");
			childPos  = extras.getString("childPos");
			parentPos  = extras.getString("parentPos");
			ONLINE  = extras.getString("ONLINE");
			section  = extras.getString("section"); 
			element = extras.getString("element"); 
							
		}
		/*
		Toast.makeText(this, 
				"DetailListView \n userName = " + userName +
				"\n user_id = " + user_id +
				"\n domain = " + domain + 
				"\n project = " + project + 
				"\n nbcMarker = " + nbcMarker + 	
				"\n resource = " + resource + 	
				"\n parentPos = " + childPos + 	
				"\n childPos = " + parentPos + 	
				"\n section = " + section + 	
				"\n element = " + element + 	
				"\n ONLINE = " + ONLINE , 
				Toast.LENGTH_LONG).show();
		*/
		
		
		
		
	/*	setTitle("userName  " + userName 
				+ " project: " + project 
				+ " resource: " + resource
				+ " Version:  " + Integer.toString(version));
				*/
		
		setTitle("Write Report");
				 
		parameters.setText("userName  " + userName 
				+ "  project: " + project 
				+ "  resource: " + resource
				+ "  NBC: " + nbcMarker
				+ "  Ver:  " + Integer.toString(version)) ; 
		
	     /*******************************************************/
	
		 locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		 getLocation();	
		 
		 spSeverity  = (Spinner) findViewById(R.id.spEditSeverity);
		 spCostEst = (Spinner) findViewById(R.id.spEditEstimate);
		
		 new GetSeverityAndCost().execute();	
		 
		 addListenerOnButton();
		 addListenerOnSpinnerItemSelection();
		 
		 // position.setText("");
		 //	 message.setText("hello");
	}
		

	private void addListenerOnButton() {
		// TODO Auto-generated method stub
		spSeverity = (Spinner) findViewById(R.id.spEditSeverity);
		spCostEst = (Spinner) findViewById(R.id.spEditEstimate);
		
		/*
		btConfirm = (Button) findViewById(R.id.btConfirm);	
		//	btConfirm.setOnClickListener(this);
		btConfirm.setOnClickListener(new OnClickListener() {
			 
			  @Override
		public void onClick(View v) {
		 
			    Toast.makeText(getApplicationContext(),
				"OnClickListener : " + 
		                "\nspSeverity : "+ String.valueOf(spSeverity.getSelectedItem()) + 
		                "\nspCostEst : "+ String.valueOf(spCostEst.getSelectedItem()),
					Toast.LENGTH_SHORT).show();
			  }	 
		});		
		*/
		
		btTalk= (Button) findViewById(R.id.btTalk);	
		btTalk.setOnClickListener(new OnClickListener() {
			 
			  @Override
		public void onClick(View v) {
		 
				severity = String.valueOf(spSeverity.getSelectedItem());
				costEst = String.valueOf(spCostEst.getSelectedItem());
			
			//	private double latitude;
			//	private double longitude;     
			/*	
			    Toast.makeText(getApplicationContext(),
				"Talk to me : " + 
					    "\nspSeverity : "+ strLatitude + 
				        "\nstrLongitude : "+ strLongitude + 
		                "\nspSeverity : "+ severity +     
		                "\nspCostEst : "+ costEst,
					Toast.LENGTH_SHORT).show();
					*/
			    
				//		Toast.makeText(getApplicationContext(),"Voice recognition" , 
				//				Toast.LENGTH_LONG).show();
					new VoiceRecognition().execute();  
			  }	 
		});			
	}

	private void addListenerOnSpinnerItemSelection() {
		// TODO Auto-generated method stub
		spSeverity = (Spinner) findViewById(R.id.spEditSeverity);
		spSeverity.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
		spCostEst = (Spinner) findViewById(R.id.spEditEstimate);
		spCostEst.setOnItemSelectedListener(new CustomOnItemSelectedListener());
		
	}
	
	private void fillEstCost() {
			
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, costList);
		
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCostEst.setAdapter(dataAdapter);	
		
	}

	private void fillSeverity() {	
		
		Log.d("Editor", "DO THE BUSINESS = " + Integer.toString(sevList.size()));
			
		try{
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, sevList);
		
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spSeverity.setAdapter(dataAdapter);
		}
		catch(Exception e){
			Log.d("Editor", "OH NO" );
		}
	}

	@Override
	public void onClick(View v) {
		
		//Toast.makeText(getApplicationContext(),"Post and load" , 
		//		Toast.LENGTH_LONG).show();
		
		switch (v.getId()) {
		
			//Class myClass;
			case R.id.btTalk:
	//			Class myClass;
							
	//		Toast.makeText(getApplicationContext(),"Voice recognition" , 
	//				Toast.LENGTH_LONG).show();
	//			new VoiceRecognition().execute();
				
				
			// message.setText(testBundle.getInt("description"));
			break;
		
		case R.id.btConfirm:
			
		//	Toast.makeText(getApplicationContext(),"Confirm" , 
		//		Toast.LENGTH_LONG).show();
		//    new PostComment().execute();
				
			break;
		case R.id.bEditGetLatestVersion:
			
			Toast.makeText(getApplicationContext(),"Get Latest Version" , 
					Toast.LENGTH_LONG).show();
			message.setText("");
		    new LoadComments().execute();
			break;		
		}
		//  Log.d("request!", "On click");	
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.fileexplorer, menu);
	    inflater.inflate(R.menu.second, menu);
		inflater.inflate(R.menu.mail, menu);
	    inflater.inflate(R.menu.action, menu);
		return true;
	}
	
	public boolean   onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.camera:
			
		Class myClass;
			try {	
				myClass = Class.forName("com.surveyorexpert.CameraPhotoCapture");
				Intent ourIntent = new Intent(getApplicationContext(), myClass);
				startActivity(ourIntent);	
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
						
		     break;
		case R.id.maps:
		//	Toast.makeText(getApplicationContext(),"Get Maps" , 
		//					Toast.LENGTH_LONG).show();
			break;
		     
		case R.id.mail:
			
			severity = String.valueOf(spSeverity.getSelectedItem());
			costEst = String.valueOf(spCostEst.getSelectedItem());
			
			try {	
				myClass = Class.forName("com.surveyorexpert.Email");
				Intent ourIntent = new Intent(getApplicationContext(), myClass);
							
				ourIntent.putExtra("domain", domain);
				ourIntent.putExtra("userName", userName);  	
				ourIntent.putExtra("user_id", user_id);  	
				ourIntent.putExtra("project", project);
				ourIntent.putExtra("resource", resource);	
				ourIntent.putExtra("nbcMarker", nbcMarker);
				ourIntent.putExtra("nbcDescription", nbcDescription);
				ourIntent.putExtra("severity", severity);	
				ourIntent.putExtra("costEst", costEst);		
				ourIntent.putExtra("version", Integer.toString(version));		
				ourIntent.putExtra("strLongitude", strLongitude);		
				ourIntent.putExtra("strLatitude", strLatitude);		
			//	ourIntent.putExtra("childPos", childPos);
			//	ourIntent.putExtra("parentPos", parentPos );	
				ourIntent.putExtra("section", section);
				ourIntent.putExtra("element", element );	
							
				startActivity(ourIntent);			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;		
			
		case R.id.fileexplorer:
			try {	
				myClass = Class.forName("com.surveyorexpert.ExplorerActivity");
				Intent ourIntent = new Intent(getApplicationContext(), myClass);
				startActivity(ourIntent);			
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;

		 default:
		     break;
		 }
		 return true;
	} 
	
	
	
	/*************************Send to database****************************************/
	
	
	/*
	class PostComment extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
       //     Log.d("request!", "Pre execute");
            pDialog = new ProgressDialog(Editor.this);
            pDialog.setMessage("Send to database...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			 // Check for success tag
            int success;     
    		 
            String post_username = userName;
            String post_building = project;
            String post_surveyorid = resource;
            String post_section = masterMap.get(parentPos);
            String post_component = detailMap.get(parentPos+childPos);
            String post_message = message.getText().toString();
            String post_version = Integer.toString(version);
          
            
            try {
           //     Log.d("request!", "set up parameters");
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("building", post_building));
                params.add(new BasicNameValuePair("surveyorid", post_surveyorid));
                params.add(new BasicNameValuePair("section", post_section));
                params.add(new BasicNameValuePair("component", post_component));               
                params.add(new BasicNameValuePair("message", post_message));    
                params.add(new BasicNameValuePair("version", post_version));
                 
             //   Log.d("request!", "call makeHttpRequest");       
                //Posting user data to script 
                JSONObject json = jsonParser.makeHttpRequest(
                		POST_COMMENT_URL, "POST", params);
                // full json response
           //     Log.d("Post Comment attempt", json.toString());
                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                //	Log.d("leaf Data Added!", json.toString());    
                //	finish();
                	return json.getString(TAG_MESSAGE);
                }else{
                //	Log.d("Leaf Data Failure!", json.getString(TAG_MESSAGE));
                	return json.getString(TAG_MESSAGE);            	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
		}
		
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
        	
        //    Log.d("onPostExecute!", "Dismiss dialog");
            pDialog.dismiss();
            pDialog.cancel();
    	
            if (file_url != null){
            //	Toast.makeText(Editor.this,"Editor " +  file_url, Toast.LENGTH_LONG).show();
            }
        }
	}
	
	*/
	
	/***************************************************************************************/
	
	/*************************retrieve from database****************************************/

	@Override
	protected void onResume() {
		super.onResume();
		 getLocation();	
		// loading the comments via AsyncTask
	//	Log.d("Editor", "Resumed ........");
	// CGM
	//	new LoadComments().execute();
	}	
	
	 // Retrieves recent post data from the server.	 
	public void updateJSONdata() {

		mCommentList = new ArrayList<HashMap<String, String>>();
		jParser = new JSONParser();
	
		//Log.d("LoadComments", "Call " + READ_COMMENTS_URL);	
		JSONObject json = jParser.getJSONFromUrl(READ_COMMENTS_URL);

		try {
			String test;					
			test =  json.toString();
//Toast.makeText(getApplicationContext(), test, 
//	Toast.LENGTH_LONG).show();

			// Look out for log in script
			if(test.contains("Login successful!")){

			}
			else {

			mComments = json.getJSONArray(TAG_POSTS);
		//	Log.d("LoadComments", "Got data " + Integer.toString(mComments.length()));	
			
			// looping through all posts according to the json object returned
			for (int i = 0; i < mComments.length(); i++) {
				JSONObject c = mComments.getJSONObject(i);
				
				// gets the content of each tag
				retMessage = c.getString("message");
				String id = c.getString("id");
				String username = c.getString(TAG_USERNAME);
				String component = c.getString("component");
				String section = c.getString("section");
				String surveyorid = c.getString("surveyorid");
				String version = c.getString("version");

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_USERNAME, username);
		
			//	Log.d("Load id: ", id);
			//	Log.d("Load username: ", username);
			//	Log.d("Load message: ", retMessage);
			//	Log.d("Load component: ", component);
			//	Log.d("Load section: ", section);
			//	Log.d("Load surveyorid: ", surveyorid);
			//	Log.d("Load version: ", version);
					
				
			//	map.put(TAG_BUILDING, building);
			//	Log.d("Load total: ", c.toString());
			//	map.put(TAG_SURVEYOR, surveyorid);
				

				// adding HashList to ArrayList
				mCommentList.add(map);
			

			}
			}

		} catch (JSONException e) {
		//	Toast.makeText(ReadComments.this, "JSONException", Toast.LENGTH_LONG).show();
		//	Log.d("ReadComments Exception!", "Failed to get comments" );
			e.printStackTrace();
		}
	}

	/*
	 // Inserts the parsed data into the listview.
	private void updateList() {

		message.setText(retMessage);
		
	}
	*/

	public class LoadComments extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	
		@Override
		protected Boolean doInBackground(Void... arg0) {
		//	Log.d("LoadComments", "Do in back ground ........");
			updateJSONdata();
			return null;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		//	Log.d("LoadComments", "Post Execute........");
			//pDialog.dismiss();
		//	updateList();
	
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		strLongitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
		strLatitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);

		String strDMS = "Latitude: "+ strLatitude +" \nLongitude: "+ strLongitude; 
		position.setText(strDMS);		
	}

	@Override
	public void onProviderDisabled(String provider) {
		/******** Called when User off Gps *********/
	//	Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {	
		/******** Called when User on Gps  *********/
	//	Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	
	public Location getLocation() {
	    try {
	    	
	  //  	Toast.makeText(getBaseContext(),"getLocation called" , Toast.LENGTH_LONG).show();
	    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);   	 
	        // getting GPS status
	        boolean isGPSEnabled = locationManager
	                .isProviderEnabled(LocationManager.GPS_PROVIDER);
	        // getting network status
	        boolean isNetworkEnabled = locationManager
	                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

	        if (!isGPSEnabled && !isNetworkEnabled) {
	            // no network provider is enabled
	        } else {
	            this.canGetLocation = true;
	            if (isNetworkEnabled) {
	                       	
	                locationManager.requestLocationUpdates(
	                        LocationManager.NETWORK_PROVIDER,
	                        MIN_TIME_BW_UPDATES,
	                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                          
	                
	                Log.d("Network", "Network Enabled");
	                if (locationManager != null) {
	                    location = locationManager
	                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
	                    if (location != null) {
	                        latitude = location.getLatitude();
	                        longitude = location.getLongitude();
	                    }
	                }
	            }
	            // if GPS Enabled get lat/long using GPS Services
	            if (isGPSEnabled) {
	                if (location == null) {
	                    locationManager.requestLocationUpdates(
	                            LocationManager.GPS_PROVIDER,
	                            MIN_TIME_BW_UPDATES,
	                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
	                    Log.d("GPS", "GPS Enabled");
	                    if (locationManager != null) {
	                        location = locationManager
	                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                            latitude = location.getLatitude();
	                            longitude = location.getLongitude();
	                        }
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return location;
	}	
	/***************************************************************************************/

	
	class GetSeverityAndCost extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args) {
		
			json = new JSONObject();
			jsonParams = new ArrayList<NameValuePair>();
			jsonParams.add(new BasicNameValuePair("user_id", user_id));
					
			addItemsOnSeverity();
			addItemsOnCostEst();
					
			return null;
		}

	
		protected void onPostExecute(String file_url) {
			 fillSeverity();
			 fillEstCost();
		//	Toast.makeText(getBaseContext(),"GetSeverityAndCost onPostExecute called" , Toast.LENGTH_LONG).show();
		}
		
		private void addItemsOnSeverity() {
	
			try{	
				json = jsonParser.makeHttpRequest(READ_SEVERITY_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);		
			} catch(Exception e){
				Log.d("Editor","Exception Success = " + Integer.toString(success));
				e.printStackTrace();
			}

			if (success == 1) {	
				Log.d("Editor", "Good Call success = " + Integer.toString(success));
				
				mSeverityList = new ArrayList<HashMap<String, String>>();
			
				try {
					mSeverity = json.getJSONArray(TAG_POSTS);
					Log.d("Editor", "mSeverity = " + Integer.toString(mSeverity.length()));
					
					for (int i = 0; i < mSeverity.length(); i++) {
						
						JSONObject c = mSeverity.getJSONObject(i);
				
						String user_id = c.getString(TAG_USERID);
						String degree = c.getString(TAG_DEGREE);
				
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_USERID, degree);
					
						// adding HashList to ArrayList
						mSeverityList.add(map);	
					}  
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("Editor", "mSeverityList = " + Integer.toString(mSeverityList.size()));
			}			
			
					
			for (int i = 0; i < mSeverityList.size(); i++) {
				   mOut = mSeverityList.get(i); 
				   for (String key: mOut.keySet()) {
					    sevList.add( mOut.get(key));        
				   }  
				}
			Log.d("Editor", "Created Severity pop = " + Integer.toString(sevList.size()));
		}
		
		private void addItemsOnCostEst() {
				
			try{	
				json = jsonParser.makeHttpRequest(READ_COSTESTIMATE_URL, "POST", jsonParams);
				success = json.getInt(TAG_SUCCESS);		
			} catch(Exception e){
				Log.d("Editor","Exception Success = " + Integer.toString(success));
				e.printStackTrace();
			}

			if (success == 1) {	
				Log.d("Editor", "Good Call success = " + Integer.toString(success));
				
				mEstCostList = new ArrayList<HashMap<String, String>>();
			
				try {
					mEstCost  = json.getJSONArray(TAG_POSTS);
					Log.d("Editor", "mSeverity = " + Integer.toString(mEstCost .length()));
					
					for (int i = 0; i < mEstCost .length(); i++) {
						
						JSONObject c = mEstCost .getJSONObject(i);
				
						String user_id = c.getString(TAG_USERID);
						String estimatedcost = c.getString(TAG_ESTIMATEDCOST);
				
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TAG_USERID, estimatedcost);
					
						// adding HashList to ArrayList
						mEstCostList.add(map);	
					}  
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.d("Editor", "mEstCostList = " + Integer.toString(mEstCostList.size()));
			}			
						
			for (int i = 0; i < mEstCostList.size(); i++) {
				   mOut = mEstCostList.get(i); 
				   for (String key: mOut.keySet()) {
					    costList.add( mOut.get(key));        
				   }  
				}
			Log.d("Editor", "Created Cost pop = " + Integer.toString(costList.size()));	
		}
	}	
	
	/********************************************/
	
	class VoiceRecognition extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		//	Toast.makeText(getBaseContext(),"VoiceRecognition onPreExecute called" , Toast.LENGTH_LONG).show();
		}

		@Override
		protected String doInBackground(String... args) {
			
		//	i = new Intent(Editor.this, TalkToMe.class);	
			
			Intent ourIntent = new Intent(Editor.this, TalkToMe.class);
			
			ourIntent.putExtra("domain", domain);
			ourIntent.putExtra("userName", userName);  	
			ourIntent.putExtra("user_id", user_id);  	
			ourIntent.putExtra("project", project);
			ourIntent.putExtra("resource", resource);	
			ourIntent.putExtra("nbcMarker", nbcMarker);
			ourIntent.putExtra("nbcDescription", nbcDescription);
			ourIntent.putExtra("severity", severity);	
			ourIntent.putExtra("costEst", costEst);		
			ourIntent.putExtra("version", Integer.toString(version));		
			ourIntent.putExtra("strLongitude", strLongitude);		
			ourIntent.putExtra("strLatitude", strLatitude);		
		//	ourIntent.putExtra("childPos", childPos);
		//	ourIntent.putExtra("parentPos", parentPos );	
			ourIntent.putExtra("section", section);
			ourIntent.putExtra("element", element );	
		
			startActivity(ourIntent);
		//	testBundle = i.getBundleExtra("description");
		//	retString = testBundle.get("description");
		//	retString = testBundle.getString("description");
		//	message.setText("Bye Bye");
			return null;
		/*	
		        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Press when complete");
	        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
	        startActivityForResult(intent, SPEECH_REQUEST_CODE);
	        return intent.getDataString();
	     */   
		}

		protected void
		onActivityResult(int requestCode, int resultCode, Intent data){
		
			  Log.d("VoiceRecognition", "started onActivityResult");	
			
      if (requestCode == SPEECH_REQUEST_CODE)
      {
          if (resultCode == RESULT_OK)
          {
              ArrayList<String> matches = data
                  .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
              
              if (matches.size() == 0)
              {
                  tts.speak("Heard nothing", TextToSpeech.QUEUE_FLUSH, null);
              }
              else
              {
                  String mostLikelyThingHeard = matches.get(0);
                  
                  Log.d("VoiceRecognition", "mostLikelyThingHeard");	
                  
                //  String magicWord = this.getResources().getString(R.string.magicword);
                  String magicWord = "tree";
                  if (mostLikelyThingHeard.equals(magicWord))
                  {
                      tts.speak("You said the magic word!", TextToSpeech.QUEUE_FLUSH, null);
                      Log.d("VoiceRecognition", "one");	
                  }
                  else
                  {
                      tts.speak("Recognised " + mostLikelyThingHeard + " Is that OK", TextToSpeech.QUEUE_FLUSH, null);
                     // intent.putExtra("description","description");
                      Log.d("VoiceRecognition", "two");	
                  }
              }
              result.setText("heard: " + matches);
          }
          else
          {
        	  Log.d("VoiceRecognition",  "result NOT ok");
          }
      }

   //   super.onActivityResult(requestCode, resultCode, data);
  }
  
		protected void onPostExecute(String file_url) {
		//	retString = testBundle.getString("description");
		//	message.setText("Bye Bye" + retString);
		//	Toast.makeText(getBaseContext(),"VoiceRecognition onPostExecute called" + test, Toast.LENGTH_LONG).show();
		}	
	
	}
	
}
