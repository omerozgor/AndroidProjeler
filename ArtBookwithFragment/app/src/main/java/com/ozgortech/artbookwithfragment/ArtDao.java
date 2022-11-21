package com.ozgortech.artbookwithfragment;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {

    @Query("SELECT * FROM Art")
    Flowable<List<Art>> getAll();

    @Delete
    Completable delete(Art art);

    @Insert
    Completable insert(Art art);


}
