package me.zogodo.stardict2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        /*
        try
        {
            cn_dic_list = AndroidHelp.me.readTextFileByLinesFromRaw(this, R.raw.cn_dic_list);
            updateListView();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        */

        String PACKAGE_NAME = getPackageName();//这个包名是你的应用程序在DDMS中file system中data下面的包名，这个位置容易出错，会写成当前的包
        String DB_NAME = "dictionary.db";//这个就是你要创建存到/data下的数据库的名字
        String DB_PATH = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/files/Documents/" + DB_NAME ; // 存放路径
        db = this.openDateBase(DB_PATH);
        updateListView2();
    }

    private String[] cn_dic_list = null;

    private SQLiteDatabase db;

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

    public void updateListView2()
    {
        Cursor cursor = db.rawQuery("select rowid _id, dict_name, type_name||' '||word_count as dict_info from v_all_dict", null);

        ListView lv = (ListView) findViewById(R.id.listView2);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.simple_list_item_2,
                cursor, new String[]{"dict_name", "dict_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(adapter);

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
                String file_url = "";
                AndroidHelp.me.downloadFileToDataPath(AllDict.this, file_url, "dict/cn");
                return true;
            }
        });
    }

    private void updateListView() throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView2);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 333; i += 3)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dic_name", cn_dic_list[i]);
            map.put("dic_info", cn_dic_list[i + 2]);
            listItem.add(map);
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dic_name", "dic_info"},
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
                String dic_name = cn_dic_list[position * 3];
                String file_url = cn_dic_list[position * 3 + 1];
                DownloadDict(dic_name, file_url);
                return true;
            }
        });
    }

    private long DownloadDict(String dic_name, String file_url)
    {
        //下载字典文件
        if (dic_name.contains("汉") && !dic_name.contains("英汉"))
        {
            //汉语词典
            return AndroidHelp.me.downloadFileToDataPath(this, file_url, "dict/cn");
        }
        else
        {
            //英语词典
            return AndroidHelp.me.downloadFileToDataPath(this, file_url, "dict/en");
        }
    }

}
