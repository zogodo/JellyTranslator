package com.zogodo.myempty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Hello world!", Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.button2).setOnClickListener(new HandleClick2());

        findViewById(R.id.button3).setOnClickListener(handleClick3);

        findViewById(R.id.button4).setOnClickListener(this);
    }

    private class HandleClick2 implements View.OnClickListener{
        public void onClick(View arg0) {
            Toast.makeText(getApplicationContext(), "Hello world 2 !", Toast.LENGTH_LONG).show();
        }
    }

    private View.OnClickListener handleClick3 = new View.OnClickListener(){
        public void onClick(View arg0) {
            Toast.makeText(getApplicationContext(), "Hello world 3 !", Toast.LENGTH_LONG).show();
        }
    };

    public void onClick(View arg0) {
        Toast.makeText(getApplicationContext(), "Hello world 4 !", Toast.LENGTH_LONG).show();
    }

    public void HandleClick5(View arg0) {
        Toast.makeText(getApplicationContext(), "Hello world 4 !", Toast.LENGTH_LONG).show();
    }
}
