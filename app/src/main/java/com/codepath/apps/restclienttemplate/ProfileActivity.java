package com.codepath.apps.restclienttemplate;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_profile );

        String screenName = getIntent().getStringExtra( "screen_name" );

        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra( Tweet.class.getSimpleName() ));

        //create user fragment
        if (tweet != null){
            screenName = tweet.user.screenName;
        }

        UserTimelineFragment userTimelineFragment = UserTimelineFragment.newInstance( screenName );

        //display the user timeline fragment inside container dynamically
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //make change
        ft.replace( R.id.flContainer, userTimelineFragment);

        //commit
        ft.commit();

        client = TwitterApp.getRestClient();

        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        if (tweet != null){
            populateUserHeadline( tweet.user );
        } else {
            client.getUserInfo( new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //deserialize user object
                        User user = User.fromJSON( response );
                        //set title of actionbar based on user information
                        getSupportActionBar().setTitle( "@" + user.screenName );
                        //populate user headline
                        populateUserHeadline( user );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } );
        }
    }
    public void populateUserHeadline(User user){
        TextView tvName = (TextView) findViewById( R.id.tvName );
        TextView tvTagline = (TextView) findViewById( R.id.tvTagline );
        TextView tvFollowers = (TextView) findViewById( R.id.tvFollowers );
        TextView tvFollowing = (TextView) findViewById( R.id.tvFollowing );
        ImageView ivProfileImage = (ImageView) findViewById( R.id.ivProfileImage );

        tvName.setText( user.name );
        tvTagline.setText( user.screenName );
        tvFollowers.setText( user.followersCount + " Followers" );
        tvFollowing.setText( user.followingCount + " Following" );

        //load profile image with glide
        Glide.with(this).load( user.profileImageUrl )
                .bitmapTransform( new RoundedCornersTransformation( this, 150, 0 ) )
                .into( ivProfileImage );

    }
}
