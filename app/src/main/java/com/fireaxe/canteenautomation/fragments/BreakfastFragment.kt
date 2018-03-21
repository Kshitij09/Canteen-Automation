package com.fireaxe.canteenautomation.fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.BREAKFAST_MENU
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.ITEM_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.PRIORITY
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.USER_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.WISH_LIST
import com.fireaxe.canteenautomation.LunchFragment
import com.fireaxe.canteenautomation.MainActivity

import com.fireaxe.canteenautomation.R
import com.fireaxe.canteenautomation.adapters.BreakfastAdapter
import com.fireaxe.canteenautomation.models.BreakfastData
import com.fireaxe.canteenautomation.models.BreakfastItem
import com.fireaxe.canteenautomation.models.UserOption
import com.fireaxe.canteenautomation.subactivities.MenuListActivity
import com.fireaxe.canteenautomation.utilities.SwipeAndDragHelper
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_menu_list.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BreakfastFragment : Fragment() {

    lateinit var recyclerBreakfast: RecyclerView
    lateinit var firestoreDB: FirebaseFirestore
    lateinit var wishlistRef: CollectionReference
    private var firestoreListener: ListenerRegistration? = null
    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid
    lateinit var adapter: BreakfastAdapter
    //private var adapter: FirestoreRecyclerAdapter<BreakfastData, ViewHolder>? = null
    var itemsList: MutableList<BreakfastData> = mutableListOf()
    lateinit var swipeAndDragHelper: SwipeAndDragHelper
    lateinit var touchHelper: ItemTouchHelper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_breakfast, container, false)
        setHasOptionsMenu(true  )
        val buttonAdd = view.findViewById<Button>(R.id.button_add_item)
        buttonAdd.setOnClickListener {
            startActivity(Intent(activity,MenuListActivity::class.java))
        }
        firestoreDB = FirebaseFirestore.getInstance()
        wishlistRef = firestoreDB.collection(WISH_LIST)
        recyclerBreakfast = view.findViewById(R.id.recycler_breakfast)
        recyclerBreakfast.layoutManager = LinearLayoutManager(context)
        adapter = BreakfastAdapter()
        //initList()

        //adapter.setItemList(itemsList)
        swipeAndDragHelper = SwipeAndDragHelper(adapter)
        touchHelper = ItemTouchHelper(swipeAndDragHelper)
        adapter.setTouchHelper(touchHelper)
        recyclerBreakfast.adapter = adapter
        touchHelper.attachToRecyclerView(recyclerBreakfast)
        return view
    }

    override fun onStart() {
        super.onStart()
        initList()
        //dummyList()
    }

    /*private fun dummyList() {
        var menuList: MutableList<BreakfastData> = arrayListOf(
                BreakfastData(name = "Pohe", price = 15.0f),
                BreakfastData(name = "Upma", price = 20.0f),
                BreakfastData(name = "Shira", price = 20.0f),
                BreakfastData(name = "Medu Wada", price = 25.0f),
                BreakfastData(name = "Masala Dosa", price = 25.0f),
                BreakfastData(name = "Uttappa", price = 25.0f),
                BreakfastData(name = "Misal", price = 30.0f),
                BreakfastData(name = "Aloo Pakode", price = 20.0f),
                BreakfastData(name = "Maggie", price = 20.0f)
        )
        adapter.setItemList(menuList)
    }*/


    private fun initList() {
        //adapter = BreakfastAdapter(activity as Activity)
        /*itemsList = mutableListOf()
        adapter.setItemList(itemsList)*/
        /*swipeAndDragHelper = SwipeAndDragHelper(adapter)
        touchHelper = ItemTouchHelper(swipeAndDragHelper)
        adapter.setTouchHelper(touchHelper)
        touchHelper.attachToRecyclerView(recyclerBreakfast)*/
        recyclerBreakfast.adapter = adapter
        val query = firestoreDB.collection(WISH_LIST).whereEqualTo(USER_ID,uid)
        query.get().addOnSuccessListener { documentSnapshots ->
                    itemsList = mutableListOf()
                    adapter.setItemList(itemsList)
                    documentSnapshots
                    .map { it.toObject(UserOption::class.java) }
                    .forEach {
                        val itemQuery = firestoreDB.collection(BREAKFAST_MENU).whereEqualTo(ID,it.item_id)
                        itemQuery.get().addOnSuccessListener { snapshot ->
                            //Log.d("Wishlist",snapshot.documents[0].get("name").toString())
                            val data = snapshot.documents[0].toObject(BreakfastData::class.java)
                            adapter.addItem(data,it.priority)
                            //adapter.setItemList(itemsList)
                            //adapter.notifyDataSetChanged()
                            //recyclerBreakfast.adapter = adapter
                            Log.d("Wishlist",data.name)
                        }
                        //adapter.setItemList(itemsList)
                    }
        }
        /*query.addSnapshotListener(EventListener { documentSnapshots, e ->
                    if (e != null) {
                        Log.e("Breakfastfragment", "Listen failed!",e)
                        return@EventListener
                    }
                    itemsList = mutableListOf()
                    documentSnapshots
                            .map { it.toObject(UserOption::class.java) }
                            .forEach {
                                val itemQuery = firestoreDB.collection(BREAKFAST_MENU).whereEqualTo(ID,it.item_id)
                                itemQuery.get().addOnSuccessListener { snapshot ->
                                    //Log.d("Wishlist",snapshot.documents[0].get("name").toString())
                                    val data = snapshot.documents[0].toObject(BreakfastData::class.java)
                                    adapter.addItem(data)
                                    //adapter.setItemList(itemsList)
                                    //adapter.notifyDataSetChanged()
                                    //recyclerBreakfast.adapter = adapter
                                    Log.d("Wishlist",data.name)
                                }
                                //adapter.setItemList(itemsList)
                            }
                    //adapter.notifyDataSetChanged()
                    //recyclerBreakfast.adapter = adapter
                })*/
        /*val query = firestoreDB.collection(WISH_LIST).whereEqualTo(USER_ID,uid)
        val response = FirestoreRecyclerOptions.Builder<BreakfastData>()
                .setQuery(query, BreakfastData::class.java)
                .build()*/
        //adapter = BreakfastAdapter(response = response)
        /*adapter = object : FirestoreRecyclerAdapter<BreakfastData,ViewHolder>(response), SwipeAndDragHelper.ActionCompletionContract {

            override fun onViewMoved(oldPosition: Int, newPosition: Int) {
                val targetItem = itemsList[oldPosition]
                val item: BreakfastData = targetItem
                itemsList.removeAt(oldPosition)
                itemsList.add(newPosition,item)
                notifyItemMoved(oldPosition,newPosition)
            }

            override fun onViewSwiped(position: Int) {
                itemsList.removeAt(position)
                notifyItemRemoved(position)
            }
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_breakfast_menu,parent,false)
                return ViewHolder(view)
            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: BreakfastData) {
                //val item = itemsList[position]
                holder.menuPreview.setImageResource(R.drawable.ic_dinner)
                holder.menuName.text = model.name
                holder.handleImageView.setImageResource(R.drawable.ic_align_handle)
                holder.handleImageView.setOnTouchListener { view, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN){
                        touchHelper.startDrag(holder)
                    }
                    return@setOnTouchListener false
                }
            }
        }
        swipeAndDragHelper = SwipeAndDragHelper(adapter)
        touchHelper = ItemTouchHelper(swipeAndDragHelper)
        adapter.setTouchHelper(touchHelper)
        recyclerBreakfast.adapter = adapter
        touchHelper.attachToRecyclerView(recyclerBreakfast)*/


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //Toast.makeText(context,"Selected",Toast.LENGTH_SHORT).show()
        updatePriorities()
        return super.onOptionsItemSelected(item)
    }
    fun updatePriorities(){
        val act = activity as MainActivity
        itemsList = adapter.getList()
        act.showProgressDialog("Updating priorities")
        wishlistRef.get()
                .addOnSuccessListener {
                    it.forEach{doc-> doc.reference.delete()}
                    for((index,ite) in itemsList.withIndex()){
                        Log.d("List",ite.name)
                        firestoreDB
                                .collection(WISH_LIST)
                                .add(UserOption(uid,ite.id,index,System.currentTimeMillis()))
                                .addOnSuccessListener {
                                    Log.d("Item","${ite.name} Priority: $index")
                                    if (index == itemsList.lastIndex){
                                        act.hideProgressDialog()
                                    }

                                }
                    }
                }
    }
        /*itemsList = arrayListOf(BreakfastItem(123,"Misal",0),
                                BreakfastItem(123,"Poha",0),
                                BreakfastItem(123,"Idli",0),
                                BreakfastItem(123,"Upma",0),
                                BreakfastItem(123,"Vada",0))*/
    companion object {

        // TODO: Customize parameter argument names
        private val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): LunchFragment {
            val fragment = LunchFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}
