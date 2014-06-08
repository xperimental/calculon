package net.sourcewalker.android.calculon.client;

import com.google.gson.annotations.SerializedName;

public class ResponseData {

    @SerializedName("isError")
    private boolean error;
    private String errorMessage;
    private int result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
