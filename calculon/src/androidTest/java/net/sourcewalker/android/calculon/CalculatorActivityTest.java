package net.sourcewalker.android.calculon;

import android.test.ActivityInstrumentationTestCase2;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.action.ViewActions.click;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class CalculatorActivityTest extends ActivityInstrumentationTestCase2<CalculatorActivity> {

    public CalculatorActivityTest() {
        super(CalculatorActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

    public void testStartUp() throws Exception {
        onView(withId(R.id.input)).check(matches(withText("0")));
    }

    public void testPressNumber() throws Exception {
        onView(withId(R.id.number_5)).perform(click());
        onView(withId(R.id.input)).check(matches(withText("5")));
    }

    public void testLongNumber() throws Exception {
        onView(withId(R.id.number_4)).perform(click());
        onView(withId(R.id.number_2)).perform(click());
        onView(withId(R.id.input)).check(matches(withText("42")));
    }

    public void testClear() throws Exception {
        onView(withId(R.id.input)).check(matches(withText("0")));
        onView(withText("9")).perform(click());
        onView(withId(R.id.input)).check(matches(withText("9")));
        onView(withId(R.id.clear)).perform(click());
        onView(withId(R.id.input)).check(matches(withText("0")));
    }

}