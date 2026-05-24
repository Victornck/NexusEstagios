package com.example.estagioapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CurriculoActivity extends AppCompatActivity {

    private EditText etNome;
    private EditText etEmail;
    private EditText etTelefone;
    private EditText etPretensao;
    private EditText etMensagem;

    private LinearLayout btnSim;
    private LinearLayout btnNao;

    private TextView txtSim;
    private TextView txtNao;

    private TextView btnEnviar;
    private TextView btnVoltar;

    private boolean temExperiencia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculo);

        // ───── IDS ─────

        etNome = findViewById(R.id.et_nome);
        etEmail = findViewById(R.id.et_email);
        etTelefone = findViewById(R.id.et_telefone);
        etPretensao = findViewById(R.id.et_pretensao);
        etMensagem = findViewById(R.id.et_mensagem);

        btnSim = findViewById(R.id.btn_sim);
        btnNao = findViewById(R.id.btn_nao);

        txtSim = findViewById(R.id.txt_sim);
        txtNao = findViewById(R.id.txt_nao);

        btnEnviar = findViewById(R.id.btn_enviar);
        btnVoltar = findViewById(R.id.btn_voltar);

        // ───── CARREGAR DADOS ─────

        etNome.setText(AppData.getNome(this));
        etEmail.setText(AppData.getCurriculoEmail(this));
        etTelefone.setText(AppData.getCurriculoTelefone(this));
        etPretensao.setText(AppData.getCurriculoPretensao(this));
        etMensagem.setText(AppData.getCurriculoMensagem(this));

        temExperiencia = AppData.getCurriculoExperiencia(this);

        atualizarExperienciaUI();

        // ───── BOTÃO SIM ─────

        btnSim.setOnClickListener(v -> {
            temExperiencia = true;
            atualizarExperienciaUI();
        });

        // ───── BOTÃO NÃO ─────

        btnNao.setOnClickListener(v -> {
            temExperiencia = false;
            atualizarExperienciaUI();
        });

        // ───── VOLTAR ─────

        btnVoltar.setOnClickListener(v -> finish());

        // ───── ENVIAR ─────

        btnEnviar.setOnClickListener(v -> {

            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String telefone = etTelefone.getText().toString().trim();
            String pretensao = etPretensao.getText().toString().trim();
            String mensagem = etMensagem.getText().toString().trim();

            // ───── VALIDAÇÃO ─────

            if (TextUtils.isEmpty(nome)) {
                etNome.setError("Digite seu nome");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Digite seu email");
                return;
            }

            if (TextUtils.isEmpty(telefone)) {
                etTelefone.setError("Digite seu telefone");
                return;
            }

            // ───── SALVAR ─────

            AppData.salvarCurriculo(
                    this,
                    nome,
                    email,
                    telefone,
                    pretensao,
                    mensagem,
                    temExperiencia
            );

            Toast.makeText(
                    this,
                    "Candidatura enviada com sucesso! ✓",
                    Toast.LENGTH_LONG
            ).show();

            finish();
        });
    }

    // ───── UI EXPERIÊNCIA ─────

    private void atualizarExperienciaUI() {

        if (temExperiencia) {

            btnSim.setBackgroundResource(R.drawable.bg_button_orange);
            btnNao.setBackgroundResource(R.drawable.bg_chip_inactive);

            txtSim.setTextColor(Color.WHITE);
            txtNao.setTextColor(Color.parseColor("#444444"));

        } else {

            btnNao.setBackgroundResource(R.drawable.bg_button_orange);
            btnSim.setBackgroundResource(R.drawable.bg_chip_inactive);

            txtNao.setTextColor(Color.WHITE);
            txtSim.setTextColor(Color.parseColor("#444444"));
        }
    }
}