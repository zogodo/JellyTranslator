package com.zogodo.myempty;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AllDict extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dict);

        findViewById(R.id.button2).setOnClickListener(new HandleClick1());
        findViewById(R.id.button3).setOnClickListener(new HandleClick2());

    }

    private class HandleClick1 implements View.OnClickListener
    {
        public void onClick(View arg0)
        {
            String file_url = "http://download.huzheng.org/zh_CN/stardict-stardict1.3-2.4.2.tar.bz2";
            DownloadDict(file_url);
        }
    }

    private class HandleClick2 implements View.OnClickListener
    {
        public void onClick(View arg0)
        {
            //解压...
            String dic_path = "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/";
            String cmd = "ls " + dic_path;
            cmd = "tar -xjvf "+dic_path+"stardict-stardict1.3-2.4.2.tar.bz2 -C "+dic_path;
            Runtime runtime = Runtime.getRuntime();
            try
            {
                //执行命令，并且获得Process对象
                Process process = runtime.exec(cmd);
                //获得结果的输入流
                InputStream input = process.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                String strLine;
                String str = "";
                while (null != (strLine = br.readLine()))
                {
                    System.out.println(strLine);
                    str += (strLine + "\n");
                }
                TextView test_text = (TextView)findViewById(R.id.textView);
                test_text.setText(str);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected long DownloadDict(String file_url)
    {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(file_url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        String[] url_split = file_url.split("/");
        String file_name = url_split[url_split.length - 1];
        //下载到 /mnt/sdcard/Android/data/packageName/files/dict/ 里
        request.setDestinationInExternalFilesDir(this, "dict", file_name);
        long down_id = downloadManager.enqueue(request);
        //TODO 把id保存好，在接收者里面要用，最好保存在Preferences里面
        return down_id;
    }
}
