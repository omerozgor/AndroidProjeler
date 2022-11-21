package com.ozgortech.artbookwithfragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.room.Room;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ozgortech.artbookwithfragment.databinding.FragmentArtBinding;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ArtFragment extends Fragment {

    FragmentArtBinding binding;
    ActivityResultLauncher<String> permissionLauncher;
    ActivityResultLauncher<Intent> activityResultLauncher;

    CompositeDisposable disposable;

    ArtDatabase database;
    ArtDao artDao;

    Uri imageData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerLauncher();
        disposable = new CompositeDisposable();

        database = Room.databaseBuilder(getContext(),ArtDatabase.class,"Arts").build();

        artDao = database.artDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentArtBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null){
            String info = ArtFragmentArgs.fromBundle(getArguments()).getInfo();
            if (info.matches("old")){
                binding.button.setText("DELETE");
                binding.editTxtArtName.setText(ArtFragmentArgs.fromBundle(getArguments()).getArt().getName());
                binding.editTextArtistName.setText(ArtFragmentArgs.fromBundle(getArguments()).getArt().getArtist());
                binding.editTextYear.setText(ArtFragmentArgs.fromBundle(getArguments()).getArt().getYear());
                Uri imageUri = Uri.parse(ArtFragmentArgs.fromBundle(getArguments()).getArt().getFoto());
                binding.imageView.setImageURI(imageUri);

                binding.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        disposable.add(artDao.delete(ArtFragmentArgs.fromBundle(getArguments()).getArt())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe());

                        NavDirections action = ArtFragmentDirections.actionArtFragmentToMainFragment();
                        Navigation.findNavController(view).navigate(action);
                    }
                });

            }else{
                binding.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED){
                            if (ActivityCompat.shouldShowRequestPermissionRationale
                                    (getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
                                Snackbar.make(view,"Permission needed!",Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //request permission
                                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                                            }
                                        }).show();
                            }else{
                                //requestPermission
                                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                            }
                        }else{
                            //galeriye intent
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            activityResultLauncher.launch(intentToGallery);
                        }
                    }
                });

                binding.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = binding.editTxtArtName.getText().toString();
                        String artist = binding.editTextArtistName.getText().toString();
                        String year = binding.editTextYear.getText().toString();
                        String foto = imageData.toString();

                        Art art = new Art(name,artist,year,foto);


                        disposable.add(artDao.insert(art)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe());

                        NavDirections action = ArtFragmentDirections.actionArtFragmentToMainFragment();
                        Navigation.findNavController(view).navigate(action);

                    }
                });
            }
        }



    }


    private void registerLauncher(){
        activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                if (result.getResultCode() == getActivity().RESULT_OK){
                                    Intent intent = result.getData();
                                    imageData = intent.getData();
                                    binding.imageView.setImageURI(imageData);
                                }
                            }
                        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result == true) {
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }else {
                    Toast.makeText(getContext(), "Permission needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}