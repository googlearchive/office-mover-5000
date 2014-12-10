package com.firebase.officemover.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * @author Jenny Tong (mimming)
 */
public class OfficeLayout extends HashMap<String, OfficeThing> {

    public int getHighestzIndex() {
        if (this.size() == 0) {
            return 0;
        } else {
            int runningHighest = 0;
            for (OfficeThing thing : this.values()) {
                if (thing.getzIndex() > runningHighest) {
                    runningHighest = thing.getzIndex();
                }
            }
            return runningHighest;
        }
    }
    public List<OfficeThing> getThingsTopDown() {
        List<OfficeThing> things = new ArrayList<OfficeThing>(this.values());
        Collections.sort(things, new Comparator<OfficeThing>() {
            @Override
            public int compare(OfficeThing lhs, OfficeThing rhs) {
                return rhs.getzIndex() - lhs.getzIndex();
            }
        });
        return things;
    }
    public List<OfficeThing> getThingsBottomUp() {
        List<OfficeThing> things = new ArrayList<OfficeThing>(this.values());
        Collections.sort(things, new Comparator<OfficeThing>() {
            @Override
            public int compare(OfficeThing lhs, OfficeThing rhs) {
                return lhs.getzIndex() - rhs.getzIndex();
            }
        });
        return things;
    }
}
