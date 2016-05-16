package com.tssssa.sgaheer.groupify;

import java.util.List;

/**
 * Created by sgaheer on 1/05/2016.
 * This is a constructor class for events
 */
public class GEvents {
    private String name;
    private String location;
    private String description;
    private String id;
    private String date;
    private String time;

   public GEvents() {

   }

    public GEvents(String eName, String eLocation, String eDescription, String id, String date, String time) {
        this.name = eName;
        this.location = eLocation;
        this.description = eDescription;
        this.id = id;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
