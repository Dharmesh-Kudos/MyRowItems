package com.example.tasol.myrowitems;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;

public class MainActivityAdmin extends AppCompatActivity {

    RecyclerView rvAdmin;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    DashboardAdapter dashboardAdapter;
    int i = 0;
    KudosTextView txtUsername, txtEmail;
    ImageView imgProfilePicture;
    AQuery aQuery;
    int[] IMAGESOFCATS = {R.drawable.profile,
            R.drawable.avatar,
            R.drawable.ads,
            R.drawable.report,
            R.drawable.faq,
            R.drawable.feedback,
            R.drawable.account,
            R.drawable.logout};
    String[] NAMESOFCATS = {"My Profile", "All Users", "All Ads", "View Reports", "Add FAQ", "View Feedback", "Settings", "Logout"};
    boolean doubleBackToExitPressedOnce = false;
    private JSONObject loginParams = null;
    private SmartCaching smartCaching;
    private SweetAlertDialog pDialogVisit;
    private FloatingActionMenu fmenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_categories_list);
        smartCaching = new SmartCaching(MainActivityAdmin.this);
        aQuery = new AQuery(MainActivityAdmin.this);

        rvAdmin = (RecyclerView) findViewById(R.id.rvAdmin);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvAdmin.setLayoutManager(staggeredGridLayoutManager);
        dashboardAdapter = new DashboardAdapter();
        rvAdmin.setAdapter(dashboardAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rent It Admin Dashboard");
        }
        fmenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fmenu.setClosedOnTouchOutside(true);
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

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.admin_drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        View v = navigationView.getHeaderView(0);
//        //imgBack=(ImageView) v.findViewById(R.id.imgBack);
//        imgProfilePicture = (ImageView) v.findViewById(R.id.imgProfilePicture);
//        txtUsername = (KudosTextView) v.findViewById(R.id.txtUsername);
//        txtEmail = (KudosTextView) v.findViewById(R.id.txtEmail);


//        try {
//            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
//                    .getString(SP_LOGGED_IN_USER_DATA, ""));
//            Glide.with(MainActivityAdmin.this).load(loginParams.getString("user_pic")).placeholder(R.drawable.man).error(R.drawable.no_image)
//                    .into((ImageView) v.findViewById(R.id.imgProfilePicture));
////            if (loginParams.getString("user_pic").equals("")) {
////                imgProfilePicture.setImageResource(R.drawable.man);
////            } else {
////                aQuery.id(imgProfilePicture).image(loginParams.getString("user_pic"), true, true);
////            }
//            // aQuery.id(imgProfilePicture).image(loginParams.getString("user_pic"), true, true);
//            txtUsername.setText(loginParams.getString("name"));
//            txtEmail.setText(loginParams.getString("email"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        navigationView.setNavigationItemSelectedListener(this);


    }


    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
        if (fmenu.isOpened()) {
            fmenu.close(true);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
//            super.onBackPressed();
        }
    }

    private class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dashboard_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.txtCatName.setText(NAMESOFCATS[position]);
            holder.ivCatName.setImageResource(IMAGESOFCATS[position]);

            holder.ivCatName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position) {
                        case 0:
                            Intent intent = new Intent(MainActivityAdmin.this, EditProfileActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            Intent loginIntent = new Intent(MainActivityAdmin.this, AllUsersActivity.class);
                            startActivity(loginIntent);
                            break;
                        case 2:
                            Intent feedbackIntent = new Intent(MainActivityAdmin.this, AllAdsActivity.class);
                            startActivity(feedbackIntent);
                            break;
                        case 3:
                            Intent contactusIntent = new Intent(MainActivityAdmin.this, AllReportsActivity.class);
                            startActivity(contactusIntent);
                            break;
                        case 4:
                            Intent contactusIntent2 = new Intent(MainActivityAdmin.this, AddFAQActivity.class);
                            startActivity(contactusIntent2);
                            break;
                        case 5:
                            Intent contactusIntent4 = new Intent(MainActivityAdmin.this, ViewFeedbackActivity.class);
                            startActivity(contactusIntent4);
                            break;
                        case 6:
                            Intent contactusIntent3 = new Intent(MainActivityAdmin.this, SettingsActivity.class);
                            startActivity(contactusIntent3);
                            break;
                        case 7:

                            pDialogVisit = new SweetAlertDialog(MainActivityAdmin.this, SweetAlertDialog.WARNING_TYPE);
                            pDialogVisit.setTitleText("Rent It");
                            pDialogVisit.setContentText("Are You Sure to Logout?");
                            pDialogVisit.setCancelText("Not Really");
                            pDialogVisit.setConfirmText("Of course");
                            pDialogVisit.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            });
                            pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
                                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

                                    Intent loginIntent2 = new Intent(MainActivityAdmin.this, RentItLoginActivity.class);
                                    startActivity(loginIntent2);
                                    finish();
                                }
                            });
                            pDialogVisit.setCancelable(true);
                            pDialogVisit.show();

                            break;
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return 8;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivCatName;
            public KudosTextView txtCatName;


            public ViewHolder(View itemView) {
                super(itemView);
                txtCatName = (KudosTextView) itemView.findViewById(R.id.txtCatName);
                ivCatName = (ImageView) itemView.findViewById(R.id.ivCatName);
            }
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

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item)
//
//    {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_my_profile) {
//            // Handle the camera action
//            Intent intent = new Intent(MainActivityAdmin.this, EditProfileActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.nav_all_users) {
//            Intent loginIntent = new Intent(MainActivityAdmin.this, AllUsersActivity.class);
//            startActivity(loginIntent);
//        } else if (id == R.id.nav_all_ads) {
//            Intent feedbackIntent = new Intent(MainActivityAdmin.this, AllAdsActivity.class);
//            startActivity(feedbackIntent);
//        } else if (id == R.id.nav_reports) {
//            Intent contactusIntent = new Intent(MainActivityAdmin.this, AllReportsActivity.class);
//            startActivity(contactusIntent);
//        } else if (id == R.id.nav_faq) {
//            Intent contactusIntent = new Intent(MainActivityAdmin.this, AddFAQActivity.class);
//            startActivity(contactusIntent);
//        } else if (id == R.id.nav_feedback) {
//            Intent contactusIntent = new Intent(MainActivityAdmin.this, ViewFeedbackActivity.class);
//            startActivity(contactusIntent);
//        }  else if (id == R.id.nav_send) {
//            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
//            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);
//
//            Intent loginIntent = new Intent(MainActivityAdmin.this, RentItLoginActivity.class);
//            startActivity(loginIntent);
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.
//                id.admin_drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


}
