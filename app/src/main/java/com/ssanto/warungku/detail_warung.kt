package com.ssanto.warungku

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ssanto.warungku.model.warung
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream

class detail_warung : AppCompatActivity(),LocationListener {

    private lateinit var editWarung: FloatingActionButton
    private lateinit var getLocation: FloatingActionButton
    private lateinit var nama_warung: EditText
    private lateinit var koordinat_warung: EditText
    private lateinit var alamat_warung: EditText
    private lateinit var submit: Button
    private lateinit var title: TextView
    private lateinit var photo: ImageView

    //////FIREBASE ////////
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null

    ///// LOCATION /////////
    private lateinit var locationManager: LocationManager

    private var PERMITION_REQUEST=44123
    private var PERMITION_REQUEST_CAMERA=423123
    private var REQUEST_CAMERA=423123


    private var flag=false
    private var docID=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_warung)

        flag = intent.extras?.getString("CALLER").equals("add")

        editWarung = findViewById(R.id.editButton) as FloatingActionButton
        getLocation = findViewById(R.id.koordinatButton) as FloatingActionButton
        nama_warung = findViewById(R.id.nama_warung) as EditText
        alamat_warung = findViewById(R.id.alamat) as EditText
        koordinat_warung = findViewById(R.id.koordinat) as EditText
        title = findViewById(R.id.title) as TextView
        submit = findViewById(R.id.submit) as Button
        photo = findViewById(R.id.imageView) as ImageView

        auth = Firebase.auth
        db = Firebase.firestore

        initTree()
    }

    private fun initTree() {
        if(flag){
            title.text="Tambah/Edit Warung"
            editWarung.visibility= GONE
            getLocation.visibility= VISIBLE
            submit.visibility = VISIBLE

            nama_warung.isEnabled = true
            alamat_warung.isEnabled = true

            photo.setOnClickListener(View.OnClickListener {
                /// Ambil Dari Camera //////
                takeGambarWarung()
            })
            getLocation.setOnClickListener(View.OnClickListener {
                /// GET KOORDINAT ////
                getKoordinatWarung()
            })
            submit.setOnClickListener(View.OnClickListener {
                /// SAVE WARUNG //////
                if(nama_warung.text.toString().length > 0 && alamat_warung.text.toString().length > 0 && koordinat_warung.text.toString().length > 0 && photo.drawable !=null){
                    saveWarung()
                }else{
                    Toast.makeText(this,"Data Warung Harus Terisi Semua !!!",Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            editWarung.visibility= VISIBLE
            getLocation.visibility= GONE
            submit.visibility = GONE
            intent.extras?.getString("ID")?.let {
                docID = it
                initData()
            }

            nama_warung.isEnabled = false
            alamat_warung.isEnabled = false

            editWarung.setOnClickListener(View.OnClickListener {
                flag = true
                initTree()
            })
        }
    }

    private fun takeGambarWarung() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMITION_REQUEST_CAMERA)
        }else{
            startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE),REQUEST_CAMERA)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_CAMERA && resultCode == RESULT_OK && data!=null){
            Glide.with(this).load(data.extras?.get("data") as Bitmap).into(photo)
        }
    }

    private fun getKoordinatWarung() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMITION_REQUEST)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onLocationChanged(p0: Location) {
        koordinat_warung.setText("${String.format("%.9f",p0.latitude)} ${String.format("%.9f",p0.longitude)}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMITION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                getKoordinatWarung()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == PERMITION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                takeGambarWarung()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveWarung() {
        var image=imageViewToB64(photo)
        val data = hashMapOf(
            "nama" to "${nama_warung.text.toString()}",
            "koordinat" to "${koordinat_warung.text.toString()}",
            "alamat" to "${alamat_warung.text.toString()}",
            "createdby" to "${ auth.currentUser?.email}",
            "base64" to "${image}"
        )
        if(docID.equals("")){
            ///////// SAVE ////////////////////////
            db?.collection("warung")?.add(data as Map<String, Any>)
                ?.addOnSuccessListener{
                    Toast.makeText(this,"Berhasil Menambahkan Warung", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this,"Gagal Menambahkan Warung", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }else{
            ///////// EDIT ////////////////////////
            db?.collection("warung")?.document(docID)?.update(data as Map<String, Any>)
                ?.addOnSuccessListener{
                    Toast.makeText(this,"Warung Berhasil di Perbarui", Toast.LENGTH_SHORT).show()
                    finish()
                }
                ?.addOnFailureListener {
                    Toast.makeText(this,"Warung Gagal Diperbarui", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

    }

    private fun imageViewToB64(photo: ImageView): String {
        photo.buildDrawingCache()
        var b = photo.getDrawingCache() as Bitmap
        var outStrrem:ByteArrayOutputStream = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.PNG,80,outStrrem)
        return Base64.encodeToString(outStrrem.toByteArray(),0);
    }

    private fun initData() {
        db?.collection("warung")?.document(docID)?.get()
            ?.addOnSuccessListener { result ->

               if(result!=null){
                   val base64 = result.get("base64") as String

                   nama_warung.setText(result?.get("nama") as String)
                   koordinat_warung.setText(result?.get("koordinat") as String)
                   alamat_warung.setText(result?.get("alamat") as String)

                   Glide.with(this).load("data:image/jpeg;base64,${base64}").into(photo)
                }else{
                    Toast.makeText(this,"No DATA !!!", Toast.LENGTH_LONG).show()
                }

            }
            ?.addOnFailureListener {
                Toast.makeText(this,"Failed to Get Data !!!", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user==null){
            var i = Intent(this,daftar_warung::class.java)
            i.flags =  Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }
}