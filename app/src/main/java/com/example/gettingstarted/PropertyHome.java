package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PropertyHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        for(int i = 0; i < 30; ++i)
        {
            LinearLayout mainLayout = (LinearLayout)findViewById(R.id.linear_layout);

            ImageView imageView = new ImageView(PropertyHome.this);
            imageView.setImageResource(R.mipmap.ic_launcher);

            TextView textView = new TextView(PropertyHome.this);
            textView.setText("Hello World");
            textView.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            imageView.setLayoutParams(params);
            textView.setLayoutParams(params);

            mainLayout.addView(imageView);
            mainLayout.addView(textView);
        }
    }
}