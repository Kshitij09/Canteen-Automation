package com.fireaxe.canteenautomation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.fireaxe.canteenautomation.models.MyUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login_details.*


class LoginDetails : BaseActivity() {
    lateinit var db: FirebaseFirestore
    override val container: View?
        get() = login_main

    override fun showContainer(): View? {
        return login_main
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_details)
        db = FirebaseFirestore.getInstance()
        btn_submit.setOnClickListener {
            showProgressDialog()
            saveUserDetails()
        }
    }

    fun saveUserDetails(){

        val myUser = MyUser(uid,
                text_mis_entry.text.toString(),
                text_name.text.toString(),
                text_phone.text.toString(),
                isVerified = true)
        db.collection("users")
                .document(uid)
                .set(myUser)
                .addOnSuccessListener { documentReference -> Toast.makeText(this@LoginDetails,"Registered",Toast.LENGTH_SHORT).show()
                    MainActivity.navigate(this)
                    hideProgressDialog()}
                .addOnFailureListener { e ->
                    toast("Error Saving")
                    hideProgressDialog() }
    }
    companion object {
        fun navigate(context: Context) {
            var intent: Intent = Intent(context, LoginDetails::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

        }
    }
}
