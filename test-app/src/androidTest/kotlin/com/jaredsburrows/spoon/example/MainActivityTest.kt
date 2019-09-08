package com.jaredsburrows.spoon.example

import android.support.test.annotation.UiThreadTest
import android.support.test.filters.LargeTest
import android.support.test.filters.MediumTest
import android.support.test.filters.SmallTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.google.common.truth.Truth.assertThat
import com.jaredsburrows.spoon.example.MainActivity
import com.squareup.spoon.SpoonRule
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
  @get:Rule val spoon = SpoonRule()
  @get:Rule val activityRule = ActivityTestRule(MainActivity::class.java)

  @Test @UiThreadTest fun testSetText() {
    val act = activityRule.getActivity()
    val text = act.findViewById(android.R.id.text1) as TextView
    spoon.screenshot(act, "startup")

    val steps = 5
    for (i in 1..steps) {
      val step = i.toString()
      act.setText(step)
      spoon.screenshot(act, "step-" + i)
      assertThat(text.getText().toString()).isEqualTo(step)
    }
  }

  @SmallTest fun testSmallTest() {
    val act = activityRule.getActivity()

    spoon.screenshot(act, "startup-smallTest")
  }

  @MediumTest fun testMediumTest() {
    val act = activityRule.getActivity()

    spoon.screenshot(act, "startup-mediumTest")
  }

  @LargeTest fun testLargeTest() {
    val act = activityRule.getActivity()

    spoon.screenshot(act, "startup-largeTest")
  }
}
