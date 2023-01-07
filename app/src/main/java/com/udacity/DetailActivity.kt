package com.udacity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fetchDataFromIntent();
        handleBtns();
    }

    private fun handleBtns() {
        ok_btn.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun fetchDataFromIntent() {
        statusText.text = intent.getStringExtra("status").also {
            filename.text = intent.getStringExtra("filename")
            if(it == "failed") statusText.setTextColor(Color.RED) else statusText.setTextColor(Color.GREEN)
        }
    }

}
