package com.surveyorexpert;

//import com.example.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Email extends Activity implements View.OnClickListener {

	EditText personsEmail, intro, personsName, things, action,
			outro;
	String emailAdd, beginning, name, action1, act, out;
	Button sendEmail;

	private String project, resource, childPos, parentPos, userName, domain, version, jsonRetString, section, element;
	private String  retMessage, user_id, params, nbcMarker, nbcDescription, retString, severity, costEst, mServerPhotoPath;
	private String strLongitude, strLatitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.email);
		initializeVars();
		sendEmail.setOnClickListener(this);
	}

	private void initializeVars() {

		personsEmail = (EditText) findViewById(R.id.etEmails);
		personsEmail.setText("calum@btinternet.com");
		intro = (EditText) findViewById(R.id.etIntro);
		personsName = (EditText) findViewById(R.id.etName);
		things = (EditText) findViewById(R.id.etThings);
		action = (EditText) findViewById(R.id.etAction);
		outro = (EditText) findViewById(R.id.etOutro);
		sendEmail = (Button) findViewById(R.id.bSentEmail);
		
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
	}

	public void onClick(View v) {

		convertEditTextVarsIntoStringsAndYesThisIsAMethodWeCreated();
		String emailaddress[] = { /*emailAdd*/ "calum@btinternet.com" };
		String message = "The collected data is " +
		/*		+ name
				+ " I just wanted to say "
				+ beginning
				+ ".  Not only that but  "
				+ action
				+ ", more text "
				+ act
				+ ".  even more"
				+ out
				+ ".  Check out Android.com"
				+ '\n' + "PS. bye bye";*/
		
		"\nuserName = " +  userName +
		"\nuser_id	= " +  user_id +		
		"\ndomain = " + domain +
		"\nproject = " + project +
		"\nresource = " + resource +
		"\nsection= " +  section +
		"\nelement = " + element +
		"\nnbcDescription = " + nbcDescription +
		"\nnbcMarker = " + nbcMarker +
		"\nseverity = " + severity +
		"\ncostEst = " + costEst +
		"\nversion = " + version +
		"\nstrLongitude = " + strLongitude +
		"\nstrLatitude = " + strLatitude;
	
		
		Intent 	emailIntent =  new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Status Message");
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		startActivity(emailIntent);	
	}

	private void convertEditTextVarsIntoStringsAndYesThisIsAMethodWeCreated() {

		emailAdd = personsEmail.getText().toString();
		beginning = intro.getText().toString();
		name = personsName.getText().toString();
		action1 = things.getText().toString();
		act = action.getText().toString();
		out = outro.getText().toString();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}