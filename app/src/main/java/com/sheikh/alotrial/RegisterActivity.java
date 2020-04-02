package com.sheikh.alotrial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {

    private EditText edtEmail, edtUsername,edtMobile,edtPassword;
    private Button btnLogin,btnRegister;
    private FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        changeStatusBarColor();

        edtEmail = findViewById(R.id.editTextEmail);
        edtMobile = findViewById(R.id.editTextMobile);
        edtUsername = findViewById(R.id.editTextName);
        edtPassword = findViewById(R.id.editTextPassword);

        btnLogin = findViewById(R.id.cirLoginButton);
        btnRegister = findViewById(R.id.cirRegisterButton);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                 String username = edtUsername.getText().toString();
                 String email = edtEmail.getText().toString();
                 String mobile = edtMobile.getText().toString();
                 String password = edtPassword.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }

                else if(password.length() < 6){
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                }

                else {
                    signUp(username, mobile, email, password);
                }


            }
        });




    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            //Transition to next activity
        }
    }
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
           window.setStatusBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(getResources().getColor(R.color.register_bk_color));
        }
    }

    public void onLoginClick(View view){
        startActivity(new Intent(this,LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    private void signUp(final String username, final String mobile, String email, String password){
    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                Toast.makeText(RegisterActivity.this,"Registration Successful!",Toast.LENGTH_LONG).show();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String userID = firebaseUser.getUid();

                reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id",userID);
                hashMap.put("username",username);
                hashMap.put("Mobile",mobile);
                hashMap.put("bio","");
                hashMap.put("imageURL","https://firebasestorage.googleapis.com/v0/b/alo-trial.appspot.com/o/placeholder.png?alt=media&token=c1f05677-bea3-4c42-b6c5-3319ab913df5");

                reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Intent intent = new Intent(RegisterActivity.this, Homepage.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });

            }else {
                pd.dismiss();
                Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
            }
        }
    });

}



}
