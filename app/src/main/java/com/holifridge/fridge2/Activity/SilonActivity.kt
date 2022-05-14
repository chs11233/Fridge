package com.holifridge.fridge2.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.holifridge.fridge2.FoodInfo
import com.holifridge.fridge2.databinding.ActivitySilonBinding
import com.holifridge.fridge2.databinding.ItemFoodBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.holifridge.fridge2.GlideApp
import com.holifridge.fridge2.R

class SilonActivity : AppCompatActivity() {
    private var mBinding: ActivitySilonBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySilonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foods = mutableListOf<String>()

        firestore = FirebaseFirestore.getInstance()

        binding.rvSilon.layoutManager = LinearLayoutManager(this)
        binding.rvSilon.adapter = RecyclerViewAdapter(foods)
    }

    inner class RecyclerViewAdapter(val binding: MutableList<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var foods: ArrayList<FoodInfo> = arrayListOf()

        init {
            firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
                ?.whereEqualTo("loc", 2)
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

            val firebaseStorage = FirebaseStorage.getInstance()
            val storageReference = firebaseStorage.reference.child("images/" + foods[position].url)
            storageReference.downloadUrl.addOnCompleteListener {
                GlideApp.with(this@SilonActivity).load(storageReference)
                    .placeholder(R.drawable.icon_kitchen)
                    .into(viewHolder.photoImg)
            }
            viewHolder.foodname.text = foods[position].name
            viewHolder.foodDday.text = foods[position].date_long.toString()
            viewHolder.foodDate.text = foods[position].date
        }

        override fun getItemCount(): Int {
            return foods.size
        }
    }

    private fun reFresh() {
        val refreshIntent = intent
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(refreshIntent)
    }

    override fun onRestart() {
        super.onRestart()
        reFresh()
    }
}