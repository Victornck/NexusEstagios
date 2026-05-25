package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class EmpresaActivity extends AppCompatActivity {

    private LinearLayout containerVagas;
    private LinearLayout containerCandidatos;
    private TextView tvSemVagas;
    private TextView tvSemCandidatos;
    private String empresaUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        containerVagas      = findViewById(R.id.container_vagas_empresa);
        containerCandidatos = findViewById(R.id.container_candidatos);
        tvSemVagas          = findViewById(R.id.tv_sem_vagas_empresa);
        tvSemCandidatos     = findViewById(R.id.tv_sem_candidatos);

        empresaUid = FirebaseHelper.getUidAtual();

        FirebaseHelper.refUsuarios().child(empresaUid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String razaoSocial = snapshot.child("razaoSocial").getValue(String.class);
                    String nome        = snapshot.child("nome").getValue(String.class);
                    String nomeExibir  = razaoSocial != null ? razaoSocial : nome;
                    if (nomeExibir == null) nomeExibir = "Empresa";

                    String inicial = String.valueOf(nomeExibir.charAt(0)).toUpperCase();
                    ((TextView) findViewById(R.id.tv_nome_empresa)).setText(nomeExibir);
                    ((TextView) findViewById(R.id.tv_avatar_empresa)).setText(inicial);
                });

        findViewById(R.id.btn_nova_vaga).setOnClickListener(v ->
                startActivity(new Intent(this, NovaVagaActivity.class)));

        carregarVagas();
        carregarCandidatos();

        findViewById(R.id.btn_sair_empresa).setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    // ─────────────────────────────────────────
    //  VAGAS
    // ─────────────────────────────────────────

    private void carregarVagas() {
        FirebaseHelper.refVagas()
                .orderByChild("empresaUid")
                .equalTo(empresaUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        containerVagas.removeAllViews();
                        containerVagas.addView(tvSemVagas);

                        if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                            tvSemVagas.setVisibility(View.VISIBLE);
                            return;
                        }

                        tvSemVagas.setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.tv_total_vagas))
                                .setText(String.valueOf(snapshot.getChildrenCount()));

                        for (DataSnapshot vaga : snapshot.getChildren()) {
                            String vagaId     = vaga.getKey();
                            String titulo     = vaga.child("titulo").getValue(String.class);
                            String local      = vaga.child("local").getValue(String.class);
                            String modalidade = vaga.child("modalidade").getValue(String.class);
                            Long candidatos   = vaga.child("candidatos").getValue(Long.class);
                            String status     = vaga.child("status").getValue(String.class);
                            if (titulo == null) continue;

                            adicionarCardVaga(vagaId, titulo, local, modalidade,
                                    candidatos != null ? candidatos : 0, status);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void adicionarCardVaga(String vagaId, String titulo, String local,
                                   String modalidade, long candidatos, String status) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(16), dp(6), dp(16), dp(6));
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setElevation(dp(2));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        LinearLayout linhaTop = new LinearLayout(this);
        linhaTop.setOrientation(LinearLayout.HORIZONTAL);
        linhaTop.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvTitulo = new TextView(this);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        tvTitulo.setLayoutParams(tituloParams);
        tvTitulo.setText(titulo);
        tvTitulo.setTextColor(0xFF1A1A1A);
        tvTitulo.setTextSize(15);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvStatus = new TextView(this);
        tvStatus.setText("ativa".equals(status) ? "Ativa" : "Encerrada");
        tvStatus.setTextColor(0xFFFFFFFF);
        tvStatus.setTextSize(11);
        tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
        tvStatus.setBackgroundResource("ativa".equals(status)
                ? R.drawable.bg_badge_pending : R.drawable.bg_badge_done);
        tvStatus.setPadding(dp(10), dp(4), dp(10), dp(4));

        linhaTop.addView(tvTitulo);
        linhaTop.addView(tvStatus);
        card.addView(linhaTop);

        if (local != null || modalidade != null) {
            TextView tvInfo = new TextView(this);
            String info = "";
            if (local != null)      info += "📍 " + local;
            if (modalidade != null) info += "     🕐 " + modalidade;
            tvInfo.setText(info);
            tvInfo.setTextColor(0xFF888888);
            tvInfo.setTextSize(12);
            LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            infoParams.topMargin = dp(6);
            tvInfo.setLayoutParams(infoParams);
            card.addView(tvInfo);
        }

        LinearLayout linhaBottom = new LinearLayout(this);
        linhaBottom.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        bottomParams.topMargin = dp(12);
        linhaBottom.setLayoutParams(bottomParams);

        TextView tvCandidatos = new TextView(this);
        LinearLayout.LayoutParams candParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        tvCandidatos.setLayoutParams(candParams);
        tvCandidatos.setText(candidatos + " candidato" + (candidatos != 1 ? "s" : ""));
        tvCandidatos.setTextColor(0xFFFF6B00);
        tvCandidatos.setTextSize(13);
        tvCandidatos.setTypeface(null, android.graphics.Typeface.BOLD);

        linhaBottom.addView(tvCandidatos);
        card.addView(linhaBottom);

        containerVagas.addView(card);
    }

    // ─────────────────────────────────────────
    //  CANDIDATOS
    // ─────────────────────────────────────────

    private void carregarCandidatos() {
        FirebaseHelper.refCandidaturas()
                .orderByChild("empresaUid")
                .equalTo(empresaUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        containerCandidatos.removeAllViews();
                        containerCandidatos.addView(tvSemCandidatos);

                        if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                            tvSemCandidatos.setVisibility(View.VISIBLE);
                            ((TextView) findViewById(R.id.tv_total_candidatos)).setText("0");
                            ((TextView) findViewById(R.id.tv_total_contratados)).setText("0");
                            return;
                        }

                        int total  = 0;
                        int aceitos = 0;

                        for (DataSnapshot cand : snapshot.getChildren()) {
                            String candId    = cand.getKey();
                            String nome      = cand.child("nome").getValue(String.class);
                            String nomeVaga  = cand.child("nomeVaga").getValue(String.class);
                            String pretensao = cand.child("pretensao").getValue(String.class);
                            String status    = cand.child("status").getValue(String.class);
                            String email     = cand.child("email").getValue(String.class);
                            String telefone  = cand.child("telefone").getValue(String.class);
                            String mensagem  = cand.child("mensagem").getValue(String.class);
                            Boolean expBool  = cand.child("experiencia").getValue(Boolean.class);
                            boolean experiencia = expBool != null && expBool;

                            if (nome == null) continue;

                            total++;
                            if ("aceito".equals(status)) aceitos++;

                            adicionarCardCandidato(candId, nome, nomeVaga, pretensao,
                                    status, email, telefone, mensagem, experiencia);
                        }

                        tvSemCandidatos.setVisibility(total == 0 ? View.VISIBLE : View.GONE);
                        ((TextView) findViewById(R.id.tv_total_candidatos))
                                .setText(String.valueOf(total));
                        ((TextView) findViewById(R.id.tv_total_contratados))
                                .setText(String.valueOf(aceitos));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        tvSemCandidatos.setVisibility(View.VISIBLE);
                        tvSemCandidatos.setText("Erro ao carregar candidatos.");
                    }
                });
    }

    private void adicionarCardCandidato(String candId, String nome,
                                        String nomeVaga, String pretensao, String status,
                                        String email, String telefone,
                                        String mensagem, boolean experiencia) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(dp(16), dp(6), dp(16), dp(6));
        card.setLayoutParams(params);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setElevation(dp(2));
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // ── Avatar ──
        LinearLayout avatar = new LinearLayout(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(44), dp(44)));
        avatar.setBackgroundResource(R.drawable.bg_avatar_orange);
        avatar.setGravity(android.view.Gravity.CENTER);

        TextView tvIniciais = new TextView(this);
        String[] partes = nome.split(" ");
        String iniciais = partes.length >= 2
                ? String.valueOf(partes[0].charAt(0)) + partes[1].charAt(0)
                : String.valueOf(partes[0].charAt(0));
        tvIniciais.setText(iniciais.toUpperCase());
        tvIniciais.setTextColor(0xFFFFFFFF);
        tvIniciais.setTextSize(15);
        tvIniciais.setTypeface(null, android.graphics.Typeface.BOLD);
        avatar.addView(tvIniciais);
        card.addView(avatar);

        // ── Info central ──
        LinearLayout info = new LinearLayout(this);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        infoParams.leftMargin = dp(12);
        info.setLayoutParams(infoParams);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView tvNome = new TextView(this);
        tvNome.setText(nome);
        tvNome.setTextColor(0xFF1A1A1A);
        tvNome.setTextSize(14);
        tvNome.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvNome);

        TextView tvVaga = new TextView(this);
        String infoTexto = nomeVaga != null ? nomeVaga : "";
        if (pretensao != null && !pretensao.isEmpty()) infoTexto += " · R$ " + pretensao;
        tvVaga.setText(infoTexto);
        tvVaga.setTextColor(0xFF888888);
        tvVaga.setTextSize(12);
        LinearLayout.LayoutParams vagaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        vagaParams.topMargin = dp(2);
        tvVaga.setLayoutParams(vagaParams);
        info.addView(tvVaga);

        // ── Botão Ver detalhes ──
        TextView btnDetalhes = new TextView(this);
        LinearLayout.LayoutParams detParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        detParams.topMargin = dp(8);
        btnDetalhes.setLayoutParams(detParams);
        btnDetalhes.setText("Ver detalhes");
        btnDetalhes.setTextColor(0xFFFF6B00);
        btnDetalhes.setTextSize(12);
        btnDetalhes.setTypeface(null, android.graphics.Typeface.BOLD);
        btnDetalhes.setBackgroundResource(R.drawable.bg_chip_inactive);
        btnDetalhes.setPadding(dp(12), dp(5), dp(12), dp(5));

        final String emailF     = email;
        final String telefoneF  = telefone;
        final String mensagemF  = mensagem;
        final boolean experF    = experiencia;
        final String nomeF      = nome;
        final String nomeVagaF  = nomeVaga;

        btnDetalhes.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            sb.append("👤 ").append(nomeF).append("\n");
            sb.append("💼 ").append(nomeVagaF != null ? nomeVagaF : "—").append("\n\n");
            sb.append("📧 Email: ").append(emailF != null ? emailF : "—").append("\n");
            sb.append("📞 Telefone: ").append(telefoneF != null ? telefoneF : "—").append("\n");
            sb.append("💡 Experiência: ").append(experF ? "Sim" : "Não").append("\n\n");
            sb.append("💬 Mensagem:\n")
                    .append(mensagemF != null && !mensagemF.isEmpty() ? mensagemF : "Nenhuma mensagem.");

            new AlertDialog.Builder(EmpresaActivity.this)
                    .setTitle("Detalhes do candidato")
                    .setMessage(sb.toString())
                    .setPositiveButton("Fechar", null)
                    .show();
        });

        info.addView(btnDetalhes);
        card.addView(info);

        // ── Botões Aceitar / Recusar ──
        LinearLayout btnContainer = new LinearLayout(this);
        btnContainer.setOrientation(LinearLayout.VERTICAL);
        btnContainer.setGravity(android.view.Gravity.CENTER);

        if ("aceito".equals(status)) {
            TextView tvAceito = new TextView(this);
            tvAceito.setText("Aceito ✓");
            tvAceito.setTextColor(0xFF888888);
            tvAceito.setTextSize(12);
            tvAceito.setBackgroundResource(R.drawable.bg_badge_done);
            tvAceito.setPadding(dp(10), dp(6), dp(10), dp(6));
            tvAceito.setGravity(android.view.Gravity.CENTER);
            btnContainer.addView(tvAceito);

        } else if ("recusado".equals(status)) {
            TextView tvRecusado = new TextView(this);
            tvRecusado.setText("Recusado");
            tvRecusado.setTextColor(0xFF888888);
            tvRecusado.setTextSize(12);
            tvRecusado.setBackgroundResource(R.drawable.bg_badge_done);
            tvRecusado.setPadding(dp(10), dp(6), dp(10), dp(6));
            tvRecusado.setGravity(android.view.Gravity.CENTER);
            btnContainer.addView(tvRecusado);

        } else {
            TextView btnAceitar = new TextView(this);
            LinearLayout.LayoutParams aceitarParams = new LinearLayout.LayoutParams(dp(80), dp(32));
            btnAceitar.setLayoutParams(aceitarParams);
            btnAceitar.setBackgroundResource(R.drawable.bg_button_orange);
            btnAceitar.setText("Aceitar");
            btnAceitar.setTextColor(0xFFFFFFFF);
            btnAceitar.setTextSize(12);
            btnAceitar.setTypeface(null, android.graphics.Typeface.BOLD);
            btnAceitar.setGravity(android.view.Gravity.CENTER);
            btnAceitar.setOnClickListener(v -> atualizarStatusCandidatura(candId, true, card));

            TextView btnRecusar = new TextView(this);
            LinearLayout.LayoutParams recusarParams = new LinearLayout.LayoutParams(dp(80), dp(32));
            recusarParams.topMargin = dp(6);
            btnRecusar.setLayoutParams(recusarParams);
            btnRecusar.setBackgroundResource(R.drawable.bg_chip_inactive);
            btnRecusar.setText("Recusar");
            btnRecusar.setTextColor(0xFF888888);
            btnRecusar.setTextSize(12);
            btnRecusar.setGravity(android.view.Gravity.CENTER);
            btnRecusar.setOnClickListener(v -> atualizarStatusCandidatura(candId, false, card));

            btnContainer.addView(btnAceitar);
            btnContainer.addView(btnRecusar);
        }

        card.addView(btnContainer);
        containerCandidatos.addView(card);
    }

    // ─────────────────────────────────────────
    //  UTILITÁRIOS
    // ─────────────────────────────────────────

    private void atualizarStatusCandidatura(String candId, boolean aceito, LinearLayout card) {
        String novoStatus = aceito ? "aceito" : "recusado";
        FirebaseHelper.refCandidaturas().child(candId).child("status")
                .setValue(novoStatus)
                .addOnSuccessListener(unused -> {
                    String msg = aceito ? "Candidatura aceita!" : "Candidatura recusada.";
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao atualizar status.", Toast.LENGTH_SHORT).show());
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}