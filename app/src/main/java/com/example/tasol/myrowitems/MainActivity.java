package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;
import com.twotoasters.jazzylistview.JazzyHelper;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import smart.caching.SmartCaching;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class MainActivity extends AppCompatActivity
        implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    //, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener


    private static final String KEY_TRANSITION_EFFECT = "transition_effect";
    RecyclerView imageRv;
    RecyclerView rvCategories;
    //   private RecyclerViewImagesAdapter recyclerViewImagesAdapter;
    ViewPager viewPager;
    CustomPagerAdapter mCustomPagerAdapter;
    //    CollapsingToolbarLayout collapsingToolbarLayout;
    int[] IMAGESRRAY = {R.drawable.mobile, R.drawable.mobile1, R.drawable.mobile2, R.drawable.mobile3};
    int[] IMAGESOFCATS = {R.drawable.mobiles,
            R.drawable.electronics,
            R.drawable.cars,
            R.drawable.bike,
            R.drawable.jobs,
            R.drawable.furniture,
            R.drawable.book,
            R.drawable.fashion,
            R.drawable.sports,
            R.drawable.services,
            R.drawable.estate,
            R.drawable.pets};
    String[] NAMESOFCATS = {"Mobile", "Electronics", "Cars", "Bikes", "Jobs", "Furniture", "Books", "Fashion", "Sports", "Services", "Real Estate", "Pets"};
    int i = 0;
    int pos = 0;
    KudosTextView txtUsername, txtEmail;
    ImageView imgProfilePicture;
    AQuery aQuery;
    CircleImageView imgProPic;
    KudosTextView txtUserName, txtUserAge;
    NavigationView navigationView;
    KudosButton btnViewReqProds;
    FloatingActionMenu fmenu;
    boolean doubleBackToExitPressedOnce = false;
    private SliderLayout mDemoSlider;
    private StaggeredGridLayoutManager gridLayoutManager;
    private RecyclerViewCategoryGridAdapter recyclerViewCategoryGridAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int NUM_PAGES = 4;
    private int currentPage = 0;
    private JSONObject loginParams = null;
    private JazzyRecyclerViewScrollListener jazzyScrollListener;
    private int mCurrentTransitionEffect = JazzyHelper.SLIDE_IN;
    private ImageView imgBack;
    private ArrayList<ContentValues> trendingData = new ArrayList<>();
    private smart.caching.SmartCaching smartCaching;
    private SweetAlertDialog pDialogVisit;
    private Typeface font;
    private KudosTextView txtCategory, txtMenu1, txtMenu2, txtMenu3, txtMenu4, txtMenu5, txtMenu6, txtMenu7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        smartCaching = new SmartCaching(MainActivity.this);
        aQuery = new AQuery(MainActivity.this);

        font = Typeface.createFromAsset(getAssets(), "fonts/Ubuntu-L.ttf");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }


//        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        txtCategory = (KudosTextView) findViewById(R.id.txtCategory);
        txtCategory.setTypeface(font);
        gridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);

        rvCategories = (RecyclerView) findViewById(R.id.rvCategories);
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(gridLayoutManager);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        btnViewReqProds = (KudosButton) findViewById(R.id.btnViewReqProds);
        getTrendingAds();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Rent It Home");
        }
        //   collapsingToolbarLayout.setTitle("Categories");

        fmenu = (FloatingActionMenu) findViewById(R.id.fabMenu);
        fmenu.setClosedOnTouchOutside(true);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabPostAd);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostAdActivity.class).putExtra("FROM", "MAIN"));
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabRequestAd);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RequestAdActivity.class).putExtra("FROM", "MAIN"));
            }
        });
        btnViewReqProds.setTypeface(font);
        btnViewReqProds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReqProdsListActivity.class));

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        //imgBack=(ImageView) v.findViewById(R.id.imgBack);
        imgProfilePicture = (ImageView) v.findViewById(R.id.imgProfilePicture);
        txtUsername = (KudosTextView) v.findViewById(R.id.txtUsername);
        txtEmail = (KudosTextView) v.findViewById(R.id.txtEmail);

        txtMenu1 = (KudosTextView) v.findViewById(R.id.txtMenu1);
        txtMenu2 = (KudosTextView) v.findViewById(R.id.txtMenu2);
        txtMenu3 = (KudosTextView) v.findViewById(R.id.txtMenu3);
        txtMenu4 = (KudosTextView) v.findViewById(R.id.txtMenu4);
        txtMenu5 = (KudosTextView) v.findViewById(R.id.txtMenu5);
        txtMenu6 = (KudosTextView) v.findViewById(R.id.txtMenu6);
        txtMenu7 = (KudosTextView) v.findViewById(R.id.txtMenu7);

        txtMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });
        txtMenu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent loginIntent = new Intent(MainActivity.this, RentItManageAdsActivity.class);
                startActivity(loginIntent);
            }
        });
        txtMenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent feedbackIntent = new Intent(MainActivity.this, AddFeedbackActivity.class);
                startActivity(feedbackIntent);
            }
        });
        txtMenu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent contactusIntent = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(contactusIntent);
            }
        });
        txtMenu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                Intent contactusIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(contactusIntent);
            }
        });
        txtMenu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, RentItAppIntro2.class).putExtra("FROM", "MAIN"));

            }
        });
        txtMenu7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                pDialogVisit = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
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
                        LoginManager.getInstance().logOut();
                        Intent loginIntent2 = new Intent(MainActivity.this, RentItLoginActivity.class);
                        startActivity(loginIntent2);
                        finish();
                    }
                });
                pDialogVisit.setCancelable(true);
                pDialogVisit.show();
            }
        });

        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, ""));
            Picasso.with(MainActivity.this).load(loginParams.getString("user_pic")).placeholder(R.drawable.no_image).into(imgProfilePicture);
//            Glide.with(MainActivity.this).load(loginParams.getString("user_pic")).placeholder(R.drawable.man).error(R.drawable.no_image)
//                    .into((ImageView) v.findViewById(R.id.imgProfilePicture));
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

//        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, RentItUserProfileActivity.class);
//                intent.putExtra("FROM", "MAIN");
//                try {
//                    intent.putExtra("UID", loginParams.getString("id"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                    Pair<View, String> p1 = Pair.create((View) imgProfilePicture, imgProfilePicture.getTransitionName());
//
//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation(MainActivity.this, p1);
//                    startActivity(intent, options.toBundle());
//                } else {
//                    startActivity(intent);
//                }
//            }
//        });
//        navigationView.setNavigationItemSelectedListener(this);
////        imageRv= (RecyclerView) findViewById(R.id.imageRV);
////        viewPager = (ViewPager) findViewById(R.id.imageRV);
////        linearLayoutManager = new LinearLayoutManager(this);
////        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
////        imageRv.setHasFixedSize(true);
////        imageRv.setLayoutManager(linearLayoutManager);
//
////        mCustomPagerAdapter = new CustomPagerAdapter(this);
////
////        viewPager.setAdapter(mCustomPagerAdapter);
////        viewPager.setPageTransformer(true, new TabletTransformer());
        recyclerViewCategoryGridAdapter = new RecyclerViewCategoryGridAdapter();
        rvCategories.setAdapter(recyclerViewCategoryGridAdapter);
        rvCategories.setNestedScrollingEnabled(false);

        jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        rvCategories.setOnScrollListener(jazzyScrollListener);

        if (savedInstanceState != null) {
            mCurrentTransitionEffect = savedInstanceState.getInt(KEY_TRANSITION_EFFECT, JazzyHelper.SLIDE_IN);
            setupJazziness(mCurrentTransitionEffect);
        }

//        ghumao();
//        try {
//            JSONObject userData = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_IN_USER_DATA, ""));
//            Log.d("HERE = ", userData.toString());
//            aQuery.id(imgProPic).image(userData.get("user_pic").toString(), true, true, getWindowManager().getDefaultDisplay().getWidth(), 0);
//            txtUserName.setText(userData.get("user_name").toString());
//            txtUserAge.setText(userData.get("user_age").toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    private void getTrendingAds() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, MainActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "OpenTrendingAds");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "trendingAds");
            JSONObject taskData = new JSONObject();

            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {

                try {
                    if (responseCode == 200) {

                        Log.d("RESULT = ", String.valueOf(response));
                        trendingData = smartCaching.parseResponse(response.getJSONArray("trendingData"), "TRENDINGDATA", "userData").get("TRENDINGDATA");
                        if (trendingData != null && trendingData.size() > 0) {


                            List<String> elephantList = null;

//                            HashMap<String, String> file_maps = new HashMap<String, String>();
//                            for (int j = 0; j < trendingData.size(); j++) {
//                                ContentValues row = trendingData.get(j);
//                                elephantList = Arrays.asList(row.getAsString("photo").split(","));
//
//                                if (elephantList.get(0).contains("http")) {
//                                    file_maps.put(row.getAsString("title"), elephantList.get(0));
//                                } else {
//                                    file_maps.put(row.getAsString("title"), "http://" + elephantList.get(0));
//
//                                }
//
//                            }

//                                file_maps.put("Moto G4 Plus", Integer.valueOf(elephantList.get(0)));
//                                file_maps.put("Moto White S3", R.drawable.mobile1);
//                                file_maps.put("Google Pixel XL", R.drawable.mobile2);
//                                file_maps.put("Black Moto", R.drawable.mobile3);
//                                file_maps.put("Cars", R.drawable.cat_cars);
//                                file_maps.put("Electronics", R.drawable.cat_electronic);
//                                file_maps.put("Fashion", R.drawable.cat_fashion);
//                                file_maps.put("Furnitures", R.drawable.cat_furniture);
                            for (int j = 0; j < trendingData.size(); j++) {
                                String imageStr;
                                elephantList = Arrays.asList(trendingData.get(j).getAsString("photo").split(","));
                                if (elephantList.get(0).contains("http")) {
                                    imageStr = elephantList.get(0);
                                } else {
                                    imageStr = "http://" + elephantList.get(0);

                                }
                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                                // initialize a SliderLayout
                                textSliderView
                                        .description(trendingData.get(j).getAsString("title"))
                                        .image(imageStr)

                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                        .setOnSliderClickListener(MainActivity.this);


                                //add your extra information
                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle()
                                        .putString("extra", trendingData.get(j).getAsString("title"));
                                textSliderView.getBundle().putString("pos", String.valueOf(pos++));
                                mDemoSlider.addSlider(textSliderView);
                            }

//                            for (String name : file_maps.keySet()) {
//
//                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
//                                // initialize a SliderLayout
//                                textSliderView
//                                        .description(name)
//                                        .image(file_maps.get(name))
//                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
//                                        .setOnSliderClickListener(MainActivity.this);
//
//
//                                //add your extra information
//                                textSliderView.bundle(new Bundle());
//                                textSliderView.getBundle()
//                                        .putString("extra", name);
//                                textSliderView.getBundle().putString("pos", String.valueOf(pos++));
//
//                                mDemoSlider.addSlider(textSliderView);
//                            }
                            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
                            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
                            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                            mDemoSlider.setDuration(3000);
                            mDemoSlider.addOnPageChangeListener(MainActivity.this);
                        }
                    } else if (responseCode == 204) {

                        //Toast.makeText(RentItCatItemsActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onResponseError() {

                SmartUtils.hideProgressDialog();
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", true);
    }

    private void setupJazziness(int effect) {
        mCurrentTransitionEffect = effect;
        jazzyScrollListener.setTransitionEffect(mCurrentTransitionEffect);
    }

    private void ghumao() {
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };


        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 3000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
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
//                super.onBackPressed();
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
//        // automatically handle clicks on the Home/Up btnSignup, so long
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
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//            Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
//            startActivity(intent);
//
//        } else if (id == R.id.nav_gallery) {
//            Intent loginIntent = new Intent(MainActivity.this, RentItManageAdsActivity.class);
//            startActivity(loginIntent);
//        } else if (id == R.id.nav_slideshow) {
//            Intent feedbackIntent = new Intent(MainActivity.this, AddFeedbackActivity.class);
//            startActivity(feedbackIntent);
//        } else if (id == R.id.nav_manage) {
//            Intent contactusIntent = new Intent(MainActivity.this, ContactUsActivity.class);
//            startActivity(contactusIntent);
//        } else if (id == R.id.nav_share) {
//            Intent contactusIntent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(contactusIntent);
//        } else if (id == R.id.nav_demo) {
//            startActivity(new Intent(MainActivity.this, RentItAppIntro2.class).putExtra("FROM", "MAIN"));
//        } else if (id == R.id.nav_send) {
//            pDialogVisit = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
//            pDialogVisit.setTitleText("Rent It");
//            pDialogVisit.setContentText("Are You Sure to Logout?");
//            pDialogVisit.setCancelText("Not Really");
//            pDialogVisit.setConfirmText("Of course");
//            pDialogVisit.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                @Override
//                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                    sweetAlertDialog.dismiss();
//                }
//            });
//            pDialogVisit.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                @Override
//                public void onClick(SweetAlertDialog sweetAlertDialog) {
//                    sweetAlertDialog.dismiss();
//                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
//                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);
//
//                    Intent loginIntent2 = new Intent(MainActivity.this, RentItLoginActivity.class);
//                    startActivity(loginIntent2);
//                    finish();
//                }
//            });
//            pDialogVisit.setCancelable(true);
//            pDialogVisit.show();
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.
//                id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        Log.d("ROW = ", trendingData.get(Integer.parseInt(slider.getBundle().get("pos").toString())).toString());
        Log.d("POS", slider.getBundle().get("pos").toString());
        startActivity(new Intent(MainActivity.this, RentItAdDetailActivity.class)
                .putExtra("IMGPOS", Integer.parseInt(trendingData.get(Integer.parseInt(slider.getBundle().get("pos").toString())).getAsString("cat_id")))
                .putExtra("ROW", trendingData.get(Integer.parseInt(slider.getBundle().get("pos").toString())))
                .putExtra("POS", Integer.parseInt(slider.getBundle().get("pos").toString())));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return IMAGESRRAY.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.rentit_mobile_row_item_two, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(IMAGESRRAY[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }

    private class RecyclerViewCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_category_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.txtCatName.setTypeface(font);

            holder.txtCatName.setText(NAMESOFCATS[position]);
            holder.ivCatName.setImageResource(IMAGESOFCATS[position]);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, RentItCatItemsActivity.class).putExtra("IN_POS", position + 1).putExtra("TITLE", NAMESOFCATS[position]));
                    overridePendingTransition(R.anim.open_next, R.anim.close_main);
//                    startActivity(new Intent(MainActivity.this, RentItCatItemsActivity.class).putExtra("TITLE", NAMESOFCATS[position]));
                }
            });

        }

        @Override
        public int getItemCount() {
            return IMAGESOFCATS.length;
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
}
