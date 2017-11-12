package me.zogodo.stardict;

import android.preference.ListPreference;
import android.preference.Preference;
import android.os.Bundle;
import me.zogodo.android.AppCompatPreferenceActivity;
import me.zogodo.tools.FileName;

import java.io.*;
import java.util.ArrayList;

public class MyDict extends AppCompatPreferenceActivity
{
    static ArrayList<String> cn_dic_names;
    static ArrayList<String> cn_dic_values;
    static ArrayList<String> en_dic_names;
    static ArrayList<String> en_dic_values;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        //英文字典列表
        ListPreference lst_pre1 = (ListPreference)findPreference("en_dic");
        setListPreference(lst_pre1, "en");

        //中文字典列表
        ListPreference lst_pre2 = (ListPreference)findPreference("cn_dic");
        setListPreference(lst_pre2, "cn");
    }

    String getDicName(File dir, ArrayList<String> dic_values)
    {
        File[] dic_file3 = dir.listFiles();
        if(dic_file3.length <= 0)
        {
            return dir.getName();
        }
        dic_values.set(
                dic_values.size() - 1,
                dic_values.get(dic_values.size() - 1) + "/"
                    + FileName.removeExtension(dic_file3[0].getName())
        );

        for(File ifo_file : dic_file3)
        {
            if(ifo_file.getName().endsWith(".ifo"))
            {
                try (BufferedReader br = new BufferedReader(new FileReader(ifo_file)))
                {
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        if(line.startsWith("bookname="))
                        {
                            return line.replace("bookname=", "");
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return dir.getName();
    }

    void setListPreference(ListPreference lst_pre, String type)
    {
        ArrayList<String> dic_names;
        ArrayList<String> dic_values;
        String dic_file_name;
        if (type.equals("en"))
        {
            dic_names = en_dic_names = new ArrayList<>();
            dic_values = en_dic_values = new ArrayList<>();
            dic_file_name = "e2c_dic";
        }
        else
        {
            dic_names = cn_dic_names = new ArrayList<>();
            dic_values = cn_dic_values = new ArrayList<>();
            dic_file_name = "c2e_dic";
        }

        try
        {
            File[] files = new File(MainActivity.sd_data_dic_dir + "/" + type).listFiles();

            for (File file : files)
            {
                if (file.isDirectory())
                {
                    dic_values.add(file.getName());
                    dic_names.add(getDicName(file, dic_values));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        lst_pre.setEntries(dic_names.toArray(new String[0]));
        lst_pre.setEntryValues(dic_values.toArray(new String[0]));
        lst_pre.setDefaultValue("default/" + dic_file_name);

        lst_pre.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                return false;
            }
        });
    }

}
