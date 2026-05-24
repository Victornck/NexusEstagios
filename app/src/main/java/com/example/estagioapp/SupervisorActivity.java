package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SupervisorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        // Carrega nome do supervisor
        String nome = AppData.getNome(this);
        String[] partes = nome.split(" ");
        String inicial = partes.length > 0 ? String.valueOf(partes[0].charAt(0)) : "S";

        ((TextView) findViewById(R.id.tv_nome_supervisor)).setText(nome);
        ((TextView) findViewById(R.id.tv_avatar_supervisor)).setText(inicial.toUpperCase());

        // Ver atividades
        findViewById(R.id.btn_ver_atividades_1).setOnClickListener(v ->
                abrirAtividades("Lucas Almeida"));
        findViewById(R.id.btn_ver_atividades_2).setOnClickListener(v ->
                abrirAtividades("Carla Santos"));
        findViewById(R.id.btn_ver_atividades_3).setOnClickListener(v ->
                abrirAtividades("Rafael Moura"));

        // Avaliar
        findViewById(R.id.btn_avaliar_1).setOnClickListener(v ->
                abrirAvaliacao("Lucas Almeida"));
        findViewById(R.id.btn_avaliar_2).setOnClickListener(v ->
                abrirAvaliacao("Carla Santos"));
        findViewById(R.id.btn_avaliar_3).setOnClickListener(v ->
                abrirAvaliacao("Rafael Moura"));

        // Sair
        findViewById(R.id.btn_sair).setOnClickListener(v -> {
            AppData.setPerfilLogado(this, "");
            AppData.setEmailLogado(this, "");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void abrirAtividades(String nomeAluno) {
        Toast.makeText(this, "Atividades de " + nomeAluno, Toast.LENGTH_SHORT).show();
        // Aqui futuramente abre AlunoAtividadesActivity passando o nome
    }

    private void abrirAvaliacao(String nomeAluno) {
        Toast.makeText(this, "Avaliando " + nomeAluno, Toast.LENGTH_SHORT).show();
        // Aqui futuramente abre AvaliacaoActivity
    }
}