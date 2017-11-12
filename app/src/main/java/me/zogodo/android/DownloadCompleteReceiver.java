/**
 * Created by zogod on 2016/11/27.
 */
package me.zogodo.android;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;
import me.zogodo.stardict.MainActivity;
import me.zogodo.tools.FileName;
import me.zogodo.tools.LinuxCmd;

public class DownloadCompleteReceiver extends BroadcastReceiver
{
    private DownloadManager downloadManager;

    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            Query query = new Query();
            query.setFilterById(id);
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Cursor cursor = downloadManager.query(query);
            cursor.moveToNext();
            String file_name = cursor.getString(4);
            String file_path = cursor.getString(1);
            String dic_dir = FileName.getDir(file_path);

            cursor.close();

            //TODO 使用线程解压下载的文件
            try
            {
                String[] result = LinuxCmd.exportBz2FIle(file_path);
                String dic_name = GetDicFileName(result);
                // 重命名字典内容文件 .dz -> .gz
                String cmd = "mv " + dic_dir + "/" + result[1] + " "
                        + dic_dir + "/" + result[1].replace(".dict.dz", ".dict.gz");
                LinuxCmd.PerformCmd(cmd);
                // 解压字典内容文件
                cmd = MainActivity.busy_box_path + "gunzip " + dic_dir + "/"
                        + result[1].replace(".dict.dz", ".dict.gz");
                LinuxCmd.PerformCmd(cmd);
                Toast.makeText(context, "字典下载成功。", Toast.LENGTH_LONG).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public String GetDicFileName(String[] result)
    {
        String dic_name = result[1].replaceAll(result[0], "");
        if(dic_name.contains(".dict.dz"))
        {
            dic_name = dic_name.replaceAll(".dict.dz", "");
        }
        else if(dic_name.contains(".idx"))
        {
            dic_name = dic_name.replaceAll(".idx", "");
        }
        else if(dic_name.contains(".ifo"))
        {
            dic_name = dic_name.replaceAll(".ifo", "");
        }
        return dic_name;
    }
}
