/**
 * this is free software 
 * It is under the The MIT License http://www.opensource.org/licenses/mit-license.php
 */
package com.surveyorexpert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

//import root.magicword.R;

//import root.magicword.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TalkToMe extends Activity implements OnInitListener
{
	Intent intent = null;
    private static final String TAG = "MagicWord";
    public String gotMessage = "";
    
    private TextView result, message;
    private TextToSpeech tts;
    
    private Button speak, confirm;
    private ProgressDialog pDialog;
    private int SPEECH_REQUEST_CODE = 1234;
    
	private String project, resource, childPos, parentPos, userName, domain, version, jsonRetString, section, element;
	private String  retMessage, user_id, params, nbcMarker, nbcDescription, retString, severity, costEst, mServerPhotoPath;

	private String strLongitude, strLatitude;
	private String ONLINE, mPhotoPath;
	
	private static final String POST_COMMENT_URL = "http://www.surveyorexpert.com/webservice/addleafdata2.php";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_SUCCESS = "success";	
	
	// JSON parser class
	private JSONParser jParser = new JSONParser(); // sort this
	private JSONParser jsonParser = new JSONParser();
	private JSONObject json  = null;
	private List<NameValuePair> jsonParams = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.talk_main);
        
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String online = preferences.getString("ONLINE","");
		if(!online.equalsIgnoreCase(""))
		{
			ONLINE = online;
		}	
		String PhotoPath = preferences.getString("PhotoPath","");
		if(!PhotoPath.equalsIgnoreCase(""))
		{
			mPhotoPath = PhotoPath;
		}
		String ServerPhotoPath = preferences.getString("ServerPhotoPath","");
		if(!ServerPhotoPath.equalsIgnoreCase(""))
		{
			mServerPhotoPath = ServerPhotoPath;
		}
        	
		setTitle("mServerPhotoPath  " + mServerPhotoPath );
		
    	Bundle extras = getIntent().getExtras(); 
    	
        if (extras != null) {
			domain = extras.getString("domain");
			project = extras.getString("project");
			resource = extras.getString("resource");   
			nbcMarker = extras.getString("nbcMarker");   
			nbcDescription = extras.getString("nbcDescription");   
			userName = extras.getString("userName"); 
			user_id  = extras.getString("user_id");		
			severity  = extras.getString("severity");
			costEst  = extras.getString("costEst");
			version  = extras.getString("version");
			strLongitude  = extras.getString("strLongitude");
			strLatitude  = extras.getString("strLatitude");
			section  = extras.getString("section"); 
			element = extras.getString("element"); 
			childPos = ""; //extras.getString("childPos");
			parentPos = ""; //extras.getString("parentPos");				
		}
 
        //     File imgFile = new  File("/sdcard/Images/test_image.jpg");
      //  File imgFile = new  File("/storage/emulated/0/1405349215602.jpg");
        String path = Environment.getExternalStorageDirectory()+ "/DCIM/Camera/1405349215602.jpg";
     
        
        /*
       	Toast.makeText(TalkToMe.this,
        		"mServerPhotoPath =  " + mServerPhotoPath
       			, Toast.LENGTH_LONG).show();
       	*/
       	
       	
       	
       	
        /*
        Toast.makeText(getBaseContext(),
        		"mPhotoPath =  " + mPhotoPath, 
        		"mServerPhotoPath =  " + mServerPhotoPath, 
            	Toast.LENGTH_LONG).show(); 
            	*/
       
        
        File imgFile = new  File(mPhotoPath);
          
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.finalImg);
            myImage.setImageBitmap(myBitmap);
   //      	Toast.makeText(getBaseContext(),"Set File " , Toast.LENGTH_LONG).show(); 
        } 
        else{
        	Toast.makeText(getBaseContext(),"Not found " + imgFile.getName(), Toast.LENGTH_LONG).show(); 
        }
        
        
        speak = (Button)findViewById(R.id.bt_speak);
        speak.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendRecognizeIntent();
            }
        });   
        speak.setEnabled(false);
        
        
        confirm = (Button)findViewById(R.id.btWriteToServer);
        confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            	new PostComment().execute();
            	  	
            	Toast.makeText(getBaseContext(),
            			"TalkToMe Send to Server : " +
            					"\nmPhotoPath : " + mPhotoPath	+
            					"\nsection : " + section	+
            					"\nelement : " + element	+
                            	"\ngotMessage : " + gotMessage	+
                    			"\ndomain : " + domain	+
                    			"\nproject : " + project +	
                    			"\nresource : " + resource +
                    			"\nnbcMarker : " + nbcMarker +
                    			"\nnbcDescription : " + nbcDescription +
                    			"\nuserName : " + userName +	
                    			"\nuser_id : " + user_id	+
                    			"\nseverity : " + severity +
                    			"\ncostEst : " + costEst	+
                    			"\nversion : " + version	+
                    			"\nstrLongitude : " + strLongitude +	
                    			"\nstrLatitude : " + strLatitude	
                    		    , Toast.LENGTH_LONG).show(); 	
                    		    
            }
        });   
      //  confirm.setEnabled(false);
           
        result = (TextView)findViewById(R.id.tv_result);
     //   message = (TextView)findViewById(R.id.tvTalkMessage);
    //    message.setText(mServerPhotoPath);      
        tts = new TextToSpeech(this, this);
    }
    
    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS)
        {
            speak.setEnabled(true);
        }
        else
        {
            //failed to init
            finish();
        }
        
    }
    
    private void sendRecognizeIntent()
    {
       // Intent 
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Press when complete");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    //	Toast.makeText(getBaseContext(),"TalkToMe Done" , Toast.LENGTH_LONG).show(); 	  
    }

    @Override
    protected void
            onActivityResult(int requestCode, int resultCode, Intent data)
    {
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
                    String magicWord = this.getResources().getString(R.string.magicword);
                    if (mostLikelyThingHeard.equals(magicWord))
                    {
                        tts.speak("You said the magic word!", TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else
                    {
              //          tts.speak("Recognised " + mostLikelyThingHeard + " Is that OK", TextToSpeech.QUEUE_FLUSH, null);
                    	gotMessage = mostLikelyThingHeard;
                    	tts.speak( gotMessage + " OK?",
                        		TextToSpeech.QUEUE_FLUSH, null);
                              
                        //intent.putExtra("description",mostLikelyThingHeard);
                    }
                }
                result.setText("heard: " + matches);
          //    Toast.makeText(getBaseContext(),"TalkToMe Done " + gotMessage, Toast.LENGTH_LONG).show(); 
            
            }
            else
            {
                Log.d(TAG, "result NOT ok");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy()
    {
        if (tts != null)
        {
            tts.shutdown();
        }
        super.onDestroy();
    }
    
    /*********************************/
class PostComment extends AsyncTask<String, String, String> {
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
     //    	Toast.makeText(getBaseContext(),"Prepare to send to database onPreExecute", Toast.LENGTH_LONG).show(); 
            
       //     Log.d("request!", "Pre execute");
            pDialog = new ProgressDialog(TalkToMe.this);
            pDialog.setMessage("Send to database...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
		
		@Override
		protected String doInBackground(String... args) {
			 // Check for success tag
            int success;     
    		 
            String post_user_id = user_id;
            String post_username = userName;
            String post_domain = domain;
            String post_resource = resource;
            String post_element = element;
            String post_section = section;
      //      String post_element = parentPos;
      //      String post_section = childPos	;
            String post_component = nbcDescription;
            String post_NBCCode = nbcMarker;
            String post_latitude = strLatitude;
            String post_longitude = strLongitude;
            String post_severity = severity;
            String post_cost = costEst;
            String post_description = gotMessage;
            String post_version = version;
            String post_photolocation = mServerPhotoPath;
       //     String post_element = element;
       //     String post_photolocation = mServerPhotoPath;
          
            
            try {
           //     Log.d("request!", "set up parameters");
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("user_id", post_user_id));
                params.add(new BasicNameValuePair("username", post_username));
                params.add(new BasicNameValuePair("domain", post_domain));
                params.add(new BasicNameValuePair("resource", post_resource));
                params.add(new BasicNameValuePair("element", post_element));
                params.add(new BasicNameValuePair("section", post_section));
                params.add(new BasicNameValuePair("component", nbcDescription));      
                params.add(new BasicNameValuePair("NBCCode", post_NBCCode));   
                params.add(new BasicNameValuePair("latitude", post_latitude)); 
                params.add(new BasicNameValuePair("longitude", post_longitude)); 
                params.add(new BasicNameValuePair("severity", post_severity)); 
                params.add(new BasicNameValuePair("cost", post_cost));              
                params.add(new BasicNameValuePair("description", post_description));    
                params.add(new BasicNameValuePair("version", post_version));
                params.add(new BasicNameValuePair("photolocation", post_photolocation)); 
                Log.d("TalkToMe", "call makeHttpRequest");       
                //Posting user data to script 
                JSONObject json = jsonParser.makeHttpRequest(
                		POST_COMMENT_URL, "POST", params);
                // full json response
                Log.d("TalkToMe", json.toString());
                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                	jsonRetString = json.toString();
                	Log.d("TalkToMe", jsonRetString);    
                //	finish();
                	return json.getString(TAG_MESSAGE);
                }else{
                	Log.d("TalkToMe", json.getString(TAG_MESSAGE));
                	return json.getString(TAG_MESSAGE);            	
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
		}
		
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
         	Toast.makeText(getBaseContext(),"send to database = " + jsonRetString, Toast.LENGTH_LONG).show(); 
            
        //    Log.d("onPostExecute!", "Dismiss dialog");
            pDialog.dismiss();
            pDialog.cancel();
    	
            if (file_url != null){
            //	Toast.makeText(Editor.this,"Editor " +  file_url, Toast.LENGTH_LONG).show();
            }
        }
	}
	
    /************************************/
    
    
    
    
    
}