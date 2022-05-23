/*
Activity - A screen which the user can interact with.
 */

package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import android.widget.Toast;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import custom.utilities.Validation;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Validation validation;

    private Drawable correctBackgroundForEditText;
    private Drawable errorBackgroundForEditText;

    private EditText emailEditText;
    private TextView emailErrorTextView;
    private EditText passwordEditText;
    private TextView passwordErrorTextView;

    private View loadingView;
    private ProgressBar loadingProgressBar;

    private void InitializeResources()
    {
        correctBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border);
        errorBackgroundForEditText = getResources().getDrawable(R.drawable.edit_text_border_error);
    }

    private void InitializeWidgets()
    {
        emailEditText = (EditText)findViewById(R.id.email_sign_in);
        emailErrorTextView = (TextView)findViewById(R.id.email_error_sign_in);
        passwordEditText = (EditText)findViewById(R.id.password_sign_in);
        passwordErrorTextView = (TextView)findViewById(R.id.password_error_sign_in);

        loadingView = (View)findViewById(R.id.interaction_block_sign_in);
        loadingProgressBar = (ProgressBar)findViewById(R.id.loading_icon_sign_in);
    }

    private void DisplaySignUpActivityMessage()
    {
        Intent intent = getIntent();
        String message = intent.getStringExtra(SignUp.SIGN_UP_MESSAGE);

        if(message != null && !message.trim().isEmpty())
        {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginFunctionality(String _email, String _password)
    {
        loadingView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(_email, _password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                    loadingView.setVisibility(View.INVISIBLE);
                    loadingProgressBar.setVisibility(View.INVISIBLE);

                    ChangeActivity(PropertyHome.class);
                }
                else
                {
                    loadingView.setVisibility(View.INVISIBLE);
                    loadingProgressBar.setVisibility(View.INVISIBLE);

                    validation.SubmitAction(emailEditText, emailErrorTextView, errorBackgroundForEditText, "Incorrect credentials!");
                    validation.SubmitAction(passwordEditText, passwordErrorTextView, errorBackgroundForEditText, "Incorrect credentials!");
                }
            }
        });
    }

    private void IsUserSignedIn()
    {
        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null)
        {
            ChangeActivity(PropertyHome.class);
        }
    }

    private void ChangeActivity(Class className)
    {
        Intent intent = new Intent(this, className);
        startActivity(intent);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        IsUserSignedIn();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        validation = new Validation();

        InitializeResources();

        InitializeWidgets();

        mAuth = FirebaseAuth.getInstance();

        DisplaySignUpActivityMessage();
    }

    public void GoToSignUpScreen(View view)
    {
        ChangeActivity(SignUp.class);
    }

    public void Login(View view)
    {
        boolean hasErrors = false;

        if(validation.Validation_IsInputEmpty(emailEditText, emailErrorTextView, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(validation.Validation_IsInputEmpty(passwordEditText, passwordErrorTextView, errorBackgroundForEditText, correctBackgroundForEditText))
        {
            hasErrors = true;
        }

        if(!hasErrors)
        {
            LoginFunctionality(emailEditText.getText().toString(), passwordEditText.getText().toString());
        }
    }
}