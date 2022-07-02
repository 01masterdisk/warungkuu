package com.ssanto.warungku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ssanto.warungku.adapter.RecyclerAdapter
import com.ssanto.warungku.model.warung

class daftar_warung : AppCompatActivity() {
    private lateinit var add_warung : FloatingActionButton
    private lateinit var list_warung : RecyclerView
    private var adapter : RecyclerAdapter? = null
    private var data:ArrayList<warung> = ArrayList()

    private var out = false

    //////FIREBASE ////////
    private lateinit var auth: FirebaseAuth
    private var db: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_warung)

        add_warung = findViewById(R.id.add_warung) as FloatingActionButton
        list_warung = findViewById(R.id.list_warung) as RecyclerView

        auth = Firebase.auth
        db = Firebase.firestore
        initevent()
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user==null){
            finish()
        }
    }

    private fun initListWarung() {
        if (adapter == null) {
            adapter = RecyclerAdapter( this,data)

            list_warung.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            list_warung.adapter = adapter
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        getDataWarung()
    }

    private fun initevent() {
        add_warung.setOnClickListener(View.OnClickListener {
            var i = Intent(this,detail_warung::class.java)
            i.putExtra("CALLER","add")
            startActivity(i)
        })
    }

    private fun getDataWarung() {
        data.clear()
        db?.collection("warung")?.whereEqualTo("createdby", auth.currentUser?.email!!)?.get()
            ?.addOnSuccessListener { result ->
                if(result.size() > 0){
                    for(document in result){
                        Log.e("AAAA", "getDataWarung: ${document.id}" )
                        data.add(
                            warung(
                                document.id,
                                document.get("nama") as String?,
                                document.get("koordinat") as String?,
                                document.get("alamat") as String?,
                                document.get("base64") as String?
                            )
                        );
                    }
                    initListWarung()
                }else{
                    Toast.makeText(this,"No DATA !!!",Toast.LENGTH_LONG).show()
                }

            }
            ?.addOnFailureListener {
                Toast.makeText(this,"Failed to Get Data !!!",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onBackPressed() {
        if (out){
            auth.signOut()
            finish()
        }else{
            var h = Handler()
            out=true
            Toast.makeText(this,"Kembali Sekali lagi untuk Logout dari aplikasi ini !!!",Toast.LENGTH_SHORT).show()
            h.postDelayed(
                Runnable {
                        out=false
                },
                1500
            )
        }

    }


}

