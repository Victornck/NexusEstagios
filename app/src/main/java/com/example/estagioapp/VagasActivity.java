package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class VagasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vagas);

        // Botões de candidatura
        findViewById(R.id.btn_candidatura_1).setOnClickListener(v ->
                abrirCandidatura("Estágio em Desenvolvimento Front-end", "Nova Tech"));

        findViewById(R.id.btn_candidatura_2).setOnClickListener(v ->
                abrirCandidatura("Estágio em Design UX/UI", "Orange Studios"));

        findViewById(R.id.btn_candidatura_3).setOnClickListener(v ->
                abrirCandidatura("Estágio em Análise Financeira", "Banco Lumen"));

        findViewById(R.id.btn_candidatura_4).setOnClickListener(v ->
                abrirCandidatura("Estágio em Marketing Digital", "Mercado Hub"));

        // Navegação
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHelper.navigate(VagasActivity.this, MainActivity.class);
            }
        });

        findViewById(R.id.nav_inicio).setOnClickListener(v ->
                NavHelper.navigate(this, MainActivity.class));
        findViewById(R.id.nav_vagas).setOnClickListener(v -> {});
        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
        findViewById(R.id.nav_perfil).setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));
    }

    private void abrirCandidatura(String nomeVaga, String empresa) {
        Intent intent = new Intent(this, CurriculoActivity.class);
        intent.putExtra("nome_vaga", nomeVaga);
        intent.putExtra("empresa_vaga", empresa);
        startActivity(intent);
    }
}