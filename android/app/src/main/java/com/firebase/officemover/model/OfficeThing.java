package com.firebase.officemover.model;

public class OfficeThing {
    private int top;
    private int left;
    private int zIndex;
    private String type;
    private String name;
    private int rotation;

    @Override
    public String toString() {
        return "OfficeThing:" + type + "{name:" + name + ",zIndex:" + zIndex + "}";
    }
}
