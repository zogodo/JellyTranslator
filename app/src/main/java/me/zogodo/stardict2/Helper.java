package me.zogodo.stardict2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import me.zogodo.android.AndroidHelp;

import java.util.ArrayList;
import java.util.HashMap;

public class Helper extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);
        updateListView();
    }

    public void updateListView()
    {
        Cursor cursor = AllDict.db.rawQuery("select dict_name, selected, type_name||' '||word_count as dict_info, down_src " +
                "from v_my_dict order by id desc", null);

        ListView lv = (ListView) findViewById(R.id.listView3);//得到ListView对象的引用, 为ListView设置Adapter来绑定数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
        final ArrayList<String> srclist = new ArrayList<>();
        int i = 0, position = 0;
        while (cursor.moveToNext())
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("dict_name", cursor.getString(cursor.getColumnIndex("dict_name")));
            map.put("dict_info", cursor.getString(cursor.getColumnIndex("dict_info")));
            srclist.add(cursor.getString(cursor.getColumnIndex("down_src")));
            listItem.add(map);
            if (cursor.getInt(cursor.getColumnIndex("selected")) == 1) {
                position = i;
            }
            i++;
        }
        SimpleAdapter mSimpleAdapter = new SimpleAdapter(this,
                listItem, R.layout.simple_list_item_2, new String[]{"dict_name", "dict_info"},
                new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(mSimpleAdapter);

        // lv.getChildAt(position).setBackgroundColor(Color.BLUE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            //单击ListView Item
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                parent.getChildAt(position).setBackgroundColor(Color.BLUE);
                Toast.makeText(getApplicationContext(), "长按选择字典", Toast.LENGTH_SHORT).show();
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            //长按ListView Item
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {

                return true;
            }
        });
    }
}
