package com.example.tasol.myrowitems;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentItItemDetailActivity extends AppCompatActivity {

    public ImageView imageCat;
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};
    CircleImageView imgProfilePicture;
    Toolbar toolbarData;
    RecyclerView rvOtherImages;
    LinearLayoutManager linearLayoutManager;
    int[] IMAGESOFCATS = {R.drawable.cat_books, R.drawable.cat_cars, R.drawable.cat_cycle, R.drawable.cat_decor, R.drawable.cat_electronic, R.drawable.cat_fashion, R.drawable.cat_furniture, R.drawable.cat_mobile, R.drawable.cat_real, R.drawable.cat_sports, R.drawable.cat_toys, R.drawable.cats_bikes};
    RecyclerViewCategoryGridAdapter recyclerViewCategoryGridAdapter;
    Animation slide_down, slide_up;
    ContentValues ROW;
    TextView txtByUsername, txtByPhone;
    JSONObject userData = null;
    private int POS = 0;
    private AQuery aQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_item_detail);
        aQuery = new AQuery(RentItItemDetailActivity.this);
        ROW = getIntent().getParcelableExtra("ROW");
        toolbarData = (Toolbar) findViewById(R.id.toolbarData);
        setSupportActionBar(toolbarData);
        getSupportActionBar().setTitle(ROW.getAsString("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarData.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();

            }
        });
        txtByPhone = (TextView) findViewById(R.id.txtByPhone);
        txtByUsername = (TextView) findViewById(R.id.txtByUsername);
        rvOtherImages = (RecyclerView) findViewById(R.id.rvOtherImages);
        linearLayoutManager = new LinearLayoutManager(this);
        rvOtherImages.setHasFixedSize(true);
        rvOtherImages.setLayoutManager(linearLayoutManager);


        imgProfilePicture = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imageCat = (ImageView) findViewById(R.id.imageCat);


        int i = getIntent().getIntExtra("POS", 0);
        POS = i;
        aQuery.id(imageCat).image(ROW.getAsString("photo"), true, true);
        try {
            userData = new JSONObject(ROW.getAsString("userData"));
            txtByPhone.setText(userData.getString("user_phone"));
            txtByUsername.setText("Uploaded By " + userData.getString("user_name"));
            if (userData.getString("user_pic").equals("")) {
                imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
            } else {
                aQuery.id(imgProfilePicture).image(userData.getString("user_pic"), true, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtByPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int checkPermission = ContextCompat.checkSelfPermission(RentItItemDetailActivity.this, Manifest.permission.CALL_PHONE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            RentItItemDetailActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE}, 201);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    try {
                        callIntent.setData(Uri.parse("tel:" + userData.getString("user_phone")));//change the number
                        startActivity(callIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        });

        //imageCat.setImageResource(IMAGESRRAY[i]);
//        if (i % 2 == 0) {
//            imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
//        } else {
//            imgProfilePicture.setImageResource(R.drawable.indo_session_avatar);
//        }


        recyclerViewCategoryGridAdapter = new RecyclerViewCategoryGridAdapter();
        rvOtherImages.setAdapter(recyclerViewCategoryGridAdapter);

        imageCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(RentItItemDetailActivity.this, FullImageActivity.class);
                intent.putExtra("POS", POS);
                intent.putExtra("ROW", ROW);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> p1 = Pair.create((View) imageCat, imageCat.getTransitionName());

                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(RentItItemDetailActivity.this, p1);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }


            }
        });


        //Load animation
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down);

//        slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.slide_up);

        // Start animation
        rvOtherImages.startAnimation(slide_down);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
    }

    private class RecyclerViewCategoryGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_gallery_images_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.ivImages.setImageResource(IMAGESRRAY[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageCat.setImageResource(IMAGESRRAY[position]);
                    POS = position;
                }
            });

        }

        @Override
        public int getItemCount() {
            return IMAGESRRAY.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImages;

            public ViewHolder(View itemView) {
                super(itemView);
                ivImages = (ImageView) itemView.findViewById(R.id.ivImages);
            }
        }
    }
}
