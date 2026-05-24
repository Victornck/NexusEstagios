package com.example.estagioapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvNomeUsuario;
    private TextView tvLetraUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ───── IDS ─────

        tvNomeUsuario = findViewById(R.id.tv_nome_usuario);
        tvLetraUsuario = findViewById(R.id.tv_letra_usuario);

        // ───── CARREGAR DADOS DO USUÁRIO ─────

        carregarUsuario();

        // ───── CLIQUE NO AVATAR (LETRA) ─────

        tvLetraUsuario.setOnClickListener(v ->
                NavHelper.navigate(
                        this,
                        PerfilActivity.class
                )
        );

        // ───── BOTTOM NAV ─────

        findViewById(R.id.nav_inicio).setOnClickListener(v -> {
            // já está na home
        });

        findViewById(R.id.nav_vagas).setOnClickListener(v ->
                NavHelper.navigate(this, VagasActivity.class));

        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));

        findViewById(R.id.nav_perfil).setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Atualiza caso o usuário edite o perfil
        carregarUsuario();
    }

    // ───── FUNÇÃO DE CARREGAR USUÁRIO ─────

    private void carregarUsuario() {

        String nome = AppData.getNome(this);

        // Nome na home
        tvNomeUsuario.setText(nome);

        // Primeira letra do nome (avatar)
        if (nome != null && !nome.isEmpty()) {

            String letra =
                    String.valueOf(nome.charAt(0))
                            .toUpperCase();

            tvLetraUsuario.setText(letra);
        }
    }
}