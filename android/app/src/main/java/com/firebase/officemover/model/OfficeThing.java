package com.firebase.officemover.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.firebase.officemover.OfficeThingRenderUtil;

/**
 * @author Jenny Tong (mimming)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficeThing {

    private static final String TAG = OfficeThing.class.getSimpleName();
    private int top;
    private int left;
    @JsonProperty("z-index")
    private int zIndex = 1;
    private String type;
    private String name;
    private int rotation;

    //Cache variables
    @JsonIgnore
    private String key;

    public OfficeThing() {
    }

    @Override
    public String toString() {
        return "OfficeThing:" + type + "{name:" + name + ",X:" + left + ",Y:" + top + ",zIndex:" + zIndex + "}";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        if(rotation % 90 != 0) {
            throw new IllegalArgumentException("Rotation must be multiple of 90 or 0, not " +
                    rotation);
        }

        if (rotation > 360) {
            rotation = rotation - 360;
        }
        this.rotation = rotation;
    }
}
