package com.example.fotoappjava;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;
import com.example.fotoappjava.databinding.ActivityUploadBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {
    private ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri imageData;
    //Bitmap selectedImage;
    StorageReference storage;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        auth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storage=firebaseStorage.getReference(); //storage dosyas??n?? referans al??yoruz i??lemleri oraya yaz??caz.
    }

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye eri??im i??in izin gerekli",Snackbar.LENGTH_INDEFINITE).setAction("??zin Ver", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //izin iste
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            Intent intenToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intenToGalery);

        }
    }
    public void uploadButton(View view){
        //imageData uri olarak kay??t edicez.Zaten registerLauncherda biz bunu ald??k.??imdi kontrol ediyoruz.imageDatada uri olarak veri varm diye
        if(imageData !=null){
            UUID uid=UUID.randomUUID();
            String imageName="images/" +uid+".jpg  ";
            //burada resim storage dosyas??na yaz??ld??.Onsucces ile oldu
            storage.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Burdada yaz??lan veriyi getiricez
                    StorageReference newReference=firebaseStorage.getReference(imageName);//resimleri getirece??im i??in referans??na resim verdim
                    newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl=uri.toString();// g??rselimizin indirilme urlsi bu art??k
                            String command=binding.yorumText.getText().toString();//kullan??c??n??n yazd??g?? yorum
                            FirebaseUser user=auth.getCurrentUser(); //hangi kullan??c??n??n i??lem yapt??g??n?? da hesapl??yoruz
                            String email=user.getEmail(); //kullan??c??n??n emailini de ald??k
                            //Art??k veritaban??na kay??t edece??imiz veriler haz??r.

                            HashMap<String,Object> hashMap=new HashMap<>();
                            hashMap.put("useremail",email);
                            hashMap.put("downloadurl",downloadUrl);
                            hashMap.put("comment",command);
                            hashMap.put("date", FieldValue.serverTimestamp());


                            firebaseFirestore.collection("Path").add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                        Intent intent=new Intent(UploadActivity.this,FeedActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void registerLauncher(){
        //parantez i??i anlam??:1.parametre veriyi almaya gidiyor 2.parametre diyor ki tamam veriyi ald??k napcaz sonuc i??lemini yapcaz
        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK){
                   Intent intentFromResult= result.getData();
                   if(intentFromResult !=null){
                       imageData=intentFromResult.getData(); //verinin lokasyonu
                       binding.imageView.setImageURI(imageData);

                       //Di??er yoll
                       /*
                       try {
                           if(Build.VERSION.SDK_INT>=28){
                               ImageDecoder.Source source=ImageDecoder.createSource(UploadActivity.this.getContentResolver(),imageData);
                               selectedImage=ImageDecoder.decodeBitmap(source);
                               binding.imageView.setImageBitmap(selectedImage);
                           }else{
                                selectedImage=MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                           }
                       }catch (Exception e){
                            e.printStackTrace();
                       }*/
                   }
                }
            }
        });
        //izin verildi mi veriilmedimi
        permissionLauncher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //izin veri??mi??se zaten galeriye gidicez.
                    Intent intenToGalery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intenToGalery);
                }else{//??zin vermezsen ilk buras?? ??al????yor sonra Snackbar cal??s??yor.
                    Toast.makeText(UploadActivity.this,"??zin Gerekli",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}