package com.ozgortech.artbookwithfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ozgortech.artbookwithfragment.databinding.FragmentMainBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class MainFragment extends Fragment {

    FragmentMainBinding binding;
    ArtAdapter adapter;

    CompositeDisposable disposable;

    ArtDatabase database;
    ArtDao artDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable();

        database = Room.databaseBuilder(getContext(),ArtDatabase.class,"Arts").build();

        artDao = database.artDao();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = MainFragmentDirections.actionMainFragmentToArtFragment("new");
                Navigation.findNavController(view).navigate(action);
            }
        });

        disposable.add(artDao.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainFragment.this::handleResponse));
    }

    public void handleResponse(List<Art> artList){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtAdapter(artList);
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.clear();
    }
}