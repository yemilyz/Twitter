package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    RecyclerView rvTweets;
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;
    MenuItem miActionProgressItem;
    EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient();

        //find RecyclerView
        rvTweets = (RecyclerView) findViewById(R.id.rvTweet);

        //initiate array list (data source)
        tweets = new ArrayList<>();

        //construct adapter from array list
        tweetAdapter = new TweetAdapter(tweets);

        //RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(this));

        //set adapter
        rvTweets.setAdapter(tweetAdapter);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                showProgressBar();
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager)  {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                populateTimeLine();
                scrollListener.resetState();
            }
        });
    }
    private void populateTimeline(){
        client.getHomeTimeline(0, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //Log.d("TwitterClient", response.toString());
                //iterate through the JSON array
                //for each entry, deserialize the JSON object
                for (int i=0; i<response.length();i++){
                    //convert each object to a tweet model
                    //add that tweet model to our data source
                    //notify the adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if (tweet!=null) {
                            tweets.add(tweet);
                            tweetAdapter.notifyItemInserted(tweets.size() - 1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

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
    public void populateTimeLine(){
        final ArrayList<Tweet> newTweet = null;
        final int curSize = tweetAdapter.getItemCount();
        long maxId = curSize > 0 ? tweets.get(curSize - 1).getUid() : 1;
        client.getHomeTimeline(maxId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //iterate through the JSON array
                //for each entry, deserialize the JSON object
                for (int i=0; i<response.length();i++){
                    //convert each object to a tweet model
                    //add that tweet model to our data source
                    //notify the adapter that we've added an item
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        if (tweet!=null) {
                             newTweet.add(tweet);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                tweets.addAll(newTweet);
                tweetAdapter.notifyItemRangeInserted(curSize, newTweet.size()-1);
                Log.d("DEBUG",tweetAdapter.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d("TwitterClient", responseString);
//                throwable.printStackTrace();
                Log.d("DEBUG","STATUS CODE = " + Integer.toString(statusCode));
                Log.d("DEBUG",responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
                Log.d("DEBUG","STATUS CODE = " + Integer.toString(statusCode));
                Log.d("DEBUG",errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                Log.d("TwitterClient", errorResponse.toString());
//                throwable.printStackTrace();
                Log.d("DEBUG",errorResponse.toString());
            }
        });
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Remember to CLEAR OUT old items before appending in the new ones
                tweetAdapter.clear();
                // ...the data has come back, add new items to your adapter...
                for (int i=0; i<response.length();i++){
                    try {
                        Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                        tweets.add(tweet);
                        tweetAdapter.notifyItemInserted(tweets.size()-1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                swipeContainer.setRefreshing(false);
                hideProgressBar();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                //super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.tweetIcon:
                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Append this tweet to the top of the feed
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            tweets.add(0, newTweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }else{
            Toast.makeText(this, "Failed to submit tweet", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgressItem);
        // Return to finish
        populateTimeline();
        return super.onPrepareOptionsMenu(menu);
    }
    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

}
