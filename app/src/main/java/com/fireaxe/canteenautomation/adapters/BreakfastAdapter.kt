package com.fireaxe.canteenautomation.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import com.fireaxe.canteenautomation.utilities.SwipeAndDragHelper
import android.widget.TextView
import com.fireaxe.canteenautomation.R
import com.fireaxe.canteenautomation.models.BreakfastItem
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.Toast
import com.fireaxe.canteenautomation.FirebaseConstants
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.ITEM_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.USER_ID
import com.fireaxe.canteenautomation.FirebaseConstants.Companion.WISH_LIST
import com.fireaxe.canteenautomation.models.BreakfastData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class BreakfastAdapter: RecyclerView.Adapter<BreakfastAdapter.ViewHolder>(), SwipeAndDragHelper.ActionCompletionContract {
    private var mItemsList: MutableList<BreakfastData>
    private var mSortedList: MutableList<BreakfastData>
    private var priorities: MutableList<Int>
    private var firestoreDB: FirebaseFirestore
    private lateinit var touchHelper: ItemTouchHelper
    val uid: String
        get() = FirebaseAuth.getInstance().currentUser!!.uid
    init {
        mItemsList = mutableListOf()
        mSortedList = mutableListOf()
        priorities = mutableListOf()
        firestoreDB = FirebaseFirestore.getInstance()
    }
    override fun onViewMoved(oldPosition: Int, newPosition: Int) {
        val targetItem = mItemsList[oldPosition]
        val item: BreakfastData = targetItem
        mItemsList.removeAt(oldPosition)
        mItemsList.add(newPosition,item)
        notifyItemMoved(oldPosition,newPosition)
        Log.d("Adapter",mItemsList.size.toString())

    }
    fun getList(): MutableList<BreakfastData> {
        return mItemsList
    }
    fun addItem(item: BreakfastData,priority: Int) {
        mItemsList.add(item)
        //mItemsList.add(item)
        //notifyDataSetChanged()
        notifyItemInserted(mItemsList.size-1)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_breakfast_menu,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mItemsList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.menuPreview.setImageResource(R.drawable.ic_dinner)
        holder.menuName.text = mItemsList[position].name
        holder.handleImageView.setImageResource(R.drawable.ic_align_handle)
        holder.handleImageView.setOnTouchListener { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN){
                touchHelper.startDrag(holder)
            }
            return@setOnTouchListener false
        }
    }

    fun setItemList(itemList: MutableList<BreakfastData>) {
        this.mItemsList = itemList
        notifyDataSetChanged()
    }

    override fun onViewSwiped(position: Int) {
        val item = mItemsList[position]
        mItemsList.removeAt(position)
        notifyItemRemoved(position)
        Log.d("Adapter",mItemsList.size.toString())
        val query = firestoreDB
                .collection(WISH_LIST)
                .whereEqualTo(USER_ID,uid)
        query.get().addOnSuccessListener { snapshot ->
            snapshot.forEach { doc: DocumentSnapshot? ->
                if (doc!!.get(ITEM_ID).equals(item.id)) {
                    Log.d("Adapter", item.name)
                    doc.reference.delete()
                }
            }
        }
    }

    fun setTouchHelper(touchHelper: ItemTouchHelper) {

        this.touchHelper = touchHelper
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var handleImageView: ImageView
        var menuPreview: ImageView
        var menuName: TextView
        var view :View
        init {
            handleImageView = itemView.findViewById(R.id.drag_handle)
            menuPreview = itemView.findViewById(R.id.menu_preview)
            menuName = itemView.findViewById(R.id.menu_name)
            view = itemView
        }
    }
}