package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
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

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.R.id.ibHeart;
import static com.codepath.apps.restclienttemplate.R.id.ibMessage;
import static com.codepath.apps.restclienttemplate.R.id.ibReply;
import static com.codepath.apps.restclienttemplate.R.id.ibRetweet;
import static com.codepath.apps.restclienttemplate.R.id.ivMedia;
import static com.codepath.apps.restclienttemplate.R.id.ivProfileImage;
import static com.codepath.apps.restclienttemplate.R.id.tvFavoriteCount;
import static com.codepath.apps.restclienttemplate.R.id.tvRetweetCount;
import static com.codepath.apps.restclienttemplate.R.id.tweetIcon;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView ivProfImage;
    TextView tvName;
    TextView tvScreenName;
    TextView tvTweet;
    TextView tvFavoriterCount;
    TextView tvRetweeterCounter;
    Tweet tweet;
    TextView tvTimeStamper;
    ImageButton ibHearter;
    ImageButton ibRespond;
    ImageButton ibRetweeter;
    ImageButton ibMessager;
    ImageView ivMedia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
        ivProfImage = (ImageView) findViewById(R.id.ivProfImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvScreenName = (TextView) findViewById(R.id.tvScreenName);
        tvTweet = (TextView) findViewById(R.id.tvTweet);
        tvTimeStamper = (TextView) findViewById(R.id.tvTimeStamper);
        tvFavoriterCount = (TextView) findViewById(R.id.tvFavoriterCounter);
        tvRetweeterCounter = (TextView) findViewById(R.id.tvRetweeterCounter);
        ibHearter = (ImageButton) findViewById(R.id.ibHearter);
        ibRespond = (ImageButton) findViewById(R.id.ibRespond);
        ibRetweeter = (ImageButton) findViewById(R.id.ibRetweeter);
        ibMessager = (ImageButton) findViewById(R.id.ibMessager);
        ivMedia = (ImageView) findViewById(R.id.ivMedia);
        ivMedia.setImageResource(android.R.color.transparent);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this,25,0))
                .into(ivProfImage);

        Glide.with(this)
                .load(tweet.mediaURL)
                .into(ivMedia);

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        tvTweet.setText(tweet.body);
        tvTimeStamper.setText(TimeFormatter.getTimeStamp(tweet.createdAt));
        tvRetweeterCounter.setText(tweet.retweetCount+ " Retweets");
        tvFavoriterCount.setText(tweet.favoriteCount+" Favorites");

    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
