package net.sourcewalker.android.calculon;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CalculatorActivity extends FragmentActivity {

    private EditText inputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        inputView = (EditText) findViewById(R.id.input);
    }

    public void onNumberPressed(View view) {
        if (view instanceof Button) {
            // Append button text to current input
            CharSequence text = ((Button) view).getText();
            inputView.getText().append(text);
        }
    }

}
