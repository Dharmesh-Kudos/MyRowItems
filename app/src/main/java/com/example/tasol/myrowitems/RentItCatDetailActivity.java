package com.example.tasol.myrowitems;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentItCatDetailActivity extends AppCompatActivity {


    RecyclerView rvCatDetail;
    Toolbar toolbar;
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewImagesAdapter recyclerViewImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentit_cat_detail);
        // setupWindowAnimations();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("TITLE"));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
        rvCatDetail = (RecyclerView) findViewById(R.id.rvCatDetail);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        rvCatDetail.setHasFixedSize(true);
        rvCatDetail.setLayoutManager(linearLayoutManager);
        recyclerViewImagesAdapter = new RecyclerViewImagesAdapter();
        rvCatDetail.setAdapter(recyclerViewImagesAdapter);

    }

    //    private void setupWindowAnimations() {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Explode explode = (Explode) TransitionInflater.from(this).inflateTransition(R.transition.explode);
//                getWindow().setEnterTransition(explode);
//            }
//
//
//    }
    private class RecyclerViewImagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_cat_detail_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            final ViewHolder holder = (ViewHolder) viewHolder;

            holder.imageCat.setImageResource(IMAGESRRAY[position]);
            if (position % 2 == 0) {
                holder.imgProfilePicture.setImageResource(R.drawable.indo_profile_avatar);
            } else {
                holder.imgProfilePicture.setImageResource(R.drawable.indo_session_avatar);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RentItCatDetailActivity.this, RentItItemDetailActivity.class);
                    intent.putExtra("POS", position);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Pair<View, String> p1 = Pair.create((View) holder.imgProfilePicture, holder.imgProfilePicture.getTransitionName());
                        Pair<View, String> p2 = Pair.create((View) holder.imageCat, holder.imageCat.getTransitionName());
//                        Pair<View, String> p3 = Pair.create((View) holder.dataLayout, holder.dataLayout.getTransitionName());
//                        Pair<View, String> p4 = Pair.create((View) holder.tv1, holder.tv1.getTransitionName());
//                        Pair<View, String> p5 = Pair.create((View) holder.tv2, holder.tv2.getTransitionName());
//                        Pair<View, String> p6 = Pair.create((View) holder.tv3,holder.tv3.getTransitionName());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(RentItCatDetailActivity.this, p1, p2);
                        startActivity(intent, options.toBundle());
                    } else {
                        startActivity(intent);
                    }

                }
            });


        }

        @Override
        public int getItemCount() {
            return IMAGESRRAY.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageCat;
            CircleImageView imgProfilePicture;
            LinearLayout dataLayout;
            TextView tv1, tv2, tv3;

            public ViewHolder(View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(R.id.textView1);
                tv2 = (TextView) itemView.findViewById(R.id.textView2);
                tv3 = (TextView) itemView.findViewById(R.id.textView3);
                dataLayout = (LinearLayout) itemView.findViewById(R.id.dataLayout);
                imgProfilePicture = (CircleImageView) itemView.findViewById(R.id.imgProfilePicture);
                imageCat = (ImageView) itemView.findViewById(R.id.imageCat);
            }
        }
    }



}
