package com.example.tasol.myrowitems;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidquery.AQuery;

public class FullImageActivity extends AppCompatActivity {

    TouchImageView imageView;
    FloatingActionButton btnClose;
    int[] IMAGESRRAY = {R.drawable.cat_fashion, R.drawable.cat_electronic, R.drawable.mobile1, R.drawable.cat_furniture, R.drawable.cat_cars, R.drawable.mobile3, R.drawable.mobile, R.drawable.mobile2};
    private ContentValues ROW;
    private AQuery aQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        aQuery = new AQuery(FullImageActivity.this);
        ROW = getIntent().getParcelableExtra("ROW");
        imageView = (TouchImageView) findViewById(R.id.imgProduct);
        btnClose = (FloatingActionButton) findViewById(R.id.btnClose);
        aQuery.id(imageView).image(ROW.getAsString("photo"), true, true);

        //imageView.setImageResource(IMAGESRRAY[getIntent().getIntExtra("POS", 0)]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

    }
}
