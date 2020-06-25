package com.example.werayouapp.model;

import android.util.Log;

public class MyFriendModel implements Comparable< MyFriendModel >{
    String id;
    Long time;


    public MyFriendModel(String id, Long time) {
        this.id = id;
        this.time = time;
    }

    public MyFriendModel() {
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
    public int compareTo(MyFriendModel o) {
        int result=o.getTime().intValue();
        Log.i("ResultComparable",result+"");
        return result-this.time.intValue();
    }
}
