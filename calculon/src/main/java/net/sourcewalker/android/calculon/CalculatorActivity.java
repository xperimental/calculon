package net.sourcewalker.android.calculon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import net.sourcewalker.android.calculon.client.CalcResultListener;
import net.sourcewalker.android.calculon.client.CalcServerRequest;
import net.sourcewalker.android.calculon.client.RequestData;
import net.sourcewalker.android.calculon.client.ResponseData;

public class CalculatorActivity extends ActionBarActivity implements NetworkStatusHelper.NetworkListener {

    private static final int[] BUTTONS = new int[]{
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
            R.id.enter
    };

    private static final int[] NETWORK_BUTTONS = new int[]{
            R.id.multiply,
            R.id.divide
    };

    private static final RequestQueue.RequestFilter ALL_REQUESTS = new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    };

    public static final String OPERATION_MULTIPLY = "MULTIPLY";
    public static final String OPERATION_DIVIDE = "DIVIDE";

    private RequestQueue requestQueue;
    private RequestListener requestListener;
    private NetworkStatusHelper networkHelper;
    private EditText inputView;
    private int currentOperator = Constants.OPERATOR_NONE;
    private int savedValue = 0;
    private boolean clearOnInput = true;
    private boolean produceHistory = true;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public void setProduceHistory(boolean produceHistory) {
        this.produceHistory = produceHistory;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        requestQueue = Volley.newRequestQueue(this);
        requestListener = new RequestListener();
        networkHelper = new NetworkStatusHelper(this);
        networkHelper.setListener(this);

        inputView = (EditText) findViewById(R.id.input);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkHelper.onResume();

        updateServerButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkHelper.onPause();

        // Cancel all pending requests
        requestQueue.cancelAll(ALL_REQUESTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calculator_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_recent) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNetworkChange(boolean connected) {
        updateServerButtons();
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
        currentOperator = Constants.OPERATOR_NONE;
        savedValue = 0;
        clearOnInput = true;
    }

    public void onOperatorPressed(View view) {
        switch (view.getId()) {
            case R.id.plus:
                setOperator(Constants.OPERATOR_PLUS);
                break;
            case R.id.minus:
                setOperator(Constants.OPERATOR_MINUS);
                break;
            case R.id.multiply:
                setOperator(Constants.OPERATOR_MULTIPLY);
                break;
            case R.id.divide:
                setOperator(Constants.OPERATOR_DIVIDE);
                break;
        }
    }

    public void onEnterPressed(View view) {
        executeOperator();
        currentOperator = Constants.OPERATOR_NONE;
    }

    private void updateServerButtons() {
        boolean connected = networkHelper.isConnected();
        for (int viewId : NETWORK_BUTTONS) {
            View v = findViewById(viewId);
            v.setEnabled(connected);
        }
    }

    private void setOperator(int operator) {
        if (currentOperator != Constants.OPERATOR_NONE) {
            executeOperator();
        }
        currentOperator = operator;
        savedValue = getInputValue();
        clearOnInput = true;
    }

    private void executeOperator() {
        int inputValue = getInputValue();
        int result;
        switch (currentOperator) {
            case Constants.OPERATOR_PLUS:
                result = savedValue + inputValue;
                finishCalculation(savedValue, inputValue, currentOperator, result);
                break;
            case Constants.OPERATOR_MINUS:
                result = savedValue - inputValue;
                finishCalculation(savedValue, inputValue, currentOperator, result);
                break;
            case Constants.OPERATOR_MULTIPLY:
            case Constants.OPERATOR_DIVIDE:
                onlineCalculation(currentOperator, savedValue, inputValue);
                break;
        }
    }

    private void finishCalculation(int operandOne, int operandTwo, int operation, int result) {
        if (produceHistory) {
            // Do database stuff in background
            AppendHistoryTask task = new AppendHistoryTask(this);
            task.execute(operandOne, operandTwo, operation, result);
        }
        savedValue = result;
        inputView.setText(Integer.toString(savedValue));
    }

    private void onlineCalculation(int operation, int opOne, int opTwo) {
        RequestData requestData = new RequestData();
        requestData.setOperation(getStringOperation(operation));
        requestData.setOperandOne(opOne);
        requestData.setOperandTwo(opTwo);
        setStatusMessage("WAIT");
        CalcServerRequest request = new CalcServerRequest(requestData, requestListener);
        requestQueue.add(request);
    }

    private String getStringOperation(int operation) {
        switch (operation) {
            case Constants.OPERATOR_MULTIPLY:
                return OPERATION_MULTIPLY;
            case Constants.OPERATOR_DIVIDE:
                return OPERATION_DIVIDE;
            default:
                return "";
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

    private void setStatusMessage(String message) {
        inputView.setText(message);
        setUiEnabled(false);
    }

    private void setUiEnabled(boolean enabled) {
        for (int id : BUTTONS) {
            findViewById(id).setEnabled(enabled);
        }
        boolean connected = networkHelper.isConnected();
        for (int id : NETWORK_BUTTONS) {
            findViewById(id).setEnabled(enabled && connected);
        }
    }

    private final class RequestListener implements CalcResultListener, Response.ErrorListener {

        @Override
        public void onCalcResult(RequestData request, ResponseData response) {
            finishCalculation(request.getOperandOne(), request.getOperandTwo(),
                    getIntOperation(request.getOperation()), response.getResult());
            setUiEnabled(true);
        }

        private int getIntOperation(String operation) {
            if (OPERATION_MULTIPLY.equals(operation)) {
                return Constants.OPERATOR_MULTIPLY;
            } else if (OPERATION_DIVIDE.equals(operation)) {
                return Constants.OPERATOR_DIVIDE;
            } else {
                return Constants.OPERATOR_NONE;
            }
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            setStatusMessage("ERR!");
        }
    }

}
