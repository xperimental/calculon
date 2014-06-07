package net.sourcewalker.android.calculon;

import android.test.ActivityInstrumentationTestCase2;

public class CalculatorActivityTest extends ActivityInstrumentationTestCase2<CalculatorActivity> {

    public CalculatorActivityTest() {
        super(CalculatorActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        getActivity();
    }

}