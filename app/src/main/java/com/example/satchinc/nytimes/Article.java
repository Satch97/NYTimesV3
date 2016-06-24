package com.example.satchinc.nytimes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by satchinc on 6/21/16.
 */
public class Article {
    String webURL;

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getWebURL() {
        return webURL;
    }

    String headline;
    String thumbnail;

    public Article(JSONObject jsonObject) throws JSONException {
        try {
            this.webURL = jsonObject.getString("web_url");
            JSONObject headline = jsonObject.getJSONObject("headline");
            this.headline = headline.getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJSON = multimedia.getJSONObject(0);
                this.thumbnail = "http://www.nytimes.com/" + multimediaJSON.getString("url");
            } else {
                this.thumbnail = "";
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }
    public Article(JSONObject jsonObject, int topstoriespassvalue) throws JSONException{
        try {this.webURL = jsonObject.getString("url");

        this.headline=jsonObject.getString("title");

        JSONArray multimedia = jsonObject.getJSONArray("multimedia");

        if (multimedia.length() > 0) {
            JSONObject multimediaJSON = multimedia.getJSONObject(1);
            this.thumbnail = multimediaJSON.getString("url");
        } else {
            this.thumbnail = "";
        }
        }
        catch (JSONException e){
            e.printStackTrace();
        }


    }
    public static ArrayList<Article> fromJSONArray(JSONArray array){
        ArrayList<Article> results = new ArrayList<>();
        for(int x=0; x<array.length();x++){
            try {
                results.add(new Article(array.getJSONObject(x)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
    public static ArrayList<Article> tsfromJSONArray(JSONArray array){
        ArrayList<Article> results = new ArrayList<>();
        for(int x=0; x<array.length();x++){
            try {
                results.add(new Article(array.getJSONObject(x), 2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
