package com.zowdow.android.example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zowdow.android.example.advanced.AdvancedIntegrationDemoActivity;
import com.zowdow.android.example.basic.BasicIntegrationDemoActivity;
import com.zowdow.android.example.external.ExternalSuggestionsDemoActivity;
import com.zowdow.android.example.widget.DiscoveryWidgetActivity;
import com.zowdow.android.example.widget.DiscoveryWidgetPagerActivity;

public class StartActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForPermissions();
    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
        }
    }

    public void clickBasicIntegration(View view) {
        startZowDowDemoActivity();
    }

    public void clickAdvancedIntegration(View view) {
        startAdvancedDemoActivity();
    }

    public void clickDiscoveryWidgetIntegration(View view) {
        startDiscoveryWidgetActivity();
    }

    public void clickDiscoveryWidgetsPagerIntegration(View view) {
        startDiscoveryWidgetsActivity();
    }

    public void clickSdkFromScratchPreview(View view) {
        startExternalSuggestionsActivity();
    }

    private void startZowDowDemoActivity() {
        Intent i = new Intent(this, BasicIntegrationDemoActivity.class);
        startActivity(i);
    }

    private void startAdvancedDemoActivity() {
        Intent i = new Intent(this, AdvancedIntegrationDemoActivity.class);
        startActivity(i);
    }

    private void startDiscoveryWidgetActivity() {
        Intent i = new Intent(this, DiscoveryWidgetActivity.class);
        startActivity(i);
    }

    private void startDiscoveryWidgetsActivity() {
        Intent i = new Intent(this, DiscoveryWidgetPagerActivity.class);
        startActivity(i);
    }

    private void startExternalSuggestionsActivity() {
        startActivity(new Intent(this, ExternalSuggestionsDemoActivity.class));
    }
}
