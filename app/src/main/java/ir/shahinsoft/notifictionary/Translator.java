package ir.shahinsoft.notifictionary;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kotlin.text.Regex;


/**
 * Created by shayan4shayan on 3/19/18.
 */

public class Translator implements Response.Listener<String>, Response.ErrorListener {
    private static RequestQueue requestQueue;
    private static Object DEFAULT_TAG = "Translator";
    private Context context;
    private TranslateListener listener;
    private String word;
    private String target = "fa";
    private String source = "en";
    private String key = "250a878383cdc41ca196";
    private String baseUrl = "https://api.mymemory.translated.net/get";
    private Object tag = new Object();

    //to remove unnecessary characters
    private Regex regex = new Regex("[!@#$%^&*()_+=\"\';:?/><.,]");


    public static Translator with(Context context) {
        return new Translator(context);
    }

    private Translator(Context context) {
        this.context = context;
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context);
    }

    public Translator callback(TranslateListener listener) {
        this.listener = listener;
        return this;
    }

    public void translate(String word, Object tag) {
        this.word = word;
        this.tag = tag;
        Log.i("Translator", "source: " + source);
        Log.i("Translator", "target: " + target);
        requestQueue.cancelAll(tag);
        String url = createUrl();
        Log.d("Translator", url);
        StringRequest request = new StringRequest(url, this, this);
        request.setTag(tag);
        requestQueue.add(request);
    }

    public void translate(String word) {
        translate(regex.replace(word, "").trim(), DEFAULT_TAG);
    }

    private String createUrl() {
        return Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter("key", key)
                .appendQueryParameter("q", word)
                .appendQueryParameter("langpair", source + '|' + target).build().toString();
    }


    public Translator TranslateTo(String target) {
        this.target = target;
        return this;
    }

    public Translator translateFrom(String source) {
        this.source = source;
        return this;
    }

    @Override
    public void onResponse(String response) {
        Log.d("Translator", response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            int code = jsonObject.getInt("responseStatus");
            if(code==200) {
                    String translate = jsonObject.getJSONObject("responseData").getString("translatedText");
                    listener.onWordTranslated(translate);
            } else {
                throw new JSONException(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFailedToTranslate(context.getString(R.string.error_unknown));
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (error instanceof NoConnectionError) {
            listener.onFailedToTranslate(context.getString(R.string.error_noConnection));
            return;
        }
        if (error instanceof TimeoutError) {
            listener.onFailedToTranslate(context.getString(R.string.error_timeout));
            return;
        }
        if (error instanceof ServerError) {
            listener.onFailedToTranslate(context.getString(R.string.error_server));
            return;
        }
        if (error instanceof NetworkError) {
            listener.onFailedToTranslate(context.getString(R.string.error_network));
            return;
        }
        listener.onFailedToTranslate(context.getString(R.string.error_unknown));

    }

    public void cancelAll() {
        requestQueue.cancelAll(tag);
    }

    public interface TranslateListener {
        void onWordTranslated(String translate);

        void onFailedToTranslate(String reason);
    }
}
