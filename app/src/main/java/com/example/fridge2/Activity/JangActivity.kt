package com.example.fridge2.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridge2.FoodInfo
import com.example.fridge2.databinding.ActivityJangBinding
import com.example.fridge2.databinding.ItemFoodBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class JangActivity : AppCompatActivity() {
    private var mBinding: ActivityJangBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foods = mutableListOf<String>()

        firestore = FirebaseFirestore.getInstance()

        binding.rvJang.layoutManager = LinearLayoutManager(this)
        binding.rvJang.adapter = RecyclerViewAdapter(foods)


    }

    inner class RecyclerViewAdapter(val binding: MutableList<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var foods: ArrayList<FoodInfo> = arrayListOf()

        init {
            firestore?.collection("foods")?.whereEqualTo("loc", 1)
                ?.addSnapshotListener { querySnapshot, _ ->
                    foods.clear()
                    for (snapshot in querySnapshot!!.documents) {
                        var item = snapshot.toObject(FoodInfo::class.java)
                        foods.add(item!!)
                    }
                    notifyDataSetChanged()
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val binding =
                ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(binding)
        }

        inner class CustomViewHolder(val binding: ItemFoodBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).binding

            viewHolder.foodname.text = foods[position].name
            viewHolder.foodDday.text = foods[position].date_long.toString()
            viewHolder.foodDate.text = foods[position].date

        }

        override fun getItemCount(): Int {
            return foods.size
        }

    }

    fun getIgnoredTimeDays(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time

            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }


    fun refresh() {
        val refreshIntent = getIntent()
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(refreshIntent)
    }

    override fun onRestart() {
        super.onRestart()
        refresh()
    }


}