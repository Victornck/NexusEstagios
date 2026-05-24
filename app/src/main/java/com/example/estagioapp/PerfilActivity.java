package com.example.estagioapp;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHelper.navigate(PerfilActivity.this, MainActivity.class);
            }
        });

        findViewById(R.id.nav_inicio).setOnClickListener(v ->
                NavHelper.navigate(this, MainActivity.class));
        findViewById(R.id.nav_vagas).setOnClickListener(v ->
                NavHelper.navigate(this, VagasActivity.class));
        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
        findViewById(R.id.nav_perfil).setOnClickListener(v -> {});
    }
}