package com.ozgortech.instagramclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ozgortech.instagramclone.databinding.ActivityFeedBinding;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    //Dipnot bu aktivitenin layoutunu kendi zevkim için SwipeRefreshLayout yaptım bu sayede ekranı
    // aşağı çektiğimiz zaman refresh yapıyor, öncelikle gradledan implemente ediyoruz
    // sonra ise bu arayüzü implemente ediyoruz
    ActivityFeedBinding binding;
    FirebaseAuth auth; // çıkış yapmak için lazım
    FirebaseFirestore firestore; // verileri çekmek için lazım
    ArrayList<Post> postArrayList;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        postArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(adapter);
        getData();

        binding.mySwipe.setOnRefreshListener(this);
        // mySwipe dediğimiz layoutumuz ona listener ekliyoruz zaten implemente ettiğimiz için bizim
        // sınıfımız da artık bir SwipeRefreshLayout.OnRefreshListener o nedenle this diyenbiliriz.


    }


    private void getData(){

        //Burada verileri çekmenin iki yolu var birincisi sürekli dinleyici ile real-time çekmek(mesela
        // mesajlaşma uygulamaları için kesin bu kullanılmalı) ikincisi ise veriyi tek seferlik çekmek
        // ben videoda anlatılandan farklı olarak tek seferlik çekmeyi kullandım ama real-time'ı da
        // alta yorum olarak yazdım, tek seferlikte get kullanırken realtime da addSnapShotListener kullanıyoruz
        //ayrıca bunları yazmadan önce filtreleme işlemleri kullanarak istediğimiz verinin istediğimiz şekilde
        // gelmesini sağlayabiliriz. Aşağıda tarihe göre en yeni olan en üstte gelsin dedik

        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // İşlem başarılı olduğunda bize QuerySnapShot diye bişey döndürüyor ondan
                // getDocuments ile verilerimizi bir DocumentSnapShot listesi olarak alabiliyoruz.

                postArrayList.clear();
                for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()) {
                    // Bu DocumentSnapShotları tek tek foreach ile gezip içeriklerini yani field'larını
                    // bir Map objesi olarak alabiliyoruz. Oradan da gerekli bilgileri anahtarları ile çekip
                    // yeni bir post objesine atıyoruz ve bir arraylistte postlarımızı topluyoruz
                    //recyclerViewde gösterebilmek için
                    Map<String,Object> data = documentSnapshot.getData();

                    String username = (String) data.get("username");
                    String comment = (String) data.get("comment");
                    String imageUrl = (String) data.get("ImageUrl");

                    Post post = new Post(username,comment,imageUrl);
                    postArrayList.add(post);

                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        /*firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null && error == null){

                    for (DocumentSnapshot documentSnapshot:value.getDocuments()) {
                        Map<String,Object> data = documentSnapshot.getData();

                        String username = (String) data.get("username");
                        String comment = (String) data.get("comment");
                        String imageUrl = (String) data.get("ImageUrl");

                        Post post = new Post(username,comment,imageUrl);
                        postArrayList.add(post);

                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // menülerimizi bağlamayı artık biliyoruz.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.sign_out_item){
            // Eğer signout seçilmiş ise tek satırcık kod ile çıkış işlemini yapabiliyoruz
            // ve intent yapıp main activitiye gidiyoruz.
            auth.signOut();
            Intent intent = new Intent(FeedActivity.this,MainActivity.class);
            finish();
            startActivity(intent);
        }else if(item.getItemId() == R.id.add_post_item){
            // Eğer yeni post eklenecekse de o aktiviteye gidiyoruz.
            // Burada kullanıcı geri tuşuna basp buraya geri gelebileceği için finish demiyoruz.
            Intent intent = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }



    void myUpdateOperation(){

        System.out.println("haloo");
        getData();
        binding.mySwipe.setRefreshing(false);
        //aşağı çekince datamızı refreshledik ve setRefreshing(false ile datamızı aldıktan sonra
        // dönmesini engelledik)
    }

    @Override
    public void onRefresh() {
        myUpdateOperation();
        //aşağı çekince olacakları bu sınıf içine yazıyoruz. Ben olacakları başka metoda yazıp onu burada çağırıdm.
    }
}