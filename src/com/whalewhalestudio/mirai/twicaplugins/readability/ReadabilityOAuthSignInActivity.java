package com.whalewhalestudio.mirai.twicaplugins.readability;

import org.scribe.model.Token;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ReadabilityOAuthSignInActivity extends Activity { 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
	}
    
	@Override
	protected void onResume()	{
		super.onResume();
		
		WebView webview = new WebView(this);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setSavePassword(false);
		webview.getSettings().setSaveFormData(false);
		webview.clearFormData();
		webview.clearHistory();
		CookieSyncManager.createInstance(ReadabilityOAuthSignInActivity.this);
		CookieManager.getInstance().removeAllCookie();
		
			webview.setVisibility(View.VISIBLE);
		setContentView(webview);
		
		try	{
			webview.setWebViewClient(new WebViewClient()	{
				@Override
				public void onPageFinished(WebView view, String url)	{
					if(url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
						try {
							Uri uri = Uri.parse(url);
							Token accessToken = OAuthAPIManager.Instance().getAccessToken(uri.getQueryParameter(Constants.OAUTH_VERIFIER));
							String username = OAuthAPIManager.Instance().getOAuthUsername(accessToken);

							if(accessToken != null && username != null) {
								UserSettingsManager mgr = new UserSettingsManager(getApplicationContext());
								boolean success = mgr.saveUserToken(username, accessToken);
								if(success) {
									setResult(RESULT_OK);
								}
								else {
									setResult(RESULT_CANCELED);
								}
							}
							else {
								setResult(RESULT_CANCELED);
							}
							finish();
						}
						catch(Exception exc) {
							exc.printStackTrace();
							setResult(RESULT_CANCELED);
							finish();
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
			
			webview.setWebChromeClient(new WebChromeClient()	{
				@Override
				public void onProgressChanged(WebView view, int newProgress) {
					setProgress(newProgress * 100);
					if(newProgress == 100) {
						setProgressBarVisibility(false);
					}
					super.onProgressChanged(view, newProgress);
				}
			});
			
			webview.loadUrl(OAuthAPIManager.Instance().getAuthorizationUrl());
		}
		catch(Exception exc)	{
			exc.printStackTrace();
			setResult(RESULT_CANCELED);
			finish();
		}
	}
}
