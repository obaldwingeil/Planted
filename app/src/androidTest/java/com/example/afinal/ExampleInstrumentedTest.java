package com.example.afinal;

import android.content.Context;
import android.provider.ContactsContract;
import android.provider.Telephony;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityScenarioRule loginTestRule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.afinal", appContext.getPackageName());
    }

    // Feature 1: test 1
    // The user is able to fill in information in each EditText view with
    // correct existing username and password combination
    @Test
    public void userEntersAllFields(){
        onView(withId(R.id.editText_loginEmail)).perform(typeText("tuggmama@email.com"));
        onView(withId(R.id.editText_loginPassword)).perform(typeText("dontmesswiththetug"))
        .perform(closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());

        // Intents.init();
        // intended(hasComponent(HomeActivity.class.getName()));
    }
}