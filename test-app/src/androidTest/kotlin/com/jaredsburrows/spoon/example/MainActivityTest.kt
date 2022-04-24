package com.jaredsburrows.spoon.example

import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.squareup.spoon.SpoonRule
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.runner.RunWith
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
  @get:Rule(order = 0)
  val activityScenarioRule: ActivityScenarioRule<MainActivity> =
    ActivityScenarioRule(MainActivity::class.java)
  @get:Rule(order = 1)
  val spoonRule = SpoonRule()

  @Test
  fun testSetText() {
    activityScenarioRule
      .scenario
      .onActivity { activity ->
        val text = activity.findViewById(android.R.id.text1) as TextView
        spoonRule.screenshot(activity, "start")

        val steps = 5
        for (i in 1..steps) {
          val step = i.toString()
          activity.setText(step)
          spoonRule.screenshot(activity, "step-" + i)
          assertThat(text.getText().toString()).isEqualTo(step)
        }
      }
  }

  @SmallTest
  @Test
  fun testSmallTest() {
    activityScenarioRule
      .scenario
      .onActivity { activity ->
        spoonRule.screenshot(activity, "smallTest")
      }
  }

  @MediumTest
  @Test
  fun testMediumTest() {
    activityScenarioRule
      .scenario
      .onActivity { activity ->
        spoonRule.screenshot(activity, "mediumTest")
      }
  }

  @LargeTest
  @Test
  fun testLargeTest() {
    activityScenarioRule
      .scenario
      .onActivity { activity ->
        spoonRule.screenshot(activity, "largeTest")
      }
  }
}
