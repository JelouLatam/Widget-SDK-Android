package ai.jelou.widget;

import android.content.Context;
import android.widget.Toast;

public class Widget {
    public static void Toast(Context c, String message){

        Toast.makeText(c,message,Toast.LENGTH_SHORT).show();

    }
}
