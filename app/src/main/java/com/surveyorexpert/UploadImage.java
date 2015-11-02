/**
 * 
 */
package com.surveyorexpert;
//import com.example.demo.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class UploadImage extends Activity {
	public  static ImageView showImg  = null;
	TextView servRet;
    InputStream inputStream; 
    Bitmap bitmap, newBitmap, img;
    ByteArrayOutputStream stream;
    byte [] byte_arr;
    String image_str;
    String photoPath,  CapturedImageDetails ;
    final ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();
        @Override
    public void onCreate(Bundle icicle) {
        	   	
            super.onCreate(icicle);
            setContentView(R.layout.main);
            servRet = (TextView) findViewById(R.id.tvServerReturn);
           
            showImg = (ImageView) findViewById(R.id.showImg1);
            
        	Bundle extras = getIntent().getExtras(); 
    		if (extras != null) {
    			photoPath = extras.getString("path");
    			CapturedImageDetails = extras.getString("CapturedImageDetails");
    		
    		}
            
    	//   	Toast.makeText(UploadImage.this, "onCreate " + photoPath, Toast.LENGTH_LONG).show();
    	     
         //   String photoPath = "/storage/emulated/0/DCIM/Camera/1401702073990.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(photoPath, options);

            if (bitmap != null) {
            	
            	/********* Creates a new bitmap, scaled from an existing bitmap. ***********/

                newBitmap = Bitmap.createScaledBitmap(bitmap, 170, 170, true); 
                
                bitmap.recycle();
                
                if (newBitmap != null) {
                	
                	bitmap = newBitmap;
                }  
            }
            
      //      bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher);           
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
            byte_arr = stream.toByteArray();
            image_str = Base64.encodeBytes(byte_arr);
    
            nameValuePairs.add(new BasicNameValuePair("image",image_str));
 
           Thread t = new Thread(new Runnable() {
             
            @Override
            public void run() {
                  try{
                         HttpClient httpclient = new DefaultHttpClient();
                         HttpPost httppost = new HttpPost("http://www.surveyorexpert.com/webservice/upload_image.php");
                         
                      //   HttpPost httppost = new HttpPost("http://www.surveyorexpert.com/development/upload_image.php");
                     
                         httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                         HttpResponse response = httpclient.execute(httppost);
                         final String the_string_response = convertResponseToString(response);
                         runOnUiThread(new Runnable() {
                                 
                                @Override
                                public void run() {
                                    
                                    servRet.setText(/*"Response: " + */the_string_response + "\n" +  CapturedImageDetails);
                                    
                                    Toast.makeText(UploadImage.this, "Big Response = " + the_string_response, Toast.LENGTH_LONG).show();
                                    

                                    if(bitmap != null)
                              	              showImg.setImageBitmap(bitmap);
                                    
                                	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UploadImage.this);
            		    			SharedPreferences.Editor editor = preferences.edit();
            		    		
            		    			editor.putString("ServerPhotoPath", the_string_response);
            		    			editor.apply();	
                              //      Log.d("CALUM", the_string_response);
                               //     finish();
                                }
                            });
                          
                     }catch(final Exception e){
                          runOnUiThread(new Runnable() {
                             
                            @Override
                            public void run() {
                                Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();                              
                            }
                        });
                           System.out.println("Error in http connection "+e.toString());
                     }  
            }
        });
         t.start();
        }
 
        
      
        public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException{
 
             String res = "";
             StringBuffer buffer = new StringBuffer();
             inputStream = response.getEntity().getContent();
             final int contentLength = (int) response.getEntity().getContentLength();

              runOnUiThread(new Runnable() {
             
            @Override
            public void run() {
        //        Toast.makeText(UploadImage.this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();                     
            }
        });
          
             if (contentLength < 0){
             }
             else{
                    byte[] data = new byte[512];
                    int len = 0;
                    try
                    {
                        while (-1 != (len = inputStream.read(data)) )
                        {
                            buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        inputStream.close(); // closing the stream�..
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    res = buffer.toString();     // converting stringbuffer to string�..
 
                    runOnUiThread(new Runnable() {
                     
                    @Override
                    public void run() {
          //             Toast.makeText(UploadImage.this, "Result : ", Toast.LENGTH_LONG).show();
                    }
                });
                    //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
             }
             return res;
        }
}
