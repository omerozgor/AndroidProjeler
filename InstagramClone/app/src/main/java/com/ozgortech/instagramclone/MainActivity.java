package com.ozgortech.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ozgortech.instagramclone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    FirebaseAuth auth; // Bir adet Authentication objesi oluşturuyoruz kullanıcı- hesap işlemleri için lazım

    FirebaseUser user; // Bu kullanıcıyı telefonda en son herhangi bir oturum açık kalmış mı diye
        // kontrol amaçlı kullanıyoruz.

    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        auth = FirebaseAuth.getInstance(); // böyle başlatıyoruz
        user = auth.getCurrentUser(); // eğer oturumu açık kalan kullanıcı varsa onu döndürüyour

        if (user != null) { // ama döndürmeye de bilir tabi çıkış yapılmış ise veya
            // hiç giriş yapılmamış ise
            Intent intent = new Intent(MainActivity.this, FeedActivity.class);
            finish();
            startActivity(intent);
            // eğer zaten mevcut bir kullanıcı var ise vakit kaybetmeden feed aktivitesine geçiyoruz
        }

    }


    public void logIn(View view) {

        username = binding.usernameText.getText().toString();
        password = binding.passwordText.getText().toString();

        // metinlerimizi metin kutularından çektikten sonra boş olmamalarına özen gösteriyoruz

        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Enter username and password", Toast.LENGTH_LONG).show();
        } else {
            // var olan kullanıcı ile giriş yapılmasını sağlıyoruz ve listenerlar ile başarı veya başarısızlık
            //durumunda aksiyon almasını sağlıyoruz
            auth.signInWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //başarılı ise intent ile feed activitesine gidiyoruz
                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    finish();
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //başarısızsa serverdan yani firebase'den gelen neden hatalı olduğu ile ilgili
                    // mesajı kullanıcıya gösteriyoruz
                    //mesela kullanıcı adı veya şifre hatalı olabilir
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    public void signUp(View view) {

        username = binding.usernameText.getText().toString();
        password = binding.passwordText.getText().toString();

        // metinlerimizi metin kutularından çektikten sonra boş olmamalarına özen gösteriyoruz

        if (username.isEmpty() || password.isEmpty()) {

            Toast.makeText(this, "Enter username and password", Toast.LENGTH_LONG).show();
        } else {
            // yeni bir kullanıcı oluşturmasını sağlıyourz ve listenerlar ile başarı veya başarısızlık
            //durumunda aksiyon almasını sağlıyoruz
            auth.createUserWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    //başarılı ise intent ile feed activitesine gidiyoruz
                    Intent intent = new Intent(MainActivity.this, FeedActivity.class);
                    finish();
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //başarısızsa serverdan yani firebase'den gelen nedenhatalı olduğu ile ilgili
                    // mesajı kullanıcıya gösteriyoruz
                    //mesela kullanıcı adı email biçiminde olmalı veya şifre 6 haneden az olmamalı
                    // firebase öyle istiyor aksi durumlarda hata mesajı veriyor
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}