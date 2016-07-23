package com.zogodo.myempty;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String PACKAGE_NAME = getPackageName(); //包名
        String DB_NAME = "dictionary.db"; //Sqlite数据库文件名
        String APP_DOC_PATH = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/files/Documents/";//文件夹名
        String DB_PATH = APP_DOC_PATH + DB_NAME ; //Sqlite数据库文件存放路径
        db = this.openDateBase(DB_PATH);

        long Time0 = System.currentTimeMillis();
        String file_path = "/sdcard/Android" + Environment.getDataDirectory().getAbsolutePath() + "/" + PACKAGE_NAME + "/files/Documents/" + "oxfordjm-ec.txt";
        file_string = FileString.readFileByLines(file_path, 142367);
        long Time1 = System.currentTimeMillis();
        Log.v("aaaaa6", new Long(Time1 - Time0).toString());

        //cursor.close();
        //db.close();
    }

    private String[] file_string;
    private SQLiteDatabase db;
    private SQLiteDatabase openDateBase(String dbFile) {
        //传递进来数据库文件名
        File file = new File(dbFile);
        if (!file.exists()) {
            //如果该文件在你的/data下面不存在，那么我们就需要从资源文件中去加载它，就是写进去
            File file4 = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);//新建/sdcard/Android/data/Package Name/files/Documents文件夹
            //打开raw中得数据库文件，获得stream流
            InputStream stream = getResources().openRawResource(R.raw.dictionary);
            try {
                FileOutputStream outputStream = new FileOutputStream(dbFile);//把输出流写入到文件dbFile中
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = stream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, count);//把输入流的内容写到输出流中，
                }
                outputStream.close();
                stream.close();
                return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
            }
            catch (FileNotFoundException e) { //文件没有找到的异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) { // 输入输出异常
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    public void updateListView(String tran) {
        if (tran.length() == 0) {
            tran = " ";
        }
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery(
                "SELECT rowid _id, word, substr(replace(meaning, '\\n', ' '), 1, 100) as meaning FROM e2cm where word like '" + tran + "%' limit 100", null);

//        long startTime = System.currentTimeMillis();   //获取开始时间
//        String[][] str_arr =  Cursor2Sreing(cursor);
//        long endTime = System.currentTimeMillis(); //获取结束时间
//        //System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
//        long time = endTime-startTime;


        ListView lv = (ListView)findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.simple_list_item_2,
                cursor, new String[]{"word","meaning"},
                new int[]{R.id.text1,R.id.text2});
        lv.setAdapter(adapter);
        long endTime = System.currentTimeMillis();
        long time = endTime-startTime;
        Log.v(new Long(startTime).toString(), new Long(time).toString());
    }

    public void updateListView2(String tran) {
        if (tran.length() == 0) {
            return;
        }

        //long Time1 = System.currentTimeMillis();
        //tran = "look";
        int start = FileString.GetSimilarWordsStart(tran, file_string);
        //long Time2 = System.currentTimeMillis();
        //Log.v("aaaaa7", new Long(Time2 - Time1).toString());

        String[] similar_word = new String[100];
        for (int i = 0; i < 100; i++)
        {
            similar_word[i] = file_string[start + i];
        }
        //long Time3 = System.currentTimeMillis();
        //Log.v("aaaaa8", new Long(Time3 - Time2).toString());



        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();/*在数组中存放数据*/
        for(int i = 0;i < 10; i++)
        {
            String[] WordList = GetWordList(file_string[start + i]);
            if (WordList.length == 3)
            {
                WordList[1] = WordList[2];
            }
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("word", WordList[0]);
            map.put("meaning", WordList[1]);
            listItem.add(map);
        }


        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,listItem,//需要绑定的数据
                R.layout.simple_list_item_2,//每一行的布局//动态数组中的数据源的键对应到定义布局的View中
                new String[] {"word","meaning"},
                new int[]{R.id.text1,R.id.text2}
        );

        ListView lv = (ListView)findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
        lv.setAdapter(mSimpleAdapter);


//        ListView lv = (ListView)findViewById(R.id.listView);//得到ListView对象的引用 /*为ListView设置Adapter来绑定数据*/
//        lv.setAdapter(new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, similar_word));
    }

    public String[] GetWordList(String file_line)
    {
        String[] WordList = file_line.split("<p>|</p>|<np>");
        return WordList;
    }

    public String[][] Cursor2Sreing(Cursor cursor){
        String[][] str_arr = new String[cursor.getCount()][2];
        for (int i = 0; cursor.moveToNext(); i++) {
            str_arr[i][0] = cursor.getString(cursor.getColumnIndex("word"));
            str_arr[i][1] = cursor.getString(cursor.getColumnIndex("meaning"));
        }
        return str_arr;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        //getMenuInflater().inflate(R.menu.options_menu, menu);

        final MenuItem item_s = menu.findItem(R.id.search);
        SearchView search_view = (SearchView) MenuItemCompat.getActionView(item_s);
        search_view.setIconified(false);  //默认展开
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //输入完成后，点击回车或是完成键
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    Log.e("onQueryTextSubmit","我是点击回车按钮");
                }
                return true;
            }
            //查询文本框有变化时事件
            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange","我是内容改变");
                updateListView2(newText);
                return false;
            }
        });

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Toast.makeText(this, "关于", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
