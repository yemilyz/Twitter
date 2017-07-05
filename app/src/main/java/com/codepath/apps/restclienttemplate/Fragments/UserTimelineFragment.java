package com.codepath.apps.restclienttemplate.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.codepath.apps.restclienttemplate.Fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by emilyz on 7/3/17.
 */

public class UserTimelineFragment extends TweetsListFragment {
    TwitterClient client;

    public static UserTimelineFragment newInstance (String screenName){
        UserTimelineFragment userTimelineFragment = new UserTimelineFragment();
        Bundle args = new Bundle( );
        args.putString( "screen_name", screenName );
        userTimelineFragment.setArguments( args );
        return userTimelineFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateTimeline();
    }
    private void populateTimeline(){
        //comes from activity
        String screenName = getArguments().getString( "screen_name" );
        client.getUserTimeline(screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                addItems(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
            }
        });
    }
}
