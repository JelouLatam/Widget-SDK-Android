package ai.jelou.widget;

import android.content.Context;
import android.widget.Toast;

import java.util.UUID;

/**
 * Created by luibasantes on 15/03/2023
 */

public class WidgetService {

    private String userId;
    private String name;

    // Default Constructor
    WidgetService() {      //constructor 1
        this.userId = UUID.randomUUID().toString();;
    }
    WidgetService(String userId,String name) {  //constructor 2
        this.name = name;
        this.userId = userId;
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

    public void connect(){

    }
}