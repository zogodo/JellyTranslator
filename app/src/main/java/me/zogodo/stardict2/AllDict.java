package me.zogodo.stardict2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import me.zogodo.android.AndroidHelp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class AllDict extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dict);

        String PACKAGE_NAME = getPackageName();//这个包名是你的应用程序在DDMS中file system中data下面的包名，这个位置容易出错，会写成当前的包
        String DB_NAME = "dictionary.db";//这个就是你要创建存到/data下的数据库的名字
        String DB_PATH = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/files/Documents/" + DB_NAME; // 存放路径
        db = this.openDateBase(DB_PATH);
        updateListView(1);
    }

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

    public static SQLiteDatabase db;

    private SQLiteDatabase openDateBase(String db_file_path)
    {
        //传递进来的是一个数据库文件名: 这个文件就是你要在/data下面存储的数据库名
        File file = new File(db_file_path);//打开这个文件
        if (!file.exists())
        {
            InputStream stream = getResources().openRawResource(R.raw.stardict);
            try
            {
                //新建/sdcard/Android/data/Package Name/files/Documents文件夹
                File file4 = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                FileOutputStream outputStream = new FileOutputStream(db_file_path);
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = stream.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, count);
                }
                outputStream.close();
                stream.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(db_file_path, null);
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
                AndroidHelp.me.downloadFileToDataPath(AllDict.this, file_url, "dict");
                return true;
            }
        });
    }

}
