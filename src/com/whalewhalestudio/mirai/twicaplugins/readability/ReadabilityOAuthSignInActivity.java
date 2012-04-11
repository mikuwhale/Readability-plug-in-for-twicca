package com.whalewhalestudio.mirai.twicaplugins.readability;

import org.scribe.model.Token;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReadabilityOAuthSignInActivity extends Activity { 
	private WebView mWebview = null;
	private ProgressDialog mLoadingDialog = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
	}
    
	@Override
	protected void onResume()	{
		super.onResume();
		
		mWebview = new WebView(this);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setSaveFormData(false);
		mWebview.clearFormData();
		mWebview.clearHistory();
		CookieSyncManager.createInstance(ReadabilityOAuthSignInActivity.this);
		CookieManager.getInstance().removeAllCookie();
		
		mWebview.setVisibility(View.VISIBLE);
		setContentView(mWebview);
		
		try	{
			mWebview.setWebViewClient(new WebViewClient()	{
				@Override
				public void onPageFinished(WebView view, String url)	{
					if(url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
						if(mLoadingDialog == null) {
							mWebview.setVisibility(View.INVISIBLE);
							mLoadingDialog = ProgressDialog.show(ReadabilityOAuthSignInActivity.this, "", getResources().getText(R.string.progress_gettinginfo), true);
							new GetAccessTokenTask().execute(url);
						}
					}
				}
				
				@Override
				public void onPageStarted(WebView view, String url,
						Bitmap favicon) {
					setProgressBarVisibility(true);
					super.onPageStarted(view, url, favicon);
				}
			});
			
			mWebview.setWebChromeClient(new WebChromeClient()	{
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					setProgress(newProgress * 100);
					if(newProgress == 100) {
						setProgressBarVisibility(false);
					}
					super.onProgressChanged(view, newProgress);
				}
			});
			
			mLoadingDialog = ProgressDialog.show(ReadabilityOAuthSignInActivity.this, "", getResources().getText(R.string.progress_loading_browser), true);
			new GetAuthorizeUrlTask().execute();
		}
		catch(Exception exc)	{
			exc.printStackTrace();
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	
	private class GetAuthorizeUrlTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			OAuthAPIManager.Instance().initializeRequestToken();
			return OAuthAPIManager.Instance().getAuthorizationUrl();
		}
		
		@Override
		protected void onPostExecute(String result) {
			if(mLoadingDialog != null) {
				mLoadingDialog.dismiss();
				mLoadingDialog = null;
			}
			
			if(mWebview != null) {
				try {
					mWebview.loadUrl(result);
				}
				catch(Exception exc) {
					exc.printStackTrace();
					setResult(RESULT_CANCELED);
					finish();
				}
			}
		}
	}
	
	private class GetAccessTokenTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				Uri uri = Uri.parse(params[0]);
				Token accessToken = OAuthAPIManager.Instance().getAccessToken(uri.getQueryParameter(Constants.OAUTH_VERIFIER));
				String username = OAuthAPIManager.Instance().getOAuthUsername(accessToken);

				if(accessToken != null && username != null) {
					UserSettingsManager mgr = new UserSettingsManager(getApplicationContext());
					boolean success = mgr.saveUserToken(username, accessToken);
					if(success) {
						return RESULT_OK;
					}
					else {
						return RESULT_CANCELED;
					}
				}
				else {
					return RESULT_CANCELED;
				}
			}
			catch(Exception exc) {
				exc.printStackTrace();
				return RESULT_CANCELED;
			}
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(mLoadingDialog != null) {
				mLoadingDialog.dismiss();
				mLoadingDialog = null;
			}
			
			setResult(result);
			finish();
		}
	}
}
