package com.fireaxe.canteenautomation

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.fireaxe.canteenautomation.fragments.LunchFragment.OnListFragmentInteractionListener
import com.fireaxe.canteenautomation.dummy.DummyContent.DummyItem
import com.fireaxe.canteenautomation.models.LunchItem




/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(private val mValues: MutableList<LunchItem>, private val mListener: OnListFragmentInteractionListener?) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mIdView.text = mValues[position].item_name
        holder.mContentView.text = position.toString()
        holder.mPriority.text = position.toString()
        if(mValues[position].isReject){
            holder.mContentView.setBackgroundColor(Color.parseColor("#000000"))
        }else{
            holder.mContentView.setBackgroundColor(Color.parseColor("#ffffff"))
        }

        holder.mView.setOnClickListener {

            val item : LunchItem = mValues[position]
            if(!item.isReject) {
                mValues.removeAt(position)
                mValues.add(if (position - 1 >= 0) position - 1 else 0, item)
                notifyDataSetChanged()
            }
        }
        holder.mPriority.setOnClickListener {
            val item: LunchItem = mValues[position]
            item.isReject = !item.isReject
            mValues.removeAt(position)
            mValues.add(mValues.size-1, item)
        }
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView
        val mContentView: TextView
        var mItem: LunchItem? = null
        val mPriority: TextView

        init {
            mIdView = mView.findViewById<View>(R.id.id) as TextView
            mContentView = mView.findViewById<View>(R.id.content) as TextView
            mPriority = mView.findViewById(R.id.priority)
        }

        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }
    }
}
