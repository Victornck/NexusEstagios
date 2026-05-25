package com.example.estagioapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.database.DataSnapshot;

public class CurriculoActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etTelefone, etPretensao, etMensagem;
    private LinearLayout btnSim, btnNao;
    private TextView txtSim, txtNao, btnEnviar;
    private boolean temExperiencia = false;
    private String vagaId, nomeVaga, empresaVaga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_curriculo);

        // Recebe dados da vaga
        vagaId     = getIntent().getStringExtra("vaga_id");
        nomeVaga   = getIntent().getStringExtra("nome_vaga");
        empresaVaga = getIntent().getStringExtra("empresa_vaga");

        // Preenche header da vaga
        ((TextView) findViewById(R.id.tv_nome_vaga)).setText(nomeVaga);
        ((TextView) findViewById(R.id.tv_empresa_vaga)).setText(empresaVaga);

        // IDs
        etNome      = findViewById(R.id.et_nome);
        etEmail     = findViewById(R.id.et_email);
        etTelefone  = findViewById(R.id.et_telefone);
        etPretensao = findViewById(R.id.et_pretensao);
        etMensagem  = findViewById(R.id.et_mensagem);
        btnSim      = findViewById(R.id.btn_sim);
        btnNao      = findViewById(R.id.btn_nao);
        txtSim      = findViewById(R.id.txt_sim);
        txtNao      = findViewById(R.id.txt_nao);
        btnEnviar   = findViewById(R.id.btn_enviar);

        // Preenche dados salvos localmente
        etNome.setText(AppData.getNome(this));
        etEmail.setText(AppData.getCurriculoEmail(this));
        etTelefone.setText(AppData.getCurriculoTelefone(this));
        etPretensao.setText(AppData.getCurriculoPretensao(this));
        etMensagem.setText(AppData.getCurriculoMensagem(this));
        temExperiencia = AppData.getCurriculoExperiencia(this);
        atualizarExperienciaUI();

        btnSim.setOnClickListener(v -> { temExperiencia = true;  atualizarExperienciaUI(); });
        btnNao.setOnClickListener(v -> { temExperiencia = false; atualizarExperienciaUI(); });

        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());

        btnEnviar.setOnClickListener(v -> {

            String nome      = etNome.getText().toString().trim();
            String email     = etEmail.getText().toString().trim();
            String telefone  = etTelefone.getText().toString().trim();
            String pretensao = etPretensao.getText().toString().trim();
            String mensagem  = etMensagem.getText().toString().trim();

            if (TextUtils.isEmpty(nome))     { etNome.setError("Obrigatório"); etNome.requestFocus(); return; }
            if (TextUtils.isEmpty(email))    { etEmail.setError("Obrigatório"); etEmail.requestFocus(); return; }
            if (TextUtils.isEmpty(telefone)) { etTelefone.setError("Obrigatório"); etTelefone.requestFocus(); return; }

            // Verifica se já candidatou para essa vaga
            String uid = FirebaseHelper.getUidAtual();
            if (uid == null) {
                Toast.makeText(this, "Faça login para candidatar-se.", Toast.LENGTH_SHORT).show();
                return;
            }

            btnEnviar.setEnabled(false);
            btnEnviar.setText("Enviando...");

            // Verifica candidatura duplicada
            FirebaseHelper.refCandidaturas()
                    .orderByChild("alunoUid")
                    .equalTo(uid)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            String vidSnap = snap.child("vagaId").getValue(String.class);
                            if (vagaId != null && vagaId.equals(vidSnap)) {
                                btnEnviar.setEnabled(true);
                                btnEnviar.setText("Enviar candidatura");
                                Toast.makeText(this,
                                        "Você já se candidatou para esta vaga.",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        enviarCandidatura(uid, nome, email, telefone, pretensao, mensagem);
                    })
                    .addOnFailureListener(e ->
                            enviarCandidatura(uid, nome, email, telefone, pretensao, mensagem));
        });
    }

    private void enviarCandidatura(String uid, String nome, String email,
                                   String telefone, String pretensao, String mensagem) {
        Map<String, Object> candidatura = new HashMap<>();
        candidatura.put("alunoUid", uid);
        candidatura.put("vagaId", vagaId);
        candidatura.put("nomeVaga", nomeVaga);
        candidatura.put("empresaVaga", empresaVaga);
        candidatura.put("nome", nome);
        candidatura.put("email", email);
        candidatura.put("telefone", telefone);
        candidatura.put("pretensao", pretensao);
        candidatura.put("mensagem", mensagem);
        candidatura.put("experiencia", temExperiencia);
        candidatura.put("status", "pendente");
        candidatura.put("timestamp", System.currentTimeMillis());

        FirebaseHelper.refCandidaturas().push()
                .setValue(candidatura)
                .addOnSuccessListener(unused -> {
                    // Salva dados localmente para próxima candidatura
                    AppData.salvarCurriculo(this, nome, email, telefone,
                            pretensao, mensagem, temExperiencia);

                    // Incrementa contador de candidatos na vaga
                    if (vagaId != null) {
                        FirebaseHelper.refVagas().child(vagaId).child("candidatos")
                                .get()
                                .addOnSuccessListener(snap -> {
                                    Long atual = snap.getValue(Long.class);
                                    long novo = (atual != null ? atual : 0) + 1;
                                    FirebaseHelper.refVagas()
                                            .child(vagaId)
                                            .child("candidatos")
                                            .setValue(novo);
                                });
                    }

                    Toast.makeText(this,
                            "Candidatura enviada com sucesso! ✓",
                            Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnEnviar.setEnabled(true);
                    btnEnviar.setText("Enviar candidatura");
                    Toast.makeText(this,
                            "Erro ao enviar candidatura: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

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