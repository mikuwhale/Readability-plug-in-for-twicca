package com.whalewhalestudio.mirai.twicaplugins.readability;

import org.scribe.model.Token;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSettingsManager {
	private final String PREF_USERNAME = "username";
	private final String PREF_TOKEN_SECRET = "token_secret";
	private final String PREF_TOKEN_KEY = "token_key";
	public  final String PREFS_FILENAME = "readabilityfortwiccapluginprefs";
	
	private SharedPreferences mSettings; 
	
	public boolean saveUserToken(String username, Token accessToken) {
		try {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.putString(PREF_USERNAME, username);
			editor.putString(PREF_TOKEN_KEY, accessToken.getToken());
			editor.putString(PREF_TOKEN_SECRET, accessToken.getSecret());
			
			return editor.commit();
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}
	
	public boolean removeUserToken() {
		try {
			SharedPreferences.Editor editor = mSettings.edit();
			editor.remove(PREF_USERNAME);
			editor.remove(PREF_TOKEN_KEY);
			editor.remove(PREF_TOKEN_SECRET);
			
			return editor.commit();
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return false;
		}
	}
	
	public Token getSignedInAccessToken() {
		String key = mSettings.getString(PREF_TOKEN_KEY, null);
		String secret = mSettings.getString(PREF_TOKEN_SECRET, null);
		
		if(key != null && secret != null) {
			return new Token(key, secret);
		}
		else {
			return null;
		}
	}
	
	public String getSignedInUsername() {
		return mSettings.getString(PREF_USERNAME, null);
	}
	
	public boolean hasUserSignedIn() {
		String username = mSettings.getString(PREF_USERNAME, null);
		// Return true if there's username in 
		if(username == null) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public UserSettingsManager(Context context) {
		mSettings = context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
	}
}
