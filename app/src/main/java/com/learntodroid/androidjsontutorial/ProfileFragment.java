package com.learntodroid.androidjsontutorial;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private Button loadButton;
    private Button saveButton;

    private EditText profileName;
    private EditText age;
    private EditText hobbies;

    private ImageView backgroundImage;
    private ImageView profileImage;

    private String backgroundImageUri;
    private String profileImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadProfile() throws JSONException {
        String defaultJsonString = "{\n" +
                "  \"name\": \"Bob Jones\",\n" +
                "  \"hobbies\": [ \"Android\", \"Programming\", \"Reading\" ],\n" +
                "  \"age\": 38,\n" +
                "  \"images\": {\n" +
                "    \"profile_image_uri\": \"https://images.pexels.com/photos/834863/pexels-photo-834863.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260\",\n" +
                "    \"background_image_uri\": \"https://images.pexels.com/photos/949587/pexels-photo-949587.jpeg?auto=compress&cs=tinysrgb&dpr=3&h=750&w=1260\"\n" +
                "  }\n" +
                "}";

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String profileJson = sharedPreferences.getString(getString(R.string.profile_preferences_key), defaultJsonString);

        JSONObject jsonObject = new JSONObject(profileJson);
        profileName.setText(jsonObject.getString("name"));
        age.setText(String.valueOf(jsonObject.getInt("age")));

        JSONArray jsonArray = jsonObject.getJSONArray("hobbies");
        List<String> hobbiesList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            hobbiesList.add(jsonArray.get(i).toString());
        }
        hobbies.setText(TextUtils.join(", ", hobbiesList));

        backgroundImageUri = jsonObject.getJSONObject("images").getString("background_image_uri");
        Glide.with(this)
                .load(backgroundImageUri)
                .into(backgroundImage);

        profileImageUri = jsonObject.getJSONObject("images").getString("profile_image_uri");
        Glide.with(this)
                .load(profileImageUri)
                .circleCrop()
                .into(profileImage);

//        Toast.makeText(getContext(), profileJson, Toast.LENGTH_LONG).show();
    }

    private void saveProfile() throws JSONException {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONObject profileJsonObject = new JSONObject();
        profileJsonObject.put("name", profileName.getText().toString());
        profileJsonObject.put("age", Integer.parseInt(age.getText().toString()));

        JSONArray hobbiesJsonArray = new JSONArray();
        String[] hobbiesArray = hobbies.getText().toString().split(",");
        for (int i = 0; i < hobbiesArray.length; i++) {
            hobbiesJsonArray.put(hobbiesArray[i].trim());
        }

        profileJsonObject.put("hobbies", hobbiesJsonArray);

        JSONObject imagesJsonObject = new JSONObject();
        imagesJsonObject.put("background_image_uri", backgroundImageUri);
        imagesJsonObject.put("profile_image_uri", profileImageUri);

        profileJsonObject.put("images", imagesJsonObject);

        editor.putString(getString(R.string.profile_preferences_key), profileJsonObject.toString());
        editor.commit();

//        Toast.makeText(getContext(), profileJsonObject.toString(), Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileName = view.findViewById(R.id.fragment_profile_name);
        age = view.findViewById(R.id.fragment_profile_age);
        backgroundImage = view.findViewById(R.id.fragment_profile_background);
        profileImage = view.findViewById(R.id.fragment_profile_picture);
        hobbies = view.findViewById(R.id.fragment_profile_hobbies);

        loadButton = view.findViewById(R.id.fragment_profile_load);
        saveButton = view.findViewById(R.id.fragment_profile_save);

        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    loadProfile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveProfile();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            loadProfile();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
