package me.zogodo.stardict;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import me.zogodo.stardict.cmd.AndroidHelp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AllDict extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_dict);

        try
        {
            cn_dic_list = AndroidHelp.me.readTextFileByLinesFromRaw(this, R.raw.cn_dic_list);
            updateListView();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private String[] cn_dic_list = null;

    private void updateListView() throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView2);
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 333; i += 3)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dic_name", cn_dic_list[i]);
            map.put("dic_info", cn_dic_list[i + 2]);
            listItem.add(map);
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dic_name", "dic_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //单击ListView Item
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "长按下载此字典", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            //长按ListView Item
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                String dic_name = cn_dic_list[position*3];
                String file_url = cn_dic_list[position*3 + 1];
                DownloadDict(dic_name, file_url);
                return true;
            }
        });
    }

    private long DownloadDict(String dic_name, String file_url)
    {
        //下载字典文件
        return AndroidHelp.me.downloadFileToDataPath(this, file_url, "en_dic");
    }

}
