package com.learntodroid.androidjsontutorial;

import android.content.Context;

public class MyRetrofitClient {
    private static MyRetrofitClient instance;
    private RedditPostsService redditPostsService;
    private static Context context;

    private MyRetrofitClient(Context context) {
        this.context = context;
        redditPostsService = getRedditPostsService();
    }

    public static synchronized MyRetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new MyRetrofitClient(context);
        }
        return instance;
    }

    public RedditPostsService getRedditPostsService() {
        if (redditPostsService == null) {
            redditPostsService = new retrofit2.Retrofit.Builder()
                    .baseUrl("https://www.reddit.com/r/")
                    .build()
                    .create(RedditPostsService.class);
        }
        return redditPostsService;
    }
}
