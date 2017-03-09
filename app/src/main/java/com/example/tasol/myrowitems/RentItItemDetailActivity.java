package com.example.tasol.myrowitems;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentItItemDetailActivity extends AppCompatActivity {

    public ImageView imageCat;
    int[] IMAGESRRAY = {R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1, R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1};
    CircleImageView imgProfilePicture;
    Toolbar toolbarData;
    RecyclerView rvOtherImages;
    LinearLayoutManager linearLayoutManager;
    int[] IMAGESOFCATS = {R.drawable.cat_books, R.drawable.cat_cars, R.drawable.cat_cycle, R.drawable.cat_decor, R.drawable.cat_electronic, R.drawable.cat_fashion, R.drawable.cat_furniture, R.drawable.cat_mobile, R.drawable.cat_real, R.drawable.cat_sports, R.drawable.cat_toys, R.drawable.cats_bikes};
    RecyclerViewCategoryGridAdapter recyclerViewCategoryGridAdapter;
    private int POS = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_item_detail);
        toolbarData = (Toolbar) findViewById(R.id.toolbarData);
        setSupportActionBar(toolbarData);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarData.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
        rvOtherImages = (RecyclerView) findViewById(R.id.rvOtherImages);
        linearLayoutManager = new LinearLayoutManager(this);
        rvOtherImages.setHasFixedSize(true);
        rvOtherImages.setLayoutManager(linearLayoutManager);
        imgProfilePicture = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imageCat = (ImageView) findViewById(R.id.imageCat);
        int i = getIntent().getIntExtra("POS", 0);
        imageCat.setImageResource(IMAGESRRAY[i]);
        if (i % 2 == 0) {
            imgProfilePicture.setImageResource(R.drawable.boy);
        } else {
            imgProfilePicture.setImageResource(R.drawable.girl);
        }
        recyclerViewCategoryGridAdapter = new RecyclerViewCategoryGridAdapter();
        rvOtherImages.setAdapter(recyclerViewCategoryGridAdapter);

        imageCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RentItItemDetailActivity.this, FullImageActivity.class).putExtra("POS", POS));
            }
        });
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
