package com.surveyorexpert;

import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class CustomiseDialog extends Dialog implements OnClickListener {
Button okButton;
Context mContext;
TextView mTitle = null;
TextView mMessage = null;
View v = null;
Button cancelButton;
ProgressBar progressBar;

public CustomiseDialog(Context context) {
 super(context);
 mContext = context;
 /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */
 requestWindowFeature(Window.FEATURE_NO_TITLE);
 /** Design the dialog in main.xml file */
 setContentView(R.layout.custom_rounded_box);
 v = getWindow().getDecorView();
 v.setBackgroundResource(android.R.color.transparent);
 mTitle = (TextView) findViewById(R.id.dialogTitle);
 mMessage = (TextView) findViewById(R.id.dialogMessage);
 okButton = (Button) findViewById(R.id.OkButton);
 cancelButton = (Button) findViewById(R.id.cancelButton);
 cancelButton.setOnClickListener(this);
 okButton.setOnClickListener(this);
 progressBar = (ProgressBar) findViewById(R.id.progress);

}

@Override
public void setTitle(CharSequence title) {
 super.setTitle(title);
 mTitle.setText(title);
}

@Override
public void setTitle(int titleId) {
 super.setTitle(titleId);
 mTitle.setText(mContext.getResources().getString(titleId));
}

/**
* Set the message text for this dialog's window.
* 
* @param message
*            - The new message to display in the title.
*/
public void setMessage(CharSequence message) {
 mMessage.setText(message);
 mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
}

/**
* Set the message ID
* 
* @param messageId
*/
public void setMessage(int messageId) {
 mMessage.setText(mContext.getResources().getString(messageId));
 mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
}

@Override
public void onClick(View v) {


}


}

