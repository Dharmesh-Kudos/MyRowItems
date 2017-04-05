package smart.framework;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.example.tasol.myrowitems.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import smart.caching.SmartCaching;
import smart.common.RealPathUtil;
import smart.customviews.CustomClickListener;
import smart.customviews.CustomTimePickerDialog;
import smart.customviews.SmartDatePickerView;
import smart.customviews.SmartTextView;
import smart.utilities.Iso2Phone;

/**
 * Created by tasol on 23/5/15.
 */

public class SmartUtils implements Constants {

    private static final String TAG = "SmartUtil";
    static Context mContext;
    static SmartCaching smartCaching;
    static StringBuilder newValue = new StringBuilder();
    static StringBuilder newIds = new StringBuilder();
    private static boolean isNetworkAvailable;
    private static ProgressDialog progressDialog;
    private static Dialog loadingDialog;
    private static Geocoder geocoder;
    private static String imgPath;
    private static AQuery aQuery;
    private static TimePickerDialog timePickerDialog;
    private static LinearLayoutManager linearLayoutManager;
    private static HashMap<String, String> selectedItems = new HashMap<>();
    private static ArrayList<ContentValues> listDataSideMenu = new ArrayList<ContentValues>();
    private static String latitude = "0";
    private static String longitude = "0";
    private static boolean isReloadRequired = false;
    private static boolean isReloadCartRequired = false;
    final private int GET_ADDRESS_FROM_MAP = 2;

    public static String getLatitude() {
        return latitude;
    }

    public static void setLatitude(String latitude) {
        SmartUtils.latitude = latitude;
    }

    public static String getLongitude() {
        return longitude;
    }

    public static void setLongitude(String longitude) {
        SmartUtils.longitude = longitude;
    }

    public static boolean isReloadRequired() {
        return isReloadRequired;
    }

    public static void setIsReloadRequired(boolean isReloadRequired) {
        SmartUtils.isReloadRequired = isReloadRequired;
    }

    public static boolean isReloadCartRequired() {
        return isReloadCartRequired;
    }

    public static void setIsReloadCartRequired(boolean isReloadCartRequired) {
        SmartUtils.isReloadCartRequired = isReloadCartRequired;
    }

    public static boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }

    public static void setNetworkStateAvailability(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {

                        isNetworkAvailable = true;
                        return;
                    }
                }
            }
        }

        isNetworkAvailable = false;
    }


    // Validation

    /**
     * This method used to email validator.
     *
     * @param mailAddress represented email
     * @return represented {@link Boolean}
     */
    public static boolean emailValidator(final String mailAddress) {
        Pattern pattern;
        Matcher matcher;

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(mailAddress);
        return matcher.matches();
    }

    /**
     * This method used to birth date validator.
     *
     * @param birthDate represented birth date
     * @return represented {@link Boolean}
     */
    public static boolean birthdateValidator(String birthDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(birthDate);
            Calendar bdate = Calendar.getInstance();
            bdate.setTime(date);
            Calendar today = Calendar.getInstance();

            if (bdate.compareTo(today) == 1) {
                return false;
            } else {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    //Dates

    /**
     * This method used to get difference from minute.
     *
     * @param miliseconds represented {@link Long} milliseconds
     * @return represented {@link Long}
     */
    public static long getDfferenceInMinute(long miliseconds) {
        long diff = (Calendar.getInstance().getTimeInMillis() - miliseconds);
        diff = diff / 60000L;
        return Math.abs(diff);
    }

    /**
     * This method used to calculate times ago from milliseconds.
     *
     * @param miliseconds represented {@link Long} milliseconds
     * @return represented {@link String}
     */
    public static String calculateTimesAgo(long miliseconds, String format) {
        Date start = new Date(miliseconds);
        Date end = new Date();

        long diffInSeconds = (end.getTime() - start.getTime()) / 1000;

        long diff[] = new long[]{0, 0, 0, 0};
        /* sec */
        diff[3] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
        /* min */
        diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        /* hours */
        diff[1] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        /* days */
        diff[0] = (diffInSeconds = (diffInSeconds / 24));

        System.out.println(String.format("%d day%s, %d hour%s, %d minute%s, %d second%s ago", diff[0], diff[0] > 1 ? "s" : "", diff[1],
                diff[1] > 1 ? "s" : "", diff[2], diff[2] > 1 ? "s" : "", diff[3], diff[3] > 1 ? "s" : ""));

        if (diff[0] > 0) {
            Calendar c = Calendar.getInstance();
            c.setTime(start);

            if (c.getMaximum(Calendar.DATE) <= diff[0]) {
                return (String) DateFormat.format(format, start);
            } else {
                return diff[0] > 1 ? String.format("%d days ago", diff[0]) : String.format("%d day ago", diff[0]);
            }
        } else if (diff[1] > 0) {
            return diff[1] > 1 ? String.format("%d hours ago", diff[1]) : String.format("%d hour ago", diff[1]);
        } else if (diff[2] > 0) {
            return diff[2] > 1 ? String.format("%d minutes ago", diff[2]) : String.format("%d minute ago", diff[2]);
        } else if (diff[3] > 0) {
            return diff[3] > 1 ? String.format("%d seconds ago", diff[3]) : String.format("%d second ago", diff[3]);
        } else {
            return (String) DateFormat.format(format, start);
        }
    }

    /**
     * This method used to get milliseconds from time zone.
     *
     * @param timestamp represented {@link Long} time stamp
     * @return represented {@link Long}
     */
    public static long getMillisecondsTimeZone(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone("TIMEZONE HERE");

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));
        System.out.println("Date : " + calendar.getTime());
        return calendar.getTimeInMillis();
    }

    /**
     * This method used to get date from string.
     *
     * @param strDate represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calender = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calender.setTime(date);
            return calender;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }


    public static String getStringFromCalendar(Calendar c, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(c.getTime());
    }

    /**
     * This method used to get time from string.
     *
     * @param strTime represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date;
        Calendar calnder = Calendar.getInstance();
        try {
            date = dateFormat.parse(strTime);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }


    public static String getFormattedDate(String date) {
        Date parsedDate = null;
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            parsedDate = input.parse(date);
            return output.format(parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get date dialog.
     *
     * @param strDate  represented date
     * @param restrict represented isRestrict
     */
    public static void getDateDialog(Context context, final String strDate,
                                     boolean restrict, final CustomClickListener target, final String format) {
        Calendar date = getDateFromString(strDate, format);
        Calendar today = Calendar.getInstance();
        if (restrict && date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date.add(Calendar.YEAR, -18);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SmartDatePickerView dateDlg = new SmartDatePickerView(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Time chosenDate = new Time();
                            chosenDate.set(dayOfMonth, monthOfYear, year);
                            long dt = chosenDate.toMillis(true);
                            CharSequence strDate = DateFormat.format(format, dt);
                            target.onClick(strDate.toString());
                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

            dateDlg.show();
        } else {
            SmartDatePickerView dateDlg = new SmartDatePickerView(context, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Time chosenDate = new Time();
                    chosenDate.set(dayOfMonth, monthOfYear, year);
                    long dt = chosenDate.toMillis(true);
                    CharSequence strDate = DateFormat.format(format, dt);
                    target.onClick(strDate.toString());
                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE), restrict);

            dateDlg.show();
        }
    }

    //this below method is used for cusotm time picker

    public static void getCustomTimePickerDialog(Context context, final String strTime, final CustomTimeDialogListener target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
        CustomTimePickerDialog timeDialog = new CustomTimePickerDialog(context, new CustomTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar date = Calendar.getInstance();
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String dateString = (("" + hourOfDay).length() == 2 ? ("" + hourOfDay) : ("0" + hourOfDay)) + ":" + (("" + minute).length() == 2 ? ("" + minute) : ("0" + minute));
                target.onClick(dateString, date, hourOfDay, minute);
            }
        }, date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), true);

        timeDialog.show();

    }

    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getDateTimeDialog(final Context context, final String strDate,
                                         final CustomClickListener target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DatePickerDialog dateDialog = new DatePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            final int y = year;
                            final int m = monthOfYear;
                            final int d = dayOfMonth;

                            SmartUtils.getCustomTimePickerDialog(context, strDate, new CustomTimeDialogListener() {
                                @Override
                                public void onClick(String value, Calendar date, int hourDay, int minutes) {
                                    Time chosenDate = new Time();
                                    chosenDate.set(0, minutes, hourDay, d, m, y);
                                    long dt = chosenDate.toMillis(true);
                                    CharSequence strDate = DateFormat.format(format, dt);
                                    target.onClick(strDate.toString());
                                }
                            }, format);

//                            new TimePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
//                                    new TimePickerDialog.OnTimeSetListener() {
//
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                            Toast.makeText(context,"Called from within time pic",Toast.LENGTH_LONG).show();
//                                            Time chosenDate = new Time();
//                                            chosenDate.set(0, minute, hourOfDay, d, m, y);
//                                            long dt = chosenDate.toMillis(true);
//                                            CharSequence strDate = DateFormat.format(format, dt);
//                                            target.onClick(strDate.toString());
//                                        }
//                                    }
//                                    ,date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false).show();

                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.show();
        } else {
            DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    final int y = year;
                    final int m = monthOfYear;
                    final int d = dayOfMonth;

                    if (timePickerDialog == null) {
                        timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Time chosenDate = new Time();
                                chosenDate.set(0, minute, hourOfDay, d, m, y);
                                long dt = chosenDate.toMillis(true);
                                CharSequence strDate = DateFormat.format(format, dt);
                                timePickerDialog = null;
                                target.onClick(strDate.toString());
                            }
                        }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), false);

                        timePickerDialog.show();
                    }


                }
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));
            dateDialog.show();
        }
    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime represented time
     * @param target  represented {@link CustomClickListener}
     */
    public static void getTimeDialog(Context context, final String strTime, final CustomClickListener target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TimePickerDialog timeDialog = new TimePickerDialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            Calendar date = Calendar.getInstance();
                            date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            date.set(Calendar.MINUTE, minute);
                            String dateString = new SimpleDateFormat(format).format(date);
                            target.onClick(dateString);
                        }
                    }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

            timeDialog.show();
        } else {
            TimePickerDialog timeDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Calendar date = Calendar.getInstance();
                    date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    date.set(Calendar.MINUTE, minute);
                    String dateString = new SimpleDateFormat(format).format(date);
                    target.onClick(dateString);
                }
            }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true);

            timeDialog.show();
        }
    }

    /**
     * This method used to get theme.
     *
     * @return represented {@link String}
     */
    public static String getDefaultAvatar() {
        return SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_DEFAULTAVATAR, "");
    }

    /**
     * This method used to set default avatar
     *
     * @return defaultAvatar represented default avatar
     */
    public static void setDefaultAvatar(String defaultAvatar) {
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_DEFAULTAVATAR, defaultAvatar);
    }

    public static void showLoadingDialog(final Context context) {
        hideLoadingDialog();

        loadingDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
        loadingDialog.setContentView(R.layout.ijoomer_loading_dialog);
        loadingDialog.setCancelable(true);
        loadingDialog.show();
    }

    public static void hideLoadingDialog() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
                loadingDialog = null;
            }
        } catch (Exception ed) {
            ed.printStackTrace();
        }
    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * SmartButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param context = Context context will be current activity's context.
     *                <b>Note</b> : A new progress dialog will be generated on
     *                screen each time this method is called.
     */
    public static void showProgressDialog(final Context context, String msg, final boolean isCancellable) {
//        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
//        }
        progressDialog = ProgressDialog.show(context, "", "");

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(false);
        ((ProgressBar) progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        ((SmartTextView) progressDialog.findViewById(R.id.txtMessage)).setText(msg == null || msg.trim().length() <= 0 ? "Wait" : msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    static public void hideProgressDialog() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        } catch (Throwable e) {
            progressDialog = null;
        }
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }


//    /**
//     * This method will generate and show the Ok dialog with given message and
//     * single message SmartButton.<br>
//     *
//     * @param title  = String title will be the title of OK dialog.
//     * @param msg    = String msg will be the message in OK dialog.
//     * @param target = String target is AlertNewtral callback for OK SmartButton
//     *               click action.
//     */
//    static public void getConfirmDialog(Context context, String title, String msg, String positiveBtnCaption,
//                                        String negativeBtnCaption, boolean isCancelable, final AlertMagnatic target) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
//        builder.setTitle(title).setMessage(msg).setCancelable(false)
//                .setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        target.PositiveMethod(dialog, id);
//                    }
//                })
//                .setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
//                    public void onClick(final DialogInterface dialog, final int id) {
//                        target.NegativeMethod(dialog, id);
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.setCancelable(isCancelable);
//        alert.show();
//    }

    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title         = String title will be the title of OK dialog.
     * @param msg           = String msg will be the message in OK dialog.
     * @param buttonCaption = String SmartButtonCaption will be the name of OK
     *                      SmartButton.
     * @param target        = String target is AlertNewtral callback for OK SmartButton
     *                      click action.
     */
    static public void getOKDialog(Context context, String title, String msg, String buttonCaption,
                                   boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg)
                .setCancelable(false)
                .setNeutralButton(buttonCaption, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        target.NeutralMathod(dialog, id);
                    }
                });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }


    /**
     * This method will show short length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void ting(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method will show long length Toast message with given string.
     *
     * @param msg = String msg to be shown in Toast message.
     */
    static public void tong(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    // Audio, Image and Video

    /**
     * This method used to decode file from string path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * This method used to decode file from uri path.
     *
     * @param path represented path
     * @return represented {@link Bitmap}
     */
    static public Bitmap decodeFile(Context context, Uri path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(getAbsolutePath(context, path), o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(getAbsolutePath(context, path), o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    static public String getYoutubeId(String videoUrl) {
        String video_id = "";
        if (videoUrl != null && videoUrl.trim().length() > 0) {
            String s = "^.*(?:youtu.be\\/|v\\/|e\\/|u\\/\\w+\\/|embed\\/|v=)([^#\\&\\?]*).*";
            CharSequence input = videoUrl;
            Pattern pattern = Pattern.compile(s, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                System.out.println("DATA" + matcher.group(1));
                String groupIndex1 = matcher.group(1);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    video_id = groupIndex1;
            }
        }
        System.out.println("VIDEOID" + video_id);
        if (video_id.trim().length() > 0) {
            return video_id;
        } else {
            return "";
        }
    }

    //General Methods

    /**
     * This method will write any text string to the log file generated by the
     * SmartFramework.
     *
     * @param text = String text is the text which is to be written to the log
     *             file.
     */
    static public void appendLog(String text) {
        File logFile = new File("sdcard/" + SmartApplication.REF_SMART_APPLICATION.LOGFILENAME);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            Calendar calendar = Calendar.getInstance();
            try {
                System.err.println("Logged Date-Time : " + ((String) DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar)));
            } catch (Throwable e) {
            }
            buf.append("Logged Date-Time : " + ((String) DateFormat.format("yyyy-MM-dd kk:mm:ss", calendar)));
            buf.append("\n\n");
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * This method will return android device UDID.
     *
     * @return DeviceID = String DeviceId will be the Unique Id of android
     * device.
     */
    static public String getDeviceUDID(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }


    public static String getB64Auth(String userName, String password) {
        String source = userName + ":" + password;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    /**
     * This method used to set image uri.
     *
     * @return represented {@link Uri}
     */
    public static Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        imgPath = file.getAbsolutePath();
        return imgUri;
    }

    /**
     * This method used to get Image path.
     *
     * @return
     */
    public static String getImagePath() {
        return imgPath;
    }

    /**
     * This method used to get absolute path from uri.
     *
     * @param uri represented uri
     * @return represented {@link String}
     */
    public static String getAbsolutePath(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT < 11)
            return RealPathUtil.getRealPathFromURI_BelowAPI11(context, uri);

            // SDK >= 11 && SDK < 19
        else if (Build.VERSION.SDK_INT < 19)
            return RealPathUtil.getRealPathFromURI_API11to18(context, uri);

            // SDK > 19 (Android 4.4)
        else
            return RealPathUtil.getRealPathFromURI_API19(context, uri);
    }

    static public Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    /**
     * This method used to hide soft keyboard.
     */
    static public void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to show soft keyboard.
     */
    static public void showSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
        }
    }

    /**
     * This method used to do ellipsize to textview.
     *
     * @param tv      represented TextView do ellipsize
     * @param maxLine represented max line to show
     */
    static public void doEllipsize(final SmartTextView tv, final int maxLine) {
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine <= 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - 3) + "...";
                    tv.setText(text);
                } else if (tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - 3) + "...";
                    tv.setText(text);
                }
            }
        });
    }

    /**
     * This method used to convert json to map.
     *
     * @param object represented json object
     * @return represented {@link Map <String, String>}
     * @throws JSONException represented {@link JSONException}
     */
    static public Map<String, String> jsonToMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap();
        Iterator keys = object.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            map.put(key, fromJson(object.get(key)).toString());
        }
        return map;
    }

    /**
     * This method used to convert json to Object.
     *
     * @param json represented json object
     * @return represented {@link Object}
     * @throws JSONException represented {@link JSONException}
     */
    static public Object fromJson(Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        } else if (json instanceof JSONObject) {
            return jsonToMap((JSONObject) json);
        } else if (json instanceof JSONArray) {
            return toList((JSONArray) json);
        } else {
            return json;
        }
    }

    /**
     * This method used to convert json array to List.
     *
     * @param array represented json array
     * @return represented {@link List}
     * @throws JSONException represented {@link JSONException}
     */
    static public List toList(JSONArray array) throws JSONException {
        List list = new ArrayList();
        int size = array.length();
        for (int i = 0; i < size; i++) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }

    /**
     * This method used to string array from string with (,) separated.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final String value) {
        try {
            if (value.length() > 0) {
                final JSONArray temp = new JSONArray(value);
                int length = temp.length();
                if (length > 0) {
                    final String[] recipients = new String[length];
                    for (int i = 0; i < length; i++) {
                        recipients[i] = temp.getString(i).equalsIgnoreCase("null") ? "1" : temp.getString(i);
                    }
                    return recipients;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * This method used to string array from arraylist.
     *
     * @param value represented value
     * @return represented {@link String} array
     */
    static public String[] getStringArray(final ArrayList<String> value) {
        try {
            String[] array = new String[value.size()];
            for (int i = 0; i < value.size(); i++) {
                array[i] = value.get(i);
            }
            return array;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    static public void exportDatabse(Context context, String databaseName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + context.getPackageName() + "//databases//" + databaseName + "";
                String backupDBPath = "backupname.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
        }
    }


    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_FLOOR);
        return bd.floatValue();
    }

    static public void setAuthPermission() {

        AQuery.setAuthHeader(SmartUtils.getB64Auth(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_USERNAME, ""),
                SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_HTTP_ACCESSS_PASSWORD, "")));
    }

    static public String removeSpecialCharacter(String string) {

        return string.replaceAll("[ ,]", "_");
    }

    static public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static public String createExternalDirectory(String directoryName) {

        if (SmartUtils.isExternalStorageAvailable()) {

            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory may exist");
            }
            return file.getAbsolutePath();
        } else {

            Log.e(TAG, "External storage is not available");
        }
        return null;
    }

    static public void clearActivityStack(Activity currentActivity, Intent intent) {
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        currentActivity.startActivity(mainIntent);
    }

    static public int convertSizeToDeviceDependent(Context context, int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    static public void showSnackBar(Context context, String message, int length) {
//        Snackbar snackbar = Snackbar.make(((SmartActivity) context).getSnackBarContainer(), message, length);
//        View snackBarView = snackbar.getView();
//        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
//        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
//        tv.setTextSize(15);
//        tv.setTextColor(Color.WHITE);
//        snackbar.show();
//        ((SmartActivity) context).setSnackbar(snackbar);
    }

    static public void hideSnackBar(Context context) {
//        if (((SmartActivity) context).getSnackbar() != null) {
//            ((SmartActivity) context).getSnackbar().dismiss();
//        }
    }

    public static boolean isOSPreLollipop() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static String format(String string, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Log.v("@@@@DATATATA", inputTimeFormat.parse(string).toString());
            return outputTimeFormat.format(inputTimeFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isEqualDates(String start, String end, String inputFormat, String outputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat = new SimpleDateFormat(outputFormat);
        try {
            Date sDate = inputTimeFormat.parse(start);
            String startDate = outputTimeFormat.format(sDate);
            Log.v("@@@@STARTDATE::", startDate.toString());

            Date eDate = inputTimeFormat.parse(end);
            String endDate = outputTimeFormat.format(eDate);
            Log.v("@@@@ENDDATE::", endDate.toString());

            if (endDate.equals(startDate)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Date format(String string, String inputFormat) {
        SimpleDateFormat inputTimeFormat = new SimpleDateFormat(inputFormat);
        try {
            return inputTimeFormat.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isTimePassed(String string, String hours24) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int minute = now.get(Calendar.MINUTE);

        SimpleDateFormat inputParser = new SimpleDateFormat("HH:mm");
        String str = inputParser.format(now.getTime());
        Date currentDate = parseDate(str, hours24);
        Date inputDate = parseDate(string, hours24);

        return currentDate.after(inputDate);
    }

    private static Date parseDate(String date, String hours24) {
        SimpleDateFormat inputParser = new SimpleDateFormat(hours24);
        try {
            return inputParser.parse(date);
        } catch (java.text.ParseException e) {
            return new Date(0);
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }


    static public void removeCookie() {
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().remove(Constants.SP_COOKIES);
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().commit();
    }

    static public String validateResponse(Context context, JSONObject response, String errorMessage) {
        if (response.has("php_server_error")) {
            try {
                System.out.println("WSPHP SERVER_WARNINGS/ERRORS" + response.getString("php_server_error"));
                response.remove("php_server_error");
            } catch (Exception e) {
            }
        }

        if (response.has("code")) {
            try {
                if (response.has("message") && response.getString("message").length() > 0) {
                    errorMessage = response.getString("message");
                } else {
                    try {
                        int code = Integer.parseInt(response.getString("code"));
                        errorMessage = context.getString(context.getResources().getIdentifier("code" + code, "string", context.getPackageName()));
                    } catch (Exception e) {
                    }
                }
            } catch (Throwable e) {
            }
        } else {
            errorMessage = "Invalid Response";
        }

        if (response.has("notification")) {
            try {
                JSONObject obj = response.getJSONObject("notification");
                if (obj.has("friendrequest")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, obj.getString("friendrequest"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_FRIEND_NOTIFICATION, "0");
                }
                if (obj.has("inbox")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, obj.getString("inbox"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_MESSAGE_NOTIFICATION, "0");
                }
                if (obj.has("general")) {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, obj.getString("general"));
                } else {
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(SP_GLOBAL_NOTIFICATION, "0");
                }
            } catch (Exception e) {
            }
        }

        removeUnnacessaryFields(response);


        return errorMessage;
    }

    static public int getResponseCode(JSONObject response) {
        if (response.has("code")) {
            try {
                int code = Integer.parseInt(response.getString("code"));
                return code;
            } catch (Throwable e) {
                e.printStackTrace();
                return 108;
            }
        }
        return 108;
    }

    static public boolean isSessionExpire(JSONObject response) {
        if (response != null && response.has("code")) {
            try {
                if (response.getInt("code") == 704) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    static private void removeUnnacessaryFields(JSONObject data) {
        data.remove("code");
        data.remove("full");
        data.remove("notification");
        data.remove("pushNotificationData");
        data.remove("timeStamp");
        data.remove("unreadMessageCount");
    }

    /**
     * This method used to auto login user params.
     *
     * @return represented {@link JSONObject}
     */
    static public JSONObject getLoginParams() {
        JSONObject loginParams = null;
        try {
            loginParams = new JSONObject(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences()
                    .getString(SP_LOGIN_REQ_OBJECT, ""));
            JSONObject taskData = loginParams.getJSONObject("taskData");
            taskData.put("lat", getLatitude());
            taskData.put("long", getLongitude());
            String udid = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SP_GCM_REGID, "");
            if (udid.length() > 0) {
                taskData.put("devicetoken", udid);
            }
        } catch (Exception e) {
        }
        return loginParams;
    }


    static public String getCountryPhoneCode(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimCountryIso() != null && tm.getSimCountryIso().length() > 0) {
            return Iso2Phone.getPhone(tm.getSimCountryIso());
        } else {
            return Iso2Phone.getPhone(tm.getNetworkCountryIso());
        }
    }

    public static boolean isPhoneValid(String input) {
        return input.length() != 10 ? false : android.util.Patterns.PHONE.matcher(input).matches();
    }


    public static float parseFloat(String value) {
        float result = (float) 0.00;
        if (value != null && value.length() > 0) {
            try {
                result = round(Float.parseFloat(value), 2);
            } catch (NumberFormatException e) {
                return (float) 0.00;
            }
        }
        return result;
    }

    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(Context context, String address) {
        if (address != null && address.length() > 0) {
            geocoder = new Geocoder(context);

            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(address, 1);
                return list.get(0);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat represented latitude (0-for current latitude)
     * @param lng represented longitude (0-for current longitude)
     * @return represented {@link Address}
     */
    public static Address getAddressFromLatLong(Context context, double lat, double lng) {
        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(getLatitude());
            lng = Double.parseDouble(getLongitude());
        }
        geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
            return list.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to get address list from latitude-longitude
     *
     * @param lat represented latitude (0-for current latitude)
     * @param lng represented longitude (0-for current longitude)
     * @return represented {@link Address} list
     */
    public static List<Address> getAddressListFromLatLong(Context context, double lat, double lng) {
        if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(getLatitude());
            lng = Double.parseDouble(getLongitude());
        }
        geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isValidLatLng(double lat, double lng) {
        if (lat < -90 || lat > 90) {
            return false;
        } else if (lng < -180 || lng > 180) {
            return false;
        }
        return true;
    }


}
