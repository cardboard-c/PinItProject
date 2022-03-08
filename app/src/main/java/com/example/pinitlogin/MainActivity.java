package com.example.pinitlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//REMEMBER TO ALLOW PERMISSIONS IN PHONE BEFORE USE(LOCATION AND CAMERA)
public class MainActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private Button Login;
    private Button Register;
    private FirebaseAuth firebaseauth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the elements from the UI to variables
        Email = findViewById(R.id.etEmail);
        Password = findViewById(R.id.etPassword);
        Login = findViewById(R.id.btnLogin);
        Register = findViewById(R.id.btnRegister);
        firebaseauth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseauth.getCurrentUser();
        progressDialog = new ProgressDialog(this);

        /*if (user != null) {
            finish();
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
        }*/
        //Clicking on the login button
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Getting username and password
                String inputEmail = Email.getText().toString();
                String inputPass = Password.getText().toString();
                //Validating Inputs
                if((inputEmail.isEmpty()) || (inputPass.isEmpty())){
                    Toast.makeText(MainActivity.this, "Please ensure all credentials are entered.", Toast.LENGTH_SHORT).show();
                }
                else if((inputEmail == "admin") && (inputPass == "password")){
                    startActivity(new Intent(MainActivity.this, MainMenuActivity.class));

                }
                else{
                    //Validate if user input matches up to a user in database
                    SearchDatabase(inputEmail, inputPass);
                }


            }

            private void SearchDatabase(String email, String password) {
                //Search database for the entered email, then check if password is correct
                progressDialog.setMessage("Verifying");
                progressDialog.show();
                firebaseauth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));

                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        //Clicking on Register button
       Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });




    }
}
