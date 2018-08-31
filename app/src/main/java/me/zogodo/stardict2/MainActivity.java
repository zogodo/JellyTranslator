package me.zogodo.stardict2;

import android.content.Intent;
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
import me.zogodo.android.AndroidHelp;
import me.zogodo.android.AppSetting;
import me.zogodo.tools.LinuxCmd;
import me.zogodo.dict.StarDictWord;
import me.zogodo.dict.StarDict;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    public static StarDict e2c_dic;
    public static StarDict c2e_dic;
    public static StarDict dic_now;
    StarDictWord word_now = new StarDictWord();

    public static String PACKAGE_NAME;        //应用包名
    public static String root_data_dir;       //数据文件夹
    public static String sd_data_dir;         //SD卡里的数据文件夹
    public static String sd_data_dic_dir;     //字典存放文件夹
    public static String busy_box_path;       //BusyBox路径

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File ext_dir = getExternalFilesDir(null);
        File ine_dir = getFilesDir();

        root_data_dir = ine_dir.getPath();  // /data/user/0/me.zogodo.stardict2/files
        sd_data_dir = ext_dir.getPath();    // /storage/emulated/0/Android/data/me.zogodo.stardict2/files
        sd_data_dic_dir = sd_data_dir + "/dict";
        busy_box_path = root_data_dir + "/cmd/busybox_armv7l";

        File dic_path_file = new File(root_data_dir + "/cmd");
        if (!dic_path_file.exists())
        {
            //第一次打开应用
            dic_path_file.mkdirs();
            new File(sd_data_dic_dir + "/default_e2c").mkdirs();
            new File(sd_data_dic_dir + "/default_c2e").mkdirs();
            try
            {
                //英汉
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_e2c/e2c_dic.idx", R.raw.e2c_idx);
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_e2c/e2c_dic.dict", R.raw.e2c_dic);
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_e2c/e2c_dic.ifo", R.raw.e2c_ifo);
                //汉英
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_c2e/c2e_dic.idx", R.raw.c2e_idx);
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_c2e/c2e_dic.dict", R.raw.c2e_dic);
                AndroidHelp.me.writeRawToFile(this, sd_data_dic_dir + "/default_c2e/c2e_dic.ifo", R.raw.c2e_ifo);
                //BusyBox
                AndroidHelp.me.writeRawToFile(this, busy_box_path, R.raw.busybox_armv7l);

                String cmd = "chmod 700 " + busy_box_path;
                LinuxCmd.getCmdReadLine(cmd);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        busy_box_path += " ";

        AppSetting setting = AppSetting.getSetting(this);

        Long time0 = System.currentTimeMillis();
        try
        {
            e2c_dic = new StarDict(sd_data_dic_dir + "/"+setting.en_dic_name+"/", setting.en_dic_file_name);
            c2e_dic = new StarDict(sd_data_dic_dir + "/"+setting.cn_dic_name+"/", setting.cn_dic_file_name);
            dic_now = e2c_dic;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Log.e("22222", ((Long)(System.currentTimeMillis() - time0)).toString());
    }

    protected void onRestart()
    {
        super.onRestart();
        SearchView search_view = (SearchView)findViewById(R.id.search);
        //search_view.setQuery(word_now.word, false);
    }

    void updateListView(String tran) throws IOException
    {
        ListView lv = (ListView) findViewById(R.id.listView);

        if (tran.length() == 0)
        {
            lv.removeAllViewsInLayout();
            return;
        }

        word_now.index = e2c_dic.GetWordStart(tran); //先查英译汉
        if (word_now.index == -1)
        {
            word_now.index = c2e_dic.GetWordStart(tran); //查汉译英
            if (word_now.index == -1)
            {
                lv.removeAllViewsInLayout();
                return;
            }
            dic_now = c2e_dic;
        }
        else
        {
            dic_now = e2c_dic;
        }

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        int i = 0;
        word_now.word_byte = new byte[StarDict.word_width];
        System.arraycopy(dic_now.index_file_align, word_now.index + i*StarDict.index_width, word_now.word_byte, 0, StarDict.word_width);
        String word = new String(word_now.word_byte, StandardCharsets.UTF_8).trim();

        while(i < 100 && word.toLowerCase().indexOf(tran) == 0)
        {
            String meaning = dic_now.GetMeaningOfWord(word_now.index + i*StarDict.index_width);
            meaning = meaning.replaceAll("[\0]", "\n");
            //meaning = meaning.replaceAll("\\s+", " ");
            //meaning = meaning.replaceAll("^t(.+?)m", "[ $1 ] ");
            //meaning = meaning.replaceAll("^m", "");
            //meaning = meaning.replaceAll(" ([a-z]{1,7}\\.)", "\n$1 ");
            //meaning = meaning.replaceAll("^(.{1,13}?)\0", "[ $1 ] ");

            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("word", word);
            map.put("meaning", meaning);
            listItem.add(map);
            i++;

            System.arraycopy(dic_now.index_file_align, word_now.index + i*StarDict.index_width, word_now.word_byte, 0, StarDict.word_width);
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
            case R.id.myDict:
                intent = new Intent(MainActivity.this, MyDict.class);
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
                intent = new Intent(MainActivity.this, DownloadDict.class);
                //intent.putExtra("dic_now", dic_now);
                startActivity(intent);
                setResult(RESULT_OK,intent);
                return true;
            case R.id.setting:
                intent = new Intent(MainActivity.this, Setting.class);
                startActivity(intent);
                setResult(RESULT_OK,intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
