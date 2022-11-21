package com.ozgortech.landmarkbookwithrw;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.ozgortech.landmarkbookwithrw.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //Bu sefer landmark book uygulamamızı listview ile değil de recycler view ile yapacağız
    //Maalesef RecyclerView kendi sınıfından miras alan bir adaptör yapmamızı istiyor
    //İşin zor kısmı bu işte. Kendimiz adaptör için bir sınıf oluşturacağız
    private ActivityMainBinding binding;

    ArrayList<Landmark> landmarks;
    ArrayList<String> landmarkNames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        landmarks = new ArrayList<>();


        Landmark pisa = new Landmark("Pisa","Italy",R.drawable.pisa);
        Landmark eiffel = new Landmark("Eiffel","France",R.drawable.eiffel);
        Landmark kizKulesi = new Landmark("Kız Kulesi","Turkey",R.drawable.kizkulesi);
        Landmark londonEye = new Landmark("London Eye","UK",R.drawable.londoneye);

        landmarks.add(pisa);
        landmarks.add(eiffel);
        landmarks.add(kizKulesi);
        landmarks.add(londonEye);


        binding.landmarkRecycle.setLayoutManager(new LinearLayoutManager(this));//recycler
        //view bir grid şeklinde birde linear layout şeklinde olabiliyor. Biz alt alta metin yazdıra
        //cağımızdan layoutunu linear olarak ayarladık.
        LandmarkAdapter adapter = new LandmarkAdapter(landmarks);//adaptörümüzü oluşturduk
        binding.landmarkRecycle.setAdapter(adapter);//adaptörünü set ettik


    }
}