package com.example.fridge2.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fridge2.FoodInfo
import com.example.fridge2.R
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

    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityJangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val Firestore: FirebaseFirestore = Firebase.firestore
        val foodsRef = Firestore.collection("/foods")
        val query: Query = foodsRef.whereEqualTo("loc", 1)
        val task: Task<QuerySnapshot> = query.get()

        task.addOnSuccessListener { querySnapshot ->
            val documents: MutableList<DocumentSnapshot> = querySnapshot.documents
            for (document in documents) {
                //성공했을때
                firestore = FirebaseFirestore.getInstance()

                binding.rvProfile.adapter = RecyclerViewAdapter()
                binding.rvProfile.layoutManager = LinearLayoutManager(this)

            }
        }.addOnFailureListener {
            // 실패했을때
        }

    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var foods : ArrayList<FoodInfo> = arrayListOf()

        init{
            firestore?.collection("foods")?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                foods.clear()

                for(snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(FoodInfo::class.java)
                    foods.add(item!!)
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
            return ViewHolder(view)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var viewHolder = (holder as ViewHolder).itemView

            viewHolder.textName
        }
        override fun getItemCount(): Int {
            return foods.size
        }
    }
}