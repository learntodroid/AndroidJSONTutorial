package com.learntodroid.androidjsontutorial;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("title")
    private String title;

    @SerializedName("score")
    private int score;

    @SerializedName("num_comments")
    private int commentCount;

    public Post(String title, int score, int commentCount) {
        this.title = title;
        this.score = score;
        this.commentCount = commentCount;
    }

    public String getTitle() {
        return title;
    }

    public int getScore() {
        return score;
    }

    public int getCommentCount() {
        return commentCount;
    }
}
