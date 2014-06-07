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
        checkInput("0");
    }

    public void testPressNumber() throws Exception {
        pressButton(R.id.number_5);
        checkInput("5");
    }

    public void testLongNumber() throws Exception {
        pressButton(R.id.number_4);
        pressButton(R.id.number_2);
        checkInput("42");
    }

    public void testClear() throws Exception {
        checkInput("0");
        onView(withText("9")).perform(click());
        checkInput("9");
        pressButton(R.id.clear);
        checkInput("0");
    }

    private void checkInput(String value) {
        onView(withId(R.id.input)).check(matches(withText(value)));
    }

    private void pressButton(int id) {
        onView(withId(id)).perform(click());
    }

}