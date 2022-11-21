package com.ozgortech.artbookkotlin

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.ozgortech.artbookkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var database: SQLiteDatabase
    lateinit var artArrayList: ArrayList<Art>
    lateinit var adapter: ArtAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artArrayList = ArrayList()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ArtAdapter(artArrayList)
        binding.recyclerView.adapter = adapter
        database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
        database.execSQL("CREATE TABLE IF NOT EXISTS arts('artName' VARCHAR, 'artistName' VARCHAR, 'artYear' VARCHAR, 'image' VARCHAR)")

        var cursor = database.rawQuery("SELECT * FROM arts",null)

        var artNameIx = cursor.getColumnIndex("artName")
        var artistNameIx = cursor.getColumnIndex("artistName")
        var artYearIx = cursor.getColumnIndex("artYear")
        var imageIx = cursor.getColumnIndex("image")

        while (cursor.moveToNext()){
            var artName = cursor.getString(artNameIx)
            var artistName = cursor.getString(artistNameIx)
            var artYear = cursor.getString(artYearIx)
            var image = cursor.getString(imageIx)

            var art = Art(artName,artistName,artYear,image)
            artArrayList.add(art)
            adapter.notifyDataSetChanged()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.art_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addArtItem){
            var intent = Intent(this,ArtActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}