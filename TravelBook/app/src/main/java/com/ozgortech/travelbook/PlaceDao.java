package com.ozgortech.travelbook;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface PlaceDao {

    @Query("SELECT * FROM Place")
    Flowable<List<Place>> getAll();

    @Delete
    Completable delete(Place place);

    @Insert
    Completable insert(Place place);

}
