package com.ozgortech.retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ozgortech.retrofit.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.schedulers.Timed;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    Retrofit retrofit;
    Gson gson;
    String BASE_URL = "https://api.nomics.com/v1/";
    ArrayList<CryptoModel> cryptoModels;
    CryptoAdapter cryptoAdapter;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        cryptoModels = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        // https://raw.githubusercontent.com/atilsamancioglu/K21-JSONDataSet/master/crypto.json
        //https://api.nomics.com/v1/currencies/ticker?key=60e92823fba866d048a5144933ff36927d6921d5

        gson = new GsonBuilder().setLenient().create(); // gson objesi böyle oluşturuluyor

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson)).build();
        // retrofit ise böyle oluşturuluyor

        getDataFromAPI();
    }

    void getDataFromAPI(){
        CryptoAPI cryptoAPI = retrofit.create(CryptoAPI.class); // Apimizi böyle initialize ediyoruz

        Observable.interval(4, TimeUnit.SECONDS).timeInterval()
                .flatMap(new Function<Timed<Long>, ObservableSource<List<CryptoModel>>>() {
                    @Override
                    public ObservableSource<List<CryptoModel>> apply(Timed<Long> longTimed) throws Throwable {
                        return cryptoAPI.getData();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse);
/*
        Call<List<CryptoModel>> call = cryptoAPI.getData(); // bu get data call'lu bişey
        // döndürüyordu hatırlarsanız bu call'u enqueue ile asenkron olarak ele alabiliriz
        // cevabını response ile ele alabiliriz

        call.enqueue(new Callback<List<CryptoModel>>() {
            @Override
            public void onResponse(Call<List<CryptoModel>> call, Response<List<CryptoModel>> response) {
                if (response.isSuccessful()) {
                    // başarılı ise response.body() ile geri dönen ve kripto paralardan oluşan
                    // listemizi ele alabiliriz.
                    cryptoModels = new ArrayList<>(response.body());
                    cryptoAdapter = new CryptoAdapter(cryptoModels,MainActivity.this);
                    binding.recyclerView.setAdapter(cryptoAdapter);
                    cryptoAdapter.notifyDataSetChanged();

                    for (CryptoModel cryptoModel:cryptoModels) {
                        System.out.println(cryptoModel.getCurrency());
                    }
                }

            }

            @Override
            public void onFailure(Call<List<CryptoModel>> call, Throwable t) {
                // başarısızlık durumu
                System.out.println(t.getLocalizedMessage());
            }
        }); */
    }

    private void handleResponse(List<CryptoModel> cryptoModelList) {
        cryptoModels = new ArrayList<>(cryptoModelList);
        cryptoAdapter = new CryptoAdapter(cryptoModels,MainActivity.this);
        binding.recyclerView.setAdapter(cryptoAdapter);
        cryptoAdapter.notifyDataSetChanged();

    }

}