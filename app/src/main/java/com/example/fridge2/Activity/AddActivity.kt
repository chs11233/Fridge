package com.example.fridge2.Activity

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fridge2.FoodInfo
import com.example.fridge2.databinding.ActivityAddBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

    private val foodCollectionRef = Firebase.firestore.collection("foods")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 달력 버튼 눌렀을 때 (DatePicker로 달력 표시)
        binding.dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            var date_listener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    binding.dateView.text = "${year}.%02d.%02d".format(month + 1, dayOfMonth)
                }
            // DatePickerDialog 뒤에 .apply{} 부분은 현재 시간을 기준으로 그것보다 minDate는 선택할 수 없게 비활성화 하는 기능
            // System.currentTimeMillis는 1/1000초 단위로 현재시간이 long Type으로 반환된다. 때문에 뒤에 ONE_DAY(= 24 * 60 * 60 * 1000) * N 을 더해줘 N일 뒤부터 날짜 선택이 가능하도록 한다.
            var picker = DatePickerDialog(this, date_listener, year, month, day).apply {
                datePicker.minDate = System.currentTimeMillis() + ONE_DAY * 1
            }
            picker.show()
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
                var date_long = calendar.timeInMillis

                /*****************************************************/
                var food = FoodInfo(name, date, date_long, loc, false)

                saveFood(food)

                startActivity(Intent(this, MainActivity::class.java))
            }
        }

    }

    private fun saveFood(food: FoodInfo) = CoroutineScope(Dispatchers.IO).launch {
        //withContext는 다른 스레드로 포커스를 전환하는 메서드입니다!
        try {
            foodCollectionRef.add(food).await() // await는 데이터가 성공적으로 업로드가 될 때 까지 기다려주는 메서드입니다.
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, "저장되었습니다.", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@AddActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}