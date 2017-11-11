package me.zogodo.stardict.cmd;

import android.app.Activity;
import android.os.Environment;

import java.io.*;

/**
 * Created by zogod on 17/11/11.
 */
public class AndroidHelp
{
    public static AndroidHelp me = new AndroidHelp();

    private AndroidHelp(){}

    public boolean isExternalStorageWritable()
    {
        //判断是否有外置存储
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public String[] readTextFileByLinesFromRaw(Activity act, int file_id) throws IOException
    {
        //从raw中读取文本文件成String[]
        InputStream stream = act.getResources().openRawResource(file_id);
        InputStreamReader isReader = new InputStreamReader(stream, "UTF-8");
        //int file_lines = getFileLines(stream);
        int file_lines = 333;
        BufferedReader reader = new BufferedReader(isReader);
        String[] file_string = new String[file_lines];
        for (int i = 0; i < file_lines; i++)
        {
            file_string[i] = "";
            file_string[i] = reader.readLine();
        }
        reader.close();
        return file_string;
    }

    public void writeRawToFile(Activity act, String dic_file_name, int file_id) throws IOException
    {
        //将Raw中的文件写入指定目录
        File dic_file = new File(dic_file_name);
        InputStream stream = act.getResources().openRawResource(file_id);
        dic_file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(dic_file.getPath());
        byte[] buffer = new byte[1024];
        int count = 0;
        while ((count = stream.read(buffer)) > 0)
        {
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        stream.close();
    }
}
