package com.zogodo.myempty.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.zogodo.myempty.R;
import com.zogodo.myempty.cmd.StarDictWord;
import com.zogodo.myempty.cmd.StarDict;

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

        //All dictionary
        String ec_idx_file = dic_path + "/all_dic.idx";
        String ec_dic_file = dic_path + "/all_dic.dict";
        String ec_ifo_file = dic_path + "/all_dic.ifo";

        File dic_path_file = new File(dic_path);
        if (!dic_path_file.exists())
        {
            dic_path_file.mkdir();
            try
            {
                WriteDicFile(ec_idx_file, R.raw.all_idx);
                WriteDicFile(ec_dic_file, R.raw.all_dic);
                WriteDicFile(ec_ifo_file, R.raw.all_ifo);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Long time0 = System.currentTimeMillis();
        try
        {
            ec_dic = new StarDict(dic_path + "/", "all_dic");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.e("22222", ((Long)(System.currentTimeMillis() - time0)).toString());
    }

    protected void onRestart() {
        super.onRestart();
        SearchView search_view = (SearchView)findViewById(R.id.search);
        //search_view.setQuery(word_now.word, false);
    }

    public static StarDict ec_dic;
    public static String sd_dic_path = "/mnt/sdcard/Android/data/com.zogodo.jelly/files/dict/";
    StarDictWord word_now = new StarDictWord();

    public void updateListView(String tran) throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView);

        if (tran.length() == 0)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        word_now.index = ec_dic.GetWordStart(tran);
        if (word_now.index == -1)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        int i = 0;
        word_now.word_byte = new byte[StarDict.word_width];
        System.arraycopy(ec_dic.index_file_align, word_now.index + i*StarDict.index_width, word_now.word_byte, 0, StarDict.word_width);
        String word = new String(word_now.word_byte, StandardCharsets.UTF_8).trim();

        while(i < 100 && word.toLowerCase().indexOf(tran) == 0)
        {
            String meaning = ec_dic.GetMeaningOfWord(word_now.index + i*StarDict.index_width);
            meaning = meaning.replaceAll("\\s+", " ");
            //meaning = meaning.replaceAll("^t(.+?)m", "[ $1 ] ");
            //meaning = meaning.replaceAll("^m", "");
            //meaning = meaning.replaceAll(" ([a-z]{1,7}\\.)", "\n$1 ");
            meaning = meaning.replaceAll("^(.{1,13}?)\0", "[ $1 ] ");

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("word", word);
            map.put("meaning", meaning);
            listItem.add(map);
            i++;

            System.arraycopy(ec_dic.index_file_align, word_now.index + i*StarDict.index_width, word_now.word_byte, 0, StarDict.word_width);
            word = new String(word_now.word_byte, StandardCharsets.UTF_8).trim();
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"word", "meaning"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"你单击的是第"+(position+1)+"条数据",Toast.LENGTH_SHORT).show();

                Class<?> aClass = parent.getItemAtPosition(position).getClass();
                HashMap word_meaning = (HashMap)parent.getItemAtPosition(position);
                word_now.word = word_meaning.get("word").toString();
                word_now.meaning = word_meaning.get("meaning").toString();

                Intent intent = new Intent(MainActivity.this, WordDetail.class);
                intent.putExtra("word", word_now.word);
                intent.putExtra("meaning", word_now.meaning);
                //打开新窗口
                startActivity(intent);

                setResult(RESULT_OK,intent);
                //finish();
            }
        });
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
        Intent intent = null;
        switch (item.getItemId())
        {
            case R.id.help:
                intent = new Intent(MainActivity.this, Helper.class);
                startActivity(intent);
                setResult(RESULT_OK,intent);
                return true;
            case R.id.about:
                //Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
                setResult(RESULT_OK,intent);
                return true;
            case R.id.downloadDict:
                intent = new Intent(MainActivity.this, AllDict.class);
                //intent.putExtra("all_dic", ec_dic);
                startActivity(intent);
                setResult(RESULT_OK,intent);
                return true;
            case R.id.myDict:
                intent = new Intent(MainActivity.this, MyDict.class);
                startActivity(intent);
                setResult(RESULT_OK,intent);
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
