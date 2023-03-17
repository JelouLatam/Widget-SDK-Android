package ai.jelou.widget;

import static ai.jelou.widget.Constant.API_WIDGET_INIT;
import static ai.jelou.widget.Constant.EVENT_MESSAGE;
import static ai.jelou.widget.Constant.PUSHER_EVENT_ROOM_MESSAGE;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.pusher.client.Pusher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by luibasantes on 15/03/2023
 */

public class WidgetService {
    private RequestQueue requestQueue;
    private String userId;
    private String apiKey;
    private String name;
    private String token;

    private JSONObject User;
    private JSONObject Widget;
    private JSONObject SocketCredentials;

    private Channel channel;

    // Default Constructor
    public WidgetService(String apiKey) {      //constructor 1
        this.apiKey = apiKey;
        this.userId = UUID.randomUUID().toString();
    }
    public WidgetService(String apiKey, String userId) {
        this.apiKey = apiKey;
        this.userId = userId;
    }
    public WidgetService(String apiKey,String userId,String name) {  //constructor 2
        this.apiKey = apiKey;
        this.userId = userId;
        this.name = name;
    }

    public void Toast(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void connect(Context context){
        try{
            callInitWidget(context);
        } catch (Exception e){
            System.out.println("Error connecting" + e.toString());
        }
    }

    public void on(String event, WidgetEventListener eventListener) throws Exception{
        this.on(event, eventListener,1);
    }
    public void on(String event, WidgetEventListener eventListener, int retries) throws Exception{
        if (this.channel == null && retries > 0) {
            System.out.println("Failed To Bind WidgetEventListener, connection not yet made");
            WidgetService instance = this;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        instance.on(event, eventListener, retries - 1);
                    }catch(Exception e){
                        System.out.println("Failed on retry to Bind WidgetEventListener");
                    }
                }
            }, 2000);
            return;
        }



        switch (event){
            case EVENT_MESSAGE:
                this.channel.bind(PUSHER_EVENT_ROOM_MESSAGE, new SubscriptionEventListener() {
                    @Override
                    public void onEvent(PusherEvent event) {
                        eventListener.run(event.getData());
                    }
                });
                break;
            default:
                throw new Exception("Event '" +event + "' does not exists." );
        }
    }

    private void callInitWidget(Context context) throws Exception {
        RequestQueue volleyQueue = Volley.newRequestQueue(context);

        JSONObject userPayload = new JSONObject();
        userPayload.put("id", this.userId);
        userPayload.put("names", this.name);

        JSONObject payloadToSend = new JSONObject();
        payloadToSend.put("user", userPayload);

        String apiKey = this.apiKey;

        // since the response we get from the api is in JSON,
        // we need to use `JsonObjectRequest` for
        // parsing the request response
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                // we are using GET HTTP request method
                Request.Method.POST,
                // url we want to send the HTTP request to
                API_WIDGET_INIT,
                // this parameter is used to send a JSON object
                // to the server.
                payloadToSend,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        handleOnInitResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("Error in call connect()" + error.toString());
                    }
                }
        ) {
            /**
             * Passing some request headers*
             */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("apiKey", apiKey);
                return headers;
            }
        };

        // add the json request object created
        // above to the Volley request queue
        volleyQueue.add(jsonObjectRequest);
    }
    private void handleOnInitResponse(JSONObject response){
        try{
            // Extract Data From Response
            JSONObject data = response.getJSONObject("data");
            String token = data.getString("token");
            JSONObject user = data.getJSONObject("User");
            JSONObject Widget = data.getJSONObject("Widget");
            JSONObject SocketCredentials = data.getJSONObject("SocketCredentials");
//            JSONObject Company = data.getJSONObject("company");
//            JSONObject Bot = data.getJSONObject("bot");
            // Store Variables
            this.User = user;
            this.Widget = Widget;
            this.SocketCredentials = SocketCredentials;
            this.token = token;
            // Try to connect to Socket
            this.connectToSocket();
        }catch(JSONException e){
            System.out.println("Error Parsing Connect Response:" + e.toString());
        }
    }
    private void connectToSocket() throws JSONException {
        PusherOptions options = new PusherOptions();
        options.setCluster(this.SocketCredentials.getString("cluster"));

        Pusher pusher = new Pusher(this.SocketCredentials.getString("key"), options);
        pusher.connect();
        String channelName = "socket-" + this.User.getString("socketId");
        this.channel = pusher.subscribe(channelName);
    }

    @Override
    public String toString() {
        return "WidgetService Instance{" +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}