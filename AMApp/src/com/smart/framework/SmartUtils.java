package com.smart.framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.IntentCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.amapp.AMAppMaster;
import com.amapp.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.smart.customviews.CustomClickListner;
import com.smart.customviews.SelectImageDialogListner;
import com.smart.customviews.SmartButton;
import com.smart.customviews.SmartDatePickerView;
import com.smart.customviews.SmartSpannable;
import com.smart.customviews.SmartTextView;
import com.smart.utilities.Iso2Phone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tasol on 23/5/15.
 */

public class SmartUtils implements Constants{

    private static final String TAG = "SmartUtil";
    private static boolean isNetworkAvailable;
    private static ProgressDialog progressDialog;

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

                        isNetworkAvailable=true;
                        return;
                    }
                }
            }
        }

        isNetworkAvailable=false;
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
    public static boolean birthdateValidator(String birthDate,String format) {
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
     * This method used to get date in string from time zone.
     *
     * @param timestamp represented {@link Long} time stamp
     * @return represented {@link String}
     */
   /* public static String getDateStringCurrentTimeZone(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone(ApplicationConfiguration.getServerTimeZone());

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));

        SimpleDateFormat sdf = new SimpleDateFormat(ApplicationConfiguration.dateTimeFormat);
        String dateString = sdf.format(calendar.getTime());
        return dateString;
    }*/

    /**
     * This method used to get current date from time zone.
     *
     * @param timestamp represented {@link Long} time stamp
     * @return represented {@link Date}
     */
   /* public static Date getDateCurrentTimeZone(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone(IjoomerGlobalConfiguration.getServerTimeZone());

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));

        return (Date) calendar.getTime();
    }*/

    /**
     * This method used to get milliseconds from time zone.
     *
     * @param timestamp represented {@link Long} time stamp
     * @return represented {@link Long}
     */
   /* public static long getMillisecondsTimeZone(long timestamp) {

        Calendar calendar = Calendar.getInstance();
        TimeZone t = TimeZone.getTimeZone(IjoomerGlobalConfiguration.getServerTimeZone());

        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.MILLISECOND, t.getOffset(calendar.getTimeInMillis()));
        System.out.println("Date : " + calendar.getTime());
        return calendar.getTimeInMillis();
    }*/

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
    public static String calculateTimesAgo(long miliseconds,String format) {
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
     * This method used to get date from string.
     *
     * @param strDate represented date
     * @return represented {@link Date}
     */
    public static Calendar getDateFromString(String strDate,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Calendar calnder = Calendar.getInstance();
        Date date;
        try {
            date = dateFormat.parse(strDate);
            calnder.setTime(date);
            return calnder;
        } catch (Throwable e) {
            return Calendar.getInstance();
        }
    }


    public static String getStringFromCalendar(Calendar c,String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(c.getTime());
    }

    /**
     * This method used to get time from string.
     *
     * @param strTime represented time
     * @return represented {@link Date}
     */
    public static Calendar getTimeFromString(String strTime,String format) {
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


    /**
     * This method used to get date dialog.
     *
     * @param strDate  represented date
     * @param restrict represented isRestrict
     */
    public static void getDateDialog(Context context, final String strDate, boolean restrict, final CustomClickListner target, final String format) {

        Calendar date = getDateFromString(strDate,format);
        Calendar today = Calendar.getInstance();

        if (restrict && date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            date.add(Calendar.YEAR, -18);
        }

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

    /**
     * This method used to get date-time dialog.
     *
     * @param strDate represented date-time
     * @param target  represented {@link CustomClickListner}
     */
    public static void getDateTimeDialog(final Context context, final String strDate, final CustomClickListner target, final String format) {
        final Calendar date = getDateFromString(strDate, format);
        DatePickerDialog dateDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                final int y = year;
                final int m = monthOfYear;
                final int d = dayOfMonth;

                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        Time chosenDate = new Time();
                        chosenDate.set(0, minute, hourOfDay, d, m, y);
                        long dt = chosenDate.toMillis(true);
                        CharSequence strDate = DateFormat.format(format, dt);
                        target.onClick(strDate.toString());
                    }
                }, date.get(Calendar.HOUR), date.get(Calendar.MINUTE), true).show();

            }
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DATE));

        dateDialog.show();

    }

    /**
     * This method used to get time dialog.
     *
     * @param strTime represented time
     * @param target  represented {@link CustomClickListner}
     */
    public static void getTimeDialog(Context context, final String strTime, final CustomClickListner target, final String format) {

        Calendar date = getTimeFromString(strTime, format);
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


    // Dialogs


    /**
     * This method used to show select image selection dialog.
     *
     * @param target represented {@link SelectImageDialogListner}
     */
    public static void selectImageDialog(Context context, final SelectImageDialogListner target) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar);
//        dialog.setContentView(R.layout.ijoomer_select_image_dialog);
        final SmartTextView txtCapture = (SmartTextView) dialog.findViewById(R.id.txtCapture);
        final SmartTextView txtPhoneGallery = (SmartTextView) dialog.findViewById(R.id.txtPhoneGallery);
        txtCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onCapture();
                dialog.dismiss();

            }
        });
        txtPhoneGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View paramView) {
                target.onPhoneGallery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * This method used to get latitude-longitude from address.
     *
     * @param address represented address
     * @return represented {@link Address}
     */
    public static Address getLatLongFromAddress(Context context, String address) {
        if (address != null && address.length() > 0) {
            Geocoder geocoder = new Geocoder(context);

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

       /* if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(((SmartActivity) context).getLatitude());
            lng = Double.parseDouble(((SmartActivity) context).getLongitude());
        }
        Geocoder geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
            return list.get(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
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

       /* if (lat == 0 || lng == 0) {
            lat = Double.parseDouble(((SmartActivity) context).getLatitude());
            lng = Double.parseDouble(((SmartActivity) context).getLongitude());
        }
        Geocoder geocoder = new Geocoder(context);

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(lat, lng, 10);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return list;*/
        return null;
    }

    /**
     * This method will show the progress dialog with given message in the given
     * activity's context.<br>
     * The progress dialog can be set cancellable by passing appropriate flag in
     * parameter. User can dismiss the current progress dialog by pressing back
     * SmartButton if the flag is set to <b>true</b>; This method can also be
     * called from non UI threads.
     *
     * @param msg     = String msg to be displayed in progress dialog.
     * @param context = Context context will be current activity's context.
     *                <b>Note</b> : A new progress dialog will be generated on
     *                screen each time this method is called.
     */

    static public void showProgressDialog(final Context context,String msg, final boolean isCancellable) {

        if(progressDialog==null){
            progressDialog=new ProgressDialog(context,R.style.AppCompatAlertDialogStyle);
        }
        progressDialog = ProgressDialog.show(context,"","");

        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(isCancellable);
        ((ProgressBar)progressDialog.findViewById(R.id.progressBar)).getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_ATOP);
        ((SmartTextView)progressDialog.findViewById(R.id.txtMessage)).setText(msg == null || msg.trim().length() <= 0 ? context.getString(R.string.dialog_loading_please_wait) : msg);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }

    public static void clearParentsBackgrounds(View view) {
        while (view != null) {
            final ViewParent parent = view.getParent();
            if (parent instanceof View) {
                view = (View) parent;
                view.setBackgroundResource(android.graphics.Color.TRANSPARENT);
            } else {
                view = null;
            }
        }
    }

    /**
     * This method will hide existing progress dialog.<br>
     * It will not throw any Exception if there is no progress dialog on the
     * screen and can also be called from non UI threads.
     */
    static public void hideProgressDialog() {

        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
                progressDialog=null;
        } catch (Throwable e) {

        }
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }



    /**
     * This method will generate and show the Ok dialog with given message and
     * single message SmartButton.<br>
     *
     * @param title              = String title will be the title of OK dialog.
     * @param msg                = String msg will be the message in OK dialog.
     * @param SmartButtonCaption = String SmartButtonCaption will be the name of OK
     *                           SmartButton.
     * @param target             = String target is AlertNewtral callback for OK SmartButton
     *                           click action.
     */
    static public void getOKDialog(Context context, String title, String msg, String SmartButtonCaption, boolean isCancelable, final AlertNeutral target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = context.getResources().getDrawable(imageResource);

        builder.setTitle(title).setMessage(msg).setIcon(image).setCancelable(false).setNeutralButton(SmartButtonCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                target.NeutralMathod(dialog, id);
            }
        });

        AlertDialog alert = builder.create();
        alert.setCancelable(isCancelable);
        alert.show();
    }

    static public void getCustomOkDialog(Context context, String title, String msg, int layoutID, int SmartTextViewID, int SmartButtonID, final CustomAlertNeutral target) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutID);

        if (title.length() > 0)
            dialog.setTitle(title);

        SmartTextView tv = (SmartTextView) dialog.findViewById(SmartTextViewID);
        tv.setText(msg);
        SmartButton ok = (SmartButton) dialog.findViewById(SmartButtonID);
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                target.NeutralMethod();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    static public void getCustomConfirmDialog(Context context, String title, String msg, int layoutID, int SmartTextViewID, int okSmartButtonID, int cancelSmartButtonId,
                                              final CustomAlertMagnatic target) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(layoutID);
        if (title.length() > 0)
            dialog.setTitle(title);

        SmartTextView tv = (SmartTextView) dialog.findViewById(SmartTextViewID);
        tv.setText(msg);
        SmartButton ok = (SmartButton) dialog.findViewById(okSmartButtonID);
        SmartButton cancel = (SmartButton) dialog.findViewById(cancelSmartButtonId);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                target.PositiveMethod();
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                target.NegativeMethod();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * This method will generate and show the OK/Cancel dialog with given
     * message and single message SmartButton.<br>
     *
     * @param title              = String title will be the title of OK dialog.
     * @param msg                = String msg will be the message in OK dialog.
     * @param positiveBtnCaption = String positiveBtnCaption will be the name of OK
     *                           SmartButton.
     * @param negativeBtnCaption = String negativeBtnCaption will be the name of OK
     *                           SmartButton.
     * @param target             = String target is AlertNewtral callback for OK SmartButton
     *                           click action.
     */
    static public void getConfirmDialog(Context context, String title, String msg, String positiveBtnCaption, String negativeBtnCaption, boolean isCancelable, final AlertMagnatic target) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        int imageResource = android.R.drawable.ic_dialog_alert;
        Drawable image = context.getResources().getDrawable(imageResource);

        builder.setTitle(title).setMessage(msg).setIcon(image).setCancelable(false).setPositiveButton(positiveBtnCaption, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                target.PositiveMethod(dialog, id);
            }
        }).setNegativeButton(negativeBtnCaption, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                target.NegativeMethod(dialog, id);
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
     * This method used to set image uri.
     *
     * @return represented {@link Uri}
     */
    static public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        return imgUri;
    }



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

    static public Uri getVideoPlayURI(String videoUrl) {

        String video_id = "";
        if (videoUrl != null && videoUrl.trim().length() > 0) {
            System.out.println("VIDEOURL" + videoUrl);
            //String expression = "(?:http|https|)(?::\\/\\/|)(?:www.|)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[a-z0-9;:@?&%=+\\/\\$_.-]*";
            String expression = "(?:http|https|)(?::\\/\\/|)(?:www.|m.)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{11})[a-z0-9;:@?&%=+\\/\\$_.-]*";
            CharSequence input = videoUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
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
            return Uri.parse("ytv://" + video_id);
        } else {
            return Uri.parse("mp4://" + videoUrl);
        }
    }


    // Contacts

    /**
     * This method used to get contact list from device.
     *
     * @return represented {@link ArrayList}
     */
    @SuppressWarnings("serial")
    public static ArrayList<HashMap<String, Object>> getContacts(Context context) {

        ArrayList<HashMap<String, Object>> contacts = new ArrayList<HashMap<String, Object>>();
        final String[] projection = new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.DELETED};

        @SuppressWarnings("deprecation")
        final Cursor rawContacts = ((Activity) context).managedQuery(ContactsContract.RawContacts.CONTENT_URI, projection, null, null, null);

        final int contactIdColumnIndex = rawContacts.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
        final int deletedColumnIndex = rawContacts.getColumnIndex(ContactsContract.RawContacts.DELETED);

        if (rawContacts.moveToFirst()) {
            while (!rawContacts.isAfterLast()) {
                final int contactId = rawContacts.getInt(contactIdColumnIndex);
                final boolean deleted = (rawContacts.getInt(deletedColumnIndex) == 1);

                if (!deleted) {
                    HashMap<String, Object> contactInfo = new HashMap<String, Object>() {
                        {
                            put("contactId", "");
                            put("name", "");
                            put("email", "");
                            put("address", "");
                            put("photo", "");
                            put("phone", "");
                        }
                    };
                    contactInfo.put("contactId", "" + contactId);
                    contactInfo.put("name", getName(context, contactId));
                    contactInfo.put("email", getEmail(context, contactId));
                    contactInfo.put("photo", getPhoto(context, contactId) != null ? getPhoto(context, contactId) : "");
                    contactInfo.put("address", getAddress(context, contactId));
                    contactInfo.put("phone", getPhoneNumber(context, contactId));
                    contactInfo.put("isChecked", "false");
                    contacts.add(contactInfo);
                }
                rawContacts.moveToNext();
            }
        }

        rawContacts.close();

        return contacts;
    }

    /**
     * This method used to get name from contact id.
     *
     * @param contactId represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    public static String getName(Context context, int contactId) {
        String name = "";
        final String[] projection = new String[]{ContactsContract.Contacts.DISPLAY_NAME};

        final Cursor contact = ((Activity) context).managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts._ID + "=?",
                new String[]{String.valueOf(contactId)}, null);

        if (contact.moveToFirst()) {
            name = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contact.close();
        }
        contact.close();
        return name;

    }

    /**
     * This method used to get mail id from contact id.
     *
     * @param contactId represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    public static String getEmail(Context context, int contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA, // use
                // Email.ADDRESS
                // for API-Level
                // 11+
                ContactsContract.CommonDataKinds.Email.TYPE};

        final Cursor email = ((Activity) context).managedQuery(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}, null);

        if (email.moveToFirst()) {
            final int contactEmailColumnIndex = email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            while (!email.isAfterLast()) {
                emailStr = emailStr + email.getString(contactEmailColumnIndex) + ";";
                email.moveToNext();
            }
        }
        email.close();
        return emailStr;

    }

    /**
     * This method used to get {@link Bitmap} From contact id.
     *
     * @param contactId represented contact id
     * @return represented {@link Bitmap}
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getPhoto(Context context, int contactId) {
        Bitmap photo = null;
        final String[] projection = new String[]{ContactsContract.Contacts.PHOTO_ID};

        final Cursor contact = ((Activity) context).managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, ContactsContract.Contacts._ID + "=?",
                new String[]{String.valueOf(contactId)}, null);

        if (contact.moveToFirst()) {
            final String photoId = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
            if (photoId != null) {
                photo = getBitmap(context, photoId);
            } else {
                photo = null;
            }
        }
        contact.close();

        return photo;
    }

    /**
     * This method used to get {@link Bitmap} From photo id.
     *
     * @param photoId represented photo id
     * @return represented {@link Bitmap}
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getBitmap(Context context, String photoId) {
        final Cursor photo = ((Activity) context).managedQuery(ContactsContract.Data.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, ContactsContract.Data._ID + "=?",
                new String[]{photoId}, null);

        final Bitmap photoBitmap;
        if (photo.moveToFirst()) {
            byte[] photoBlob = photo.getBlob(photo.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
            photoBitmap = BitmapFactory.decodeByteArray(photoBlob, 0, photoBlob.length);
        } else {
            photoBitmap = null;
        }
        photo.close();
        return photoBitmap;
    }

    /**
     * This method used to get address from contact id.
     *
     * @param contactId represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    public static String getAddress(Context context, int contactId) {
        String postalData = "";
        String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] addrWhereParams = new String[]{String.valueOf(contactId),
                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};

        Cursor addrCur = ((Activity) context).managedQuery(ContactsContract.Data.CONTENT_URI, null, addrWhere, addrWhereParams, null);

        if (addrCur.moveToFirst()) {
            postalData = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
        }
        addrCur.close();
        return postalData;
    }

    /**
     * This method used to get phone number from contact id.
     *
     * @param contactId represented contact id
     * @return represented {@link String}
     */
    @SuppressWarnings("deprecation")
    public static String getPhoneNumber(Context context, int contactId) {

        String phoneNumber = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.TYPE,};
        final Cursor phone = ((Activity) context).managedQuery(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?",
                new String[]{String.valueOf(contactId)}, null);

        if (phone.moveToFirst()) {
            final int contactNumberColumnIndex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);

            while (!phone.isAfterLast()) {
                phoneNumber = phoneNumber + phone.getString(contactNumberColumnIndex) + ";";
                phone.moveToNext();
            }

        }
        phone.close();
        return phoneNumber;
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

    /**
     * This method will print full device configuration to log file and on log
     * cat.
     *
     * @param context
     */
    static public void printDeviceConfig(Context context) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            stringBuilder.append("=============== Current Version ===============================\n");
            stringBuilder.append("1.1 Build (Released on 28-09-2011 12:23 PM)");
            stringBuilder.append("\n");
            System.err.println("=============== HEAP INFO ===============================");
            stringBuilder.append("=============== HEAP INFO(S) ===============================");
            stringBuilder.append("\n");

            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            System.err.println("Over All Memory: " + (memoryInfo.availMem / 1024) + " KB");
            stringBuilder.append("Over All Memory: " + (memoryInfo.availMem / 1024) + " KB");
            stringBuilder.append("\n");
            System.err.println("low Memory: " + memoryInfo.lowMemory);
            stringBuilder.append("low Memory: " + memoryInfo.lowMemory);
            stringBuilder.append("\n");
            System.err.println("Threshold Memory: " + (memoryInfo.threshold / 1024) + " KB");
            stringBuilder.append("Threshold Memory: " + (memoryInfo.threshold / 1024) + " KB");
            stringBuilder.append("\n");

            System.err.println("=============== OS INFO ===============================");
            stringBuilder.append("=============== OS INFO ===============================");
            stringBuilder.append("\n");
            System.err.println("Device MODEL: " + android.os.Build.MODEL);
            stringBuilder.append("Device MODEL: " + android.os.Build.MODEL);
            stringBuilder.append("\n");
            System.err.println("VERSION RELEASE: " + android.os.Build.VERSION.RELEASE);
            stringBuilder.append("VERSION RELEASE: " + android.os.Build.VERSION.RELEASE);
            stringBuilder.append("\n");
            System.err.println("VERSION SDK: " + android.os.Build.VERSION.SDK_INT);
            stringBuilder.append("VERSION SDK: " + android.os.Build.VERSION.SDK_INT);
            stringBuilder.append("\n");

            System.err.println("=============== Device Information ===============================");
            stringBuilder.append("=============== Device Information ===============================");
            stringBuilder.append("\n");
            DisplayMetrics dm = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
            System.err.println("Device Resolution (WxH)= " + dm.widthPixels + " x " + dm.heightPixels);
//            width = dm.widthPixels;
//            height = dm.heightPixels;
//            density = dm.densityDpi;
//            orientation = getResources().getConfiguration().orientation;

            stringBuilder.append("Device Resolution (WxH)= " + dm.widthPixels + " x " + dm.heightPixels);
            stringBuilder.append("\n");
            System.err.println("Density DPI= " + dm.densityDpi);
            stringBuilder.append("Density DPI= " + dm.densityDpi);
            stringBuilder.append("\n");

        } catch (Throwable e) {
            e.printStackTrace();
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            stringBuilder.append("\n");
            stringBuilder.append("=============== Exception while Fetching Information ===============================");
            stringBuilder.append("\n");
            stringBuilder.append(stackTrace);
            stringBuilder.append("\n");
        }

        appendLog(stringBuilder.toString());

    }


    public static String getB64Auth(String userName, String password) {
        String source = userName + ":" + password;
        String ret = "Basic " + Base64.encodeToString(source.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
        return ret;
    }

    /**
     * This method used to get absolute path from uri.
     *
     * @param uri represented uri
     * @return represented {@link String}
     */

    static public String getAbsolutePath(Context context, Uri uri) {

        String path = "";
        try{
            String[] projection = { MediaStore.MediaColumns.DATA };
            @SuppressWarnings("deprecation")
            Cursor cursor = ((Activity) context).managedQuery(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                path= cursor.getString(column_index);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if(path==null || path.length()<=0){

            try{
                Uri originalUri = uri;

                String id = originalUri.getLastPathSegment().split(":")[1];
                final String[] imageColumns = {MediaStore.Images.Media.DATA };
                final String imageOrderBy = null;

                Uri finalUri = getUri();

                Cursor imageCursor = ((Activity) context).managedQuery(finalUri, imageColumns,
                        MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

                if (imageCursor.moveToFirst()) {
                    path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(path==null || path.length()<=0){
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = ((Activity) context).getContentResolver().query(uri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        path= cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            }
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                path= uri.getPath();
            }
        }

        if(path==null || path.length()<=0){
            path= getPath(uri,context);
        }

        return path;
    }
    static public Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    static public  String getPath(final Uri uri,Context context) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * This method used to hide soft keyboard.
     */
    static public void hideSoftKeyboard(Context context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
    @SuppressWarnings({"rawtypes", "unchecked"})
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
    @SuppressWarnings({"rawtypes", "unchecked"})
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
            e.printStackTrace();
            return null;
        }
        return null;
    }

    @SuppressWarnings("resource")
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


    /**
     * This method used to add clickable part on {@link TextView}.
     *
     * @param strSpanned represented {@link Spanned} string
     * @param tv         represented {@link TextView}
     * @param maxLine    represented max line
     * @param expandText represented expand text
     * @return represented {@link SpannableStringBuilder}
     */
    public static SpannableStringBuilder addClickablePartSmartTextViewResizable(Context context, final Spanned strSpanned, final SmartTextView tv,
                                                                                final int maxLine, final String expandText) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(expandText)) {
            ssb.setSpan(new SmartSpannable(Color.parseColor(context.getString(R.color.yello)), true) {

                @Override
                public void onClick(View widget) {
                    tv.setLayoutParams(tv.getLayoutParams());
                    tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                    tv.invalidate();
                }

            }, str.indexOf(expandText), str.indexOf(expandText) + expandText.length(), 0);

        }
        return ssb;

    }

    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_FLOOR);
        return bd.floatValue();
    }

    public static double convertDistance(String valueToConvert, int fromUnit, int toUnit) {

        double dvalueToConvert = Double.parseDouble(valueToConvert);
        if (dvalueToConvert > 0) {
            if (fromUnit == Constants.KILOMETER) {
                dvalueToConvert = dvalueToConvert * 1000d;
            } else if (fromUnit == Constants.MILE) {
                dvalueToConvert = dvalueToConvert * 1609.34d;
            } else if (fromUnit == Constants.DEGREE) {
                dvalueToConvert = (Math.acos(dvalueToConvert) * 6371) * 1000d;
            }

            if (toUnit == Constants.KILOMETER) {
                dvalueToConvert = dvalueToConvert / 1000d;
            } else if (toUnit == Constants.MILE) {
                dvalueToConvert = dvalueToConvert / 1609.34d;
            } else if (fromUnit == Constants.DEGREE) {
                dvalueToConvert = Math.acos(((dvalueToConvert / 1000d) / 6371d));
            }

            String s = String.format(String.format("%.2f", dvalueToConvert));
            return Double.parseDouble(s);
        }
        return 0;
    }

    static public void setAuthPermission(){

        AQuery.setAuthHeader(SmartUtils.getB64Auth(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SharedPreferenceConstants.SP_HTTP_ACCESSS_USERNAME, ""), SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(SharedPreferenceConstants.SP_HTTP_ACCESSS_PASSWORD, "")));
    }

    static public String removeSpecialCharacter(String string){

        return string.replaceAll("[ ,]","_");
    }

    static public boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    static public String createExternalDirectory(String directoryName){

        if(SmartUtils.isExternalStorageAvailable()){

            File file = new File(Environment.getExternalStorageDirectory(), directoryName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory may exist");
            }
            return file.getAbsolutePath();
        }else{

            Log.e(TAG, "External storage is not available");
        }
        return null;
    }

    static public void clearActivityStack(Context context,Intent intent){

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((AMAppMaster) context);
        Intent mainIntent=IntentCompat.makeRestartActivityTask(intent.getComponent());
        mainIntent.putExtra(DO_LOGOUT,true);
        ActivityCompat.startActivity((AMAppMaster)context,mainIntent , options.toBundle());
    }

    static public Drawable getTintedDrawable(Resources res,@DrawableRes int drawableResId, @ColorRes int colorResId) {
        Drawable drawable = res.getDrawable(drawableResId);
        int color = res.getColor(colorResId);
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    static public Drawable getTintedDrawable(Resources res,@DrawableRes Drawable drawable, @ColorRes int colorResId) {

        int color = res.getColor(colorResId);
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    static public Drawable getTintedDrawable(Resources res,@DrawableRes Bitmap bitmap, @ColorRes int colorResId) {
        Drawable drawable = new BitmapDrawable(res,bitmap);
        int color = res.getColor(colorResId);
//        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }

    static public void clearSuggestionData(Context context){
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(context,
                SmartSuggestionProvider.AUTHORITY, SmartSuggestionProvider.MODE);
        suggestions.clearHistory();
    }

    static public int convertSizeToDeviceDependent(Context context,int value) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return ((dm.densityDpi * value) / 160);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /*static public int getPixelsFromDp(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
*/


  /*  static public boolean isApplicationLaunchedFirstTime(Context context){

        return isApplicationLaunchedFirstTime;
    }

    static public void SetIsApplicationLaunchedFirstTime(boolean isApplicationLaunchedEver){

        SmartUtils.isApplicationLaunchedFirstTime =isApplicationLaunchedEver;
    }*/

    static public void showSnackBar(Context context, String message, int length) {


        Snackbar snackbar=Snackbar.make(((SmartActivity)context).getSnackBarContainer(),message,length);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(context.getResources().getColor(R.color.primary900));
        TextView tv = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextSize(18);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
        ((SmartActivity)context).setSnackbar(snackbar);

    }

    static public void hideSnackBar(Context context) {

        if(((SmartActivity)context).getSnackbar()!=null){

            ((SmartActivity)context).getSnackbar().dismiss();
        }
    }

    public static boolean isOSPreLollipop() {

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    public static String format(String string, String inputFormat, String outputFormat) {

        SimpleDateFormat inputTimeFormat= new SimpleDateFormat(inputFormat);
        SimpleDateFormat outputTimeFormat= new SimpleDateFormat(outputFormat);
        try {
           return outputTimeFormat.format(inputTimeFormat.parse(string));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Date format(String string, String inputFormat) {

        SimpleDateFormat inputTimeFormat= new SimpleDateFormat(inputFormat);
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
        String str=inputParser.format(now.getTime());
        Date currentDate = parseDate(str, hours24);
        Date inputDate= parseDate(string,hours24);

        return currentDate.after(inputDate);
    }

    private static Date parseDate(String date,String hours24) {
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

    /*public static  void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }*/


    public static void stopLocationUpdates(com.google.android.gms.location.LocationListener locationListener,GoogleApiClient  googleApiClient) {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);

    }

    static public String getCookie()
    {

        String cookie=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(COOKIE, "");
        if (cookie.contains("expires")) {
    /** you might need to make sure that your cookie returns expires when its expired. I also noted that cokephp returns deleted */
            removeCookie();
            return "";
        }
        return cookie;
    }

    static public void removeCookie() {

        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().remove(Constants.COOKIE);
        SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().edit().commit();
    }


    static public String validateResponse(Context context,JSONObject response,String errorMessage) {

        if(response==null && response.length()<=0){
            errorMessage=context.getString(R.string.no_content_found);
        }
        int code=0;
        try{
            System.out.println("WSResponse : " +response);

            if (response.has("code")) {
                try {
                    code = Integer.parseInt(response.getString("code"));

                    if (response.has("message") && response.getString("message").length()>0) {
                        errorMessage=response.getString("message");
                    }else{
                        try{
                            errorMessage=context.getString(context.getResources().getIdentifier("code" + code, "string", context.getPackageName()));
                        }catch (Exception e){
                        }
                    }

                    removeUnnacessaryFields(response);


                    if (code == 200 || code == 703) {
                        errorMessage=null;
                    } else {

                        errorMessage="Invalid response";
                    }

                } catch (Throwable e) {

                }
            }

          /*  if(errorMessage== null || errorMessage.length()<=0){
                errorMessage="Server may be down please try again later.";
            }
*/

            removeUnnacessaryFields(response);
        }catch (Exception e){

            errorMessage=context.getString(context.getResources().getIdentifier("code" + code, "string", context.getPackageName()));
            e.printStackTrace();
        }

        return errorMessage;
    }



    static private void removeUnnacessaryFields(JSONObject data) {
        data.remove("code");
        data.remove("full");
        data.remove("notification");
        data.remove("pushNotificationData");
        data.remove("total");
        data.remove("timeStamp");
        data.remove("unreadMessageCount");

    }

    static public String getCountryPhoneCode(Context context){

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getSimCountryIso()!=null && tm.getSimCountryIso().length()>0){

            return Iso2Phone.getPhone(tm.getSimCountryIso());
        }else{

            return Iso2Phone.getPhone(tm.getNetworkCountryIso());
        }


    }


    public static boolean isPhoneValid(String input) {

        return input.length()!=10?false:android.util.Patterns.PHONE.matcher(input).matches();
    }
}
