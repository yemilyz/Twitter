package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

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
//    public int favoriteCount;

    public User(){ super(); }

    public long getUid() { return uid; }

    //deserialize JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        //extract and fill the values
        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            user.favoriteCount = json.getInt("favorite_count");
//        }
//        catch (Exception e){
//            //user.favoriteCount = 0;
//            Log.d("DEBUG", "error");
//        }
        return user;
    }

}
