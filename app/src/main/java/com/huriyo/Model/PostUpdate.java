package com.huriyo.Model;

/**
 * Created by Jay on 27/12/17.
 */

public class PostUpdate {
    int position, postStatus;
    Feed.Posts posts;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(int postStatus) {
        this.postStatus = postStatus;
    }

    public Feed.Posts getPosts() {
        return posts;
    }

    public void setPosts(Feed.Posts posts) {
        this.posts = posts;
    }
}
