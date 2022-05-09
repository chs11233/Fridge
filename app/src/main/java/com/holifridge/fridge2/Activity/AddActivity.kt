package com.holifridge.fridge2.Activity

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.holifridge.fridge2.FoodInfo
import com.holifridge.fridge2.databinding.ActivityAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {
    private var mBinding: ActivityAddBinding? = null
    private val binding get() = mBinding!!

    val ONE_DAY = (24 * 60 * 60 * 1000)

    var curFile: Uri? = null
    val imageRef = Firebase.storage.reference
    private val REQUEST_CODE_IMAGE_PICK = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cIV.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQUEST_CODE_IMAGE_PICK)
            }
        }

        binding.dateBtn.setOnClickListener {
            datePicker()
        }

        binding.freezeCheckBox.setOnClickListener {
            binding.fridgeCheckBox.isChecked = false
            binding.roomCheckBox.isChecked = false
        }
        binding.fridgeCheckBox.setOnClickListener {
            binding.freezeCheckBox.isChecked = false
            binding.roomCheckBox.isChecked = false
        }
        binding.roomCheckBox.setOnClickListener {
            binding.freezeCheckBox.isChecked = false
            binding.fridgeCheckBox.isChecked = false
        }

        binding.cg.setOnClickListener {
            save()

        }
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance()
        var year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        var day = calendar.get(Calendar.DAY_OF_MONTH)

        var date_listener =
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.dateView.text = "${year}.%02d.%02d".format(month + 1, dayOfMonth)
            }
        // .apply{} = 현재시간보다 앞으로 못하게 적어줌
        var picker = DatePickerDialog(this, date_listener, year, month, day).apply {
            datePicker.minDate = System.currentTimeMillis() + ONE_DAY * 1
        }
        picker.show()
    }

    private fun save() {
        if (binding.nameText.text.toString() == "") {
            Toast.makeText(this, "식재료 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else if (binding.dateView.text.toString() == "") {
            Toast.makeText(this, "유통기한을 입력해주세요.", Toast.LENGTH_SHORT).show()
        } else if (!binding.freezeCheckBox.isChecked && !binding.fridgeCheckBox.isChecked && !binding.roomCheckBox.isChecked) {
            Toast.makeText(this, "보관장소를 선택해주세요.", Toast.LENGTH_SHORT).show()
        } else {
            var name = binding.nameText.text.toString()
            var date = binding.dateView.text.toString()
            var loc = 3
            when {
                binding.freezeCheckBox.isChecked -> {
                    loc = 0
                }
                binding.fridgeCheckBox.isChecked -> {
                    loc = 1
                }
                binding.roomCheckBox.isChecked -> {
                    loc = 2
                }
            }
            /****************** D - day 계산용 *********************/
            // D-day 계산용 Long Type의 날짜를 저장
            var calendar = Calendar.getInstance()
            val df = SimpleDateFormat("yyyy.MM.dd")
            calendar.time =
                df.parse(binding.dateView.text.toString())!!    // dateView에 작성되어있는 string을 이용
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time
            var date_long = (calendar.time.time - today) / ONE_DAY

            /*****************************************************/

            var food = FoodInfo(name, date, date_long, loc, false)

            saveFood(food)
            uploadImageToStorage()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun saveFood(food: FoodInfo) = CoroutineScope(Dispatchers.IO).launch {
        //withContext는 다른 스레드로 포커스를 전환하는 메서드
        try {
            var firebaseUser = FirebaseAuth.getInstance().currentUser
            var foodCollectionRef = FirebaseFirestore.getInstance()
            if (firebaseUser != null) {
                foodCollectionRef.collection("user").document(firebaseUser.uid).collection("foods")
                    .add(food).await()
            } // await는 데이터가 성공적으로 업로드가 될 때 까지 기다려주는 메서드
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, "저장되었습니다.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_IMAGE_PICK) {
            data?.data?.let {
                curFile = it
                binding.cIV.setImageURI(it)
            }
        }
    }

    private fun uploadImageToStorage() = CoroutineScope(Dispatchers.IO).launch {
        try {
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var imgFileName = "IMAGE$timeStamp.png"
            curFile?.let {
                imageRef.child("images").child(imgFileName).putFile(it).await()
                withContext(Dispatchers.Main) {
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}