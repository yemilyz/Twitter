package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by emilyz on 6/29/17.
 */
@Parcel
public class RetweetedStatus {
    public int favoriteCount;

    public RetweetedStatus(){}

    public static RetweetedStatus fromJSON(JSONObject jsonObject){
        RetweetedStatus retweetedStatus = new RetweetedStatus();
        try {
            retweetedStatus.favoriteCount = jsonObject.getInt("favorite_count");
        } catch (JSONException e) {
            retweetedStatus.favoriteCount = 0;
        }
        return retweetedStatus;
    }
}
