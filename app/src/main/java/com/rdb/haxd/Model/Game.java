package com.rdb.haxd.Model;

/**
 * Created by Randy Bruner on 6/8/2017.
 */

public class Game {
    private int downloadTime;
    private int tick;

    public Game() {
        downloadTime = 100000;
        tick = 1000;
    }

    public int getDownloadTime() {
        return downloadTime;
    }

    public int getTick() {
        return tick;
    }

    public void setDownloadTime(int downloadTime) {
        this.downloadTime = downloadTime;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    /**
     * If Slow Download time is used double time and tick
     */
}
