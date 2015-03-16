package com.android.npcf34.recommendmemusic.util;

/**
 * Created by Nick on 3/15/2015.
 * POJO for holding artist information
 */
public class Artist {

    private String name;
    private String lastFmLink;

    public Artist(String name, String lastFmLink) {
        this.name = name;
        this.lastFmLink = lastFmLink;
    }

    public String getName() {
        return name;
    }

    public String getLastFmLink() {
        return lastFmLink;
    }

}
