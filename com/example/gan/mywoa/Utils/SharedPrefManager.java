package com.example.gan.mywoa.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GUSWIK on 10/22/2017.
 */
public class SharedPrefManager {
    public static final String SP_USER_APP = "spUSERApp";

    public static final String SP_USERID = "spUserId";
    public static final String SP_USERNAME = "spUsername";
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_BIRTHDATE = "spBirthdate";
    public static final String SP_FULL_NAME = "spFullName";
    public static final String SP_PASSWORD = "spPassword";
    public static final String SP_NOTELP = "spNoTelp";

    public static final String SP_SUDAH_LOGIN = "spSudahLogin";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_USER_APP, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value){
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value){
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPUserId(){
        return sp.getString(SP_USERID, "");
    }

    public String getSPNama(){
        return sp.getString(SP_USERNAME, "");
    }
    public String getSPNoTelp(){
        return sp.getString(SP_NOTELP, "");
    }

    public String getSPEmail(){
        return sp.getString(SP_EMAIL, "");
    }
    public String getSPBirthDate(){
        return sp.getString(SP_BIRTHDATE, "");
    }
    public String getSPFullName(){
        return sp.getString(SP_FULL_NAME, "");
    }public String getSPPassword(){
        return sp.getString(SP_PASSWORD, "");
    }

    public Boolean getSPSudahLogin(){
        return sp.getBoolean(SP_SUDAH_LOGIN, false);
    }
}
