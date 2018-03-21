package com.fireaxe.canteenautomation.subactivities

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import com.fireaxe.canteenautomation.BaseActivity
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.BREAKFAST_MENU
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.ITEM_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.USER_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.WISH_LIST
import com.fireaxe.canteenautomation.MainActivity
import com.fireaxe.canteenautomation.R
import com.fireaxe.canteenautomation.models.UserOption
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.android.synthetic.main.activity_menu_list.*
import java.util.*

class MenuListActivity : BaseActivity() {

    override val container: View?
        get() = menu_main

    override fun showContainer(): View? {
       return menu_main
    }

    lateinit var firebaseDB:FirebaseFirestore
    private var modifiedItems: Int = 0
    private var mProgressDialog: ProgressDialog? = null
    private var lastIndex: Int = 0
    lateinit var collectionReference: CollectionReference
    private var adapter: FirestoreRecyclerAdapter<BreakfastData,BreakfastViewholder>? = null
    private var firestoreListener: ListenerRegistration? = null
    private var itemsList = mutableListOf<BreakfastData>()
    private var selectedItems = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)
        setSupportActionBar(toolbar)
        firebaseDB = FirebaseFirestore.getInstance()
        collectionReference = firebaseDB.collection(BREAKFAST_MENU)
        val mLinearLayoutManager = LinearLayoutManager(applicationContext)
        recycler_menu_select.layoutManager = mLinearLayoutManager
        recycler_menu_select.itemAnimator = DefaultItemAnimator()
        loadMenuList()

        firebaseDB.collection(WISH_LIST).whereEqualTo(USER_ID, uid).get()
                .addOnSuccessListener {
                    data->
                    for(d in data) {
                        val option = d.toObject(UserOption::class.java)
                        val optionId = option.item_id
                        if(optionId !=null && optionId != ""){
                            selectedItems.add(optionId)
                        }
                    }
                }

        firestoreListener = firebaseDB!!.collection(BREAKFAST_MENU)
                .addSnapshotListener(EventListener{ documentSnapshots, e ->
                    if (e != null) {
                        Log.e("MenuListActivity", "Listen failed!",e)
                        return@EventListener
                    }
                    itemsList = mutableListOf()
                    for (item in documentSnapshots) {
                        val breakfastData = item.toObject(BreakfastData::class.java)
                        breakfastData.isChecked = selectedItems.contains(breakfastData.id)
                        itemsList.add(breakfastData)
                    }
                    adapter!!.notifyDataSetChanged()
                    recycler_menu_select.adapter = adapter
                })
        //initDataList()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.base_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        //val query = firebaseDB.collection(WISH_LIST).orderBy("name").equals(itemsList[0].name)
        when (item!!.itemId) {
            R.id.init -> {
                initDataList()
                return true
            }
            R.id.check -> {
                showProgressDialog("Adding to your wishlist")
                //Log.d("Last",lastIndex.toString())
                firebaseDB.collection(WISH_LIST).whereEqualTo("user_id",uid).get()
                        .addOnSuccessListener {
                            it.forEach{doc-> doc.reference.delete()}
                            var i = 0
                            for((index,ite) in itemsList.withIndex()){
                                if(ite.isChecked){
                                    Log.d("List",ite.name)
                                    firebaseDB.collection(WISH_LIST).add(UserOption(uid,ite.id,i++,System.currentTimeMillis()))
                                            .addOnSuccessListener {
                                                Log.d("Index",index.toString())
                                                if (index == lastIndex){
                                                    hideProgressDialog()
                                                    val intent = Intent(this@MenuListActivity,MainActivity::class.java)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                    startActivity(intent)
                                                }

                                            }
                                }
                            }
                        }
            }
        }
        /*val documentSnapshot = query.get().result.documents
        val existingList: MutableList<BreakfastData> = documentSnapshot
                .map { it.toObject(BreakfastData::class.java) }
                .toMutableList()*/
//        for (i in itemsList) {
//            collectionReference.add(i)
//        }
//        collectionReference.get().addOnCompleteListener {task ->
//            if (task.isSuccessful){
//            }
//        }
        return super.onOptionsItemSelected(item)
    }
    /*override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        //val view = buttonView!!.rootView
        val item:BreakfastData = buttonView!!.tag as BreakfastData
        //val nameView = view.findViewById<TextView>(R.id.menu_name)
        //val name = nameView.text.toString()
        itemsList[itemsList.indexOf(item)].isChecked = isChecked
        modifiedMap[item] = isChecked
        if (isChecked)
            modifiedItems++
        else
            modifiedItems--
        val menuCheck = toolbar.menu.getItem(0)
        menuCheck.isVisible = modifiedItems != 0
    }*/
    override fun onStart() {
        super.onStart()
        showProgressDialog()
        recycler_menu_select.visibility = View.INVISIBLE
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    private fun loadMenuList() {
        val query = firebaseDB.collection(BREAKFAST_MENU)
        val response = FirestoreRecyclerOptions.Builder<BreakfastData>()
                .setQuery(query,BreakfastData::class.java)
                .build()
        adapter = object :FirestoreRecyclerAdapter<BreakfastData,BreakfastViewholder>(response) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreakfastViewholder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_breakfast_choice,parent,false)
                return BreakfastViewholder(view)
            }

            override fun onBindViewHolder(holder: BreakfastViewholder, position: Int, model: BreakfastData) {
                //val item = itemsList[position]
                val query = firebaseDB.collection(WISH_LIST)
                        .whereEqualTo(ITEM_ID,model.id)
                        .whereEqualTo(USER_ID,uid)
                query.get().addOnSuccessListener { snapshot ->
                    holder.nameText.text = model.name
                    val checked = !snapshot.isEmpty
                    if (checked){
                        modifiedItems++
                        lastIndex = position
                    }
                    itemsList[position].isChecked = checked
                    holder.check.isChecked = checked
                    holder.check.setOnCheckedChangeListener { buttonView, isChecked ->
                        itemsList[position].isChecked = isChecked
                        if (isChecked)
                            modifiedItems++
                        else
                            modifiedItems--
                        val menuCheck = toolbar.menu.getItem(0)
                        menuCheck.isVisible = modifiedItems != 0
                    }
                    if (position == itemsList.size-1){
                        recycler_menu_select.visibility = View.VISIBLE
                        hideProgressDialog()
                    }
                    holder.root.tag = model
                }
                //if (query.get().isSuccessful)
            }

        }
    }

    private fun initDataList() {
        var menuList: MutableList<BreakfastData> = arrayListOf(
                BreakfastData(name="Pohe",price=15.0f),
                BreakfastData(name="Upma",price=20.0f),
                BreakfastData(name="Shira",price=20.0f),
                BreakfastData(name="Medu Wada",price=25.0f),
                BreakfastData(name="Masala Dosa",price=25.0f),
                BreakfastData(name="Uttappa",price=25.0f),
                BreakfastData(name="Misal",price=30.0f),
                BreakfastData(name="Aloo Pakode",price=20.0f),
                BreakfastData(name="Maggie",price=20.0f)
        )
        for (item in menuList) {
            collectionReference.add(item).addOnSuccessListener { it ->
                item.id = it.id
                it.set(item)
            }
        }

    }
    data class BreakfastData(var id : String = "",
                        var name: String = "",
                        var price: Float = 0.0f,
                        var isChecked: Boolean = false)
    inner class BreakfastViewholder(view: View): RecyclerView.ViewHolder(view) {
        var root:View
        var nameText: TextView
        var check: CheckBox
        init {
            nameText = view.findViewById(R.id.menu_name)
            check = view.findViewById(R.id.checkbox)
            root = view
        }
    }

}
