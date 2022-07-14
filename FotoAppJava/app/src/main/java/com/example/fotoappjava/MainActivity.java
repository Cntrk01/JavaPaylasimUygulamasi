package com.example.fotoappjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fotoappjava.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        auth=FirebaseAuth.getInstance();



        FirebaseUser user=auth.getCurrentUser();
        if(user !=null){
            Intent intent=new Intent(MainActivity.this,FeedActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void signUp(View view){
        String  username=binding.userName.getText().toString();
        String pass=binding.password.getText().toString();
        if(username.equals("") || pass.equals("")){
            Toast.makeText(this,"Kullanıcı adı yada şifre boş",Toast.LENGTH_SHORT).show();
        }else{
            auth.createUserWithEmailAndPassword(username,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                        Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                        startActivity(intent);
                        finish();
                    }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });

        }


    }
    public void login(View view){
        String  username=binding.userName.getText().toString();
        String pass=binding.password.getText().toString();
        if(username.equals("") || pass.equals("")){
            Toast.makeText(this,"Kullanıcı adı yada şifre boş",Toast.LENGTH_SHORT).show();
        }else{
            auth.signInWithEmailAndPassword(username,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Intent intent=new Intent(MainActivity.this,FeedActivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}