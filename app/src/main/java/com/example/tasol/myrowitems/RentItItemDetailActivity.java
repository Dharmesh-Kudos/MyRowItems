package com.example.tasol.myrowitems;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RentItItemDetailActivity extends AppCompatActivity {

    public ImageView imageCat;
    int[] IMAGESRRAY = {R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1, R.drawable.mobile3, R.drawable.mobile2, R.drawable.mobile, R.drawable.mobile1};
    CircleImageView imgProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_it_item_detail);
        imgProfilePicture = (CircleImageView) findViewById(R.id.imgProfilePicture);
        imageCat = (ImageView) findViewById(R.id.imageCat);
        int i = getIntent().getIntExtra("POS", 0);
        imageCat.setImageResource(IMAGESRRAY[i]);
        if (i % 2 == 0) {
            imgProfilePicture.setImageResource(R.drawable.boy);
        } else {
            imgProfilePicture.setImageResource(R.drawable.girl);
        }
    }
}
