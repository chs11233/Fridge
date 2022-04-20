package com.example.fridge2.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fridge2.FoodInfo
import com.example.fridge2.databinding.ItemFoodBinding
import com.google.firebase.firestore.FirebaseFirestore

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    var firestore : FirebaseFirestore? = null

    var foods : ArrayList<FoodInfo> = arrayListOf()

    init {
        firestore?.collection("foods")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            // ArrayList 비워줌
            foods.clear()

            for (snapshot in querySnapshot!!.documents) {
                var item = snapshot.toObject(FoodInfo::class.java)
                foods.add(item!!)
            }
            notifyDataSetChanged()
        }
    }

    inner class MyViewHolder(private val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //binding전달받았기 때문에 홀더 내부 어디에서나 binding 사용가능
        fun bind(foodInfo: FoodInfo) {
            binding.textName.text = foodInfo.name
            binding.textLoc.text = foodInfo.loc.toString()
            binding.textDday.text = foodInfo.date_long.toString()
            binding.textDate.text = foodInfo.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = foods.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(foods[position])
    }

}