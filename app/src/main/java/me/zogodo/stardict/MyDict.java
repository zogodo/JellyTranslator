package me.zogodo.stardict;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import me.zogodo.stardict.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MyDict extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_dict);

        try
        {
            updateListView();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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
}
