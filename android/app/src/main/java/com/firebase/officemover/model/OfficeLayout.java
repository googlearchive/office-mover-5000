package com.firebase.officemover.model;

import java.util.HashMap;

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
}
