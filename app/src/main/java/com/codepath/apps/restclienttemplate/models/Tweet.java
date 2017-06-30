package com.codepath.apps.restclienttemplate.models;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;
import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by emilyz on 6/26/17.
 */

@Parcel

public class Tweet {
    //list out attributes
    public String body;
    public long uid; //database for the tweet
    public User user;
    public String createdAt;
    public int retweetCount;
    public int favoriteCount;
    public boolean isFavorited;
    public boolean isRetweeted;
    public String mediaURL;
//    public RetweetedStatus retweetedStatus;

    public Tweet(){ super(); }

    public String getMediaURL() {
        return mediaURL;
    }

    public int getFavoriteCount() { return favoriteCount; }

    public long getUid() { return uid; }

    //deserialize JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        //extract values from JSON
        try {
            tweet.retweetCount = jsonObject.getInt("retweet_count");
            tweet.favoriteCount = jsonObject.getInt("favorite_count");
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
            tweet.isFavorited = jsonObject.getBoolean("favorited");
            tweet.isRetweeted = jsonObject.getBoolean("retweeted");
            JSONArray mediaArray = jsonObject.getJSONObject("entities").getJSONArray("media");
            if (mediaArray != null) {
                tweet.mediaURL = mediaArray.getJSONObject(0).getString("media_url");
            }else{
                tweet.mediaURL = null;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJSONArray(JSONArray jsonArray) {
        ArrayList<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject tweetJson = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJSON(tweetJson);
                if (tweet != null) {
                    tweets.add(i,tweet);
                    }
                } catch (JSONException e) {
                e.printStackTrace();
                continue;
                }
            }
        return tweets;
        }


        //tweet.retweetedStatus = RetweetedStatus.fromJSON(jsonObject.getJSONObject("retweeted_status"));

//        Tweet tweet = new Tweet();
//        try {
//            tweet.uid = jsonObject.getLong("id");
//            tweet.body = jsonObject.getString("text");
//            tweet.createdAt = jsonObject.getString("created_at");
//            tweet.retweetCount = jsonObject.getInt("retweet_count");
//            tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return tweet;


}
