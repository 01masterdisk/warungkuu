package com.ssanto.warungku

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class registrasi : AppCompatActivity() {

    private lateinit var register: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var repassword: EditText

    //////FIREBASE ////////
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        email = findViewById(R.id.email) as EditText
        password = findViewById(R.id.password) as EditText
        repassword = findViewById(R.id.repassword) as EditText
        register = findViewById(R.id.register) as Button


        auth=Firebase.auth

        initevent()
    }

    private fun initevent() {
        register.setOnClickListener(View.OnClickListener {
            ////// Verify //////////
            if(email.text.toString().length > 0 && password.text.toString().length > 0 && repassword.text.toString().length > 0){
                if(password.text.toString().equals(repassword.text.toString())){
                    /////////////// SAVE FIREBASE /////////////
                    registerAccount()
                }else{
                    Toast.makeText(this,"Password Harus Sesuai dengan RePassword",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Data Harus Diisi !",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerAccount() {
        auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, "${user?.email} Berhasil Terdaftar !!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(baseContext, "Gagal Registrasi User", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        finish()
    }
}