package com.zogodo.myempty;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class AllDict extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dict);

        findViewById(R.id.button2).setOnClickListener(new HandleClick1());
        findViewById(R.id.button3).setOnClickListener(new HandleClick2());

        try
        {
            cn_dic_list = readTextFileByLinesFromRaw(R.raw.cn_dic_list);
            updateListView();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public String[] cn_dic_list = null;

    public void updateListView() throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView2);

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

        int i = 0;
        while(i < 333)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dic_name", cn_dic_list[i]);
            map.put("dic_info", cn_dic_list[i + 2]);
            listItem.add(map);
            i += 3;
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dic_name", "dic_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "长按下载此字典", Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                String file_url = cn_dic_list[position*3 + 1];
                DownloadDict(file_url);
                return true;
            }
        });
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

    public void exportBz2FIle(String bz2_file_name) throws IOException
    {
        //解压...
        String dic_path = "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/";
        String cmd = "ls " + dic_path;
        cmd = "tar -xjvf " + dic_path + bz2_file_name + " -C " + dic_path;
        Runtime runtime = Runtime.getRuntime();

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

    public String[] readTextFileByLinesFromRaw(int file_id) throws IOException
    {
        InputStream stream = getResources().openRawResource(file_id);
        InputStreamReader isReader = new InputStreamReader(stream, "UTF-8");
        //int file_lines = getFileLines(stream);
        int file_lines = 333;
        BufferedReader reader = new BufferedReader(isReader);
        String[] file_string = new String[file_lines];
        for (int i = 0; i < file_lines; i++)
        {
            file_string[i] = new String();
            file_string[i] = reader.readLine();
        }
        reader.close();
        return file_string;
    }

    public int getFileLines(InputStream stream) throws IOException
    {
        byte[] c = new byte[1024];
        int count = 0;
        int readChars = 0;
        while ((readChars = stream.read(c)) != -1)
        {
            for (int i = 0; i < readChars; ++i)
            {
                if (c[i] == '\n')
                {
                    ++count;
                }
            }
        }
        return count;
    }

}
