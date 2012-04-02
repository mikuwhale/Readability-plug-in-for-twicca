package com.whalewhalestudio.mirai.twicaplugins.readability;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class ReadabilityApi extends DefaultApi10a {

	@Override
	public String getAccessTokenEndpoint() {
		return "https://www.readability.com/api/rest/v1/oauth/access_token/";
	}

	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return "https://www.readability.com/api/rest/v1/oauth/authorize?oauth_token=" + requestToken.getToken();
	}

	@Override
	public String getRequestTokenEndpoint() {
		return "https://www.readability.com/api/rest/v1/oauth/request_token/";
	}

}
