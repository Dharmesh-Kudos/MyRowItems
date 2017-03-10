package smart.weservice;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import smart.framework.Constants;
import smart.framework.SmartApplication;

/**
 * Created by tasol on 12/10/15.
 */
public class MultipartRequestMedia extends JsonObjectRequest {

    private HttpEntity mHttpEntity;

    public MultipartRequestMedia(String url, JSONObject jsonRequest,
                                 String filePath, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        if (TextUtils.isEmpty(filePath)) {
            mHttpEntity = buildMultipartEntity(jsonRequest);
        } else {
            mHttpEntity = buildMultipartEntity(new File(filePath), jsonRequest);
        }
    }

    public MultipartRequestMedia(String url, JSONObject jsonRequest,
                                 String filePath, String fileTag, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        if (TextUtils.isEmpty(filePath)) {
            mHttpEntity = buildMultipartEntity(jsonRequest);
        } else {
            mHttpEntity = buildMultipartEntity(fileTag, new File(filePath), jsonRequest);
        }
    }

    public MultipartRequestMedia(String url, JSONObject jsonRequest,
                                 String[] filePaths, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        mHttpEntity = buildMultipartEntity(filePaths, jsonRequest);
    }

    public MultipartRequestMedia(String url, JSONObject jsonRequest, ArrayList<String> imagePaths,
                                 ArrayList<String> filePaths, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
        mHttpEntity = buildMultipartEntity(imagePaths, filePaths, jsonRequest);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Map headers = response.headers;
        String cookie = (String) headers.get("Set-Cookie");
        if (cookie != null && cookie.length() > 0) {
            Log.e("@@@@@GOT COOKIES", cookie);
            sync(cookie);
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(Constants.SP_COOKIES, cookie);
        }


        return super.parseNetworkResponse(response);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        return headers;
    }

    //Multipart string body
    private HttpEntity buildMultipartEntity(JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //Multipart upload single image file
    private HttpEntity buildMultipartEntity(File file, JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if (getMimeType(file.getAbsolutePath()).contains("video")) {
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart("video", fileBody);
        } else {
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart("image", fileBody);
        }

        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    private HttpEntity buildMultipartEntity(String fileTag, File file, JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        if (getMimeType(file.getAbsolutePath()).contains("video")) {
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart(fileTag, fileBody);
        } else {
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart(fileTag, fileBody);
        }

        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //Multipart upload multiple images files
    private HttpEntity buildMultipartEntity(String[] filePaths, JSONObject jsonObject) {
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            for (int i = 0; i < filePaths.length; i++) {
                Log.v("@@@@ADATATATATA", filePaths[i]);
                File file = new File(filePaths[i]);
                FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
                builder.addPart("image[]", fileBody);
            }

            try {
                builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return builder.build();
        } catch (Exception se) {
            se.printStackTrace();
            return null;
        }
    }

    //Multipart upload multiple images files
    private HttpEntity buildMultipartEntity(ArrayList<String> imagePaths, ArrayList<String> filePaths, JSONObject jsonObject) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        for (int i = 0; i < imagePaths.size(); i++) {
            File file = new File(imagePaths.get(i));
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart("image" + (i + 1), fileBody);
        }

        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            FileBody fileBody = new FileBody(file, ContentType.create(getMimeType(file.getAbsolutePath())), file.getName());
            builder.addPart("file" + (i + 1), fileBody);
        }

        try {
            builder.addPart("reqObject", new StringBody(jsonObject.toString(), Charset.forName("utf-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    @Override
    public String getBodyContentType() {
        if (mHttpEntity != null) {
            return mHttpEntity.getContentType().getValue();
        }
        return null;
    }

    @Override
    public byte[] getBody() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            if (mHttpEntity != null) {
                Log.d("request: multipart", mHttpEntity.toString());
                mHttpEntity.writeTo(bos);
            } else {
                return null;
            }
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    /**
     * A method used to get mime type of given file path of image or video.
     *
     * @param filePath
     * @return {@link String}
     */
    public String getMimeType(String filePath) {
        String type = null;
        String extension = getFileExtensionFromUrl(filePath);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        if (type != null) {
            Log.v("@@@@@CONTENT_TYPE", type);
        }

        return type;
    }

    /**
     * A method used to get extension type of given file path of image or video.
     *
     * @param url
     * @return {@link String}
     */
    public String getFileExtensionFromUrl(String url) {
        int dotPos = url.lastIndexOf('.');
        if (0 <= dotPos) {
            return (url.substring(dotPos + 1)).toLowerCase();
        }

        return "";
    }

    public void sync(String cookies) {
        try {
            if (cookies != null) {

                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setAcceptCookie(true);
                cookieManager.setCookie(SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME, cookies);
                CookieSyncManager.getInstance().sync();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
