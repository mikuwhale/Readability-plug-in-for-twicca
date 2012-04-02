package com.whalewhalestudio.mirai.twicaplugins.readability;

public class Constants {
	// Readability OAUTH URLs
	public static final String REQUEST_URL = "https://www.readability.com/api/rest/v1/oauth/request_token/";
	public static final String ACCESS_URL = "https://www.readability.com/api/rest/v1/oauth/access_token/";
	public static final String AUTHORIZE_URL = "https://www.readability.com/api/rest/v1/oauth/authorize/";
	
	// Readability API URLs
	public static final String GET_CURRENT_USER_URL = "https://www.readability.com/api/rest/v1/users/_current";
	public static final String POST_BOOKMARK_URL = "https://www.readability.com/api/rest/v1/bookmarks";
	
	// OAuth constants
	public static final String OAUTH_CALLBACK_SCHEME = "x-twicca-oauth-readability-plugin";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	
	// Post bookmark potential fault
	public static final int POST_BOOKMARK_RESULT_SUCCESS = 202;
	public static final int POST_BOOKMARK_RESULT_BAD_REQ = 400;
	public static final int POST_BOOKMARK_RESULT_AUTH_REQ = 401;
	public static final int POST_BOOKMARK_RESULT_CONFLICT = 409;
	public static final int POST_BOOKMARK_RESULT_SERVER_ERR = 500;
	public static final int POST_BOOKMARK_RESULT_INTERNAL_ERR = 999;
}
