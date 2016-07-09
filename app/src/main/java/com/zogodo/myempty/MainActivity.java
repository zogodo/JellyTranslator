package com.zogodo.myempty;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "Hello world!", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        findViewById(R.id.button2).setOnClickListener(new HandleClick2());
//
//        findViewById(R.id.button3).setOnClickListener(handleClick3);
//
//        findViewById(R.id.button4).setOnClickListener(this);

        //InputStream db_file = getResources().openRawResource(R.raw.dictionary);

        String PACKAGE_NAME = getPackageName();//这个包名是你的应用程序在DDMS中file system中data下面的包名，这个位置容易出错，会写成当前的包
        String DB_NAME = "dictionary.db";//这个就是你要创建存到/data下的数据库的名字
        String DB_PATH = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/" + DB_NAME ; // 存放路径
        SQLiteDatabase db = this.openDateBase(DB_PATH);
        Cursor cursor = db.rawQuery("SELECT word, meaning FROM e2c where word=? or id=765", new String[]{"a"});
        int i = 0;
        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String meaning = cursor.getString(cursor.getColumnIndex("meaning"));
        }

        String[] strs = new String[] {
                "first", "second", "third", "fourth", "fifth"
        };
        ListView lv = (ListView)findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs));

//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
//                android.R.layout.expandable_list_content,
//                cursor, new String[]{"word","meaning"},
//                new int[]{R.id.listView,R.id.listView});
        //lv.setAdapter(adapter);

        cursor.close();
        db.close();
    }

    private SQLiteDatabase openDateBase(String dbFile)
    {//传递进来的是一个数据库文件名  ：这个文件就是你要在/data下面存储的数据库名
        File file = new File(dbFile);//打开这个文件
        if (!file.exists())
        {   //如果该文件在你的/data下面不存在，那么我们就需要从资源文件中去加载它，就是写进去
            // // 打开raw中得数据库文件，获得stream流
            InputStream stream = getResources().openRawResource(R.raw.dictionary);//这个资源索引就是我们存放的数据库
            try
            {
                //将获取到的stream 流写入道data中
                //我们获取的是一个数据库文件，这个如果你直接打开肯定是乱码，但是起始字段肯定是“SQLite format ”，这个字符串系统懂，它代表着数据库文件
                FileOutputStream outputStream = new FileOutputStream(dbFile);//我们把输出流写入到文件dbFile中去
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = stream.read(buffer)) > 0)
                {
                    outputStream.write(buffer, 0, count);//把输入流的内容写到输出流中，
                }
                outputStream.close();//关闭输出流
                stream.close();//关闭输入流
                SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
                return db;
            }
            catch (FileNotFoundException e)
            { //文件没有找到的异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e)
            { // 输入输出异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    private class HandleClick2 implements View.OnClickListener{
        public void onClick(View arg0) {
            Toast.makeText(getApplicationContext(), "Hello world 2 !", Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener handleClick3 = new View.OnClickListener(){
        public void onClick(View arg0) {
            Toast.makeText(getApplicationContext(), "Hello world 3 !", Toast.LENGTH_LONG).show();
        }
    };

    public void onClick(View arg0) {
        Toast.makeText(getApplicationContext(), "Hello world 4 !", Toast.LENGTH_LONG).show();
    }

    public void HandleClick5(View arg0) {
        Toast.makeText(getApplicationContext(), "Hello world 4 !", Toast.LENGTH_LONG).show();
    }
}
