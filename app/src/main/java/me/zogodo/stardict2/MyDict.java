package me.zogodo.stardict2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import me.zogodo.dict.StarDict;
import me.zogodo.sqlite.SqliteHelper;

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

        String DB_NAME = "dictionary.db";
        String DB_PATH = MainActivity.sd_data_dir + "/Documents/" + DB_NAME; // 存放路径
        db = SqliteHelper.getDB(this, DB_PATH);
        updateListView();
    }

    private static SQLiteDatabase db;

    int position = 0;
    ArrayList<HashMap<String, Object>> listItem;
    ArrayList<String> pathlist = new ArrayList<>();
    public void updateListView()
    {
        Cursor cursor = db.rawQuery(
                "select id, dict_name, selected, type_name, word_count, file_size, dict_path " +
                "from v_my_dict order by id desc", null);

        final ListView lv = (ListView) findViewById(R.id.listView3);
        listItem = new ArrayList<>();
        int i = 0;
        while (cursor.moveToNext())
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dict_name", cursor.getString(cursor.getColumnIndex("dict_name")));
            String type_name = cursor.getString(cursor.getColumnIndex("type_name"));
            String word_count = cursor.getString(cursor.getColumnIndex("word_count"));
            String file_size = cursor.getString(cursor.getColumnIndex("file_size"));
            map.put("dict_info", type_name + "  " + word_count + "词  " + file_size);
            map.put("dict_id", cursor.getInt(cursor.getColumnIndex("id")));
            pathlist.add(cursor.getString(cursor.getColumnIndex("dict_path")));
            listItem.add(map);
            if (cursor.getInt(cursor.getColumnIndex("selected")) == 1) {
                position = i;
            }
            i++;
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dict_name", "dict_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        // lv.getChildAt(position).setBackgroundColor(Color.BLUE);

        final ViewTreeObserver.OnPreDrawListener mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener()
        {
            @Override
            public boolean onPreDraw()
            {
                lv.getViewTreeObserver().removeOnPreDrawListener(this);
                // Do what you want to do on data loading here
                String color_string = "#99FFD7";
                int myColor = Color.parseColor(color_string);
                View item = lv.getChildAt(position);
                item.setBackgroundColor(myColor);
                return true;
            }
        };

        lv.getViewTreeObserver().addOnPreDrawListener(mOnPreDrawListener);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //单击ListView Item
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "长按选择字典", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            //长按ListView Item
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == MyDict.this.position) return true;
                String sql = "update my_dict set selected=0 where selected=1";
                db.execSQL(sql);
                int dict_id = (int)listItem.get(position).get("dict_id");
                sql = "update my_dict set selected=1 where id=" + dict_id;
                db.execSQL(sql);
                updateListView();
                //TODO MainActivity 换字典
                try
                {
                    String dict_path = pathlist.get(position);
                    String[] dict_path_arr = dict_path.split("/");
                    String dic_name = dict_path_arr[0];
                    String dic_file_name = dict_path_arr[1];
                    MainActivity.e2c_dic = new StarDict(MainActivity.sd_data_dic_dir + "/" + dic_name + "/", dic_file_name);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
        });
    }
}
