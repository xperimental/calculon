package net.sourcewalker.android.calculon;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CalculatorActivity extends FragmentActivity {

    private static final int OPERATOR_NONE = 0;
    private static final int OPERATOR_PLUS = 1;
    private static final int OPERATOR_MINUS = 2;

    private EditText inputView;
    private int currentOperator = OPERATOR_NONE;
    private int savedValue = 0;
    private boolean clearOnInput = true;

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
            if (clearOnInput) {
                inputView.setText(text);
                clearOnInput = false;
            } else {
                inputView.getText().append(text);
            }
        }
    }

    public void onClearPressed(View view) {
        inputView.setText("0");
        currentOperator = OPERATOR_NONE;
        savedValue = 0;
        clearOnInput = true;
    }

    public void onOperatorPressed(View view) {
        switch (view.getId()) {
            case R.id.plus:
                setOperator(OPERATOR_PLUS);
                break;
            case R.id.minus:
                setOperator(OPERATOR_MINUS);
                break;
            case R.id.multiply:
            case R.id.divide:
                Log.d(Constants.TAG, "Currently not implemented!");
                break;
        }
    }

    public void onEnterPressed(View view) {
        executeOperator();
        currentOperator = OPERATOR_NONE;
    }

    private void setOperator(int operator) {
        if (currentOperator != OPERATOR_NONE) {
            executeOperator();
        }
        currentOperator = operator;
        savedValue = getInputValue();
        clearOnInput = true;
    }

    private void executeOperator() {
        int inputValue = getInputValue();
        switch (currentOperator) {
            case OPERATOR_PLUS:
                savedValue = savedValue + inputValue;
                inputView.setText(Integer.toString(savedValue));
                break;
            case OPERATOR_MINUS:
                savedValue = savedValue - inputValue;
                inputView.setText(Integer.toString(savedValue));
                break;
        }
    }

    public int getInputValue() {
        String input = inputView.getText().toString();
        try {
            return Integer.valueOf(input, 10);
        } catch (NumberFormatException e) {
            Log.e(Constants.TAG, "Invalid number: " + input);
            return 0;
        }
    }

}
