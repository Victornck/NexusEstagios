package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EmpresaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        // Carrega nome da empresa
        String empresa = AppData.getEmpresa(this);
        String inicial = empresa.length() > 0
                ? String.valueOf(empresa.charAt(0)).toUpperCase()
                : "E";

        ((TextView) findViewById(R.id.tv_nome_empresa)).setText(empresa);
        ((TextView) findViewById(R.id.tv_avatar_empresa)).setText(inicial);

        // Nova vaga
        findViewById(R.id.btn_nova_vaga).setOnClickListener(v ->
                startActivity(new Intent(this, NovaVagaActivity.class)));

        // Ver candidatos
        findViewById(R.id.btn_ver_candidatos_1).setOnClickListener(v ->
                Toast.makeText(this, "Candidatos: Front-end", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btn_ver_candidatos_2).setOnClickListener(v ->
                Toast.makeText(this, "Candidatos: Design UX/UI", Toast.LENGTH_SHORT).show());

        // Aceitar / Recusar candidatos
        findViewById(R.id.btn_aceitar_1).setOnClickListener(v ->
                atualizarStatus(R.id.btn_aceitar_1, R.id.btn_recusar_1, "Lucas Almeida", true));
        findViewById(R.id.btn_recusar_1).setOnClickListener(v ->
                atualizarStatus(R.id.btn_aceitar_1, R.id.btn_recusar_1, "Lucas Almeida", false));
        findViewById(R.id.btn_aceitar_2).setOnClickListener(v ->
                atualizarStatus(R.id.btn_aceitar_2, R.id.btn_recusar_2, "Carla Santos", true));
        findViewById(R.id.btn_recusar_2).setOnClickListener(v ->
                atualizarStatus(R.id.btn_aceitar_2, R.id.btn_recusar_2, "Carla Santos", false));

        // Sair
        findViewById(R.id.btn_sair_empresa).setOnClickListener(v -> {
            AppData.setPerfilLogado(this, "");
            AppData.setEmailLogado(this, "");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void atualizarStatus(int idAceitar, int idRecusar,
                                 String nome, boolean aceito) {
        TextView btnAceitar = findViewById(idAceitar);
        TextView btnRecusar = findViewById(idRecusar);

        if (aceito) {
            btnAceitar.setText("Aceito ✓");
            btnAceitar.setBackgroundResource(R.drawable.bg_badge_done);
            btnRecusar.setVisibility(android.view.View.GONE);
            Toast.makeText(this, nome + " aceito!", Toast.LENGTH_SHORT).show();
        } else {
            btnRecusar.setText("Recusado");
            btnRecusar.setBackgroundResource(R.drawable.bg_badge_done);
            btnAceitar.setVisibility(android.view.View.GONE);
            Toast.makeText(this, nome + " recusado.", Toast.LENGTH_SHORT).show();
        }
    }
}