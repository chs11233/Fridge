package com.holifridge.fridge2.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.holifridge.fridge2.GlideApp
import com.holifridge.fridge2.R
import com.holifridge.fridge2.SwiperHelper.SSwipeHelperCallback
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SilonActivity : AppCompatActivity() {
    private var mBinding: ActivitySilonBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser
    val ONE_DAY = (24 * 60 * 60 * 1000)

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

            dateColor(holder, position)
            dDay(holder, position)

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

        fun dateColor(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as SilonActivity.RecyclerViewAdapter.CustomViewHolder).binding
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

        fun dDay(holder: RecyclerView.ViewHolder, position: Int) {
            var calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy.MM.dd")
            calendar.time = df.parse(foods[position].date)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time
            var date_long = (calendar.time.time - today) / ONE_DAY
            firestore?.collection("user")?.document(firebaseUser!!.uid)?.collection("foods")
                ?.document(foods[position].url.toString())?.update("date_long", date_long)
                ?.addOnSuccessListener {
                }?.addOnFailureListener {
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