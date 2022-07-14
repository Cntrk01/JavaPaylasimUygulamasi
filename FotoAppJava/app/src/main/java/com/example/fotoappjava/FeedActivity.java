package com.example.fotoappjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fotoappjava.databinding.ActivityFeedBinding;
import com.example.fotoappjava.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {
    private ActivityFeedBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    ArrayList<Post> postList;
    RecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();
        postList=new ArrayList<>();
        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RecyclerAdapter(postList);
        binding.recyclerView.setAdapter(adapter);
    }

    public void getData(){
        firestore.collection("Path").orderBy("date", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error !=null){
                    Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                } if(value !=null){

                    for (DocumentSnapshot d:value.getDocuments()){
                        Map<String,Object> data=d.getData();
                        String email=(String) data.get("useremail");
                        String comment=(String) data.get("comment");
                        String downloadUrl=(String) data.get("downloadurl");
                        Post post=new Post(email,comment,downloadUrl);
                        postList.add(post);
                    }
                    adapter.notifyDataSetChanged(); //bunu burada yazmamız gerekiyor.Adapterda denedim çalışmadı.
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.postEkle){
                //post sayfası
            Intent intent=new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intent);
            finish();
        }else if(item.getItemId()==R.id.cikisYap){
            //cikis sayfasi
            Intent intent=new Intent(FeedActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}