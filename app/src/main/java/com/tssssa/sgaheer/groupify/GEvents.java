package com.tssssa.sgaheer.groupify;

import java.util.List;

/**
 * Created by sgaheer on 1/05/2016.
 */
public class GEvents {
    private String eName;
    private String eLocation;
    private String eDescription;
    private List<String> eAttendees;

   public GEvents() {

   }

    public GEvents(String eName, String eLocation, String eDescription, List<String> eAttendees) {
        this.eName = eName;
        this.eLocation = eLocation;
        this.eDescription = eDescription;
        this.eAttendees = eAttendees;
    }

    public String geteName() {
        return eName;
    }

    public String geteLocation() {
        return eLocation;
    }

    public String geteDescription() {
        return eDescription;
    }

    public List<String> geteAttendees() {
        return eAttendees;
    }

    public void seteAttendees(List<String> eAttendees) {
        this.eAttendees = eAttendees;
    }

    public void addeAttendees(String eMember) {
        eAttendees.add(eMember);
    }

    public void removeeAttendees(String eMember) {
        eAttendees.remove(eMember);
    }
}
