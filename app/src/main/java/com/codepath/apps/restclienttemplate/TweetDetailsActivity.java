package com.codepath.apps.restclienttemplate;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.sql.Time;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.R.id.ivProfileImage;
import static com.codepath.apps.restclienttemplate.R.id.tweetIcon;

public class TweetDetailsActivity extends AppCompatActivity {

    ImageView ivProfImage;
    TextView tvName;
    TextView tvScreenName;
    TextView tvTweet;
    TextView tvFavoriterCount;
    TextView tvRetweeterCount;
    Tweet tweet;
    TextView tvTimeStamper;

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
        tvRetweeterCount = (TextView) findViewById(R.id.tvRetweeterCounter);
        tweet = (Tweet) Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(this,25,0))
                .into(ivProfImage);

        tvName.setText(tweet.user.name);
        tvScreenName.setText("@"+tweet.user.screenName);
        tvTweet.setText(tweet.body);
        tvTimeStamper.setText(TimeFormatter.getTimeStamp(tweet.createdAt));
        tvFavoriterCount.setText(tweet.user.favoriteCount+" Favorites");
        tvRetweeterCount.setText(tweet.user.retweetCount+ " Retweets");


    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
