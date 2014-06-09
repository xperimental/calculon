package net.sourcewalker.android.calculon.client;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class CalcServerRequest extends Request<ResponseData> {

    /**
     * Base URL of backend. Change this if you want to run your own server...
     */
    private static final String BASE_URL = "http://calculon-server.herokuapp.com";

    private final RequestData requestData;
    private final CalcResultListener successListener;
    private final Gson gson;

    public <T extends CalcResultListener & Response.ErrorListener> CalcServerRequest(RequestData requestData, T listener) {
        super(Method.POST, BASE_URL + "/calc", listener);
        this.requestData = requestData;
        this.successListener = listener;
        this.gson = new Gson();
        setShouldCache(false);
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        String json = gson.toJson(requestData);
        try {
            return json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new AuthFailureError("Unsupported encoding!", e);
        }
    }

    @Override
    protected Response<ResponseData> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, "utf-8");
            ResponseData data = gson.fromJson(json, ResponseData.class);
            return Response.success(data, null);
        } catch (UnsupportedEncodingException e) {
            return Response.error(new VolleyError("Unsupported encoding while parsing response.", e));
        }
    }

    @Override
    protected void deliverResponse(ResponseData response) {
        if (successListener != null) {
            successListener.onCalcResult(requestData, response);
        }
    }

}
