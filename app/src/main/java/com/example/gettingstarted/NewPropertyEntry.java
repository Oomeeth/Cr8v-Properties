package com.example.gettingstarted;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.view.View;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import custom.utilities.Validation;
import java.time.Instant;
import java.net.URI;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.database.Cursor;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import java.io.ByteArrayOutputStream;
import android.graphics.drawable.BitmapDrawable;
import com.google.firebase.storage.UploadTask;
import android.graphics.Bitmap.CompressFormat;

public class NewPropertyEntry extends AppCompatActivity {

    private EditText location;
    private TextView locationError;
    private EditText price;
    private TextView priceError;
    private EditText bedrooms;
    private TextView bedroomsError;
    private EditText bathrooms;
    private TextView bathroomsError;
    private EditText garages;
    private TextView garagesError;
    private EditText landSize;
    private TextView landSizeError;
    private TextInputLayout description;

    private Drawable correctBackgroundForEditText;
    private Drawable errorBackgroundForEditText;

    private View loadingView;
    private ProgressBar loadingProgressBar;

    private FirebaseFirestore db;

    private Context mContext;

    public static final String MESSAGE = "";

    private Validation validation;

    private ActivityResultLauncher<Intent> launcher;

    private void InitializeResources()
    {
        correctBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border);
        errorBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border_error);
    }

    private void InitializeWidgets()
    {
        location = (EditText)findViewById(R.id.location_input_add_new);
        locationError = (TextView)findViewById(R.id.location_error_add_new);
        price = (EditText)findViewById(R.id.price_input_add_new);
        priceError = (TextView)findViewById(R.id.price_error_add_new);
        bedrooms = (EditText)findViewById(R.id.bedrooms_input_add_new);
        bedroomsError = (TextView)findViewById(R.id.bedrooms_error_add_new);
        bathrooms = (EditText)findViewById(R.id.bathrooms_input_add_new);
        bathroomsError = (TextView)findViewById(R.id.bathrooms_error_add_new);
        garages = (EditText)findViewById(R.id.garages_input_add_new);
        garagesError = (TextView)findViewById(R.id.garages_error_add_new);
        landSize = (EditText)findViewById(R.id.land_size_input_add_new);
        landSizeError = (TextView)findViewById(R.id.land_size_error_add_new);
        description = (TextInputLayout)findViewById(R.id.description_add_new);

        loadingView = (View)findViewById(R.id.interaction_blocker_add_new);
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_icon_add_new);
    }

    private String GetUserUID()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            return user.getUid();
        }
        else
        {
            return "";
        }
    }

    //Get property count
       //Set new property count
          //Submit data to FireStore
       //Upload Property Image
    private void InitiateDataSubmissionProcess()
    {
        loadingView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        GetPropertyLength();
    }

    private void GetPropertyLength()
    {
        db.collection("properties").document("property_length").get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot document = task.getResult();

                            if(document.exists())
                            {
                                int getPropertyLength = (int)Math.floor(document.getDouble("length"));
                                getPropertyLength += 1;
                                UpdatePropertyLength(getPropertyLength);
                                UploadPropertyImage(getPropertyLength);
                            }
                            else
                            {
                                loadingView.setVisibility(View.INVISIBLE);
                                loadingProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(NewPropertyEntry.this, "Document does not exist!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            loadingView.setVisibility(View.INVISIBLE);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(NewPropertyEntry.this, "Something Went Wrong With Retrieving!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void UpdatePropertyLength(final int _getPropertyLength)
    {
        Map<String, Object> _propertyLength = new HashMap<>();

        _propertyLength.put("length", _getPropertyLength);

        db.collection("properties").document("property_length").update(_propertyLength).addOnSuccessListener(
                new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        SubmitEntryData(location.getText().toString(), price.getText().toString(), bedrooms.getText().toString(), bathrooms.getText().toString(), garages.getText().toString(), landSize.getText().toString(), description.getEditText().getText().toString(), _getPropertyLength);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        loadingView.setVisibility(View.INVISIBLE);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(NewPropertyEntry.this, "Something Went Wrong With Updating!", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void SubmitEntryData(String _location, String _price, String _bedrooms, String _bathrooms, String _garages, String _landSize, String _description, int _getPropertyLength)
    {
        String userUID = GetUserUID();

        if(!TextUtils.isEmpty(userUID))
        {
            Map<String, Object> propertyRoot = new HashMap<>();
            Map<String, Object> property = new HashMap<>();

            property.put("location", _location);
            property.put("price", _price);
            property.put("bedrooms", _bedrooms);
            property.put("bathrooms", _bathrooms);
            property.put("garages", _garages);
            property.put("landSize", _landSize);
            property.put("description", _description);
            property.put("created_by", userUID);
            property.put("created_on", FieldValue.serverTimestamp());

            propertyRoot.put(String.valueOf(_getPropertyLength), property);

            db.collection("properties").document("all_properties").update(propertyRoot).addOnSuccessListener(
                    new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(NewPropertyEntry.this, "Successfully Created A New Entry!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(mContext, PropertyHome.class);
                            String message = "Successfully Added A New Property!";
                            intent.putExtra(MESSAGE, message);
                            startActivity(intent);
                        }
                    }
            ).addOnFailureListener(
                    new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(NewPropertyEntry.this, "Error Submitting Data", Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        else
        {
            loadingView.setVisibility(View.INVISIBLE);
            loadingProgressBar.setVisibility(View.INVISIBLE);

            Toast.makeText(NewPropertyEntry.this, "Error! Could not get user's UID", Toast.LENGTH_LONG).show();
        }
    }

    private void InitializeFilePicker()
    {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try {
                        Uri img = result.getData().getData();

                        Bitmap imgBitmap = BitmapFactory.decodeStream(getBaseContext().getContentResolver().openInputStream(img));

                        ImageView imgView = (ImageView) findViewById(R.id.upload_image_preview_add_new);

                        imgView.setImageBitmap(imgBitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        Toast.makeText(NewPropertyEntry.this, "Bitmap Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property_entry);

        mContext = this;

        db = FirebaseFirestore.getInstance();

        validation = new Validation();

        InitializeResources();

        InitializeWidgets();

        InitializeFilePicker();
    }

    private void PreviewPropertyImage()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void UploadPropertyImage(int _propertyLength)
    {
        String userUID = GetUserUID();

        StorageReference fileUploadStorage = FirebaseStorage.getInstance().getReference().child("properties/" + userUID + "/" + String.valueOf(_propertyLength) + ".png");

        fileUploadStorage.listAll().addOnSuccessListener(
                new OnSuccessListener<ListResult>(){
                    @Override
                    public void onSuccess(ListResult listResult)
                    {
                        //Get all folders inside "properties"
                        /*for(StorageReference prefix : listResult.getPrefixes())
                        {
                            Toast.makeText(NewPropertyEntry.this, "Folder: " + prefix, Toast.LENGTH_SHORT).show();
                        }*/

                        ImageView imgView = (ImageView)findViewById(R.id.upload_image_preview_add_new);

                        imgView.setDrawingCacheEnabled(true);
                        imgView.buildDrawingCache();

                        Bitmap imageToUpload = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageToUpload.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] data = baos.toByteArray();

                        UploadTask uploadTask = fileUploadStorage.putBytes(data);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot snapshot)
                            {
                                Toast.makeText(NewPropertyEntry.this, "Uploading...", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(NewPropertyEntry.this, "Error uploading: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(NewPropertyEntry.this, "Could not get FirebaseStorage Reference " + e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void GetPropertyImage(View view)
    {
        PreviewPropertyImage();
    }

    public void SubmitNewEntry(View view)
    {
        boolean hasErrors = false;

        if(validation.Validation_IsInputEmpty(location, locationError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(price, priceError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(bedrooms, bedroomsError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(bathrooms, bathroomsError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(garages, garagesError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(landSize, landSizeError, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(!hasErrors)
        {
            InitiateDataSubmissionProcess();
        }
    }
}