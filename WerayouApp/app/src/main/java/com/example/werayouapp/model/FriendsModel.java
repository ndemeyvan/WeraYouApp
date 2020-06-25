package com.example.werayouapp.model;

import android.util.Log;

public class FriendsModel implements Comparable< FriendsModel > {
    String id;
    Long time;


    public FriendsModel(String id, Long time) {
        this.id = id;
        this.time = time;
    }

    public FriendsModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public int compareTo(FriendsModel o) {
        int result=o.getTime().intValue();
        Log.i("ResultComparable",result+"");
        return result-this.time.intValue();
    }
}


