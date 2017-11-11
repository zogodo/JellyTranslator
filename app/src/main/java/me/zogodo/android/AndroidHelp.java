package me.zogodo.android;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
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

    public void writeRawToFile(Activity act, String file_save_path, int file_id) throws IOException
    {
        //将Raw中的文件写入指定目录
        File dic_file = new File(file_save_path);
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

    public long downloadFileToSdPath(Activity act, String file_url, String sd_path)
    {
        //下载文件到 SD 卡目录

        //获取文件名
        String[] url_split = file_url.split("/");
        String file_name = url_split[url_split.length - 1];

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file_url));
        request.setDestinationInExternalPublicDir(sd_path, file_name); //下载到SD卡的 sd_path/ 目录

        DownloadManager downloadManager= (DownloadManager) act.getSystemService(Context.DOWNLOAD_SERVICE);
        return downloadManager.enqueue(request);
    }

    public long downloadFileToDataPath(Activity act, String file_url, String data_path)
    {
        //下载文件到应用数据目录

        //获取文件名
        String[] url_split = file_url.split("/");
        String file_name = url_split[url_split.length - 1];

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(file_url));
        //下载到 /SD卡/Android/data/包名/files/data_path/ 里
        request.setDestinationInExternalFilesDir(act, data_path, file_name);

        DownloadManager downloadManager = (DownloadManager)act.getSystemService(Context.DOWNLOAD_SERVICE);
        //把id保存好，在接收者里面要用，最好保存在Preferences里面
        return downloadManager.enqueue(request);
    }

}
