package ai.jelou.widgetsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ai.jelou.widget.WidgetService;

public class MainActivity extends AppCompatActivity {
    public static final String JelouApiKey = "MTVlN2E0MmMtYzExYS00Mzk3LWI1ZGEtMjAyZGRiNjM3ZjBh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WidgetService widgetService = new WidgetService(JelouApiKey, "qwerty");
        widgetService.setName("Luigi");
        widgetService.Toast(MainActivity.this, "Hwllwlw ");

        widgetService.connect(MainActivity.this);
    }
}