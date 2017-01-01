/**
 * Created by zogod on 2016/11/27.
 */
package com.zogodo.myempty;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import java.io.IOException;

public class DownloadCompleteReceiver extends BroadcastReceiver
{
    private DownloadManager downloadManager;

    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            //TODO 判断这个id与之前的id是否相等，如果相等说明是之前的那个要下载的文件
            Query query = new Query();
            query.setFilterById(id);
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Cursor cursor = downloadManager.query(query);
            cursor.moveToNext();
            String file_name = cursor.getString(4);
            String file_path = cursor.getString(1);

            cursor.close();

            //解压下载的文件
            try
            {
                LinuxCmd.exportBz2FIle(file_path);
                StarDict add_dic = new StarDict(
                        "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/" + file_name + ".idx",
                        "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/" + file_name + ".dict",
                        "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/" + file_name + ".ifo");
                MainActivity.ec_dic.AddDic(add_dic);
                Toast.makeText(context, "字典添加成功。", Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
