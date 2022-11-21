package com.ozgortech.landmarkbookkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ozgortech.landmarkbookkotlin.databinding.ActivityLandmarkBinding

class LandmarkActivity : AppCompatActivity() {
    lateinit var binding: ActivityLandmarkBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent = intent

        // casting kotlinde böyle
        var landmark = intent.getSerializableExtra("landmark") as Landmark

        binding.nameText.text = landmark.name
        binding.countryText.text = landmark.country
        binding.imageView.setImageResource(landmark.image)
    }
}