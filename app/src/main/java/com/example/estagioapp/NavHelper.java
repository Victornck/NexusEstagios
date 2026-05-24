package com.example.estagioapp;

import android.app.Activity;
import android.content.Intent;

public class NavHelper {

    public static void navigate(Activity from, Class<?> to) {
        if (from.getClass().equals(to)) return;

        Intent intent = new Intent(from, to);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        from.startActivity(intent);
        from.overridePendingTransition(0, 0);
    }
}