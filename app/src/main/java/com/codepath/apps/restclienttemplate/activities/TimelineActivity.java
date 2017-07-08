package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetDetailsActivity;
import com.codepath.apps.restclienttemplate.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.fragments.ComposeFragment;
import com.codepath.apps.restclienttemplate.fragments.HomeTimelineFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TimelineActivity extends AppCompatActivity implements TweetsListFragment.TweetSelectedListener , ComposeFragment.ComposeDialogListener {

    private final int REQUEST_CODE = 2;
    public static final int REQUEST_CODE_DETAILS = 1;
    public static final int REQUEST_CODE_REPLY = 3;
    public static final String POSITION_KEY = "postionKey";

    public static ViewPager vpPager;
    public static TweetsPagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_timeline );

        //get the view pager
        vpPager = (ViewPager) findViewById( R.id.viewpager );

        //set the adapter for the pager
        pagerAdapter = new TweetsPagerAdapter( getSupportFragmentManager(), this );

        vpPager.setAdapter( pagerAdapter );

        //setup the TabLayout to use view pager
        TabLayout tabLayout = (TabLayout) findViewById( R.id.sliding_tabs );
        tabLayout.setupWithViewPager( vpPager );

        getSupportActionBar().setCustomView(R.layout.actionbar_title);
        getSupportActionBar().setDisplayOptions( ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById( R.id.floatingActionButton );

        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fm, "fragment_compose");
            }
        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_timeline, menu );
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchItem.expandActionView();
//        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("search_query", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public void onProfileView(MenuItem item) {
        //launch profile view
        Intent intent = new Intent( this, ProfileActivity.class );
        startActivity( intent );
    }

    @Override
    public void onTweetSelected(Tweet tweet, int position) {
        if (position != RecyclerView.NO_POSITION) {
            // Create an intent to the TweetDetailsActivity
            Intent i = new Intent( this, TweetDetailsActivity.class );
            i.putExtra( Tweet.class.getSimpleName(), Parcels.wrap( tweet ) );
            i.putExtra( POSITION_KEY, position );
            // Start the activity
            startActivityForResult( i, REQUEST_CODE_DETAILS );
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            Tweet newTweet = (Tweet) Parcels.unwrap( data.getParcelableExtra( Tweet.class.getSimpleName() ) );
            int position = data.getIntExtra( POSITION_KEY, 0 );
            TweetsListFragment currentFragment = pagerAdapter.getRegisteredFragment( vpPager.getCurrentItem() );
            currentFragment.tweets.set( position, newTweet );
            currentFragment.tweetAdapter.notifyItemChanged( position );
            currentFragment.rvTweets.scrollToPosition( position );
        } else if (REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            // Append this tweet to the top of the feed
            Tweet newTweet = (Tweet) Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            TweetsListFragment tweetsListFragment = (TweetsListFragment) pagerAdapter.getRegisteredFragment(0);
            tweetsListFragment.appendTweet(newTweet);
        }
    }

    @Override
    public void onFinishComposeDialog(Tweet tweet) {
        TweetsListFragment currentFragment = pagerAdapter.getRegisteredFragment(vpPager.getCurrentItem());
        if(currentFragment instanceof HomeTimelineFragment) {
            currentFragment.tweets.set( 0, tweet );
            currentFragment.tweetAdapter.notifyItemInserted( 0 );
            currentFragment.rvTweets.scrollToPosition( 0 );
        }
    }
}
