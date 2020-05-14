package com.learntodroid.androidjsontutorial;

import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
                getPosts();
            }
        });

        posts = new ArrayList<>();
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(posts);

        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postsRecyclerView.setAdapter(postsRecyclerViewAdapter);

//        readLocalJsonFile();
//        writeLocalJsonFile();

        return view;
    }


    private void getPosts() {
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
                                double postCreated = postsJson.getJSONObject(i).getJSONObject("data").getDouble("created");
                                Post post = new Post(postTitle, postScore, postComments, postCreated);
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
/*
    private void parseJSON() {
        String jsonString = "{ \"test\": \"hello\", \"test2\": 1.0, \"test3\": 100, \"test4\": true, \"test5\": { \"test6\": \"nested\" }}";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readLocalJsonFile() {
        // todo make the file the same used for writing and reading
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.posts);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            List<String> lines = new ArrayList<String>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }

            String jsonString = TextUtils.join("", lines);
            JSONObject jsonObject = new JSONObject(jsonString);
            Toast.makeText(getApplicationContext(), jsonObject.getString("test"), Toast.LENGTH_SHORT).show();


            Toast.makeText(getApplicationContext(), TextUtils.join("\n", lines), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeLocalJsonFile() {
        // todo write to the same file read from internal storage
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("test", "hello");
            jsonObject.put("test2", 1.0);
            jsonObject.put("test3", 100);
            jsonObject.put("test4", true);

            JSONObject nestedJsonObject = new JSONObject();
            nestedJsonObject.put("test6", "nested");

            jsonObject.put("test5", nestedJsonObject);

            String jsonString = jsonObject.toString();
            Log.i("write", jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
