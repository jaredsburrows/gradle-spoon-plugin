package com.jaredsburrows.spoon.example

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {
    companion object {
        private const val TEXT_SIZE = 30F
    }

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        textView = TextView(this)
        textView.setId(android.R.id.text1)
        textView.setGravity(Gravity.CENTER)
        textView.setTextSize(TEXT_SIZE)
        setContentView(textView)
    }

    fun setText(text: String) {
        textView.setText(text)
    }
}
