package me.zogodo.stardict;

import android.preference.ListPreference;
import android.preference.Preference;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import me.zogodo.android.AppCompatPreferenceActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyDict extends AppCompatPreferenceActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_dict);

        // try
        // {
        //     updateListView();
        // }
        // catch (IOException e)
        // {
        //     e.printStackTrace();
        // }

        try
        {
            addPreferencesFromResource(R.xml.settings);
        }
        catch (Exception e)
        {

        }

        final ListPreference listPreference = (ListPreference) findPreference("language");

        // THIS IS REQUIRED IF YOU DON'T HAVE 'entries' and 'entryValues' in your XML
        setListPreferenceData(listPreference);

        listPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                setListPreferenceData(listPreference);
                return false;
            }
        });
    }

    public void updateListView() throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView3);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        String[] my_dic_list = MainActivity.e2c_dic.bookname.split("\\|");
        String[] my_dic_list_info = MainActivity.e2c_dic.description.split("\\|");
        for (int i = 0; i < my_dic_list.length; i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dic_name", my_dic_list[i]);
            map.put("dic_info", my_dic_list_info[i]);
            listItem.add(map);
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dic_name", "dic_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);
    }

    protected static void setListPreferenceData(ListPreference lp)
    {
        CharSequence[] entries = {"English", "French"};
        CharSequence[] entryValues = {"1", "2"};
        lp.setEntries(entries);
        lp.setDefaultValue("1");
        lp.setEntryValues(entryValues);
    }

}
