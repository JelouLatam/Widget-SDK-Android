package ai.jelou.widget;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by luibasantes on 15/03/2023
 */

public class Widget {
    public static void Toast(Context c, String message){
        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();
    }
}