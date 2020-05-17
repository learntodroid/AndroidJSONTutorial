package com.learntodroid.androidjsontutorial;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RedditPostsService {
    @GET("{subreddit}.json")
    Call<ResponseBody> getPosts(@Path("subreddit") String subreddit);
}
