package smart.common;

import java.util.HashMap;

/**
 * This Class Contains All Method Related To IjoomerScreenHolder.
 *
 * @author tasol
 */
public class IjoomerScreenHolder {

    public static HashMap<String, String> originalScreens = new HashMap<String, String>() {
        {
            put("Login", "com.WeWe.src.IjoomerLoginActivity");
            put("Registration", "com.WeWe.src.IjoomerRegistrationStep1Activity");
            put("Home", "com.WeWe.Jomsocial.JomGroupsActivity");
            put("Web", "com.smart.common.IjoomerWebClientActivity");
            put("JomAlbums", "com.WeWe.Jomsocial.JomAlbumsActivity");
            put("JomVideo", "com.WeWe.Jomsocial.JomVideosActivity");
            put("JomPrivacySetting", "com.WeWe.Jomsocial.JomPreferencesActivity");
            put("JomEvent", "com.WeWe.Jomsocial.JomEventsActivity");
            put("JomFriendList", "com.WeWe.Jomsocial.JomHomeActivity");
            put("JomGroup", "com.WeWe.Jomsocial.JomGroupsActivity");
            put("JomMessage", "com.WeWe.Jomsocial.JomHomeActivity");
            put("JomProfile", "com.WeWe.Jomsocial.JomProfileActivity");
            put("JomActivities", "com.WeWe.Jomsocial.JomHomeActivity");
            put("JomAdvanceSearch", "com.WeWe.Jomsocial.JomAdvanceSearchActivity");
        }
    };

    public static HashMap<String, String> aliasScreens = new HashMap<String, String>() {
        {
            put("IjoomerWebClientActivity", "Web");
            put("IjoomerLoginActivity", "Login");
            put("IjoomerRegistrationStep1Activity", "Registration");
            put("JomGroupsActivity", "Home");
            put("JomAlbumsActivity", "JomAlbums");
            put("JomVideosActivity", "JomVideo");
            put("JomPreferencesActivity", "JomPrivacySetting");
            put("JomEventsActivity", "JomEvent");
            put("JomHomeActivity", "JomFriendList");
            put("JomGroupsActivity", "JomGroup");
            put("JomHomeActivity", "JomMessage");
            put("JomProfileActivity", "JomProfile");
            put("JomHomeActivity", "JomActivities");
            put("JomAdvanceSearchActivity", "JomAdvanceSearch");
        }
    };
}
