package com.example.tasol.myrowitems;

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

public class DashboardActivity extends AppCompatActivity {


    RecyclerView rvCatDetail;
    Toolbar toolbar;
    int[] IMAGESRRAY = {R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1,R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1};
    private LinearLayoutManager linearLayoutManager;
    private RecyclerViewImagesAdapter recyclerViewImagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rentit_cat_detail);
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
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.imageCat.setImageResource(IMAGESRRAY[position]);
            if (position % 2 == 0) {
                holder.imgProfilePicture.setImageResource(R.drawable.boy);
            } else {
                holder.imgProfilePicture.setImageResource(R.drawable.girl);
            }


        }

        @Override
        public int getItemCount() {
            return IMAGESRRAY.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageCat;
            CircleImageView imgProfilePicture;


            public ViewHolder(View itemView) {
                super(itemView);
                imgProfilePicture = (CircleImageView) itemView.findViewById(R.id.imgProfilePicture);
                imageCat = (ImageView) itemView.findViewById(R.id.imageCat);
            }
        }
    }



}
