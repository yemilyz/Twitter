package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.Fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.Fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.restclienttemplate.R.id.swipeContainer;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener {

    private final int REQUEST_CODE = 2;
    public static final int REQUEST_CODE_DETAILS = 1;
    public static final int REQUEST_CODE_REPLY = 3;

    private TwitterClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //get the view pager
        ViewPager vpPager = (ViewPager) findViewById( R.id.viewpager );

        //set the adapter for the pager
        vpPager.setAdapter( new TweetsPagerAdapter( getSupportFragmentManager(), this ) );

        //setup the TabLayout to use view pager
        TabLayout tabLayout = (TabLayout) findViewById( R.id.sliding_tabs );
        tabLayout.setupWithViewPager( vpPager );

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ComposeActivity.class);
                startActivityForResult(i, REQUEST_CODE);
            }
        });

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.ic_launcher_twitter);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_timeline, menu );
        return true;
    }

    public void onProfileView(MenuItem item) {
        //launch profile view
        Intent intent = new Intent(this, ProfileActivity.class);
         startActivity( intent );
    }

    @Override
    public void onTweetSelected(Tweet tweet) {

    }
}
