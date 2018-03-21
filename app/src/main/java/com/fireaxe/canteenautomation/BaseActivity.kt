package com.fireaxe.canteenautomation

import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.os.Message
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast


import com.fireaxe.canteenautomation.other.SnackBarContainerInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by chait on 5/26/2016.
 */
abstract class BaseActivity : AppCompatActivity(), SnackBarContainerInterface {

    private var mProgressDialog: ProgressDialog? = null
    private var exit = false

    val user: FirebaseUser?
        get() = FirebaseAuth.getInstance().currentUser

    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid
    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = getConnectivityStatus(baseContext)
            setSnackBarMessage(status, showContainer())
        }
    }

    fun showProgressDialog(message: String = "Loading") {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setMessage(message)
        }
        mProgressDialog!!.show()
    }

    fun hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) {
            mProgressDialog!!.dismiss()
        }

    }

    private fun registerInternetCheckReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.wifi.STATE_CHANGE")
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadcastReceiver, intentFilter)


    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        //Log.d(TAG, "onOptionsItemSelected: logout success");
    }

    private fun setSnackBarMessage(status: Int, view: View?) {
        val internetStatus: String
        val length: Int
        if (status == TYPE_WIFI || status == TYPE_MOBILE) {
            if (isConnected)
                return
            length = Snackbar.LENGTH_SHORT
            internetStatus = "Internet Connected"
            isConnected = true

        } else {
            length = Snackbar.LENGTH_INDEFINITE
            internetStatus = "Lost Internet Connection"
            isConnected = false
        }
        if(view!=null) {
            internetStatusSnack = Snackbar.make(view, internetStatus, length)
                    .setAction("X") { internetStatusSnack!!.dismiss() }
            internetStatusSnack!!.setActionTextColor(Color.WHITE)
            internetStatusSnack!!.show()
        }

    }

    override fun onResume() {
        super.onResume()
        registerInternetCheckReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }



    abstract fun showContainer(): View?

    fun exitApp() {
        if (exit) {
            finish()
        } else {
            exit = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({ exit = false }, (3 * 1000).toLong())
        }
    }
    fun Context.toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }

    companion object {

        //Connection States
        var TYPE_WIFI = 1
        var TYPE_MOBILE = 2
        var TYPE_NOT_CONNECTED = 0
        private var internetStatusSnack: Snackbar? = null
        private var isConnected = false

        fun getConnectivityStatusString(context: Context): String {
            val conn = getConnectivityStatus(context)
            val status: String
            if (conn == TYPE_WIFI)
                status = "Wifi Enabled"
            else if (conn == TYPE_MOBILE)
                status = "Mobile Data Enabled"
            else
                status = "Not connected to internet"

            return status
        }

        fun getConnectivityStatus(context: Context): Int {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                    return TYPE_WIFI

                if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                    return TYPE_MOBILE

            }
            return TYPE_NOT_CONNECTED
        }
    }

}
