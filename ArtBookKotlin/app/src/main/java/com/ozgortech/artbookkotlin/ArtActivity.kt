package com.ozgortech.artbookkotlin

import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.ozgortech.artbookkotlin.databinding.ActivityArtBinding
import java.util.jar.Manifest

class ArtActivity : AppCompatActivity() {
    lateinit var binding: ActivityArtBinding
    lateinit var permissionLauncher : ActivityResultLauncher<String>
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var database : SQLiteDatabase
    lateinit var imageData : Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var intent = intent


        var info = intent.getStringExtra("info")!!
        if (info.equals("old")){
            var art = intent.getSerializableExtra("art") as Art
            binding.artText.setText(art.artName)
            binding.artistText.setText(art.artistName)
            binding.yearText.setText(art.artYear)
            var uri : Uri = Uri.parse("art.image + ")
            binding.imageView.setImageURI(uri)
            binding.addArtButton.visibility = View.GONE
        }else{
            registerLauncher()
            database = this.openOrCreateDatabase("Arts", MODE_PRIVATE,null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts('artName' VARCHAR, 'artistName' VARCHAR, 'artYear' VARCHAR, 'image' VARCHAR)")
        }


    }

    fun selectImage(view : View){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale
                    (this,android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"You need to give permission",Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", View.OnClickListener { view ->
                        // Request Permission
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            }
            else{
                // Request Permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        else{
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }

    }

    fun save(view : View){



        var artName = binding.artText.text.toString()
        var artistName = binding.artistText.text.toString()
        var artYear = binding.yearText.text.toString()
        var foto = imageData.toString()

        if (artName.isNullOrEmpty() || artistName.isNullOrEmpty() || artYear.isNullOrEmpty() || foto.isNullOrEmpty()){
            Toast.makeText(this,"Lütfen tüm alanları doldurunuz",Toast.LENGTH_SHORT).show()
        }else{
            var sqlStatement : String = "INSERT INTO arts('artname','artistName','artYear','image') VALUES(?,?,?,?)"

            var statement = database.compileStatement(sqlStatement)
            statement.bindString(1,artName)
            statement.bindString(2,artistName)
            statement.bindString(3,artYear)
            statement.bindString(4,foto)

            statement.execute()

            var intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    fun registerLauncher(){

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK){
                    var intent = result.data!!
                    imageData = intent.data!!
                    binding.imageView.setImageURI(intent.data)

                }
            })

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(),
            ActivityResultCallback { result ->
                if (result == true){
                    val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }else{
                    Toast.makeText(this@ArtActivity,"Permission denied!",Toast.LENGTH_LONG).show()
                }
            })
    }
}