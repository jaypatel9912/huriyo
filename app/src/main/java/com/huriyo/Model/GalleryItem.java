package com.huriyo.Model;

/**
 * Created by Jay on 14/11/17.
 */

public class GalleryItem {
    String path, caption;
    int type;
    boolean isSelected;
    public GalleryItem(){

    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public GalleryItem(String path, int type) {
        this.path = path;
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
