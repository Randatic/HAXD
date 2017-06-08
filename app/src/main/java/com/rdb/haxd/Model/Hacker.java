package com.rdb.haxd.Model;

/**
 * Created by Randy Bruner on 6/7/2017.
 */

public class Hacker {

    private static Hacker currentUser = new Hacker();

    public static Hacker currentUser()
    {
        return currentUser;
    }

    private String deviceId;
    private String username;
    private String objectId;
    private String ownerId;
    private int level;

    public void set(Hacker hacker) {
        deviceId = hacker.getDeviceId();
        username = hacker.getUsername();
        objectId = hacker.getObjectId();
        ownerId = hacker.getOwnerId();
        level = hacker.getLevel();
    }
    public String info() {
        return (username+" "+deviceId+" "+objectId+" "+ownerId);
    }

    //Getters and Setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getObjectId() {
        return objectId;
    }
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
