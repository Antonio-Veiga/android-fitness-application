package com.estg.ipp.fitnessapp.Locations

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.estg.ipp.fitnessapp.R
import com.estg.ipp.fitnessapp.databinding.ExpandableListChildBinding
import com.estg.ipp.fitnessapp.databinding.ExpandableListParentBinding
import com.google.android.material.switchmaterial.SwitchMaterial

class LocationExpandableListAdapter(
    var context: Context,
    private var group_titles: ArrayList<String>,
    private var child_values: Map<String, ArrayList<String>>,
) : BaseExpandableListAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private lateinit var groupBinding: ExpandableListParentBinding
    private lateinit var itemBinding: ExpandableListChildBinding


    override fun getGroupCount(): Int {
        return group_titles.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return child_values[group_titles[groupPosition]]!!.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return group_titles[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return child_values[group_titles[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        var view = convertView
        val holder: GroupViewHolder
        if (convertView == null) {
            groupBinding = ExpandableListParentBinding.inflate(inflater)
            view = groupBinding.root
            holder = GroupViewHolder()
            holder.label = groupBinding.expListParent
            view.tag = holder
        } else {
            holder = view!!.tag as GroupViewHolder
        }
        val listTitle = getGroup(groupPosition) as String
        (holder.label!!.getChildAt(0) as TextView).text = listTitle
        (holder.label!!.getChildAt(1) as ImageView).isSelected = isExpanded
        return view
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?,
    ): View {
        var view = convertView
        val holder: ItemViewHolder

        if (convertView == null) {
            itemBinding = ExpandableListChildBinding.inflate(inflater)
            view = itemBinding.root

            holder = ItemViewHolder()
            holder.label = itemBinding.expListChild
            view.tag = holder

            if(isLastChild){
                Log.d("Child_pos","dentro:$childPosition")
                holder.label!!.background = context.getDrawable(R.drawable.flat_top_round_bottom)
            }
        } else {
            holder = view!!.tag as ItemViewHolder
        }

        val expandedListText = getChild(groupPosition, childPosition) as String

        val switch : SwitchMaterial = (holder.label!!.getChildAt(0) as SwitchMaterial)
        switch.text = expandedListText

        switch.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                if (!CheckedStates.checked_values.contains(expandedListText)){
                    CheckedStates.checked_values.add(expandedListText)
                }
            }else{
                if(CheckedStates.checked_values.contains(expandedListText)){
                    CheckedStates.checked_values.remove(expandedListText)
                }
            }
        }

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    inner class ItemViewHolder {
        internal var label: LinearLayout? = null
    }

    inner class GroupViewHolder {
        internal var label: LinearLayout? = null
    }
}