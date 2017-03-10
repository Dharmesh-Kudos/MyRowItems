package smart.framework;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.tasol.myrowitems.R;

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
    public String getGCMProjectId() {
        return "725447998213";
    }

    @Override
    public String getCrashHandlerFileName() {
        return getAppName() + "log.file";
    }

    @Override
    public boolean IsCrashHandlerEnabled() {
        return false;
    }

    @Override
    public String getAppName() {
        return "WeWe";
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
        return "WeWe";
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

//        return "http://192.168.5.187/joomla_3.5.1/index.php?option=com_ijoomeradv&lang=en";
//        return "http://192.168.5.138/wewe/index.php?option=com_ijoomeradv&lang=en";// Local for Virtue Mart Vendor
//        return "http://192.168.5.144/wewe/index.php?option=com_ijoomeradv&lang=en";
//        return "https://matiz.websitewelcome.com/~tasolglo/dev/wewe/index.php?option=com_ijoomeradv";//For Mattis
        return "http://istage.website/wewe/index.php?option=com_ijoomeradv";//For ISTAGE

        //return "http://dev.tasolglobal.com/wewe/index.php?option=com_ijoomeradv&lang=en";
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
        return "fonts/Roboto-Regular.ttf";
    }

    @Override
    public String getBoldFontName() {
        return "fonts/Roboto-Bold.ttf";
    }

    @Override
    public String getGCMID() {
        return "265622724567";
    }

    @Override
    public String getGCMVersion() {
        return "4030500";
    }

    @Override
    public String getMapAPIKey() {
        return "AIzaSyAmf-z2ZOnfA5p7xLvkiYltjyTeY3_rBa8";
    }

    @Override
    public String getAppMetadataStoragePath(Context context, String extension) {
        if (shouldStoreAppDataInExternalStorage()) {
            String directoryPath = SmartUtils.createExternalDirectory(SmartUtils.removeSpecialCharacter(getAppName()));
            if (directoryPath != null) {
                StringBuilder fileName = new StringBuilder(directoryPath).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
                return fileName.toString();
            } else {
                Log.w(TAG, "Failed to create external directory");
            }
        } else {
            StringBuilder fileName = new StringBuilder(context.getFilesDir().getAbsolutePath()).append(File.separator).append(SmartUtils.removeSpecialCharacter(getAppName())).append(System.currentTimeMillis()).append(".").append(extension);
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
        return "WeWe" + ".sql";
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
