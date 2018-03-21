package com.fireaxe.canteenautomation

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.WISH_LIST
import com.fireaxe.canteenautomation.fragments.BreakfastFragment
import com.fireaxe.canteenautomation.fragments.DinnerFragment
import com.fireaxe.canteenautomation.fragments.InformationFragment
import com.fireaxe.canteenautomation.models.LunchItem
import com.fireaxe.canteenautomation.fragments.LunchFragment
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : BaseActivity(), LunchFragment.OnListFragmentInteractionListener, InformationFragment.LogOut {
    override val container: View?
        get() = main_main

    override fun showContainer(): View? {
        return main_main

    }
    override fun logout() {
        logOut()

    }

    private val NUM_PAGES : Int = 3
    //private lateinit var mPagerAdapter: PagerAdapter
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        when (item.itemId) {
            R.id.breakfast -> {
               // message.setText(R.string.title_home)
                //pager.currentItem = 0
                val fragment = BreakfastFragment()
                navigation.itemBackgroundResource = R.color.colorPrimary
                addFragment(fragment)

                return@OnNavigationItemSelectedListener true
            }
            R.id.lunch-> {
                val fragment = LunchFragment()
                navigation.itemBackgroundResource = android.R.color.holo_green_dark
                addFragment(fragment)
                //message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.dinner -> {
                val fragment = DinnerFragment()
                navigation.itemBackgroundResource = android.R.color.holo_orange_dark
                addFragment(fragment)
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
            R.id.info -> {
                val fragment = InformationFragment()
                navigation.itemBackgroundResource = android.R.color.darker_gray
                addFragment(fragment)
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }

            R.id.scheduled -> {
                val fragment = BreakfastFragment()
                navigation.itemBackgroundResource = android.R.color.holo_blue_bright
                addFragment(fragment)
                //message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        exitApp()
    }

    private fun addFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content,fragment,fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.breakfast
        addFragment(BreakfastFragment())
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when(position) {
                0 ->
                    return BreakfastFragment.newInstance(position)
                1 ->
                    return LunchFragment.newInstance(position)
                2 ->
                    return DinnerFragment.newInstance(position)
            }
            return BreakfastFragment.newInstance(position)
        }

        override fun getCount(): Int {
            return NUM_PAGES
        }
    }

    override fun onListFragmentInteraction(item: LunchItem) {
        Toast.makeText(this,item.item_name,Toast.LENGTH_LONG).show()
        return
    }
    companion object {
        fun navigate(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)

        }
    }


}
