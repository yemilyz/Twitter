package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.app.TwitterApp;
import com.codepath.apps.restclienttemplate.client.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by emilyz on 7/7/17.
 */

public class SearchFragment extends TweetsListFragment {
    TwitterClient client;
//    private String searchQuery;

    public static SearchFragment getInstance(String searchQuery) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString("search_query", searchQuery);
        searchFragment.setArguments(args);
        return searchFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApp.getRestClient();
        populateSearchResults();
    }

    private void populateSearchResults() {
//        showProgressBar();
        String searchQuery = getArguments().getString("search_query");
        client.searchTweets(searchQuery, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
                try {
                    addItems(response.getJSONArray("statuses"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                hideProgressBar();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.e("TwitterClient", response.toString());
                addItems(response);
//                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(getContext(), "Failed to get tweets", Toast.LENGTH_SHORT).show();
                Log.e("TwitterClient", responseString);
                throwable.printStackTrace();
//                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Failed to get tweets", Toast.LENGTH_SHORT).show();
                Log.e("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
//                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Toast.makeText(getContext(), "Failed to get tweets", Toast.LENGTH_SHORT).show();
                Log.e("TwitterClient", errorResponse.toString());
                throwable.printStackTrace();
//                hideProgressBar();
            }
        });
    }
}
