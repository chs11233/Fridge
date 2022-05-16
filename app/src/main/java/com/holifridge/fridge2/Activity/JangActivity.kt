package com.holifridge.fridge2.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.holifridge.fridge2.FoodInfo
import com.holifridge.fridge2.GlideApp
import com.holifridge.fridge2.R
import com.holifridge.fridge2.SwiperHelper.JSwipeHelperCallback
import com.holifridge.fridge2.databinding.ActivityJangBinding
import com.holifridge.fridge2.databinding.ItemFoodBinding
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timer

class JangActivity : AppCompatActivity() {
    private var mBinding: ActivityJangBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser

    var oSysMainLoop = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val foods = mutableListOf<String>()

        firestore = FirebaseFirestore.getInstance()

        binding.rvJang.layoutManager = LinearLayoutManager(this)
        val recyclerViewAdapter = RecyclerViewAdapter(foods)
        binding.rvJang.adapter = recyclerViewAdapter

        val swipeHelperCallback = JSwipeHelperCallback(recyclerViewAdapter).apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 4)
        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.rvJang)

        binding.rvJang.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.rvJang.setOnTouchListener { _, _ ->
            swipeHelperCallback.removePreviousClamp(binding.rvJang)
            false
        }
    }

    inner class RecyclerViewAdapter(val binding: MutableList<String>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var foods: ArrayList<FoodInfo> = arrayListOf()

        init {
            firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
                ?.whereEqualTo("loc", 1)
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

            dateColor(holder, position)

            val highNoon = Calendar.getInstance()
            highNoon.set(Calendar.HOUR_OF_DAY, 12)
            highNoon.set(Calendar.MINUTE, 0)
            highNoon.set(Calendar.SECOND, 0)

            val timer = Timer()
            var date_long = foods[position].date_long!!
            timer.schedule(object: TimerTask() {
                override fun run() {
                    firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
                        ?.document(foods[position].url.toString())?.update("date_long", --date_long)
                        ?.addOnSuccessListener {
                        }?.addOnFailureListener() {
                        }
                }
            }, highNoon.time)



//            oSysMainLoop = 1
//            timer(period = 10000, initialDelay = 1000) {
//                if (oSysMainLoop != 1) {
//                    cancel()
//                }
//                var date_long = foods[position].date_long!!
//                firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
//                    ?.document(foods[position].url.toString())?.update("date_long", --date_long)
//                    ?.addOnSuccessListener {
//                    }?.addOnFailureListener() {
//                    }
//            }

            val firebaseStorage = FirebaseStorage.getInstance()
            val storageReference = firebaseStorage.reference.child("images/" + foods[position].url)
            storageReference.downloadUrl.addOnCompleteListener {
                GlideApp.with(this@JangActivity).load(storageReference)
                    .placeholder(R.drawable.icon_kitchen)
                    .into(viewHolder.photoImg)
            }
            viewHolder.foodname.text = foods[position].name
            viewHolder.foodDday.text = foods[position].date_long.toString()
            viewHolder.foodDate.text = foods[position].date

            viewHolder.tvRemove.setOnClickListener {
                removeData(position)
                Toast.makeText(this@JangActivity, "삭제했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        fun dateColor(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as CustomViewHolder).binding
            var dday = foods[position].date_long!!.toLong()
            if (dday == 0L) {
                viewHolder.foodDday.setTextColor(Color.parseColor("#FF2727")) // 더 빨간색(백업 #FF0000)
            } else if (dday > 0L) {
                if (dday <= 3L) {
                    viewHolder.foodDday.setTextColor(Color.parseColor("#FF2727")) // 빨간색(백업 #E84646)
                } else if (dday <= 7L) {
                    viewHolder.foodDday.setTextColor(Color.parseColor("#FF8B00")) // 노란색(약간 겨자색)  (백업 #D8B713)
                } else {
                    viewHolder.foodDday.setTextColor(Color.parseColor("#1BB222")) // 연두색(백업 #7CB639)
                }
            } else {
                viewHolder.foodDday.setTextColor(Color.parseColor("#000000")) // 검은색
            }
        }

        override fun getItemCount(): Int {
            return foods.size
        }

        // position 위치의 데이터를 삭제 후 어댑터 갱신
        fun removeData(position: Int) {
            val storage = Firebase.storage
            val storageX = storage.reference
            storageX.child("images/" + foods[position].url).delete()
            firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
                ?.document(foods[position].url.toString())
                ?.delete()
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


    override fun onRestart() {
        super.onRestart()
        reFresh()
    }

    private fun reFresh() {
        val refreshIntent = intent
        refreshIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        finish()
        startActivity(refreshIntent)
    }

}