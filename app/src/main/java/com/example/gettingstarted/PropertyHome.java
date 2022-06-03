package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.firestore.DocumentSnapshot;
import android.view.InflateException;
import android.widget.ProgressBar;

import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import android.graphics.BitmapFactory;
import androidx.constraintlayout.widget.ConstraintLayout;

public class PropertyHome extends AppCompatActivity
{
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerAdapter;
    private RecyclerView.LayoutManager recyclerLayoutManager;
    private StorageReference storage;
    private Property[] allProperties;
    private View backgroundBlocker;
    private ProgressBar progressBar;

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

    private void DisplayAllProperties()
    {
        recyclerView = (RecyclerView) findViewById(R.id.get_property_entry_property_home);

        recyclerAdapter = new GetPropertyRecyclerViewAdapater(allProperties, PropertyHome.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(PropertyHome.this));
        recyclerView.setAdapter(recyclerAdapter);

        backgroundBlocker.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void DownloadImageBitmap(final int _currenIteration, final StorageReference imageRef, final boolean isLastEntry)
    {
        imageRef.getBytes(6291456).addOnSuccessListener(
                new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap _imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        allProperties[_currenIteration].imageBitmap = _imageBitmap;

                        if(isLastEntry){
                            DisplayAllProperties();
                        }
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                }
        );
    }

    private void GetAllImages()
    {
        try {
            StorageReference fileDownload = FirebaseStorage.getInstance().getReference().child("properties");

            fileDownload.listAll().addOnSuccessListener(
                    new OnSuccessListener<ListResult>(){
                        @Override
                        public void onSuccess(ListResult listResult) {
                            int iteration = 0;

                            //Get all files inside "properties"
                            for(StorageReference prefix : listResult.getItems())
                            {
                                if(iteration == (listResult.getItems().size() - 1)) {
                                    DownloadImageBitmap(iteration, prefix, true);
                                }
                                else
                                {
                                    DownloadImageBitmap(iteration, prefix, false);
                                }

                                iteration++;
                            }
                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(PropertyHome.this, "Could not get FirebaseStorage Reference " + e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        catch(Exception e)
        {
            Toast.makeText(PropertyHome.this, "Could Not Get Images!", Toast.LENGTH_LONG).show();
        }
    }

    private void GetAllProperties()
    {
        db.collection("properties").document("all_properties").get().addOnCompleteListener(
            new OnCompleteListener<DocumentSnapshot>(){
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    DocumentSnapshot document = task.getResult();
                    int iteration = 0;

                    if(document.exists())
                    {
                        /*TextView test = (TextView)findViewById(R.id.test);
                        test.setText(document.getData().toString());*/

                        allProperties = new Property[document.getData().size()];

                        for(Map.Entry<String, Object> dataEntry : document.getData().entrySet())
                        {
                            Property property = new Property();
                            property.id = Integer.parseInt(dataEntry.getKey());

                            Map<String, Object> allSubDataEntries = (Map<String, Object>)dataEntry.getValue();

                            for(Map.Entry<String, Object> subDataEntry : allSubDataEntries.entrySet())
                            {
                                if(subDataEntry.getKey().equals("bathrooms"))
                                {
                                    property.bathrooms = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("bedrooms"))
                                {
                                    property.bedrooms = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("garages"))
                                {
                                    property.garages = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("created_by"))
                                {
                                    property.createdBy = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("description"))
                                {
                                    property.description = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("location"))
                                {
                                    property.location = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("land_size"))
                                {
                                    property.landSize = String.valueOf(subDataEntry.getValue());
                                }
                                else if(subDataEntry.getKey().equals("price"))
                                {
                                    property.price = String.valueOf(subDataEntry.getValue());
                                }
                            }

                            allProperties[iteration] = property;

                            iteration++;
                        }

                        GetAllImages();
                    }
                    else
                    {

                    }
                }
            }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_home);

        backgroundBlocker = (View)findViewById(R.id.background_blocker_property_home);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar_property_home);

        DisplayNewPropertyEntryMessage();

        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance().getReference().child("properties/");

        GetAllProperties();
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