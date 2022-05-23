package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import android.widget.EditText;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.view.WindowManager;
import android.text.TextUtils;
import android.graphics.drawable.Drawable;
import java.util.Map;
import java.util.HashMap;

public class SignUp extends AppCompatActivity {

    public static final String SIGN_UP_MESSAGE = "com.example.gettingstarted.MESSAGE";

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private Context mContext;

    private EditText usernameEditText;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;

    private TextView usernameTextView;
    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView emailTextView;
    private TextView passwordTextView;
    private TextView passwordConfirmTextView;
    private TextView sixCharacterPasswordTextView;
    private TextView sixCharacterPasswordConfirmTextView;

    private View loadingBackgroundView;
    private ProgressBar loadingProgressBar;

    private Drawable correctBackgroundForEditText;
    private Drawable errorBackgroundForEditText;

    private void InitializeResources()
    {
        correctBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border);
        errorBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border_error);
    }

    private void InitializeWidgets()
    {
        usernameEditText = (EditText)findViewById(R.id.username_sign_up);
        nameEditText = (EditText)findViewById(R.id.name_sign_up);
        surnameEditText = (EditText)findViewById(R.id.surname_sign_up);
        emailEditText = (EditText)findViewById(R.id.email_sign_up);
        passwordEditText = (EditText)findViewById(R.id.password_sign_up);
        passwordConfirmEditText = (EditText)findViewById(R.id.password_confirm_sign_up);

        usernameTextView = (TextView)findViewById(R.id.username_error_sign_up);
        nameTextView = (TextView)findViewById(R.id.name_error_sign_up);
        surnameTextView = (TextView)findViewById(R.id.surname_error_sign_up);
        emailTextView = (TextView)findViewById(R.id.email_error_sign_up);
        passwordTextView = (TextView)findViewById(R.id.password_error_sign_up);
        passwordConfirmTextView = (TextView)findViewById(R.id.password_confirm_error_sign_up);
        sixCharacterPasswordTextView = (TextView)findViewById(R.id.password_error_six_characters);
        sixCharacterPasswordConfirmTextView = (TextView)findViewById(R.id.password_confirm_error_six_characters);

        loadingBackgroundView = (View)findViewById(R.id.interaction_blocker_sign_up);
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_icon_sign_up);
    }

    //Actions to take if user enters invalid data.
    private void ErrorAction(EditText _editTextInput, TextView _errorTextView, Drawable _backgroundForEditText, String errorMessage)
    {
        _editTextInput.setBackgroundDrawable(_backgroundForEditText);
        _errorTextView.setText(errorMessage);
        _errorTextView.setVisibility(View.VISIBLE);
    }

    //Actions to take if user enters correct data.
    private void CorrectAction(EditText _editTextInput, TextView _errorTextView, Drawable _backgroundForEditText)
    {
        _editTextInput.setBackgroundDrawable(_backgroundForEditText);
        _errorTextView.setText("");
        _errorTextView.setVisibility(View.INVISIBLE);
    }

    //Ensures fields are not empty.
    private boolean Validation_IsInputEmpty(EditText _editTextInput, TextView _errorTextView, Drawable _correctBackgroundForEditText, Drawable _errorBackgroundForEditText)
    {
        String errorMessage = "Field cannot be empty!";

        if(TextUtils.isEmpty(_editTextInput.getText()))
        {
            ErrorAction(_editTextInput, _errorTextView, _errorBackgroundForEditText, errorMessage);

            return true;
        }
        else
        {
            CorrectAction(_editTextInput, _errorTextView, _correctBackgroundForEditText);

            return false;
        }
    }

    //Ensures passwords match.
    private boolean Validation_ArePasswordsDifferent(EditText _editTextPassword, TextView _textViewError, EditText _editTextPasswordConfirm, TextView _textViewErrorConfirm, Drawable _correctBackgroundForEditText, Drawable _errorBackgroundForEditText)
    {
        String errorMessage = "Passwords do not match!";

        if(!TextUtils.isEmpty(_editTextPassword.getText()) && !TextUtils.isEmpty(_editTextPasswordConfirm.getText()))
        {
            if(!_editTextPassword.getText().toString().equals(_editTextPasswordConfirm.getText().toString()))
            {
                ErrorAction(_editTextPassword, _textViewError, _errorBackgroundForEditText, errorMessage);
                ErrorAction(_editTextPasswordConfirm, _textViewErrorConfirm, _errorBackgroundForEditText, errorMessage);

                return true;
            }
            else
            {
                CorrectAction(_editTextPassword, _textViewError, _correctBackgroundForEditText);
                CorrectAction(_editTextPasswordConfirm, _textViewErrorConfirm, _correctBackgroundForEditText);

                return false;
            }
        }
        else
        {
            return true;
        }
    }

    //Ensures passwords are more than 6 characters (Firebase Auth requirement)
    private boolean Validation_ArePasswordsSixCharactersOrMore(EditText _editTextPassword, TextView _textViewError, EditText _editTextPasswordConfirm, TextView _textViewErrorConfirm, Drawable _correctBackgroundForEditText, Drawable _errorBackgroundForEditText)
    {
        boolean hasErrors = false;
        String errorMessage = "Password is less than 6 characters!";

        if(_editTextPassword.getText().toString().length() < 6)
        {
            ErrorAction(_editTextPassword, _textViewError, _errorBackgroundForEditText, errorMessage);
        }
        else
        {
            CorrectAction(_editTextPassword, _textViewError, _correctBackgroundForEditText);
        }

        if(_editTextPasswordConfirm.getText().toString().length() < 6)
        {
            ErrorAction(_editTextPasswordConfirm, _textViewErrorConfirm, _errorBackgroundForEditText, errorMessage);
        }
        else
        {
            CorrectAction(_editTextPasswordConfirm, _textViewErrorConfirm, _correctBackgroundForEditText);
        }

        return hasErrors;
    }

    //Submits data (email and password) to Firebase Auth. Additional arguments are sent to SubmitUserData().
    private void SubmitAuthData(EditText _editTextEmail, EditText _editTextPassword, String _username, String _name, String _surname)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        loadingBackgroundView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(_editTextEmail.getText().toString(), _editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            SubmitUserData(_username, _name, _surname, _editTextEmail.getText().toString(), task.getResult().getUser().getUid());
                        }
                        else{
                            loadingBackgroundView.setVisibility(View.INVISIBLE);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUp.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    //Submits data (name, surname, username, email, UID) to Firebase FireStore. Stored as nested fields (UID -> username, name, surname, email, uid).
    private void SubmitUserData(String _username, String _name, String _surname, String _email, String _userID)
    {
        Map<String, Object> userRoot = new HashMap<>();
        Map<String, Object> user = new HashMap<>();

        user.put("username", _username);
        user.put("name", _name);
        user.put("surname", _surname);
        user.put("email", _email);
        user.put("uid", _userID);

        userRoot.put(_userID, user);

        db.collection("users").document("user_details").update(userRoot).addOnSuccessListener(
                new OnSuccessListener<Void>(){
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        String message = "Successfully Signed Up";
                        intent.putExtra(SIGN_UP_MESSAGE, message);
                        startActivity(intent);
                    }
                }
        ).addOnFailureListener(
                new OnFailureListener(){
                    @Override
                    public void onFailure(@NonNull Exception e){
                        loadingBackgroundView.setVisibility(View.INVISIBLE);
                        loadingProgressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignUp.this, "Sign Up Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        mContext = this;

        InitializeResources();

        InitializeWidgets();
    }

    public void SignUpButton(View view)
    {
        boolean hasError = false;

        if(Validation_IsInputEmpty(usernameEditText, usernameTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_IsInputEmpty(nameEditText, nameTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_IsInputEmpty(surnameEditText, surnameTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_IsInputEmpty(emailEditText, emailTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_IsInputEmpty(passwordEditText, passwordTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_IsInputEmpty(passwordConfirmEditText, passwordConfirmTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_ArePasswordsDifferent(passwordEditText, passwordTextView, passwordConfirmEditText, passwordConfirmTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(Validation_ArePasswordsSixCharactersOrMore(passwordEditText, sixCharacterPasswordTextView, passwordConfirmEditText, sixCharacterPasswordConfirmTextView, correctBackgroundForEditText, errorBackgroundForEditText))
        {
            hasError = true;
        }

        if(!hasError)
        {
            SubmitAuthData(emailEditText, passwordEditText, usernameEditText.getText().toString(), nameEditText.getText().toString(), surnameEditText.getText().toString());
        }
    }
}