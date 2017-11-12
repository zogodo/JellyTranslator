package me.zogodo.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zogod on 17/11/12.
 */
public class AppSetting
{
    public static AppSetting me = null;
    public String en_dic_name;
    public String en_dic_file_name;
    public String cn_dic_name;
    public String cn_dic_file_name;

    private AppSetting(Context context)
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String[] en_dic_setting = settings.getString("en_dic", "default/e2c_dic").split("/");
        en_dic_name = en_dic_setting[0];
        en_dic_file_name = en_dic_setting[1];
        String[] cn_dic_setting = settings.getString("cn_dic", "default/c2e_dic").split("/");
        cn_dic_name = cn_dic_setting[0];
        cn_dic_file_name = cn_dic_setting[1];
    }

    public static AppSetting getSetting(Context context)
    {
        if (me != null)
        {
            return me;
        }
        return new AppSetting(context);
    }
}
