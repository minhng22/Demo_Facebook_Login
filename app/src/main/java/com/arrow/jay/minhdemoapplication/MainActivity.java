package com.arrow.jay.minhdemoapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.arrow.jay.minhdemoapplication.util.IabBroadcastReceiver;
import com.arrow.jay.minhdemoapplication.util.IabBroadcastReceiver.IabBroadcastListener;
import com.arrow.jay.minhdemoapplication.util.IabHelper;
import com.arrow.jay.minhdemoapplication.util.IabResult;
import com.arrow.jay.minhdemoapplication.util.Inventory;
import com.arrow.jay.minhdemoapplication.util.Purchase;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends Activity {

    TextView loginStatus;
    Button loginButton;
    Button multiplayerButton;
    Button storeButton;
    Button friends_button;
    Button friends_button_back;
    String[] my_items = {"Ryan", "Kelly"};
    ImageView profilePictureView;
    Boolean friends_list = false;

    String[] web = {
            "User",
            "User",
            "User",
            "User",
            "User",
            "User",
            "User"
    };
    Integer[] imageId = {
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square,
            R.drawable.com_facebook_profile_picture_blank_square

    };

    // Custom button
    private Button fbbutton;

    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;
    // Private method to handle Facebook login and callback

    String str_firstname;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        //AccessToken accessToken = AccessToken.getCurrentAccessToken();
        //if(accessToken != null) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode,
                resultCode, data);
        //

    }


    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();
        //loginStatus.setText("trying to login");
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "user_photos", "public_profile","read_custom_friendlists"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {


                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        Toast.makeText(getApplicationContext(),"sign in called", Toast.LENGTH_LONG).show();
                        //loginStatus.setText("sucess");
                        str_firstname = "something";
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");

                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_email = json.getString("email");
                                                String str_id = json.getString("id");
                                                String str_firstname = json.getString("first_name");
                                                String str_lastname = json.getString("last_name");

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        str_firstname = "something";
                        Log.d("", "On cancel");
                        //loginStatus.setText("cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        str_firstname = "something";
                        Log.d("", error.toString());
                        //loginStatus.setText("errors");
                    }

                });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FacebookSdk.sdkInitialize(getApplicationContext());
        CallbackManager callbackManager = CallbackManager.Factory.create();
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.login_button);
        multiplayerButton = (Button) findViewById(R.id.button_mult);
        storeButton = (Button) findViewById(R.id.storeButton);
        friends_button = (Button) findViewById(R.id.friends_button);
        loginStatus = (TextView) findViewById(R.id.loginStatus);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            Profile profile = Profile.getCurrentProfile();
            loginStatus.setText(profile.getName() + "   " + profile.getId());
            profilePictureView = (ImageView) findViewById(R.id.imageViewProfile);
            Picasso.with(this).load("https://graph.facebook.com/" + profile.getId() + "/picture?_rdr=p").into(profilePictureView);
            loginButton.setVisibility(View.VISIBLE);
            //new DownloadImagesTask.execute("https://graph.facebook.com/10207316689412090/picture?_rdr=p");

        } else {
            profilePictureView = (ImageView) findViewById(R.id.imageViewProfile);
            profilePictureView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
            loginStatus.setText("Signed Out");
            loginButton.setVisibility(View.GONE);
        }




        ProfileTracker mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    profile = Profile.getCurrentProfile();
                    loginStatus.setText(profile.getName() + "   " + profile.getId());
                    profilePictureView = (ImageView) findViewById(R.id.imageViewProfile);
                    Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + profile.getId() + "/picture?_rdr=p").into(profilePictureView);
                    loginButton.setVisibility(View.VISIBLE);
                } else {
                    profilePictureView = (ImageView) findViewById(R.id.imageViewProfile);
                    profilePictureView.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);
                    loginStatus.setText("Signed Out");
                    loginButton.setVisibility(View.GONE);
                }

            }
        };
        profilePictureView = (ImageView) findViewById(R.id.imageViewProfile);

        multiplayerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), MultiplayerActivity.class);
                        startActivity(i);
            }
        });

        storeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), StoreActivity.class);
                startActivity(i);
            }
        });


        CustomList adapter = new CustomList(MainActivity.this, web, imageId);

        ListView myList = (ListView) findViewById(R.id.listViewFriends);
        myList.setAdapter(adapter);
        myList.setVisibility(View.INVISIBLE);

        friends_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ListView myList = (ListView) findViewById(R.id.listViewFriends);

                friends_list = !friends_list;

                if(!friends_list)
                    myList.setVisibility(View.INVISIBLE);
                        else
                             myList.setVisibility(View.VISIBLE);

            }
        });

        profilePictureView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                } else {
                    onFblogin();
                }


            }
        });


        //Profile profile = Profile.getCurrentProfile();
        //loginStatus.setText("yo");
        //loginStatus.setText(profile.getId().toString());



    }


}


