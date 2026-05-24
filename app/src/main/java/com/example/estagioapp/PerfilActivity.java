package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        findViewById(R.id.btn_editar).setOnClickListener(v ->
                startActivity(new Intent(this, EditarPerfilActivity.class)));

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

    @Override
    protected void onResume() {
        super.onResume();
        carregarDados();
    }

    private void carregarDados() {
        String nome = AppData.getNome(this);
        String[] partes = nome.split(" ");
        String iniciais = partes.length >= 2
                ? String.valueOf(partes[0].charAt(0)) + partes[1].charAt(0)
                : String.valueOf(partes[0].charAt(0));

        ((TextView) findViewById(R.id.tv_avatar)).setText(iniciais.toUpperCase());
        ((TextView) findViewById(R.id.tv_nome)).setText(nome);
        ((TextView) findViewById(R.id.tv_curso)).setText(AppData.getCurso(this));
        ((TextView) findViewById(R.id.tv_estagio)).setText(AppData.getEstagio(this));
        ((TextView) findViewById(R.id.tv_empresa)).setText(AppData.getEmpresa(this));
        ((TextView) findViewById(R.id.tv_supervisor)).setText(AppData.getSupervisor(this));
        ((TextView) findViewById(R.id.tv_curriculos)).setText(
                AppData.getCurriculosCount(this) + " versões salvas");
    }
}