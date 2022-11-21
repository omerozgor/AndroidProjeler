package com.ozgortech.instagramclone;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

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
import com.ozgortech.instagramclone.databinding.ActivityUploadBinding;

import java.util.HashMap;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    ActivityUploadBinding binding;

    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Uri imageData;

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance(); // veritabanı işlemleri için gerekli
        storage = FirebaseStorage.getInstance(); //depo işlemleri için gerekli
        reference = storage.getReference(); // bu da depoda nereye konulcağını tutan bir obje gibi
        // düşünebiliriz zaten depodan oluşturuluyor.

        registerLauncher();
    }


    public void upload(View view){

        if (imageData != null){ // görsel seçmeden basarsak uploada bişey olmasın diye

            // universel unique id, yani bununla random bir id oluşturuyor bizim için her seferinde
            // kaydedilen foto için random bir isim veriyoruz bunun sayesinde
            UUID uuid = UUID.randomUUID();
            String imagePath = "images/" + uuid + ".jpg"; // yolu dosya ismi içinde olacak şekilde
            // bir stringe verdik
            //referance child içinde koymak istediğimiz yeri yazıp putFile ile koyuyoruz ve bu yine uzun
            // sürebilecek bir işlem olduğu için asenkron çalıştırma ile listenerları kullanıp sonuca göre
            // tavır alıyoruz

            reference.child(imagePath).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //eğer gerçekten depomuza resmimizi koyabilmişsek yeni referans alıp direkt dosyanın
                    // olduğu yere gidebiliyoruz ve url'sini alabiliyoruz. Url alma işlemi de başarı ve
                    // başarısızlıkla sonuçlanabilir
                    StorageReference newReferance = storage.getReference(imagePath);
                    newReferance.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Eğer url yi başarılı bir şekilde alabildiysek artık elimizdeki tüm verileri
                            // veritabanına kaydetme vakti gelmiş demektir
                            String downloadUrl = uri.toString(); // başarılı olmuşsak download url'miz
                            // bize uri olarak veriliyor onu stringe çevirip kullanabiliriz.
                            String comment = binding.uploadCommentText.getText().toString();

                            FirebaseUser user = auth.getCurrentUser();
                            String username = user.getEmail();//user'ın emailine bu şekilde uğraşabiliriz

                            HashMap<String,Object> postData = new HashMap<>();
                            // Firebase de verilerimizi koleksiyonlar içine(mesela postlar, mesajlar yok efendim
                            // arabalar, hayvanlar gibi) kaydediyoruz ve kaydettiğimiz dökümanların field ve value
                            // değerleri oluyor(bir nevi key-value yani) bu nedenle hashmap yani sözlük ile kaydetmeye
                            // gayet müsait bir veri
                            postData.put("username",username);
                            postData.put("comment",comment);
                            postData.put("ImageUrl",downloadUrl);
                            postData.put("date", FieldValue.serverTimestamp());

                            firestore.collection("Posts").add(postData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //eğer cidden veritabanına kaydedebildiysek artık feedActivitesine dönme vakti gelmiştir
                                    Intent intent = new Intent(UploadActivity.this,FeedActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Bir resim seçin ve metin girin!",Toast.LENGTH_LONG).show();
        }

    }

    public void selectImage(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"You need to give permission!",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                System.out.println("hop");
            }
        }else{
            //go to gallery
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }

    }

    private void registerLauncher(){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    imageData = intent.getData();
                    binding.selectImageView.setImageURI(imageData);

                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result){
                    //galeriye gitme
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(UploadActivity.this, "Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}