package me.zogodo.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import me.zogodo.stardict2.R;

public class SqliteHelper
{
    private static SQLiteDatabase db;

    public static SQLiteDatabase getDB(Context context, String db_file_path)
    {
        if (SqliteHelper.db != null)
        {
            return SqliteHelper.db;
        }
        return SqliteHelper.openDateBase(context, db_file_path);
    }

    public static SQLiteDatabase openDateBase(Context context, String db_file_path)
    {
        //传递进来的是一个数据库文件名: 这个文件就是你要在/data下面存储的数据库名
        File file = new File(db_file_path);//打开这个文件
        if (!file.exists())
        {
            InputStream stream = context.getResources().openRawResource(R.raw.stardict);
            try
            {
                //新建/sdcard/Android/data/Package Name/files/Documents文件夹
                File file4 = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
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
}
