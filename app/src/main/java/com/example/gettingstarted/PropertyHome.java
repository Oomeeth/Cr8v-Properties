package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import android.view.View;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.Toolbar;
import android.view.InflateException;
import android.widget.Toast;

public class PropertyHome extends AppCompatActivity {

    private void ChangeActivity(Class className)
    {
        Intent intent = new Intent(this, className);
        startActivity(intent);
    }

    private void DisplayNewPropertyEntryMessage()
    {
        Intent intent = getIntent();
        String message = intent.getStringExtra(NewPropertyEntry.MESSAGE);

        if(message != null && !message.trim().isEmpty())
        {
            Toast.makeText(PropertyHome.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        DisplayNewPropertyEntryMessage();

        /*for(int i = 0; i < 40; ++i)
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
        }*/
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.actionbarmenu, menu);
        }
        catch(InflateException e)
        {
            Toast.makeText(PropertyHome.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void NewPropertyScreen(MenuItem menuItem)
    {
        ChangeActivity(NewPropertyEntry.class);
    }

    public void UserLogout(MenuItem menuItem)
    {
        FirebaseAuth.getInstance().signOut();
        ChangeActivity(MainActivity.class);
    }
}