package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by emilyz on 6/26/17.
 */

@Parcel
public class User {
    //list all attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;
    public String favoriteCount;
    public String retweetCount;

    public User(){}

    //deserialize JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        //extract and fill the values
        user.name = json.getString("name");
        user.uid = json.getLong("id");
        user.screenName = json.getString("screen_name");
        user.profileImageUrl = json.getString("profile_image_url");
        try {
            user.favoriteCount = json.getString("favourites_count");
        }
        catch (Exception e){
            user.favoriteCount = "0";
        }
        try{
            user.retweetCount = json.getString("retweet_count");
        } catch(Exception e){
            user.retweetCount = "0";
        }
        return user;
    }

}
