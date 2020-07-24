package com.zxy.marqueetextswitcher;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MarqueeTextSwitcher mtsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtsContent = findViewById(R.id.mts_content);
        List<String> list = Arrays.asList(
                "Hello World",
                "MarqueeTextSwitcher滚动栏",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "MarqueeTextSwitcher滚动栏 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        mtsContent.setTextList(list);
        mtsContent.startRun();
    }
}
