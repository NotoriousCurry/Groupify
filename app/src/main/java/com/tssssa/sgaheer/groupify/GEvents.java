package com.tssssa.sgaheer.groupify;

import java.util.List;

/**
 * Created by sgaheer on 1/05/2016.
 */
public class GEvents {
    private String name;
    private String location;
    private String description;
    private String members;
    private String id;

   public GEvents() {

   }

    public GEvents(String eName, String eLocation, String eDescription, String eAttendees, String id) {
        this.name = eName;
        this.location = eLocation;
        this.description = eDescription;
        this.members = eAttendees;
        this.id = id;
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

    public String getMembers() {
        return members;
    }

    public void setMembers(String eAttendees) {
        this.members = eAttendees;
    }
}
