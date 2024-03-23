package core.data;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Utilidades.Utilidades;

public class VolleyRequest {

    private static final String BASE_URL = "http://" + Utilidades.IP + "/";
    private static VolleyRequest instance;
    private RequestQueue requestQueue;
    private Context context;

    private VolleyRequest(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VolleyRequest getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyRequest(context);
        }
        return instance;
    }

    public void postData(String usuario, final VolleyCallback callback) {
        String url = BASE_URL + "ws/control_select_clientes.aspx";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject respuesta_json = null;
                        try {
                            respuesta_json = new JSONObject(response);
                            JSONArray jsonArray_list = respuesta_json.getJSONArray("contenido_grilla");

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuario", usuario);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
    public interface VolleyCallback {
        void onSuccess(String response);
        void onError(VolleyError error);
    }

}
