package org.anoopam.ext.smart.framework;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.anoopam.main.R;

import org.anoopam.ext.smart.customviews.SmartButton;
import org.anoopam.ext.smart.customviews.SmartTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
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

    public static boolean isOSPreLollipop() {

        return Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
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


                    if (code >= 200 && code < 300 || code == 703) {
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

    static public String getStorageDirectory(){
        String cacheDir = android.os.Environment.getExternalStorageDirectory() + File.separator + "AnoopamMission";
        File dir = new File(cacheDir);
        if(!dir.exists()){
            dir.mkdir();
        }
        return cacheDir;
    }
}
