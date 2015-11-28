package com.smart.framework;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amapp.R;

import java.io.File;

/**
 * This Class Contains All Method Related To ApplicationConfiguration.
 *
 * @author tasol
 *         NOTE : currently this class not used,future development
 */

public class ApplicationConfiguration implements SmartApplicationConfiguration, SmartVersionHandler {

    private static final String TAG = "ApplicationConfig";

    @Override
    public  String getGCMProjectId() {
        return "725447998213";
    }

    @Override
    public String getCrashHandlerFileName() {
        return  getAppName()+ "log.file";
    }

    @Override
    public boolean IsCrashHandlerEnabled() {
        return false;
    }

    @Override
    public String getAppName() {
        return "AMApp";
    }

    @Override
    public boolean IsSharedPreferenceEnabled() {
        return true;
    }

    @Override
    public String getSecurityKey() {
        return "901f15a565f8eac8265bacede4b1c17";

    }

    @Override
    public String getDatabaseName() {
        return "amapp";
    }

    @Override
    public int getDatabaseVersion() {
        return 1;
    }

    @Override
    public String getFacebookAppID() {
        return "431844313570473";
    }

    @Override
    public String getDomain() {
        return "http://www.mocky.io/v2/565320442400005027629a41";
    }

    @Override
    public String getTwitterConsumerKey() {
        return "ACGuGZRQI4rASvX4uHgDw";
    }

    @Override
    public String getTwitterSecretKey() {
        return "n2zv5dXGbvav3FCb63sk3rIYH8zz74is69dUkINlsgg";
    }

    @Override
    public String getFontName() {
        return "fonts/Helvetica_LT_55_Roman_0.ttf";
    }

    @Override
    public String getGCMID() {

        return "AIzaSyCIaTtlpoTfT1PB4mu3zvx_UhTAQGIlQVY";
    }

    @Override
    public String getGCMVersion() {

        return "4030500";
    }

    @Override
    public String getMapAPIKey() {
        return "";
    }

    @Override
    public String getAppMetadataStoragePath(Context context,String extension) {

        if(shouldStoreAppDataInExternalStorage()){

            String directoryPath=SmartUtils.createExternalDirectory(SmartUtils.removeSpecialCharacter(getAppName()));
            if(directoryPath!=null){

                StringBuilder fileName=new StringBuilder(directoryPath).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
                return fileName.toString();
            }else{

                Log.w(TAG,"Failed to create external directory");
            }

        }else{

            StringBuilder fileName=new StringBuilder(context.getFilesDir().getAbsolutePath()).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
            return fileName.toString();
        }

        return null;
    }

    @Override
    public boolean shouldStoreAppDataInExternalStorage() {
        return true;
    }

    @Override
    public boolean isDBEnable() {
        return true;
    }


    @Override
    public boolean isDebugOn() {
        return true;
    }

    @Override
    public boolean isHttpAccessAllow() {
        return false;
    }



    @Override
    public boolean isCrashHandlerEnable() {
        return false;
    }

    @Override
    public boolean isSharedPrefrenceEnable() {
        return true;
    }

    @Override
    public int getDefaultImageResource() {
        return R.drawable.ic_launcher;
    }

    @Override
    public String getDatabaseSQL() {
        return "amapp" + ".sql";
    }

    public String getFacebookAppId(){
        return "431844313570473";
    }


    @Override
    public void onInstalling(SmartApplication smartApplication) {
        Toast.makeText(smartApplication, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrading(int oldVersion, int newVersion, SmartApplication smartApplication) {
        Toast.makeText(smartApplication, "Old Version = " + oldVersion + ", New version = " + newVersion, Toast.LENGTH_SHORT).show();
    }

}
