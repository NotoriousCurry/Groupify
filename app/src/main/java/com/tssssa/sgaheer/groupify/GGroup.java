package com.tssssa.sgaheer.groupify;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by sgaheer on 1/05/2016.
 */
public class GGroup {
    private String gName;
    private String gDescription;
    private List<String> gMembers;

    public GGroup() {

    }

    public GGroup(String gName, String gDescription, List<String> gMembers) {
        this.gName = gName;
        this.gDescription = gDescription;
        this.gMembers = gMembers;
    }

    public String getName() {
        return gName;
    }

    public String getLocation() {
        return gDescription;
    }

    public List<String> getMembers() {
        return gMembers;
    }

    public void setMembers(List<String> members) {
        this.gMembers = members;
    }

    public void addMembers(String nMember) {
        gMembers.add(nMember);
    }

    public void removeMember(String nMember) {
        gMembers.remove(nMember);
    }
}
