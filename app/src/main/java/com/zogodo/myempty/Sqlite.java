package com.zogodo.myempty;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zogod on 2016/7/23.
 */
public class Sqlite
{
    private SQLiteDatabase db;

    private SQLiteDatabase openDateBase(String dbFile)
    {
        //传递进来数据库文件名
        File file = new File(dbFile);
        if (!file.exists())
        {
//            //如果该文件在你的 / data下面不存在，那么我们就需要从资源文件中去加载它，就是写进去
//            File file4 = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//新建/sdcard/Android/data/Package Name/files/Documents文件夹
//            //打开raw中得数据库文件，获得stream流
//            InputStream stream = getResources().openRawResource(R.raw.dictionary);
//            try
//            {
//                FileOutputStream outputStream = new FileOutputStream(dbFile);//把输出流写入到文件dbFile中
//                byte[] buffer = new byte[1024];
//                int count = 0;
//                while ((count = stream.read(buffer)) > 0)
//                {
//                    outputStream.write(buffer, 0, count);//把输入流的内容写到输出流中，
//                }
//                outputStream.close();
//                stream.close();
//                return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
//            } catch (FileNotFoundException e)
//            { //文件没有找到的异常
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e)
//            { // 输入输出异常
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        } return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    public void updateListView(String tran)
    {
        if (tran.length() == 0)
        {
            tran = " ";
        }
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery("SELECT rowid _id, word, substr(replace(meaning, '\\n', ' '), 1, 100) as meaning FROM e2cm where word like '" + tran + "%' limit 100", null);

//        ListView lv = (ListView) findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
//        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.simple_list_item_2, cursor, new String[]{"word", "meaning"}, new int[]{R.id.text1, R.id.text2});
//        lv.setAdapter(adapter);
//        long endTime = System.currentTimeMillis();
//        long time = endTime - startTime;
//        Log.v(new Long(startTime).toString(), new Long(time).toString());
    }

    public String[][] Cursor2Sreing(Cursor cursor)
    {
        String[][] str_arr = new String[cursor.getCount()][2];
        for (int i = 0; cursor.moveToNext(); i++)
        {
            str_arr[i][0] = cursor.getString(cursor.getColumnIndex("word"));
            str_arr[i][1] = cursor.getString(cursor.getColumnIndex("meaning"));
        }
        return str_arr;
    }
}
