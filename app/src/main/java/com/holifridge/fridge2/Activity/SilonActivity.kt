package com.holifridge.fridge2.Activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
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
import com.holifridge.fridge2.SwiperHelper.SSwipeHelperCallback
import java.util.*
import kotlin.collections.ArrayList

class SilonActivity : AppCompatActivity() {
    private var mBinding: ActivitySilonBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySilonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foods = mutableListOf<String>()

        firestore = FirebaseFirestore.getInstance()

        binding.rvSilon.layoutManager = LinearLayoutManager(this)
        val recyclerViewAdapter = RecyclerViewAdapter(foods)
        binding.rvSilon.adapter = recyclerViewAdapter

        val swipeHelperCallback = SSwipeHelperCallback(recyclerViewAdapter).apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)
        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.rvSilon)

        binding.rvSilon.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvSilon.setOnTouchListener { _, _ ->
            swipeHelperCallback.removePreviousClamp(binding.rvSilon)
            false
        }
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

            viewHolder.tvRemove.setOnClickListener {
                removeData(position)
                Toast.makeText(this@SilonActivity, "삭제했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int {
            return foods.size
        }

        // position 위치의 데이터를 삭제 후 어댑터 갱신
        fun removeData(position: Int) {
            foods.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, itemCount - position)
        }

        // 현재 선택된 데이터와 드래그한 위치에 있는 데이터를 교환
        fun swapData(fromPos: Int, toPos: Int) {
            Collections.swap(foods, fromPos, toPos)
            notifyItemMoved(fromPos, toPos)
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