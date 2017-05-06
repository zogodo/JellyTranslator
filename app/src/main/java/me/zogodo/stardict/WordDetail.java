package me.zogodo.stardict;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import me.zogodo.stardict.R;

public class WordDetail extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        Bundle extras = getIntent().getExtras();
        String word = extras.getString("word");
        String meaning = extras.getString("meaning");

        TextView word_select = (TextView)findViewById(R.id.textView1);
        word_select.setText(word);

        TextView meaning_select = (TextView)findViewById(R.id.textView2);
        meaning_select.setText(meaning);
    }

}
