package com.android.otpgeneratorusingfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.android.otpgeneratorusingfirebase.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
lateinit var binding: ActivityMainBinding
lateinit var sendOtp : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnGenerateOtp.setOnClickListener {
            if (binding.etPhoneNum.text.toString().length == 10) {
                FirebaseApp.initializeApp(this)
                FirebaseAuth.getInstance().addAuthStateListener {
                    val options = PhoneAuthOptions.newBuilder(it)// it-> firebase auth token.
                        .setPhoneNumber("+91" + binding.etPhoneNum.text.toString())
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)// Activity (for callback binding)
                        .setCallbacks(object :
                            PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                            override fun onVerificationCompleted(p0: PhoneAuthCredential){
                                Toast.makeText(applicationContext,
                                    "completed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            override fun onVerificationFailed(p0: FirebaseException){
                                Toast.makeText(applicationContext,
                                    "failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            override fun onCodeSent(
                                otp: String, // this is the otp which is send by firebase
                                token: PhoneAuthProvider.ForceResendingToken
                            ) {
                                sendOtp = otp // i store this in one global variable
                                Toast.makeText(
                                    applicationContext,
                                    "otp send successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }).build()
                    PhoneAuthProvider.verifyPhoneNumber(options)
                }

            } else {
                Toast.makeText(
                    applicationContext,
                    "Mobile number must be 10 Digit",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.btnVerifyOtp.setOnClickListener {
            if (binding.etEnterOtp.text.toString().length == 6) {
                val enterOtp = binding.etEnterOtp.text.toString()
                val credential=PhoneAuthProvider.getCredential(sendOtp,enterOtp)
                //sendOtp is taken from the onCodeSent Method.

                FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                applicationContext,
                                "login successful",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(
                                Intent(
                                    this,
                                    MainActivity2::class.java //in dash
                                ))
                            //Dashboard activity is for sign in purpose.
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "wrong otp",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Otp must be 6 digits",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }




}
