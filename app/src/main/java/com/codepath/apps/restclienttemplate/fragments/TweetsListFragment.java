package com.codepath.apps.restclienttemplate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * Created by emilyz on 7/3/17.
 */

public class TweetsListFragment extends Fragment implements TweetAdapter.TweetAdapterListener {

    MenuItem miActionProgressItem;

    public TweetAdapter tweetAdapter;
    public ArrayList<Tweet> tweets;
    public RecyclerView rvTweets;

    public interface TweetSelectedListener {
        //handle tweet selection
        public void onTweetSelected(Tweet tweet, int position);
    }

    public SwipeRefreshLayout swipeContainer;

    private TwitterClient client;

//    private final int REQUEST_CODE = 2;
//    public static final int REQUEST_CODE_DETAILS = 1;
//    public static final int REQUEST_CODE_REPLY = 3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragments_tweets_list,container,false);

        //find RecyclerView
        rvTweets = (RecyclerView)v.findViewById(R.id.rvTweet);

        //find the swipe container
        swipeContainer = (SwipeRefreshLayout)v.findViewById(R.id.swipeContainer);

        //initiate array list (data source)
        tweets = new ArrayList<>();

        //construct adapter from array list
        tweetAdapter = new TweetAdapter(tweets, this);

        //RecyclerView setup (layout manager, use adapter)
        rvTweets.setLayoutManager(new LinearLayoutManager(getContext()));

        //set adapter
        rvTweets.setAdapter(tweetAdapter);

        client = TwitterApp.getRestClient();

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                //showProgressBar();
                fetchTimelineAsync( 0 );
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        rvTweets.setAdapter(tweetAdapter);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        rvTweets.setLayoutManager(linearLayoutManager);
//
//        rvTweets.addOnScrollListener(new EndlessRecyclerViewScrollListener(linearLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                int curSize = tweetAdapter.getItemCount();
//                long maxId = curSize > 0 ? tweets.get( curSize - 1 ).getUid() : 1;
//                populateTimeline( maxId );
//            }
//        });

        return v;

    }
    public void addItems(JSONArray response){
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
    public void onItemSelected(View v, int position) {
        Tweet tweet = tweets.get(position);
        ((TweetSelectedListener) getActivity()).onTweetSelected( tweet, position );
    }

    private void populateTimeline(){
        client.getHomeTimeline(0, new JsonHttpResponseHandler(){
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

    public void fetchTimelineAsync(long page) {
    }

    public void showProgressBar() {
        // Show progress item
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgressItem.setVisible(false);
    }

    public void appendTweet(Tweet tweet) {
        tweets.add(0, tweet);
        // inserted at position 0
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
//            // Append this tweet to the top of the feed
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            tweets.add(0, newTweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            int position = data.getIntExtra("Position", 0);
//            tweets.set(position, newTweet);
//            tweetAdapter.notifyItemChanged(position);
//            rvTweets.scrollToPosition(position);
//        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_REPLY) {
//            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
//            tweets.add(0, newTweet);
//            tweetAdapter.notifyItemInserted(0);
//            rvTweets.scrollToPosition(0);
//        } else {
//            Toast.makeText(getContext(), "Failed to submit tweet", Toast.LENGTH_SHORT).show();
//        }
//    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            // action with ID action_refresh was selected
//            case R.id.tweetIcon:
//                Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
//                startActivityForResult(i, REQUEST_CODE);
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}
