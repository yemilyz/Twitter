package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by emilyz on 7/3/17.
 */

public class TweetsPagerAdapter extends FragmentPagerAdapter {
    //return total no. of fragments

    private String tabTitles[] = new String[]{"Home", "Mentions"};
    private Context context;

    SparseArray<TweetsListFragment> registeredFragments = new SparseArray<TweetsListFragment>();

    public TweetsPagerAdapter(FragmentManager fm, Context context){
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    //return fragment to use depending on the position

    @Override
    public Fragment getItem(int position) {
        if(position == 0 ){
            return new HomeTimelineFragment();
        } else if (position == 1){
            return new MentionsTimelineFragment();
        } else {
            return null;
        }
    }

    public TweetsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    //return title
    @Override
    public CharSequence getPageTitle(int position) {
        //generate title based on item position
        return tabTitles[position];
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TweetsListFragment fragment = (TweetsListFragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove( position );
        super.destroyItem( container, position, object );
    }


    public TweetsListFragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
