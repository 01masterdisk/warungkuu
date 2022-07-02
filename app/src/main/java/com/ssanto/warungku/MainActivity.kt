package com.ssanto.warungku
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class MainActivity() : AppCompatActivity() {

    private lateinit var register: TextView
    private lateinit var login:Button
    private lateinit var email:EditText
    private lateinit var password:EditText

    //////FIREBASE ////////
    private lateinit var auth:FirebaseAuth

    private var out = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        register = findViewById(R.id.register) as TextView
        login = findViewById(R.id.login) as Button
        email = findViewById(R.id.email) as EditText
        password = findViewById(R.id.password) as EditText

        auth=Firebase.auth

        initEvent()
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user!=null){
            startActivity(Intent(this,daftar_warung::class.java))
        }
    }

    private fun initEvent() {
        register.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,registrasi::class.java))
        })

        login.setOnClickListener(View.OnClickListener {
            if(email.text.toString().length>0 && password.text.toString().length > 0){
                doLogin()
            }else{
                Toast.makeText(this,"Email/PhoneNumber dan Password Harus diisi !",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun doLogin() {
        auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Selamat Datang, ${user?.email}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,daftar_warung::class.java))
                } else {
                    Toast.makeText(this, "Email/PhoneNumber atau Password Salah !!!", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onBackPressed() {
        if(out){
            finish()
        }else{
            var h = Handler()
            out=true
            Toast.makeText(this,"Tekan Sekali lagi untuk Keluar dari Apliaksi",Toast.LENGTH_SHORT).show()
            h.postDelayed(Runnable {
                out=false
            },1000)
        }

    }
}