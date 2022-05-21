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
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.view.WindowManager;
import android.text.TextUtils;

public class SignUp extends AppCompatActivity {

    public static final String SIGN_UP_MESSAGE = "com.example.gettingstarted.MESSAGE";

    private FirebaseAuth mAuth;

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

    private TextView passwordMismatchTextView;
    private TextView passwordConfirmMismatchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mContext = this;

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

        passwordMismatchTextView = (TextView)findViewById(R.id.password_mismatch_sign_up);
        passwordConfirmMismatchTextView = (TextView)findViewById(R.id.password_confirm_mismatch_sign_up);
    }

    public void SignUpButton(View view)
    {
        boolean hasError = false;

        if(TextUtils.isEmpty(usernameEditText.getText()))
        {
            usernameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            usernameTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            usernameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            usernameTextView.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(nameEditText.getText()))
        {
            nameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            nameTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            nameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            nameTextView.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(surnameEditText.getText()))
        {
            surnameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            surnameTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            surnameEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            surnameTextView.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(emailEditText.getText()))
        {
            emailEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            emailTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            emailEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            emailTextView.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(passwordEditText.getText()))
        {
            passwordEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            passwordTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            passwordEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            passwordTextView.setVisibility(View.INVISIBLE);
        }

        if(TextUtils.isEmpty(passwordConfirmEditText.getText()))
        {
            passwordConfirmEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
            passwordConfirmTextView.setVisibility(View.VISIBLE);
            hasError = true;
        }
        else
        {
            passwordConfirmEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
            passwordConfirmTextView.setVisibility(View.INVISIBLE);
        }

        if(!TextUtils.isEmpty(passwordEditText.getText()) && !TextUtils.isEmpty(passwordConfirmEditText.getText()))
        {
            if(!passwordEditText.getText().toString().equals(passwordConfirmEditText.getText().toString()))
            {
                passwordEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));
                passwordConfirmEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border_error));

                passwordTextView.setVisibility(View.INVISIBLE);
                passwordConfirmTextView.setVisibility(View.INVISIBLE);

                passwordMismatchTextView.setVisibility(View.VISIBLE);
                passwordConfirmMismatchTextView.setVisibility(View.VISIBLE);

                hasError = true;
            }
            else
            {
                passwordEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));
                passwordConfirmEditText.setBackgroundDrawable(getResources().getDrawable(R.drawable.edit_text_border));

                passwordMismatchTextView.setVisibility(View.INVISIBLE);
                passwordConfirmMismatchTextView.setVisibility(View.INVISIBLE);
            }
        }

        if(hasError)
        {
            hasError = false;
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        findViewById(R.id.interaction_blocker_sign_up).setVisibility(View.VISIBLE);
        findViewById(R.id.loading_icon_sign_up).setVisibility(View.VISIBLE);

        EditText emailValue = (EditText)findViewById(R.id.email_sign_up);
        EditText passwordValue = (EditText)findViewById(R.id.password_sign_up);

        mAuth.createUserWithEmailAndPassword(emailValue.getText().toString(), passwordValue.getText().toString())
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
                            findViewById(R.id.interaction_blocker_sign_up).setVisibility(View.INVISIBLE);
                            findViewById(R.id.loading_icon_sign_up).setVisibility(View.INVISIBLE);
                            Toast.makeText(SignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}