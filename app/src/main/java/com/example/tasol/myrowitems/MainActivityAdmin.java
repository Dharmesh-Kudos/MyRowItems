package com.example.tasol.myrowitems;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import smart.caching.SmartCaching;
import smart.framework.SmartApplication;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;

public class MainActivityAdmin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int i = 0;
    TextView txtUsername, txtEmail;
    ImageView imgProfilePicture;
    AQuery aQuery;
    private JSONObject loginParams = null;
    private SmartCaching smartCaching;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);
        smartCaching = new SmartCaching(MainActivityAdmin.this);
        aQuery = new AQuery(MainActivityAdmin.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome Admin");
        }

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabPostAd);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityAdmin.this, PostAdActivity.class).putExtra("FROM", "MAIN"));
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabRequestAd);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivityAdmin.this, PostAdActivity.class).putExtra("FROM", "MAIN"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        //imgBack=(ImageView) v.findViewById(R.id.imgBack);
        imgProfilePicture = (ImageView) v.findViewById(R.id.imgProfilePicture);
        txtUsername = (TextView) v.findViewById(R.id.txtUsername);
        txtEmail = (TextView) v.findViewById(R.id.txtEmail);


        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, ""));
            Glide.with(MainActivityAdmin.this).load(loginParams.getString("user_pic")).placeholder(R.drawable.man).error(R.drawable.no_image)
                    .into((ImageView) v.findViewById(R.id.imgProfilePicture));
//            if (loginParams.getString("user_pic").equals("")) {
//                imgProfilePicture.setImageResource(R.drawable.man);
//            } else {
//                aQuery.id(imgProfilePicture).image(loginParams.getString("user_pic"), true, true);
//            }
            // aQuery.id(imgProfilePicture).image(loginParams.getString("user_pic"), true, true);
            txtUsername.setText(loginParams.getString("name"));
            txtEmail.setText(loginParams.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)

    {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_profile) {
            // Handle the camera action
            Intent intent = new Intent(MainActivityAdmin.this, EditProfileActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_all_users) {
//            Intent loginIntent = new Intent(MainActivityAdmin.this, RentItManageAdsActivity.class);
//            startActivity(loginIntent);
        } else if (id == R.id.nav_all_ads) {
            Intent feedbackIntent = new Intent(MainActivityAdmin.this, AllAdsActivity.class);
            startActivity(feedbackIntent);
        } else if (id == R.id.nav_reports) {
//            Intent contactusIntent = new Intent(MainActivityAdmin.this, ContactUsActivity.class);
//            startActivity(contactusIntent);
        } else if (id == R.id.nav_faq) {

        } else if (id == R.id.nav_feedback) {

        } else if (id == R.id.nav_send) {
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

            Intent loginIntent = new Intent(MainActivityAdmin.this, RentItLoginActivity.class);
            startActivity(loginIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.
                id.admin_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
