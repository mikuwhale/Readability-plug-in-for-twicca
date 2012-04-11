package com.whalewhalestudio.mirai.twicaplugins.readability;

import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

public class OAuthAPIManager {
	private static OAuthAPIManager mInstance;
	
	private OAuthService mService;
	private Token mRequestToken;
	
	public static OAuthAPIManager Instance() {
		if(mInstance == null) {
			mInstance = new OAuthAPIManager();
		}
		return mInstance;
	}
	
	public String getAuthorizationUrl()	{
		return mService.getAuthorizationUrl(mRequestToken);
	}
	
	public Token getAccessToken(String outhVerifier) {
		Token accessToken;
		
		try {
			Verifier verifier = new Verifier(outhVerifier);
			accessToken = mService.getAccessToken(mRequestToken, verifier);
			return accessToken;
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	public String getOAuthUsername(Token accessToken) {
		try {
			OAuthRequest request = new OAuthRequest(Verb.GET, Constants.GET_CURRENT_USER_URL);
			mService.signRequest(accessToken, request);
			Response response = request.send();
			
			JSONObject json = new JSONObject(response.getBody());
			return json.getString("username");
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
	
	public int postBookmark(Token accessToken, String url) {
		return postBookmark(accessToken, url, false, false);
	}
	
	public int postBookmark(Token accessToken, String url, boolean favorite, boolean archive) {
		try {
			String paramFavorite = favorite == true ? "1" :"0";
			String paramArchive = archive == true ? "1" :"0";
			
			OAuthRequest request = new OAuthRequest(Verb.POST, Constants.POST_BOOKMARK_URL);
			request.addHeader("Content-Type", "application/x-www-form-urlencoded");
			request.addBodyParameter("url", url);
			request.addBodyParameter("favorite", paramFavorite);
			request.addBodyParameter("archive", paramArchive);

			mService.signRequest(accessToken, request);
			Response response = request.send();
			int code = response.getCode();
			
			return code;
		}
		catch(Exception exc) {
			exc.printStackTrace();
			return Constants.POST_BOOKMARK_RESULT_INTERNAL_ERR;
		}
	}
	
	public void initializeRequestToken() {
		mRequestToken = mService.getRequestToken();
	}
	
	private OAuthAPIManager() {
		mService = new ServiceBuilder()
						.provider(ReadabilityApi.class)
						.apiKey(APIKeys.CONSUMER_KEY)
						.apiSecret(APIKeys.CONSUMER_SECRET)
						.callback(Constants.OAUTH_CALLBACK_URL)
						.build();
	}
}
