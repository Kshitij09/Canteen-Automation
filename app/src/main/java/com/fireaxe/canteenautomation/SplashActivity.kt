package com.fireaxe.canteenautomation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.fireaxe.canteenautomation.FirebaseConstants
import com.fireaxe.canteenautomation.LoginDetails
import com.fireaxe.canteenautomation.MainActivity
import com.fireaxe.canteenautomation.R
import com.fireaxe.canteenautomation.models.MyUser
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_details.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class SplashActivity : BaseActivity() {
    lateinit var db: FirebaseFirestore
    private val RC_SIGN_IN = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        db = FirebaseFirestore.getInstance()
        if(BaseActivity.getConnectivityStatus(this) == TYPE_NOT_CONNECTED){
            toast("Please connect to the internet")
            finish()
        }
        val providers = Arrays.asList(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build())
        if(user == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.AppTheme)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }else {
            if (user != null)
                db.collection(FirebaseConstants.USERS).whereEqualTo("id", uid)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val currentUser: List<MyUser> = documentSnapshot.toObjects(MyUser::class.java)
                            if (!currentUser.isEmpty() && currentUser[0].isVerified) {
                                MainActivity.navigate(this)
                                finish()
                            } else {
                                db.collection(FirebaseConstants.USERS).document(uid).set(MyUser(uid, false))
                                        .addOnSuccessListener {
                                            toast("Added to DB")
                                            LoginDetails.navigate(this)
                                            finish()
                                        }
                            }
                        }
                        .addOnFailureListener{
                            toast("Error Fetching user")
                        }
        }
    }

    override val container: View?
        get() = splash_main

    override fun showContainer(): View? {
        return splash_main
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == RC_SIGN_IN){
            var response: IdpResponse = IdpResponse.fromResultIntent(data)!!

            if(resultCode == Activity.RESULT_OK) {
                var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    db.collection(FirebaseConstants.USERS).whereEqualTo("id", user.uid)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { documentSnapshot ->
                                val currentUser: List<MyUser> = documentSnapshot.toObjects(MyUser::class.java)
                                if (!currentUser.isEmpty() && currentUser[0].isVerified) {
                                    MainActivity.navigate(this)
                                    finish()
                                } else {
                                    db.collection(FirebaseConstants.USERS).add(MyUser(user.uid, false))
                                            .addOnSuccessListener {
                                                Toast.makeText(this@SplashActivity, "Added to db", Toast.LENGTH_SHORT).show()
                                                LoginDetails.navigate(this)
                                                finish()
                                            }
                                }
                            }
                }

            } else {
                Toast.makeText(this, "Error Logging in", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
