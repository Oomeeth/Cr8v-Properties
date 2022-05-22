package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.EditText;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.view.WindowManager;
import android.text.TextUtils;
import android.graphics.drawable.Drawable;

public class SignUp extends AppCompatActivity {

    public static final String SIGN_UP_MESSAGE = "com.example.gettingstarted.MESSAGE";

    private FirebaseAuth mAuth;

    private Context mContext;

    enum FieldType{
        NORMAL,
        EMAIL,
        PASSWORD
    }

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

    private void ErrorAction(EditText _editTextInput, TextView _errorTextView, Drawable _backgroundForEditText, String errorMessage)
    {
        _editTextInput.setBackgroundDrawable(_backgroundForEditText);
        _errorTextView.setText(errorMessage);
        _errorTextView.setVisibility(View.VISIBLE);
    }

    private void CorrectAction(EditText _editTextInput, TextView _errorTextView, Drawable _backgroundForEditText)
    {
        _editTextInput.setBackgroundDrawable(_backgroundForEditText);
        _errorTextView.setText("");
        _errorTextView.setVisibility(View.INVISIBLE);
    }

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

    private void SubmitUserDataToFireBase(EditText _editTextEmail, EditText _editTextPassword)
    {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        loadingBackgroundView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(_editTextEmail.getText().toString(), _editTextPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        if(task.isSuccessful()){
                            Intent intent = new Intent(mContext, MainActivity.class);
                            String message = "Successfully Signed Up";
                            intent.putExtra(SIGN_UP_MESSAGE, message);
                            startActivity(intent);
                        }
                        else{
                            loadingBackgroundView.setVisibility(View.INVISIBLE);
                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUp.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

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
            SubmitUserDataToFireBase(emailEditText, passwordEditText);
        }
    }
}