package com.ozgortech.landmarkbookwithrw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ozgortech.landmarkbookwithrw.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    TextView nameText,contryText;
    ImageView image;
    Landmark landmark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        nameText = findViewById(R.id.nameText);
        contryText = findViewById(R.id.countryText);
        image = findViewById(R.id.imageView);
        //Intent intent = getIntent();
        //landmark = (Landmark) intent.getSerializableExtra("landmark");
        Singleton singleton = Singleton.getInstance();//oluşturdupumuz nesneyi dönecek
        landmark = singleton.getLandmark();
        nameText.setText(landmark.getName());
        contryText.setText(landmark.getCountry());
        image.setImageResource(landmark.getImage());


    }
}