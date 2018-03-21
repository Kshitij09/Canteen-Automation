package com.fireaxe.canteenautomation.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fireaxe.canteenautomation.LunchFragment

import com.fireaxe.canteenautomation.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_info.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class InformationFragment : Fragment() {

    lateinit var logOut: LogOut
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_info, container, false)
        try{
            logOut = activity as LogOut
        }catch (e: ClassCastException){
            throw RuntimeException("Activity Must implement Logout Listener")
        }
        rootView.findViewById<View>(R.id.btn_sign_out).setOnClickListener{
            logOut.logout()
        }
        return rootView
    }

     interface LogOut{
         fun logout()
    }

}
