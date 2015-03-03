package com.csm117.foodapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.scribe.builder.ServiceBuilder;


public class YelpActivity extends ActionBarActivity {

    private static final String API_HOST = "api.yelp.com";
    private static final String DEFAULT_TERM = "dinner";
    private static final String DEFAULT_LOCATION = "San Francisco, CA";
    private static final int SEARCH_LIMIT = 3;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";

    private String consumer_key = "hT1WDC2qYQgFuG4HnuY9wA";
    private String consumer_secret = "ZRZC-eaDZN77DZNpyp2d71bTSuI";
    private String token = "JZPlaCcd1wZ5vAxGO4TSw8oS0bWvioVq";
    private String token_secret = "v9vgIe_aFR30uZbb6t0dzMZrESs";

    private OAuthService service = null;
    private Token accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yelp);
        this.service = new ServiceBuilder().provider(TwoStepOAuth.class)
                .apiKey(consumer_key)
                .apiSecret(consumer_secret).build();
        this.accessToken = new Token(token, token_secret);
        new Thread(new Runnable() {
            public void run() {
                String s = searchForBusinessesByLocation(DEFAULT_TERM, DEFAULT_LOCATION);
                Log.d("meh?", s);
            }
        }).start();

    }

    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    private String sendRequestAndGetResponse(OAuthRequest request) {
        System.out.println("Querying " + request.getCompleteUrl() + " ...");
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    public String searchForBusinessesByLocation(String term, String location) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yelp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
