package com.example.werayouapp.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.werayouapp.Activity.SetupActivity;
import com.example.werayouapp.R;
import com.example.werayouapp.intro.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerificationCodeActivity extends AppCompatActivity {
    TextView numberIntent;
    EditText phoneText;
    Button buttonVerifier;
    Button resendCode;
    String code;
    TextView backText;
    String phoneVerificationId;
     FirebaseAuth fbAuth;
     ProgressBar progressBar;
     String country;
     String phone;
     PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
     PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        numberIntent=findViewById(R.id.numberIntent);
        phoneText=findViewById(R.id.phoneText);
        buttonVerifier=findViewById(R.id.buttonVerifier);
        resendCode=findViewById(R.id.resendCode);
        progressBar=findViewById(R.id.progressBar);
        backText=findViewById(R.id.backText);
        country=getIntent().getStringExtra("country");
        phone=getIntent().getStringExtra("phone");
        phoneVerificationId=getIntent().getStringExtra("verificationId");
        numberIntent.setText(phone);
        fbAuth = FirebaseAuth.getInstance();
        buttonVerifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("phoneVerificationId",phoneVerificationId);
                progressBar.setVisibility(View.VISIBLE);
                verificode();
            }
        });
        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                resendCode();
            }
        });

        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VerificationCodeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }

    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(
                            PhoneAuthCredential credential) {
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded

                        };
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        phoneVerificationId = verificationId;
                        resendToken = token;

                        //verifyButton.setEnabled(true);
                       // buttonVerifier.setEnabled(false);
                        // resendButton.setEnabled(true);
                    }
                };
    }

    public void resendCode() {
        setUpVerificatonCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks,
                resendToken);
    }


    void verificode(){
         code = phoneText.getText().toString();
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredentialVerificationCode(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            progressBar.setVisibility(View.INVISIBLE);
//                            FirebaseUser user = task.getResult().getUser();
//                            Intent intent = new Intent(VerificationCodeActivity.this, SetupActivity.class);
//                            intent.putExtra("country",country);
//                            intent.putExtra("phone",phone);
//                            startActivity(intent);
//                            finish();
                            //code renvoyer
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(VerificationCodeActivity.this,"Code resend",Toast.LENGTH_LONG).show();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void signInWithPhoneAuthCredentialVerificationCode(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent(VerificationCodeActivity.this, SetupActivity.class);
                            intent.putExtra("country",country);
                            intent.putExtra("phone",phone);
                            startActivity(intent);
                            finish();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
