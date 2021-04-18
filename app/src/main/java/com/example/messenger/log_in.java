package com.example.messenger;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class log_in extends AppCompatActivity {


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_emailpassword);
        Button emailSignInButton = findViewById(R.id.emailSignInButton);
        Button emailCreateAccountButton = findViewById(R.id.emailCreateAccountButton);
        Button signOutButton = findViewById(R.id.signOutButton);
        Button signOutButton_2 = findViewById(R.id.signOutButton_2);
        Button reloadButton = findViewById(R.id.reloadButton);
        Button forgotpasswordButton = findViewById(R.id.passwordForgotButton);
        Button goToMessengerButton = findViewById(R.id.goToMessengerButton);
        EditText fieldEmail = findViewById(R.id.fieldEmail);
        EditText fieldPassword = findViewById(R.id.fieldPassword);


        emailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = fieldEmail.getText().toString();
                String password = fieldPassword.getText().toString();
                signIn(email, password);
            }
        });
        emailCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = fieldEmail.getText().toString();
                String password = fieldPassword.getText().toString();
                createAccount(email, password);
            }
        });
        forgotpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = fieldEmail.getText().toString();
                sendPasswordResetEmail(email);
            }
        });
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        signOutButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        reloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reload();
            }
        });

        goToMessengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(log_in.this, listofdialogs.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();


    }




    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Verification email sent to " + user.getEmail(),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Failed to send verification email.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });


    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    private void sendPasswordResetEmail(String email){
        Button forgotpasswordButton = findViewById(R.id.passwordForgotButton);
        EditText fieldEmail = findViewById(R.id.fieldEmail);
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError("Required.");
            Toast.makeText(getApplicationContext(),
                    "Fill in the email field to reset your password",
                    Toast.LENGTH_SHORT).show();}
        else{
            forgotpasswordButton.setEnabled(false);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    forgotpasswordButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                "Verification email sent to " + email,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Failed to send reset password email.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        });}
    }

    private void reload() {
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateUI(mAuth.getCurrentUser());
                    Toast.makeText(getApplicationContext(),
                            "Reload successful!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Failed to reload user.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;
        EditText fieldEmail = findViewById(R.id.fieldEmail);
        EditText fieldPassword = findViewById(R.id.fieldPassword);
        String email = fieldEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            fieldEmail.setError("Required.");
            valid = false;
        } else {
            fieldEmail.setError(null);
        }

        String password = fieldPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            fieldPassword.setError("Required.");
            valid = false;
        } else {
            fieldPassword.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {

        TextView hint = findViewById(R.id.hint);
        Group emailPasswordButtons = findViewById(R.id.emailPasswordButtons);
        Group emailPasswordFields = findViewById(R.id.emailPasswordFields);
        Group signedInButtons = findViewById(R.id.signedInButtons);
        Group finalRegButtons = findViewById(R.id.finalRegButtons);
        if (user != null) {



            emailPasswordButtons.setVisibility(View.GONE);
            emailPasswordFields.setVisibility(View.GONE);


            if (user.isEmailVerified()) {
                finalRegButtons.setVisibility(View.VISIBLE);
                signedInButtons.setVisibility(View.GONE);
                hint.setText(getString(R.string.logged_in_text));
            } else {

                finalRegButtons.setVisibility(View.GONE);
                signedInButtons.setVisibility(View.VISIBLE);
                hint.setText(getString(R.string.log_in_verify_text));
            }
        } else {
            hint.setText(getString(R.string.log_in_title_text));
            finalRegButtons.setVisibility(View.GONE);
            emailPasswordButtons.setVisibility(View.VISIBLE);
            emailPasswordFields.setVisibility(View.VISIBLE);
            signedInButtons.setVisibility(View.GONE);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
