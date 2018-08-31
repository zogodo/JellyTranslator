package me.zogodo.stardict2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import me.zogodo.android.AndroidHelp;
import me.zogodo.sqlite.SqliteHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DownloadDict extends AppCompatActivity
{
    public static String DB_NAME;
    public static String DB_PATH;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dict);

        DB_NAME = "dictionary.db";
        DB_PATH = MainActivity.sd_data_dir + "/Documents/" + DB_NAME; // 存放路径
        db = SqliteHelper.getDB(this, DB_PATH);
        updateListView(1);
    }

    private static SQLiteDatabase db;

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu2, menu);
        final MenuItem item_s = menu.findItem(R.id.dic_types);
        Spinner dic_types = (Spinner) MenuItemCompat.getActionView(item_s);
        ArrayList<String> list1 = new ArrayList<>();
        final ArrayList<Integer> list2 = new ArrayList<>();
        Cursor cursor = db.rawQuery("select id, type_name from dict_type", null);
        while (cursor.moveToNext())
        {
            list1.add(cursor.getString(cursor.getColumnIndex("type_name")));
            list2.add(cursor.getInt(cursor.getColumnIndex("id")));
        }
        SpinnerAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list1);
        dic_types.setAdapter(adapter);
        dic_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                Integer type_id = list2.get(position);
                updateListView(type_id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                // your code here
            }

        });
        return true;
    }

    public void updateListView(int dict_type_id)
    {
        Cursor cursor = db.rawQuery("select dict_name, type_name||' '||word_count as dict_info, down_src " +
                "from v_all_dict where dict_type_id="+dict_type_id, null);

        ListView lv = (ListView) findViewById(R.id.listView2);//得到ListView对象的引用, 为ListView设置Adapter来绑定数据
        /*
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.simple_list_item_2,
                cursor, new String[]{"dict_name", "dict_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(adapter);
        */
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        final ArrayList<String> srclist = new ArrayList<>();
        while (cursor.moveToNext())
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dict_name", cursor.getString(cursor.getColumnIndex("dict_name")));
            map.put("dict_info", cursor.getString(cursor.getColumnIndex("dict_info")));
            srclist.add(cursor.getString(cursor.getColumnIndex("down_src")));
            listItem.add(map);
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dict_name", "dict_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //单击ListView Item
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "长按下载此字典", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            //长按ListView Item
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                String file_url = srclist.get(position);
                AndroidHelp.me.downloadFileToDataPath(DownloadDict.this, file_url, "dict");
                return true;
            }
        });
    }

}
