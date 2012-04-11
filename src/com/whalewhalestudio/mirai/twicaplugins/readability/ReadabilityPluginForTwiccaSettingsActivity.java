package com.whalewhalestudio.mirai.twicaplugins.readability;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

public class ReadabilityPluginForTwiccaSettingsActivity extends Activity {
	final int DIALOG_LOGOUT = 100;
	final int DIALOG_ERROR = 99;
	final int LOADING_DIALOG = 90;
	final int LOGIN_ACTIVITY = 101;
	
	private UserSettingsManager mSettingsMgr;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mSettingsMgr = new UserSettingsManager(getApplication());
        boolean mLoggedIn = mSettingsMgr.hasUserSignedIn();
        
    	if(!mLoggedIn) {
    		setLoginUI(true, null);
    	}
    	else {
    		setLoginUI(false, mSettingsMgr.getSignedInUsername());
    	}
    	
    	final Button btnSignIn = (Button)findViewById(R.id.button_loginlogout);
        // Sign in and out
        btnSignIn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Login user
				if(!mSettingsMgr.hasUserSignedIn())	{
					Intent i = new Intent(getApplicationContext(), ReadabilityOAuthSignInActivity.class);
			    	startActivityForResult(i, LOGIN_ACTIVITY);
				}
				// Logout user
				else {
					showDialog(DIALOG_LOGOUT);
				}
			}
		});
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	if(id == DIALOG_LOGOUT) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.label_logout_comfirm_message)
    		       .setCancelable(false)
    		       .setPositiveButton(R.string.label_logout_Yes, new DialogInterface.OnClickListener() {
    		           @Override
    		    	   public void onClick(DialogInterface dialog, int id) {
    		        	   // Remove the data from the shared preferences 
     		        	   mSettingsMgr.removeUserToken();
     		        	   // Update UI
    		               setLoginUI(true, null);
    		           }
    		       })
    		       .setNegativeButton(R.string.label_logout_NO, new DialogInterface.OnClickListener() {
    		           @Override
    		    	   public void onClick(DialogInterface dialog, int id) {
    		                dialog.cancel();
    		           }
    		       });
    		
    		AlertDialog alert = builder.create();
    		return alert;
    	}
    	if(id == DIALOG_ERROR) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.label_unable_signin)
    				.setPositiveButton(R.string.label_login_error_OK, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
    		
    		AlertDialog alert = builder.create();
    		return alert;
    	}
    	else {
    		return null;
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// Clear the cache during signing in
    	WebView webview = new WebView(this);
    	webview.clearCache(true);
    	
    	if(requestCode == LOGIN_ACTIVITY) {
    		if(resultCode == RESULT_OK) {
    			// Update UI
    			String username = mSettingsMgr.getSignedInUsername();
    			if(username != null) {
    				setLoginUI(false, username);
    			}
    		}
    		else {
    			showDialog(DIALOG_ERROR);
    		}
    	}
    }
    
    private void setLoginUI(boolean loginscreen, String username) {
    	final Button btnSignIn = (Button)findViewById(R.id.button_loginlogout);
        final TextView lblInstruction = (TextView)findViewById(R.id.label_instruction);
        final TextView lblUsername = (TextView)findViewById(R.id.label_username);
        
    	if(loginscreen) {
    		btnSignIn.setText(R.string.button_login);
    		lblInstruction.setText(R.string.label_login_instructions);
    		lblUsername.setText("");
    	}
    	else {
    		btnSignIn.setText(R.string.button_logout);
    		lblInstruction.setText(R.string.label_logout_instructions);
    		lblUsername.setText(username);
    	}
    }
}