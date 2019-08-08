package com.huriyo.Model;

/**
 * Created by Jay on 28/12/17.
 */

public class CommentUpdate {
    int position, commentStatus;
    PostWithComments.Comments comment;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(int commentStatus) {
        this.commentStatus = commentStatus;
    }

    public PostWithComments.Comments getComment() {
        return comment;
    }

    public void setComment(PostWithComments.Comments comment) {
        this.comment = comment;
    }
}
