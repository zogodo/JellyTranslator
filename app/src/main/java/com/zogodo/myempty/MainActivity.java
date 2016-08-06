package com.zogodo.myempty;

import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
                + "/files/dic/";//文件夹名

        String PACKAGE_NAME = getPackageName();
        String packge_path = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"  + PACKAGE_NAME;
        String dic_path = packge_path + "/dic";

        //English to Chinese dictionary
        String ec_idx_file = dic_path + "/e2c_idx.idx";
        String ec_dic_file = dic_path + "/e2c_dic.dict";
        String ec_ifo_file = dic_path + "/e2c_ifo.ifo";
        //Chinese to English dictionary
        String ce_idx_file = dic_path + "/c2e_idx.idx";
        String ce_dic_file = dic_path + "/c2e_dic.dict";
        String ce_ifo_file = dic_path + "/c2e_ifo.ifo";

        File dic_path_file = new File(dic_path);
        if (!dic_path_file.exists())
        {
            dic_path_file.mkdir();
            try
            {
                WriteDicFile(ec_idx_file, R.raw.e2c_idx);
                WriteDicFile(ec_dic_file, R.raw.e2c_dic);
                WriteDicFile(ec_ifo_file, R.raw.e2c_ifo);
                WriteDicFile(ce_idx_file, R.raw.c2e_idx);
                WriteDicFile(ce_dic_file, R.raw.c2e_dic);
                WriteDicFile(ce_ifo_file, R.raw.c2e_inf);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Long time0 = System.currentTimeMillis();
        try
        {
            ec_dic = new StarDict(ec_idx_file, ec_dic_file, ec_ifo_file);
            ce_dic = new StarDict(ce_idx_file, ce_dic_file, ce_ifo_file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Log.e("22222", ((Long)(System.currentTimeMillis() - time0)).toString());
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
            start = dic_now.GetWordStart(tran);
            if (start == -1)
            {
                lv.removeAllViewsInLayout();
                return;
            }
        }

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        int i = 0;
        byte[] word_byte = new byte[48];
        System.arraycopy(dic_now.index_file_align, start + i*56, word_byte, 0, 48);
        String word = new String(word_byte, StandardCharsets.UTF_8).trim();

        while(i < 100 && word.toLowerCase().indexOf(tran) == 0)
        {
            String meaning = dic_now.GetMeaningOfWord(start + i*56);
            meaning = meaning.replaceAll(" *\n *", " ");
            //meaning = meaning.replaceAll("^t(.+?)m", "[ $1 ] ");
            //meaning = meaning.replaceAll("^m", "");
            //meaning = meaning.replaceAll(" ([a-z]{1,7}\\.)", "\n$1 ");
            meaning = meaning.replaceAll("^(.{1,13}?)\0", "[ $1 ] ");

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
                    updateListView(newText.toLowerCase());
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

    public void WriteDicFile(String dic_file_name, int file_id) throws IOException
    {
        File dic_file = new File(dic_file_name);
        InputStream stream = getResources().openRawResource(file_id);
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
