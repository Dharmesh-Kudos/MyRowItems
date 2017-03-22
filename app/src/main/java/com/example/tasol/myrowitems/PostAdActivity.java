package com.example.tasol.myrowitems;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import smart.caching.SmartCaching;
import smart.framework.Constants;
import smart.framework.SmartApplication;
import smart.framework.SmartUtils;
import smart.weservice.SmartWebManager;

import static smart.framework.Constants.SP_LOGGED_IN_USER_DATA;
import static smart.framework.Constants.TASK;
import static smart.framework.Constants.TASKDATA;

public class PostAdActivity extends AppCompatActivity {

    Button btnPostAd;
    ImageView closeIV;
    LinearLayout greyLayout;
    RecyclerView rvImages;
    GridLayoutManager gridLayoutManager;
    FloatingActionButton btnUpload;
    RecyclerViewUploadedImagesGridAdapter recyclerViewUploadedImagesGridAdapter;
    EditText edtTitle, edtDesc, edtPrice, edtDeposit, edtDays;
    Spinner spinnerCategory, spinnerSubCategory, spinnerCondition;
    String AVAILABLE = "1";
    String TIME, CREATED_AT, UPDATED_AT;
    String CATID, SUBCATID, CONDITION;
    String[] arrConditions;
    ProgressBar spnProBar;
    CustomCatAdapter customCatAdapter;
    CustomSubCatAdapter customSubCatAdapter;
    private int PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 601;
    private int SELECT_PICTURE = 1;
    private String imgPath;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ArrayList<String> catData = new ArrayList<>();
    private ArrayList<String> subCatData = new ArrayList<>();
    private ArrayList<ContentValues> cvCatData = new ArrayList<>();
    private ArrayList<ContentValues> cvSubCatData = new ArrayList<>();
    private smart.caching.SmartCaching smartCaching;
    private JSONObject loginParams = null;
    private String CATNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        smartCaching = new SmartCaching(PostAdActivity.this);
        spnProBar = (ProgressBar) findViewById(R.id.spnProgress);
        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDesc = (EditText) findViewById(R.id.edtDesc);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        edtDeposit = (EditText) findViewById(R.id.edtDeposit);
        edtDays = (EditText) findViewById(R.id.edtDays);

        arrConditions = getResources().getStringArray(R.array.conditions);

        spinnerCategory = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerSubCategory = (Spinner) findViewById(R.id.spinnerSubCategory);
        spinnerCondition = (Spinner) findViewById(R.id.spinnerCondition);


        btnPostAd = (Button) findViewById(R.id.btnPostAd);
        closeIV = (ImageView) findViewById(R.id.closeIV);
        greyLayout = (LinearLayout) findViewById(R.id.greyLayout);
        rvImages = (RecyclerView) findViewById(R.id.rvImages);
        gridLayoutManager = new GridLayoutManager(this, 3);
        rvImages.setLayoutManager(gridLayoutManager);
        btnUpload = (FloatingActionButton) findViewById(R.id.btnUpload);


        fillCategory();

        closeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

        btnPostAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("HELLO", "1");
                progressDialog = ProgressDialog.show(PostAdActivity.this, "Rent It", "Authenticating...");
                progressDialog.setContentView(R.layout.progress_dialog);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setTitle("Rent It");
                progressDialog.setMessage("Posting Ad...");
                ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.show();


                HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, PostAdActivity.this);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
                final JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put(TASK, "submitPostAd");
                    JSONObject taskData = new JSONObject();
                    try {
                        loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                                .getString(SP_LOGGED_IN_USER_DATA, ""));
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        taskData.put("user_id", loginParams.getString("id"));
                        taskData.put("cat_id", CATID);
                        taskData.put("subCat_id", SUBCATID);
                        taskData.put("title", edtTitle.getText().toString());
                        taskData.put("desc", edtDesc.getText().toString());
                        taskData.put("price", edtPrice.getText().toString());
                        taskData.put("deposit", edtDeposit.getText().toString());
                        taskData.put("days", edtDays.getText().toString());
                        taskData.put("condition", CONDITION);
                        taskData.put("time", currentDateandTime);
                        taskData.put("available", "1");
                        taskData.put("created_at", currentDateandTime);
                        taskData.put("updated_at", currentDateandTime);
                        taskData.put("total_image", selectedPhotos.size());

                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    jsonObject.put(TASKDATA, taskData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
                requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

                    @Override
                    public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                        progressDialog.dismiss();
                        if (responseCode == 200) {
                            try {
                                Log.d("RESULT = ", String.valueOf(response));

                                startActivity(new Intent(PostAdActivity.this, RentItCatDetailActivity.class).putExtra("IN_POS", Integer.valueOf(CATID)).putExtra("TITLE", CATNAME));
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PostAdActivity.this, "SOME OTHER ERROR", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onResponseError() {

                        SmartUtils.hideProgressDialog();
                    }
                });

                if (selectedPhotos != null && selectedPhotos.size() > 0) {

                    String[] images = new String[selectedPhotos.size()];
                    images = selectedPhotos.toArray(images);
                    SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipartUpload(requestParams, images, "", false);
                } else {

                    SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, null, "", false);
                }
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckPermissionForWriteStorage()) {

                    OpenImageChooser();
                }
            }
        });

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.d("ITEM = ", "ZERO");
                } else {
                    fillSubCategory(cvCatData.get(i - 1).getAsString("cat_id"));
                    CATID = cvCatData.get(i - 1).getAsString("cat_id");
                    CATNAME = cvCatData.get(i - 1).getAsString("cat_name");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSubCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.d("ITEM = ", "ZERO");
                } else {
                    SUBCATID = cvSubCatData.get(i - 1).getAsString("subCat_id");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerCondition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Log.d("ITEM = ", "ZERO");
                } else {
                    CONDITION = arrConditions[i];
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void fillCategory() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, PostAdActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, "Fill Categories");
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "getAllCats");
            JSONObject taskData = new JSONObject();
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                Log.d("RESULT = ", String.valueOf(response));
                try {
                    catData = new ArrayList<String>();
                    subCatData = new ArrayList<String>();
                    if (responseCode == 200) {
                        cvCatData = smartCaching.parseResponse(response.getJSONArray("allCategories"), "ALLCATEGORIES", null).get("ALLCATEGORIES");
                        catData.add(0, "Choose Category");
                        for (int i = 0; i < cvCatData.size(); i++) {

                            catData.add(cvCatData.get(i).getAsString("cat_name"));
                        }
                        customCatAdapter = new CustomCatAdapter(PostAdActivity.this, catData);
                        spinnerCategory.setAdapter(customCatAdapter);

                        subCatData.add("Choose Sub Category");
                        customSubCatAdapter = new CustomSubCatAdapter(PostAdActivity.this, subCatData);
                        spinnerSubCategory.setAdapter(customSubCatAdapter);
                    } else if (responseCode == 204) {
                        Toast.makeText(PostAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(PostAdActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }

    private void fillSubCategory(String catid) {
        spnProBar.setVisibility(View.VISIBLE);
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT, PostAdActivity.this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, Constants.WEB_PERFORM_LOGIN);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(TASK, "getAllSubCats");
            JSONObject taskData = new JSONObject();
            taskData.put("cat_id", catid);
            jsonObject.put(TASKDATA, taskData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, jsonObject);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, boolean isValidResponse, int responseCode) {
                Log.d("RESULT = ", String.valueOf(response));
                try {
                    subCatData = new ArrayList<String>();
                    if (responseCode == 200) {
                        cvSubCatData = smartCaching.parseResponse(response.getJSONArray("subCategories"), "SUBCATEGORIES", null).get("SUBCATEGORIES");
                        subCatData.add("Choose Sub Category");
                        for (int i = 0; i < cvSubCatData.size(); i++) {
                            subCatData.add(cvSubCatData.get(i).getAsString("subCat_name"));
                        }
                        customSubCatAdapter = new CustomSubCatAdapter(PostAdActivity.this, subCatData);
                        spinnerSubCategory.setAdapter(customSubCatAdapter);
                        spnProBar.setVisibility(View.GONE);
                    } else if (responseCode == 204) {
                        Toast.makeText(PostAdActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onResponseError() {
                Toast.makeText(PostAdActivity.this, "In Response Error", Toast.LENGTH_SHORT).show();

            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueueMultipart(requestParams, "", null, true);
    }

    private void OpenImageChooser() {
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            isCamera = true;
                        } else {
                            isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                        }
                    }
                }

                if (isCamera) {
                    String selectedImagePath = imgPath;
                    scaleImage(selectedImagePath);
                    selectedPhotos.add(selectedImagePath);
                } else {
                    String selectedImagePath = getAbsolutePath(data.getData());
                    selectedImagePath = getRightAngleImage(selectedImagePath);
                    scaleImage(selectedImagePath);
                    selectedPhotos.add(selectedImagePath);
                }

                if (selectedPhotos.size() == 6) {
                    btnUpload.setEnabled(false);
                }
                invalidateOptionsMenu();
                greyLayout.setVisibility(View.VISIBLE);
                recyclerViewUploadedImagesGridAdapter = new RecyclerViewUploadedImagesGridAdapter();
                rvImages.setAdapter(recyclerViewUploadedImagesGridAdapter);

            }
        }
    }

    public String scaleImage(String path) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, 800, 800, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, 800, 800, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/myTmpDir");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(path);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    /**
     * This method is used to get image uri from file path.
     *
     * @param path represents image path of SD card.
     * @return Uri
     */
    public Uri getImageUri(String path) {
        return Uri.fromFile(new File(path));
    }

    /**
     * This method is used to decode image file path to bitmap.
     *
     * @param path represents selected image path.
     * @return Bitmap
     */
    public Bitmap decodeFileFromPath(String path) {
        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            int inSampleSize = 1024;
            if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(inSampleSize / (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = getContentResolver().openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is used to change orietation and rotate image as per needed as per its aspect ratio.
     *
     * @param degree represents degree to rotate the image.
     * @param path   represents selected image path.
     * @return String
     */
    public String rotateImage(int degree, String path) {
        try {
            Bitmap b = decodeFileFromPath(path);

            Matrix matrix = new Matrix();
            if (b.getWidth() > b.getHeight()) {
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(path);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * This method is used to get right angle of image, this method will automatically make image oriented as per its aspect ratio.
     *
     * @param photoPath represents selected image path.
     * @return String
     */
    public String getRightAngleImage(String photoPath) {
        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(90, photoPath);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(180, photoPath);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(270, photoPath);
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photoPath;
    }

    /**
     * This method used to get absolute path from uri.
     *
     * @param uri represented uri
     * @return represented {@link String}
     */
    public String getAbsolutePath(Uri uri) {
        if (Build.VERSION.SDK_INT < 11)
            return RealPathUtil.getRealPathFromURI_BelowAPI11(this, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            return RealPathUtil.getRealPathFromURI_API11to18(this, uri);

            // SDK > 19 (Android 4.4)
        else
            return RealPathUtil.getRealPathFromURI_API19(this, uri);
    }

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        imgPath = file.getAbsolutePath();
        return imgUri;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            OpenImageChooser();
        } else {
            Toast.makeText(PostAdActivity.this, "No Permission", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean CheckPermissionForWriteStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);

            return false;
        }

        return true;
    }

    private class RecyclerViewUploadedImagesGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rentit_uploaded_images_row_item,
                    parent, false);
            RecyclerView.ViewHolder viewHolder = new ViewHolder(parentView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
            ViewHolder holder = (ViewHolder) viewHolder;

            holder.ivImages.setImageURI(Uri.parse(selectedPhotos.get(position)));
            holder.ivClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPhotos.remove(position);
                    notifyDataSetChanged();
                    if (selectedPhotos.size() >= 1 && selectedPhotos.size() <= 6) {
                        btnUpload.setEnabled(true);
                    } else if (selectedPhotos.size() == 0) {
                        greyLayout.setVisibility(View.GONE);
                        btnUpload.setEnabled(true);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return selectedPhotos.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView ivImages;
            public TextView ivClose;


            public ViewHolder(View itemView) {
                super(itemView);
                ivImages = (ImageView) itemView.findViewById(R.id.ivImages);
                ivClose = (TextView) itemView.findViewById(R.id.ivClose);
            }
        }
    }


    /***** Adapter class extends with ArrayAdapter ******/
    public class CustomCatAdapter extends BaseAdapter {

        LayoutInflater inflater;
        private ArrayList<String> mCatData;

        /*************  CustomAdapter Constructor *****************/
        public CustomCatAdapter(Context applicationContext, ArrayList<String> mCatData) {

            this.mCatData = mCatData;
            inflater = (LayoutInflater.from(applicationContext));
            /***********  Layout inflator to call external xml layout () **********************/

        }

        @Override
        public int getCount() {
            return mCatData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_rows, null);
            TextView names = (TextView) view.findViewById(R.id.txtItem);
            names.setText(catData.get(i));
            return view;
        }

    }

    /***** Adapter class extends with ArrayAdapter ******/
    public class CustomSubCatAdapter extends BaseAdapter {

        LayoutInflater inflater;
        private ArrayList<String> mSubCatData;

        /*************  CustomAdapter Constructor *****************/
        public CustomSubCatAdapter(Context applicationContext, ArrayList<String> mSubCatData) {

            this.mSubCatData = mSubCatData;
            inflater = (LayoutInflater.from(applicationContext));
            /***********  Layout inflator to call external xml layout () **********************/

        }

        @Override
        public int getCount() {
            return mSubCatData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflater.inflate(R.layout.spinner_rows, null);
            TextView names = (TextView) view.findViewById(R.id.txtItem);
            names.setText(mSubCatData.get(i));
            return view;
        }

    }


}
