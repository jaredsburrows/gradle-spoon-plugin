package com.jaredsburrows.spoon.example

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView

class MainActivity : Activity() {

  private lateinit var textView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    textView = TextView(this).apply {
      setId(android.R.id.text1)
      setGravity(Gravity.CENTER)
      setTextSize(TEXT_SIZE)
    }

    setContentView(textView)
  }

  fun setText(text: String) {
    textView.setText(text)
  }

  companion object {
    private const val TEXT_SIZE = 30F
  }
}
