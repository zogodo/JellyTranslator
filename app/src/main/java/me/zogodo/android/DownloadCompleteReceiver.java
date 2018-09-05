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
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import me.zogodo.sqlite.SqliteHelper;
import me.zogodo.stardict2.DownloadDict;
import me.zogodo.stardict2.MainActivity;
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
            int col = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String file_uri = cursor.getString(col);
            String file_path = Uri.parse(file_uri).getPath();
            String dic_dir = FileName.getDir(file_path);

            cursor.close();

            //TODO 使用线程解压下载的文件
            try
            {
                String[] result = LinuxCmd.exportBz2FIle(file_path);

                String dic_dz_name   = result[1];  // result[1] 是压缩文件名 xxx.dict.dz
                String dic_gz_name   = dic_dz_name.replace(".dict.dz", ".dict.gz");
                String dic_file_name = dic_dz_name.replace(".dict.dz", "");

                // 重命名字典内容文件 .dz -> .gz
                String cmd = "mv " + dic_dir + "/" + dic_dz_name + " " + dic_dir + "/" + dic_gz_name;
                LinuxCmd.getCmdReadLine(cmd);

                // 解压字典内容文件
                cmd = MainActivity.busy_box_path + "gunzip " + dic_dir + "/" + dic_gz_name;
                LinuxCmd.getCmdReadLine(cmd);

                Toast.makeText(context, "字典下载成功。", Toast.LENGTH_LONG).show();

                // 将字典存到数据库
                String sql = "insert into my_dict(dict_id, down_time, dict_path, selected) " +
                        "values(?, CURRENT_TIMESTAMP, ?, 0)";  // CURRENT_TIMESTAMP 是格林尼治时间
                SQLiteDatabase db = SqliteHelper.getDB(context, DownloadDict.DB_PATH);
                db.execSQL(sql, new Object[]{DownloadDict.sel_dict_id, dic_file_name});
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
