package com.firebase.officemover.model;

import java.util.ArrayList;

public class OfficeLayout extends ArrayList<OfficeThing> {

    public int getHighestzIndex() {
        if (this.size() == 0) {
            return 0;
        } else {
            int runningHighest = 0;
            for (OfficeThing thing : this) {
                if (thing.getzIndex() > runningHighest) {
                    runningHighest = thing.getzIndex();
                }
            }
            return runningHighest;
        }
    }
}
