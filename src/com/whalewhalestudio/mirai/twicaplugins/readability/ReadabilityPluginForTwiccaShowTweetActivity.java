package com.whalewhalestudio.mirai.twicaplugins.readability;

import java.util.List;
import org.scribe.model.Token;
import com.twitter.Extractor;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

public class ReadabilityPluginForTwiccaShowTweetActivity extends Activity {
	final int TOAST_SUCCESS = 0;
	final int TOAST_FAILED = 1;
	final int TOAST_NOUSER = 2;
	final int TOAST_NO_URL_FOUND = 3;
	final int TOAST_URL_EXISTED = 4;
	
	ProgressDialog mPostingDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postbookmark);
		
		Intent intent = getIntent();
		try {
			if(intent != null) {
				String text = (String)intent.getExtras().get(Intent.EXTRA_TEXT);
				if(text != null) {
					UserSettingsManager mgr = new UserSettingsManager(getApplicationContext());
					if(!mgr.hasUserSignedIn()) {
						makeToast(TOAST_NOUSER);
					}
					List<String> urls = findUrls(text);
					if(urls != null && urls.size() > 0) {
						mPostingDialog = ProgressDialog.show(ReadabilityPluginForTwiccaShowTweetActivity.this, "", getResources().getText(R.string.app_sending_title), true);
						new PostBookmarkTask().execute(urls.toArray());
					}
					else {
						makeToast(TOAST_NO_URL_FOUND);
					}
				}
			}
			else {
				finish();
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
			finish();
		}
	}
	
	private List<String> findUrls(String text)	{
		List<String> retVal = null;
		
		Extractor extractor = new Extractor();
		retVal = extractor.extractURLs(text);
		
		if(retVal != null){
			return retVal;
		}
		else	{
			return null;
		}
	}
	
	private void makeToast(int toastCode) {
		Toast toast = null;
		switch(toastCode) {
		case TOAST_SUCCESS: toast = Toast.makeText(getApplicationContext(), R.string.toast_post_success, Toast.LENGTH_SHORT);
							break;
		case TOAST_FAILED: toast = Toast.makeText(getApplicationContext(), R.string.toast_post_failed, Toast.LENGTH_LONG);
						   break;
		case TOAST_NOUSER: toast = Toast.makeText(getApplicationContext(), R.string.toast_no_username, Toast.LENGTH_LONG);
						   break;
		case TOAST_NO_URL_FOUND: toast = Toast.makeText(getApplicationContext(), R.string.toast_no_url_found, Toast.LENGTH_SHORT);
								break;
		case TOAST_URL_EXISTED: toast = Toast.makeText(getApplicationContext(), R.string.toast_url_exist, Toast.LENGTH_LONG);
								break;
		}
		
		if(toast != null) {
			toast.show();
		}
		
		if(mPostingDialog != null) {
			mPostingDialog.dismiss();
		}
		
		finish();
	}
	
	private class PostBookmarkTask extends AsyncTask<Object, Void, Integer> {
		@Override
		protected Integer doInBackground(Object... params) {
			UserSettingsManager mgr = new UserSettingsManager(getApplicationContext());
			Token accessToken = mgr.getSignedInAccessToken();
			
			for(int i = 0; i < params.length; i++) {
				String encodedurl = params[i].toString();
				int success = OAuthAPIManager.Instance().postBookmark(accessToken, encodedurl);
				if(success != Constants.POST_BOOKMARK_RESULT_SUCCESS) {
					if(success == Constants.POST_BOOKMARK_RESULT_CONFLICT) {
						return TOAST_URL_EXISTED;
					}
					else {
						return TOAST_FAILED;
					}
				}
			}
			return TOAST_SUCCESS;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			makeToast(result);
		}
	}
}
