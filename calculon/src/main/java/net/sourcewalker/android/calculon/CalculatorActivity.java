package net.sourcewalker.android.calculon;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import net.sourcewalker.android.calculon.client.CalcServerRequest;
import net.sourcewalker.android.calculon.client.RequestData;
import net.sourcewalker.android.calculon.client.ResponseData;

public class CalculatorActivity extends FragmentActivity {

    private static final int OPERATOR_NONE = 0;
    private static final int OPERATOR_PLUS = 1;
    private static final int OPERATOR_MINUS = 2;
    private static final int OPERATOR_MULTIPLY = 3;
    private static final int OPERATOR_DIVIDE = 4;

    private static final int[] BUTTONS = new int[] {
            R.id.number_0,
            R.id.number_1,
            R.id.number_2,
            R.id.number_3,
            R.id.number_4,
            R.id.number_5,
            R.id.number_6,
            R.id.number_7,
            R.id.number_8,
            R.id.number_9,
            R.id.plus,
            R.id.minus,
            R.id.multiply,
            R.id.divide,
            R.id.enter
    };

    private static final RequestQueue.RequestFilter ALL_REQUESTS = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    private RequestQueue requestQueue;
    private RequestListener requestListener;
    private EditText inputView;
    private int currentOperator = OPERATOR_NONE;
    private int savedValue = 0;
    private boolean clearOnInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        requestQueue = Volley.newRequestQueue(this);
        requestListener = new RequestListener();

        inputView = (EditText) findViewById(R.id.input);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Cancel all pending requests
        requestQueue.cancelAll(ALL_REQUESTS);
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
        requestQueue.cancelAll(ALL_REQUESTS);
        setUiEnabled(true);
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
                setOperator(OPERATOR_MULTIPLY);
                break;
            case R.id.divide:
                setOperator(OPERATOR_DIVIDE);
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
            case OPERATOR_MULTIPLY:
            case OPERATOR_DIVIDE:
                onlineCalculation(currentOperator, savedValue, inputValue);
                break;
        }
    }

    private void onlineCalculation(int operation, int opOne, int opTwo) {
        RequestData requestData = new RequestData();
        switch (operation) {
            case OPERATOR_MULTIPLY:
                requestData.setOperation("MULTIPLY");
                break;
            case OPERATOR_DIVIDE:
                requestData.setOperation("DIVIDE");
                break;
        }
        requestData.setOperandOne(opOne);
        requestData.setOperandTwo(opTwo);
        setStatusMessage("WAIT");
        CalcServerRequest request = new CalcServerRequest(requestData, requestListener);
        requestQueue.add(request);
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

    private void setStatusMessage(String message) {
        inputView.setText(message);
        setUiEnabled(false);
    }

    private void setUiEnabled(boolean enabled) {
        for (int id : BUTTONS) {
            findViewById(id).setEnabled(enabled);
        }
    }

    private final class RequestListener implements Response.Listener<ResponseData>, Response.ErrorListener {

        @Override
        public void onResponse(ResponseData response) {
            inputView.setText(Integer.toString(response.getResult()));
            setUiEnabled(true);
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setStatusMessage("ERR!");
        }

    }

}
