package com.example.pinitlogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private ImageButton Back;
    private EditText FName;
    private EditText LName;
    private EditText PhoneNum;
    private EditText Email;
    private EditText Password;
    private EditText PasswordConfirm;
    private Button Confirm;
    private FirebaseAuth firebaseauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        firebaseauth = FirebaseAuth.getInstance();
        Back = findViewById(R.id.btnBack);
        FName = findViewById(R.id.etRegFName);
        LName = findViewById(R.id.etRegLName);
        PhoneNum = findViewById(R.id.etRegPhone);
        Email = findViewById(R.id.etRegEmail);
        Password = findViewById(R.id.etRegPassword);
        PasswordConfirm = findViewById(R.id.etRegPasswordConfirm);
        Confirm = findViewById(R.id.btnRegConfirm);


        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String FNameInput = FName.getText().toString();
                String LNameInput = LName.getText().toString();
                String PhoneNumInput = PhoneNum.getText().toString();
                String EmailInput = Email.getText().toString();
                String PasswordInput = Password.getText().toString();
                String PasswordConfirmInput = PasswordConfirm.getText().toString();
                //Check if everything is filled out
                if((FNameInput.isEmpty()) || (LNameInput.isEmpty()) || (PhoneNumInput.isEmpty()) || (EmailInput.isEmpty()) || (PasswordInput.isEmpty()) || (PasswordConfirmInput.isEmpty())){
                    Toast.makeText(RegisterActivity.this, "Please ensure all credentials are entered.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Validate phone number is valid
                    if(ValidatePhone(PhoneNumInput)==true){
                        //Validate Password for strength and that password == confirmpassword
                        if(ValidatePassword(PasswordInput, PasswordConfirmInput)){
                            //Validate email(how?)
                            if(ValidateEmail(EmailInput) == true){
                                RegisterUser(FNameInput, LNameInput, PhoneNumInput, EmailInput, PasswordInput);
                            }
                            else{
                                Toast.makeText(RegisterActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Passwords do not match, please reconfirm password", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    }


                    //Once everything is validated, store data in database
                }

            }
        });




        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }
    private boolean ValidatePassword(String pass1, String pass2){
        //Validate strength needed
        if(pass1.equals(pass2)){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean ValidatePhone(String PhoneNum){
        //Needs to check if phone is already in use
        if((PhoneNum.length() == 10) && (PhoneNum != "0000000000")){
            return true;
        }
        else{
            return false;
        }
    }
    private boolean ValidateEmail(String email){
        //Need to code in verification for email. Should start with something that just check for a proper @domain but should look into finding if the email exists or not
        //Also needs to search current user database if email is already in use
        return true;
    }
    private void RegisterUser(String Fname, String Lname, String PhoneNum, String Email, String Password){
        //Put information into database
        String user_email = Email.trim();
        String user_password = Password.trim();

        firebaseauth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User successfully registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}