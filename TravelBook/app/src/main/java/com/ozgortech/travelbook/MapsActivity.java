package com.ozgortech.travelbook;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ozgortech.travelbook.databinding.ActivityMapsBinding;

import java.security.Permission;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    // Bu projeyi oluştururken google maps activity diyerek oluşturduk
    // Bu nedenle bazı kodlar hazır olarak geldi ayrıca google_maps_api_xml deki
    // yönergeleri izleyerek bir api anahtarı aldık
    // Burada kendisi bir GoogleMap objesi oluşturmuş ve view bindingi kendisi yapmış
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ActivityResultLauncher<String> permissionLauncher;

    Place selectedPlace;

    LocationManager locationManager;
    LocationListener locationListener;

    PlaceDatabase database;
    PlaceDao placeDao;

    double selectedLatitude;
    double selectedLongitude;
    String info;

    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        compositeDisposable = new CompositeDisposable(); // kullan-at torba

        registerLauncher();

        database = Room.databaseBuilder(getApplicationContext(), PlaceDatabase.class, "Places")
                //.allowMainThreadQueries()
                .build();
        placeDao = database.placeDao();

        binding.saveButton.setEnabled(false);
        binding.placeText.setEnabled(false);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Bu metod da hazır geliyor zaten adından belli ne zaman çalıştığı
        // Globalde yazdığı mapi burada parametre ile gelene eşitlemiş
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        info = intent.getStringExtra("info");
        if (info.matches("old")) { // Eğer recyclerVİewden bişey seçip geldiysek
            //konumla monumla ilgilenmesine gerek yok save tuşunu kapatsın delete tuşunu açsın tıkladığımız yerin
            //konumunu göstersin
            selectedPlace = (Place) intent.getSerializableExtra("place");
            LatLng latLng = new LatLng(selectedPlace.latitute, selectedPlace.longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(selectedPlace.name));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            binding.deleteButton.setVisibility(View.VISIBLE);
            binding.saveButton.setVisibility(View.GONE);
            binding.placeText.setText(selectedPlace.name);
        }else{
            // he eğer yeni bişey ekleyeceksek eğer bulunan konumda başlatsın save butonu açsın deleteyi kapatsın
            binding.saveButton.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.GONE);

            // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            //LatLng Latidute(enlem) ve longitude(boylam) kelimelerinin birleşimi ile
            // oluşturulmuş konum tutmak için kullanılan bir sınıf
            // Daha sonra mape marker ekleyip pozisyonunu ve başlığını vermiş
            // moveCamera ile de harita açılırkenki başlangıç pozisyonunu sydneye ayarlamış
        /*
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
        Bu şekilde newLatLngZoom diyerek de istediğimiz konumda istediğimiz yakınlaştırma
        oranı ile başlatabiliyoruz. Galiba 0 ile 25 arası değer alıyor deneyerek
        test edilebilir
         */

            // Konum Yöneticisi
            // Kullanıcının konumunu ile çalımak için 1 sınıf ve 1 arayüzden yardım alıyoruz. LocationManager
            // ve LocationListener
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // Kullanıcının konumu ile işlem yapmamızı sağlamak için bir sınıf. Böyle initialize ediliyor

            // LocationListener ise eli kulağında bir interface. Konum değiştiği zaman ne yapılacağını
            // onLocationChanged içine yazıyoruz
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    // Buradaki location LngLtd nin barındırdığı bildiler ve ekstra olarak
                    // yükseklik, hız gibi daha fazla bilgiyi barındıran bir sınıf.
                    // Yani bu sınıftan da enlem ve boylama ayrıca çok daha ayrıntılı konum bilgilerine
                    // erişebilinir
                    System.out.println("locationn = " + location.toString());
                }
            };

            // Tabi ki konum istemek de izin gerektiriyor ve yine izin işlemlerini yapıyoruz burayı
            // uzun uzadıya anlatmayacağım
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    Snackbar.make(binding.getRoot(), "You need to give permission",
                            Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                        }
                    }).show();
                } else {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            } else {
                // Bir şekilde izni aldıktan sonra yaptığımız işlem işte burada.. locationManager ile
                // konum güncellemelerini talep ediyoruz, servis sağlayıcı olarak gps kullanmak en mantıklısı
                // 2. parametre ne kadar sürede bir yapayım diye soruyor, 3. parametre ise konum ne kadar
                // değişirse ben de değiştireyim diyor, 4. sü ise listener istiyor elimizdeki liztener'ı
                // veriyoruz.
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                // Bu alttaki kısım ise son bilinen konum ile ilgili. Uygulama açıldığında ve izinler tamam
                // olduğunda kamerayı son bilinen konumdan başlatmak için
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastLocation != null) {
                    LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                }

                mMap.setMyLocationEnabled(true); // Bu ise kullanıcının o anki konumunu mavi nokta olarak
                // gösteriyor
            }
        }

    }

    private void registerLauncher() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (lastLocation == null) {
                            LatLng lastUserLocation = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 15));
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Permission needed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Uzun tıklanınca ne olacak
        if (info.matches("new")){ // Uzun tıklama sadece yeni birşeyler ekleyeceğimiz zaman etkin
            // olsun istiyorum
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng));

            binding.saveButton.setEnabled(true);
            binding.placeText.setEnabled(true);

            selectedLatitude = latLng.latitude;
            selectedLongitude = latLng.longitude;
        }

    }

    public void save(View view) {
        Place place = new Place(binding.placeText.getText().toString(), selectedLatitude, selectedLongitude);

        //threading -> Main (UI), Default (CPU intensive), IO (network, database)
        compositeDisposable.add(placeDao.insert(place)
                .subscribeOn(Schedulers.io()) // io threade abone ol
                .observeOn(AndroidSchedulers.mainThread())// şu anlık opsiyonel bu satır
                .subscribe());
        //placeDao.insert(place);
        /*
        Biliyorum fazlasıyla kafa karıştırıcı... Şimdi çeşitli threadler var io da input-output demek veritabanı
        işlemleri o thread'de yapılıyormuş o nedenle io thread'e abone ol diyoruz, mainThreadimizde gözlemleyeceğiz
        diyoruz ama bize bişey dönmediği için açıkçası şu an bi işe yaramıyor orası, son subscribe parantezinin
        içine de işlem bitince olacak şeyleri ele alıyoruz ama şu anda yapacak bişey yok bize bişey döndürmediği
        için bu kısmı Select kısmında detaylı olarak göreceğiz.

         */

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void delete(View view) {
        compositeDisposable.add(placeDao.delete(selectedPlace)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear(); // tek kullanımlık torbayı temizle
    }
}