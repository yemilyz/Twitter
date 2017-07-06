package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.R.id.ibRetweeter;
import static com.codepath.apps.restclienttemplate.TimelineActivity.POSITION_KEY;
import static com.codepath.apps.restclienttemplate.TimelineActivity.REQUEST_CODE_REPLY;
import static com.codepath.apps.restclienttemplate.TwitterApp.context;


public class TweetDetailsActivity extends AppCompatActivity{

    private TwitterClient client;
    Tweet tweet;
    private int position;

    @BindView(R.id.ivProfImage) ImageView ivProfImage;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvTweet) TextView tvTweet;
    @BindView(R.id.tvFavoriterCounter) TextView tvFavoriterCounter;
    @BindView(R.id.tvRetweeterCounter) TextView tvRetweeterCounter;
    @BindView(R.id.tvTimeStamper) TextView tvTimeStamper;
    @BindView(R.id.ibHearter) ImageButton ibHearter;
    @BindView(R.id.ibRespond) ImageButton ibRespond;
    @BindView(R.id.ibRetweeter) ImageButton ibRetweeter;
    @BindView(R.id.ibMessager) ImageButton ibMessager;
    @BindView(R.id.ivMedia) ImageView ivMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        ButterKnife.bind(this);
        client = TwitterApp.getRestClient();
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        position = getIntent().getIntExtra("Position", 0);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this,25,0))
                .into(ivProfImage);

        Glide.with(this)
                .load(tweet.mediaURL)
                .bitmapTransform(new RoundedCornersTransformation(this,25,0))
                .into(ivMedia);

        int count = tweet.favoriteCount;
        if(tweet.isFavorited && tweet.retweetedStatus!=null) {
            ibHearter.setImageResource(R.drawable.ic_vector_heart);
            count = tweet.retweetedStatus.favoriteCount;
        } else if (tweet.isFavorited) {
            ibHearter.setImageResource(R.drawable.ic_vector_heart);
        } else if (tweet.retweetedStatus != null && tweet.isFavorited == false){
            ibHearter.setImageResource(R.drawable.ic_vector_heart_stroke);
            count = tweet.retweetedStatus.favoriteCount;
        }else{
            ibHearter.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        tvFavoriterCounter.setText(String.valueOf(count));

        if(tweet.isRetweeted) {
            ibRetweeter.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            ibRetweeter.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
        tvRetweeterCounter.setText(String.valueOf(tweet.retweetCount));

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        tvTweet.setText(tweet.body);
        tvTimeStamper.setText(TimeFormatter.getTimeStamp(tweet.createdAt));
        setFavorited();
        setRetweeted();
    }
    public void setFavorited() {
        // Set the tweet favorited status
        int count = tweet.favoriteCount;
        if(tweet.isFavorited && tweet.retweetedStatus!=null) {
            ibHearter.setImageResource(R.drawable.ic_vector_heart);
            count = tweet.retweetedStatus.favoriteCount;
        } else if (tweet.isFavorited) {
            ibHearter.setImageResource(R.drawable.ic_vector_heart);
        } else if (tweet.retweetedStatus != null && tweet.isFavorited == false){
            ibHearter.setImageResource(R.drawable.ic_vector_heart_stroke);
            count = tweet.retweetedStatus.favoriteCount;
        }else{
            ibHearter.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        // Set the number of favorited tweets
        tvFavoriterCounter.setText(String.valueOf(count));
    }

    public void setRetweeted() {
        // Set the retweeted status
        if (tweet.isRetweeted) {
            ibRetweeter.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            ibRetweeter.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
        // Set the number of retweets
        tvRetweeterCounter.setText(String.valueOf(tweet.retweetCount));
    }

    @OnClick(R.id.ibRespond)
    public void putReply() {
        Intent i = new Intent(TweetDetailsActivity.this, ReplyActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        //((AppCompatActivity)context).startActivityForResult(i, REQUEST_CODE_REPLY);
        context.startActivity( i );
    }



    @OnClick(R.id.ibRetweeter)
    public void putRetweet() {
        if(tweet.isRetweeted) {
            client.postNotRetweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        tweet = Tweet.fromJSON(response);
                        Tweet newTweet = Tweet.fromJSON(response);
                        tweet.isRetweeted = false;
                        if(tweet.retweetCount > newTweet.retweetCount) {
                            tweet.retweetCount = newTweet.retweetCount;
                        }
                        setRetweeted();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        } else {
            client.postRetweet(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        Tweet newTweet = Tweet.fromJSON(response);
                        tweet.isRetweeted = true;
                        if(tweet.retweetCount < newTweet.retweetCount) {
                            tweet.retweetCount = newTweet.retweetCount;
                        }
                        setRetweeted();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }
    }

    @OnClick(R.id.ibHearter)
    public void putFavorite() {
        if(tweet.isFavorited) {
            client.setUnfavorite(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int oldFavoriteCount = tweet.favoriteCount;
                        if (tweet.retweetedStatus!=null){
                            oldFavoriteCount = tweet.retweetedStatus.favoriteCount;
                        }
                        tweet = Tweet.fromJSON(response);
                        if(tweet.isFavorited) {
                            tweet.isFavorited = false;
                        }
                        if(tweet.favoriteCount > oldFavoriteCount - 1) {
                            tweet.favoriteCount = oldFavoriteCount - 1;
                        }
                        setFavorited();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }


            });
            // Change the icon
        } else {
            // Favorite the tweet
            client.setFavorite(tweet.uid, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        int oldFavoriteCount = tweet.favoriteCount;
                        if (tweet.retweetedStatus!=null){
                            oldFavoriteCount = tweet.retweetedStatus.favoriteCount;
                        }
                        tweet = Tweet.fromJSON(response);
                        if(!tweet.isFavorited) {
                            tweet.isFavorited = true;
                        }
                        if(tweet.favoriteCount < oldFavoriteCount + 1) {
                            tweet.favoriteCount = oldFavoriteCount + 1;
                        }
                        setFavorited();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });

        }
    }
    @Override
    public void onBackPressed() {
        Log.e("BackPressed", "started");
        Intent i = new Intent();
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        i.putExtra(POSITION_KEY, position);
        setResult(RESULT_OK, i);
        finish();
    }
}
