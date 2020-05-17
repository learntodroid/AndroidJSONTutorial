package com.learntodroid.androidjsontutorial;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class SubredditFragment extends Fragment {
    private EditText subreddit;
    private Button getPostsButton;
    private RecyclerView postsRecyclerView;
    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;
    private List<Post> posts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subreddit, container, false);

        subreddit = view.findViewById(R.id.fragment_subreddit_subreddit);
        getPostsButton = view.findViewById(R.id.fragment_subreddit_getposts);
        postsRecyclerView = view.findViewById(R.id.fragment_subreddit_posts);

        getPostsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getPostsUsingVolley();
//                getPostsUsingRetrofit();
//                getPostsUsingRetrofitWithGSON();
            }
        });

        posts = new ArrayList<>();
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(posts);

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(postsRecyclerViewAdapter);
        
        try {
            readJSONFile("posts.json");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void getPostsUsingVolley() {
        String uri = String.format("https://www.reddit.com/r/%s.json", subreddit.getText().toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                uri,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            posts = new ArrayList<>();
                            JSONArray postsJson = response.getJSONObject("data").getJSONArray("children");
                            for (int i = 0; i < postsJson.length(); i++) {
                                String postTitle = postsJson.getJSONObject(i).getJSONObject("data").getString("title");
                                int postScore = postsJson.getJSONObject(i).getJSONObject("data").getInt("score");
                                int postComments = postsJson.getJSONObject(i).getJSONObject("data").getInt("num_comments");
                                Post post = new Post(postTitle, postScore, postComments);
                                posts.add(post);
                            }
                            postsRecyclerViewAdapter.setPosts(posts);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("onErrorResponse", error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        MyRequestQueue.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void getPostsUsingRetrofit() {
        MyRetrofitClient.getInstance(getContext()).getRedditPostsService().getPosts(subreddit.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        try {
                            posts = new ArrayList<>();

                            String responseJSONString = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseJSONString);

                            JSONArray postsJson = responseJSON.getJSONObject("data").getJSONArray("children");
                            for (int i = 0; i < postsJson.length(); i++) {
                                String postTitle = postsJson.getJSONObject(i).getJSONObject("data").getString("title");
                                int postScore = postsJson.getJSONObject(i).getJSONObject("data").getInt("score");
                                int postComments = postsJson.getJSONObject(i).getJSONObject("data").getInt("num_comments");
                                Post post = new Post(postTitle, postScore, postComments);
                                posts.add(post);
                            }
                            postsRecyclerViewAdapter.setPosts(posts);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("onFailure", t.getMessage());
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });
    }

    private void getPostsUsingRetrofitWithGSON() {
        MyRetrofitClient.getInstance(getContext()).getRedditPostsService().getPosts(subreddit.getText().toString())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                        try {
                            posts = new ArrayList<>();

                            String responseJSONString = response.body().string();
                            JSONObject responseJSON = new JSONObject(responseJSONString);

                            JSONArray postsJson = responseJSON.getJSONObject("data").getJSONArray("children");
                            for (int i = 0; i < postsJson.length(); i++) {
                                Post post = convertJSONToPost(postsJson.getJSONObject(i).getJSONObject("data").toString());
                                posts.add(post);
                            }
                            postsRecyclerViewAdapter.setPosts(posts);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.i("onFailure", t.getMessage());
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Post convertJSONToPost(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Post.class);
    }

    private String convertPostToJson(Post post) {
        Gson gson = new Gson();
        return gson.toJson(post);
    }

    private void writeJSONFile(String json, String fileName) throws IOException {
        File file = new File(getContext().getFilesDir(), fileName);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileOutputStream fileOutputStream = getContext().openFileOutput(fileName, Context.MODE_PRIVATE);
        fileOutputStream.write(json.getBytes(Charset.forName("UTF-8")));
    }

    private String readJSONFile(String fileName) throws IOException {
        FileInputStream fileInputStream = getContext().openFileInput(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName("UTF-8"));
        List<String> lines = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            lines.add(line);
            line = reader.readLine();
        }
        String json = TextUtils.join("\n", lines);

        Toast.makeText(getContext(), json, Toast.LENGTH_SHORT).show();

        return json;
    }
}
