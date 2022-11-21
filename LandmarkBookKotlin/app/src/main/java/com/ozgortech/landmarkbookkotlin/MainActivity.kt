package com.ozgortech.landmarkbookkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozgortech.landmarkbookkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var landmarkList = ArrayList<Landmark>()

        var eiffel = Landmark("Eiffel","France",R.drawable.eiffel)
        var pisa = Landmark("Pisa","Italy",R.drawable.pisa)
        var kizKulesi = Landmark("Kız kulesi","Turkey",R.drawable.kizkulesi)
        var londonEye = Landmark("London Eye","England",R.drawable.londoneye)

        landmarkList.add(eiffel)
        landmarkList.add(pisa)
        landmarkList.add(kizKulesi)
        landmarkList.add(londonEye)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        var adapter = LandmarkAdapter(landmarkList)
        binding.recyclerView.adapter = adapter

        // Singleton

        MySingleton.myLandmark = null

        // Kotlinde singleton kullanmak javaya göre çok daha kolay
        // dosyalardan bir tane obje oluşturuyoruz onun içine yazılan her değişken
        // her yerde erişilebilir oluyor (gerçi riskli gibi ama neyse hatta biraz farklı yani)
    }
}