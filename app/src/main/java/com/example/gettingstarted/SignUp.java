package com.example.gettingstarted;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.widget.EditText;
import android.content.Context;
import android.widget.Toast;
import android.view.View;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

public class SignUp extends AppCompatActivity {

    public static final String SIGN_UP_MESSAGE = "com.example.gettingstarted.MESSAGE";

    private FirebaseAuth mAuth;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mContext = this;
    }

    public void SignUpButton(View view)
    {
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
                            Toast.makeText(SignUp.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}