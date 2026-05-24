package com.example.estagioapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CurriculoActivity extends AppCompatActivity {

    private boolean temExperiencia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculo);

        String nomeVaga    = getIntent().getStringExtra("nome_vaga");
        String empresaVaga = getIntent().getStringExtra("empresa_vaga");

        ((TextView) findViewById(R.id.tv_nome_vaga)).setText(nomeVaga);
        ((TextView) findViewById(R.id.tv_empresa_vaga)).setText(empresaVaga);

        EditText etNome = findViewById(R.id.et_nome);
        etNome.setText(AppData.getNome(this));

        LinearLayout btnSim = findViewById(R.id.btn_sim);
        LinearLayout btnNao = findViewById(R.id.btn_nao);
        TextView txtSim     = findViewById(R.id.txt_sim);
        TextView txtNao     = findViewById(R.id.txt_nao);

        btnSim.setOnClickListener(v -> {
            temExperiencia = true;
            btnSim.setBackgroundResource(R.drawable.bg_chip_active);
            btnNao.setBackgroundResource(R.drawable.bg_chip_inactive);
            txtSim.setTextColor(0xFFFFFFFF);
            txtNao.setTextColor(0xFF444444);
        });

        btnNao.setOnClickListener(v -> {
            temExperiencia = false;
            btnNao.setBackgroundResource(R.drawable.bg_chip_active);
            btnSim.setBackgroundResource(R.drawable.bg_chip_inactive);
            txtNao.setTextColor(0xFFFFFFFF);
            txtSim.setTextColor(0xFF444444);
        });

        // Voltar
        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());

        // Enviar
        findViewById(R.id.btn_enviar).setOnClickListener(v -> {

            EditText etEmail     = findViewById(R.id.et_email);
            EditText etTelefone  = findViewById(R.id.et_telefone);
            EditText etPretensao = findViewById(R.id.et_pretensao);
            EditText etMensagem  = findViewById(R.id.et_mensagem);

            String nome      = etNome.getText().toString().trim();
            String email     = etEmail.getText().toString().trim();
            String telefone  = etTelefone.getText().toString().trim();
            String pretensao = etPretensao.getText().toString().trim();
            String mensagem  = etMensagem.getText().toString().trim();

            if (nome.isEmpty()) {
                etNome.setError("Informe seu nome");
                etNome.requestFocus();
                return;
            }
            if (email.isEmpty()) {
                etEmail.setError("Informe seu e-mail");
                etEmail.requestFocus();
                return;
            }
            if (telefone.isEmpty()) {
                etTelefone.setError("Informe seu telefone");
                etTelefone.requestFocus();
                return;
            }
            if (pretensao.isEmpty()) {
                etPretensao.setError("Informe sua pretensão salarial");
                etPretensao.requestFocus();
                return;
            }

            Toast.makeText(this,
                    "Candidatura enviada com sucesso! ✓",
                    Toast.LENGTH_LONG).show();

            finish();
        });
    }
}