package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.codepath.apps.restclienttemplate.TimelineActivity.REQUEST_CODE_DETAILS;


/**
 * Created by emilyz on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private List<Tweet> mTweets;
    Context context;
    TwitterClient client = TwitterApp.getRestClient();
    LayoutInflater inflater;

    //pass in Tweets array in the constructor
    public  TweetAdapter(List<Tweet> tweets){
        mTweets = tweets;

    }
    //for each row, inflate layout and cache references (all the findById lookups) into ViewHolder class
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }
    public TweetAdapter(Context context, AdapterView.OnItemClickListener listener, List<Tweet> tweets) {
        inflater = LayoutInflater.from(context);
        this.mTweets = tweets;
//        this.onItemClickListener = listener;
    }

    //bind values based on position of the element
    //passes a position and a previously cached viewholder, and repopulate data based on posiiton of that element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get data according to position
        Tweet tweet = mTweets.get(position);

        //populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        holder.tvBody.setText(tweet.body);
        holder.tvHandle.setText("@"+ tweet.user.screenName);
        holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));

        int count = tweet.favoriteCount;
        if(tweet.isFavorited && tweet.retweetedStatus!=null) {
            holder.ibHeart.setImageResource(R.drawable.ic_vector_heart);
            count = tweet.retweetedStatus.favoriteCount;
        } else if (tweet.isFavorited) {
            holder.ibHeart.setImageResource(R.drawable.ic_vector_heart);
        } else if (tweet.retweetedStatus != null && tweet.isFavorited == false){
            holder.ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
            count = tweet.retweetedStatus.favoriteCount;
        }else{
            holder.ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
        }
        holder.tvFavoriteCount.setText(String.valueOf(count));


        if(tweet.isRetweeted) {
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
        } else {
            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
        }
        holder.tvRetweetCount.setText(String.valueOf(tweet.retweetCount));


        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context,25,0))
                .into(holder.ivProfileImage);

        Glide.with(context)
                .load(tweet.mediaURL)
                .bitmapTransform(new RoundedCornersTransformation(context,25,0))
                .into(holder.ivMedia);

        String formattedTime = TimeFormatter.getTimeDifference(tweet.createdAt);
        holder.tvCreatedAt.setText("\u00b7 "+formattedTime);

    }

    @Override
    public int getItemCount() {
        return mTweets.size(); //if leave at 0, items will not render on screen
    }
    //create ViewHolder

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivProfileImage;
        public ImageView ivMedia;
        public TextView tvUsername;
        public TextView tvBody;
        public TextView tvCreatedAt;
        public TextView tvHandle;
        public TextView tvRetweetCount;
        public TextView tvFavoriteCount;
        public ImageButton ibHeart;
        public ImageButton ibReply;
        public ImageButton ibMessage;
        public ImageButton ibRetweet;

        public ViewHolder(View itemView){
            super(itemView);

            //perform findViewById lookups
            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            ivMedia = (ImageView) itemView.findViewById(R.id.ivMedia);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUserName);
            tvBody = (TextView) itemView.findViewById(R.id.tvBody);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tvCreatedAt);
            tvHandle = (TextView) itemView.findViewById(R.id.tvHandle);
            tvRetweetCount = (TextView) itemView.findViewById(R.id.tvRetweetCount);
            tvFavoriteCount = (TextView) itemView.findViewById(R.id.tvFavoriteCount);
            ibHeart = (ImageButton) itemView.findViewById(R.id.ibHeart);
            ibReply = (ImageButton) itemView.findViewById(R.id.ibReply);
            ibMessage = (ImageButton) itemView.findViewById(R.id.ibMessage);
            ibRetweet = (ImageButton) itemView.findViewById(R.id.ibRetweet);

            ibRetweet.setOnClickListener(this);
            tvRetweetCount.setOnClickListener(this);
            ibReply.setOnClickListener(this);
            ibHeart.setOnClickListener(this);
            tvFavoriteCount.setOnClickListener(this);
            ibMessage.setOnClickListener(this);
            itemView.setOnClickListener(this);


        }
        @Override
        public void onClick(View v) {
            final int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                final Tweet tweet = mTweets.get(position);
                switch(v.getId()){
                    case R.id.ibHeart:
                        if (tweet.isFavorited){
                            client.setUnfavorite(tweet.uid, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Tweet tweet = null;
                                    try {
                                        tweet = Tweet.fromJSON(response);
                                        mTweets.set(position, tweet);
                                        setFavorited(tweet);
                                        ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }
                            });

                        }else{
                            client.setFavorite(tweet.uid, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Tweet tweet = null;
                                    try {
                                        tweet = Tweet.fromJSON(response);
                                        mTweets.set(position,tweet);
                                        setFavorited(tweet);
                                        ibHeart.setImageResource(R.drawable.ic_vector_heart);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }
                            });
                        }
                        break;
                    case R.id.tvFavoriteCount:
                        if (tweet.isFavorited){
                            client.setUnfavorite(tweet.uid, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Tweet tweet = null;
                                    try {
                                        tweet = Tweet.fromJSON(response);
                                        mTweets.set(position, tweet);
                                        setFavorited(tweet);
                                        ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("DEBUG", "Failed to unfavorite: " + throwable.toString());
                                }
                            });

                        }else{
                            client.setFavorite(tweet.uid, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    Tweet tweet = null;
                                    try {
                                        tweet = Tweet.fromJSON(response);
                                        mTweets.set(position,tweet);
                                        setFavorited(tweet);
                                        ibHeart.setImageResource(R.drawable.ic_vector_heart);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    //tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

                                }
                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                    Log.d("DEBUG", "Failed to favorite: " + throwable.toString());
                                }
                            });
                        }
                        break;
                    case R.id.ibReply: //TODO
                        Toast.makeText(context, "reply", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.ibRetweet:
                        if(position != RecyclerView.NO_POSITION) {
                            if (tweet.isRetweeted) {
                                // Unretweet the tweet
                                client.postNotRetweet(tweet.uid, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Tweet tweet = null;
                                        try {
                                            tweet = Tweet.fromJSON(response);
                                            mTweets.set(position, tweet);
                                            setRetweeted(tweet);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }
                                });
                            } else {
                                // Retweet the tweet
                                client.postRetweet(tweet.uid, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Tweet tweet = null;
                                        try {
                                            tweet = Tweet.fromJSON(response);
                                            mTweets.set(position, tweet);
                                            setRetweeted(tweet);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }
                                });
                            }
                        }
                        break;
                    case R.id.tvRetweetCount:
                        if(position != RecyclerView.NO_POSITION) {
                            if (tweet.isRetweeted) {
                                // Unretweet the tweet
                                client.postNotRetweet(tweet.uid, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Tweet tweet = null;
                                        try {
                                            tweet = Tweet.fromJSON(response);
                                            mTweets.set(position, tweet);
                                            setRetweeted(tweet);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }
                                });
                            } else {
                                // Retweet the tweet
                                client.postRetweet(tweet.uid, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Tweet tweet = null;
                                        try {
                                            tweet = Tweet.fromJSON(response);
                                            mTweets.set(position, tweet);
                                            setRetweeted(tweet);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                                        Log.d("DEBUG", "Failed to untweet: " + throwable.toString());
                                    }
                                });
                            }
                        }
                        break;
                    case R.id.ibMessage:
                        Toast.makeText(context, "message", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        //tweet = mTweets.get(position);
                        Intent intent = new Intent(context, TweetDetailsActivity.class);
                        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                        intent.putExtra("Position", position);
                        ((AppCompatActivity)context).startActivityForResult(intent, REQUEST_CODE_DETAILS);
                        break;
                }
            }
        }
        public void setFavorited(Tweet tweet) {
            // Set the tweet favorited status
            int count = tweet.favoriteCount;
            if(tweet.isFavorited && tweet.retweetedStatus!=null) {
                ibHeart.setImageResource(R.drawable.ic_vector_heart);
                count = tweet.retweetedStatus.favoriteCount;
            } else if (tweet.isFavorited) {
                ibHeart.setImageResource(R.drawable.ic_vector_heart);
            } else if (tweet.retweetedStatus != null && tweet.isFavorited == false){
                ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
                count = tweet.retweetedStatus.favoriteCount;
            }else{
                ibHeart.setImageResource(R.drawable.ic_vector_heart_stroke);
            }
            // Set the number of favorited tweets
            tvFavoriteCount.setText(String.valueOf(count));
        }

        public void setRetweeted(Tweet tweet) {
            // Set the tweet retweeted status
            if(tweet.isRetweeted) {
                ibRetweet.setImageResource(R.drawable.ic_vector_retweet);
            } else {
                ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
            }

            tvRetweetCount.setText(String.valueOf(tweet.retweetCount));
        }
    }
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
