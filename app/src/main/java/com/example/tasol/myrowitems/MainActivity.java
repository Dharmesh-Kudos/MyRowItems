package com.example.tasol.myrowitems;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import smart.framework.SmartApplication;

import static smart.framework.Constants.SP_ISLOGOUT;
import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.SP_LOGIN_REQ_OBJECT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    RecyclerView imageRv;
    RecyclerView rvCategories;
    // private RecyclerViewImagesAdapter recyclerViewImagesAdapter;
    ViewPager viewPager;
    CustomPagerAdapter mCustomPagerAdapter;
    CollapsingToolbarLayout collapsingToolbarLayout;
    int[] IMAGESRRAY = {R.drawable.mobile, R.drawable.mobile1, R.drawable.mobile2, R.drawable.mobile3};
    int[] IMAGESOFCATS = {R.drawable.cat_books, R.drawable.cat_cars, R.drawable.cat_cycle, R.drawable.cat_decor, R.drawable.cat_electronic, R.drawable.cat_fashion, R.drawable.cat_furniture, R.drawable.cat_mobile, R.drawable.cat_real, R.drawable.cat_sports, R.drawable.cat_toys, R.drawable.cats_bikes};
    String[] NAMESOFCATS = {"Books", "Cars", "Cycles", "Decorations", "Electronics", "Fashion", "Furniture", "Mobiles", "Real Estate", "Sports", "Toys", "Bikes"};
    int i = 0;
    int pos = 0;
    TextView txtUsername, txtEmail;
    AQuery aQuery;
    CircleImageView imgProPic;
    TextView txtUserName, txtUserAge;
    NavigationView navigationView;
    private SliderLayout mDemoSlider;
    private GridLayoutManager gridLayoutManager;
    private RecyclerViewCategoryGridAdapter recyclerViewCategoryGridAdapter;
    private LinearLayoutManager linearLayoutManager;
    private int NUM_PAGES = 4;
    private int currentPage = 0;
    private JSONObject loginParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aQuery = new AQuery(MainActivity.this);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow(); // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        }


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        gridLayoutManager = new GridLayoutManager(this, 2);
        rvCategories = (RecyclerView) findViewById(R.id.rvCategories);
        rvCategories.setHasFixedSize(true);
        rvCategories.setLayoutManager(gridLayoutManager);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Moto G4 Plus", R.drawable.mobile);
        file_maps.put("Moto White S3", R.drawable.mobile1);
        file_maps.put("Google Pixel XL", R.drawable.mobile2);
        file_maps.put("Black Moto", R.drawable.mobile3);
        file_maps.put("Cars", R.drawable.cat_cars);
        file_maps.put("Electronics", R.drawable.cat_electronic);
        file_maps.put("Fashion", R.drawable.cat_fashion);
        file_maps.put("Furnitures", R.drawable.cat_furniture);


        for (String name : file_maps.keySet()) {

            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(MainActivity.this);


            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            textSliderView.getBundle().putString("pos", String.valueOf(pos++));

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(3000);
        mDemoSlider.addOnPageChangeListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle("Categories");

        com.github.clans.fab.FloatingActionButton fab1 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabPostAd);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostAdActivity.class));
            }
        });
        com.github.clans.fab.FloatingActionButton fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabRequestAd);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostAdActivity.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        txtUsername = (TextView) v.findViewById(R.id.txtUsername);
        txtEmail = (TextView) v.findViewById(R.id.txtEmail);
        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGGED_IN_USER_DATA, ""));

            txtUsername.setText(loginParams.getString("name"));
            txtEmail.setText(loginParams.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navigationView.setNavigationItemSelectedListener(this);
        //        imageRv= (RecyclerView) findViewById(R.id.imageRV);
        // viewPager = (ViewPager) findViewById(R.id.imageRV);
//        linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
//        imageRv.setHasFixedSize(true);
//        imageRv.setLayoutManager(linearLayoutManager);

//        mCustomPagerAdapter = new CustomPagerAdapter(this);
//
//        viewPager.setAdapter(mCustomPagerAdapter);
//        viewPager.setPageTransformer(true, new TabletTransformer());
        recyclerViewCategoryGridAdapter = new RecyclerViewCategoryGridAdapter();
        rvCategories.setAdapter(recyclerViewCategoryGridAdapter);
        rvCategories.setNestedScrollingEnabled(false);
//        ghumao();
        try {
            JSONObject userData = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_LOGGED_IN_USER_DATA, ""));
            Log.d("HERE = ", userData.toString());
            aQuery.id(imgProPic).image(userData.get("user_pic").toString(), true, true, getWindowManager().getDefaultDisplay().getWidth(), 0);
            txtUserName.setText(userData.get("user_name").toString());
            txtUserAge.setText(userData.get("user_age").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_ISLOGOUT, true);
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_LOGIN_REQ_OBJECT, null);

            Intent loginIntent = new Intent(MainActivity.this, RentItLoginActivity.class);
            startActivity(loginIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        //mDemoSlider.stopAutoCycle();
        super.onStop();
    }


    @Override
    public void onSliderClick(BaseSliderView slider) {
        startActivity(new Intent(MainActivity.this, FullImageActivity.class).putExtra("POS", Integer.parseInt(slider.getBundle().get("pos").toString())));
//        Log.d("HELO = ",slider.getBundle().get("extra") + " - " + slider.getBundle().get("pos"));
//        Toast.makeText(this,slider.getBundle().get("extra") + " - " + slider.getBundle().get("pos"), Toast.LENGTH_SHORT).show();
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

            holder.txtCatName.setText(NAMESOFCATS[position]);
            holder.ivCatName.setImageResource(IMAGESOFCATS[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, RentItCatDetailActivity.class).putExtra("TITLE", NAMESOFCATS[position]));
                }
            });

        }

        @Override
        public int getItemCount() {
            return IMAGESOFCATS.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivCatName;
            public TextView txtCatName;


            public ViewHolder(View itemView) {
                super(itemView);
                txtCatName = (TextView) itemView.findViewById(R.id.txtCatName);
                ivCatName = (ImageView) itemView.findViewById(R.id.ivCatName);
            }
        }
    }
}
