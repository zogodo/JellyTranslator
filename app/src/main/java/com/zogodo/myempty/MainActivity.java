package com.zogodo.myempty;

import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String APP_DOC_PATH = "/sdcard/Android"
                + Environment.getDataDirectory().getAbsolutePath() //数据文件夹路径
                + "/" + getPackageName() //包名
                + "/files/Documents/";//文件夹名
        String ec_idx_file = APP_DOC_PATH + "oxfordjm-ec.idx";
        String ec_dic_file = APP_DOC_PATH + "oxfordjm-ec.dict";
        int ec_word_count = 142367;
        String ce_idx_file = APP_DOC_PATH + "langdao-ce-gb.idx";
        String ce_dic_file = APP_DOC_PATH + "langdao-ce-gb.dict";
        int ce_word_count = 405719;

        try
        {
            ec_dic = new StarDict(ec_idx_file, ec_dic_file, ec_word_count);
            ce_dic = new StarDict(ce_idx_file, ce_dic_file, ce_word_count);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    StarDict ec_dic;
    StarDict ce_dic;
    StarDict dic_now;

    public void updateListView(String tran) throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView);

        if (tran.length() == 0)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        dic_now = ec_dic;
        int start = dic_now.GetWordStart(tran);
        if (start == -1)
        {
            lv.removeAllViewsInLayout();
            dic_now = ce_dic;
        }
        start = dic_now.GetWordStart(tran);
        if (start == -1)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        int i = 0;
        byte[] word_byte = new byte[48];
        System.arraycopy(dic_now.index_file_align, start + i*56, word_byte, 0, 48);
        String word = new String(word_byte, StandardCharsets.UTF_8).trim();

        while(i < 100 && word.indexOf(tran) == 0)
        {
            String meaning = dic_now.GetMeaningOfWord(start + i*56);

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("word", word);
            map.put("meaning", meaning);
            listItem.add(map);
            i++;

            System.arraycopy(dic_now.index_file_align, start + i*56, word_byte, 0, 48);
            word = new String(word_byte, StandardCharsets.UTF_8).trim();
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"word", "meaning"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        final MenuItem item_s = menu.findItem(R.id.search);
        SearchView search_view = (SearchView) MenuItemCompat.getActionView(item_s);
        search_view.setIconified(false);  //默认展开 SearchView
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (query.length() > 0)
                {
                    Log.e("onQueryTextSubmit", "我是点击回车按钮");
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                try
                {
                    updateListView(newText);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                return false;
            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.about:
                Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
