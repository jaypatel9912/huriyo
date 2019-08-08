package com.huriyo.Utility;

public class CameraEntity {
    private boolean cropped;
    private String path;
    private int requestType;

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRequestType() {
        return this.requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public boolean isCropped() {
        return this.cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }
}
