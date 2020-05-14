package com.learntodroid.androidjsontutorial;

public class Post {
    private String title;
    private int score;
    private int commentCount;
    private double created;

    public Post(String title, int score, int commentCount, double created) {
        this.title = title;
        this.score = score;
        this.commentCount = commentCount;
        this.created = created;
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

    public double getCreated() {
        return created;
    }
}
