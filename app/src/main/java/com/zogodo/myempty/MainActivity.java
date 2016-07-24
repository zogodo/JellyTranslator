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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String PACKAGE_NAME = getPackageName(); //包名
        String DB_NAME = "dictionary.db"; //Sqlite数据库文件名
        String APP_DOC_PATH = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/files/Documents/";//文件夹名

        String file_path = APP_DOC_PATH + "oxfordjm-ec.txt";
        file_string = FileString.readFileByLines(file_path, 142367);

    }

    private String[] file_string;

    public void updateListView2(String tran)
    {
        ListView lv = (ListView) findViewById(R.id.listView);
        int start = FileString.GetSimilarWordsStart(tran, file_string);

        if (tran.length() == 0 || start == -1)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 100 && (tran + "zzz").compareTo(file_string[start + i]) >= 0; i++)
        {
            String[] WordList = GetWordList(file_string[start + i]);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("word", WordList[0]);
            map.put("meaning", WordList[1]);
            listItem.add(map);
        }

        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this, listItem, R.layout.simple_list_item_2, new String[]{"word", "meaning"}, new int[]{R.id.text1, R.id.text2});

        lv.setAdapter(mSimpleAdapter);
    }

    public String[] GetWordList(String file_line)
    {
        String[] WordList = file_line.split("<p>|</p>|<np>");
        if (WordList.length == 3)
        {
            WordList[1] = WordList[2];
        }
        return WordList;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        //getMenuInflater().inflate(R.menu.options_menu, menu);

        final MenuItem item_s = menu.findItem(R.id.search);
        SearchView search_view = (SearchView) MenuItemCompat.getActionView(item_s);
        search_view.setIconified(false);  //默认展开
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            //输入完成后，点击回车或是完成键
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (query.length() > 0)
                {
                    Log.e("onQueryTextSubmit", "我是点击回车按钮");
                }
                return true;
            }

            //查询文本框有变化时事件
            @Override
            public boolean onQueryTextChange(String newText)
            {
                //Log.e("onQueryTextChange","我是内容改变");
                updateListView2(newText);
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
