package com.example.fridge2.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fridge2.Adapter.RecyclerViewAdapter
import com.example.fridge2.FoodInfo
import com.example.fridge2.databinding.ActivityJangBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class JangActivity : AppCompatActivity() {
    private var mBinding: ActivityJangBinding? = null
    private val binding get() = mBinding!!

    var firestore: FirebaseFirestore? = null
    val mDatas = mutableListOf<FoodInfo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val Firestore: FirebaseFirestore = Firebase.firestore
        val foodsRef = Firestore.collection("/foods")
        val query: Query = foodsRef.whereEqualTo("loc", 1)
        val task: Task<QuerySnapshot> = query.get()

        task.addOnSuccessListener { querySnapshot ->
            val documents: MutableList<DocumentSnapshot> = querySnapshot.documents
            for (document in documents) {
                // 성공했을 때
                firestore!!.collection("foods").get().addOnSuccessListener { result ->
                    binding.rvProfile.adapter = RecyclerViewAdapter()
                    binding.rvProfile.layoutManager = LinearLayoutManager(this)
                }
            }
        }.addOnFailureListener {
            // 실패했을때
        }

    }

}