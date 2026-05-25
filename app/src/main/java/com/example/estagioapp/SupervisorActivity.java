package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SupervisorActivity extends AppCompatActivity {

    private LinearLayout containerAlunos;
    private TextView tvSemAlunos;
    private String supervisorUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor);

        containerAlunos = findViewById(R.id.container_alunos);
        tvSemAlunos     = findViewById(R.id.tv_sem_alunos);
        supervisorUid   = FirebaseHelper.getUidAtual();

        // Carrega nome do supervisor do Firebase
        FirebaseHelper.refUsuarios().child(supervisorUid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    String nome = snapshot.child("nome").getValue(String.class);
                    if (nome == null) nome = "Supervisor";
                    String inicial = String.valueOf(nome.charAt(0)).toUpperCase();
                    ((TextView) findViewById(R.id.tv_nome_supervisor)).setText(nome);
                    ((TextView) findViewById(R.id.tv_avatar_supervisor)).setText(inicial);
                });

        carregarAlunos();

        // Sair
        findViewById(R.id.btn_sair).setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finishAffinity();
        });
    }

    private void carregarAlunos() {
        // Busca todos os usuários com perfil "aluno" e supervisor = uid atual
        FirebaseHelper.refUsuarios()
                .orderByChild("perfil")
                .equalTo("aluno")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        containerAlunos.removeAllViews();
                        containerAlunos.addView(tvSemAlunos);

                        if (!snapshot.exists()) {
                            tvSemAlunos.setVisibility(View.VISIBLE);
                            return;
                        }

                        int totalAlunos    = 0;
                        int totalPendentes = 0;
                        int totalConcluidos = 0;

                        for (DataSnapshot aluno : snapshot.getChildren()) {
                            String supervisorDoAluno = aluno.child("supervisorUid")
                                    .getValue(String.class);

                            // Mostra apenas alunos vinculados a este supervisor
                            if (!supervisorUid.equals(supervisorDoAluno)) continue;

                            String uid       = aluno.getKey();
                            String nome      = aluno.child("nome").getValue(String.class);
                            String empresa   = aluno.child("empresa").getValue(String.class);
                            String curso     = aluno.child("curso").getValue(String.class);
                            Long horasConc   = aluno.child("horasConcluidas").getValue(Long.class);
                            Long horasTotal  = aluno.child("horasTotal").getValue(Long.class);
                            Long ativPend    = aluno.child("atividadesPendentes").getValue(Long.class);

                            if (nome == null) continue;

                            long hConc  = horasConc  != null ? horasConc  : 0;
                            long hTotal = horasTotal != null ? horasTotal : 600;
                            long pend   = ativPend   != null ? ativPend   : 0;

                            int progresso = hTotal > 0 ? (int) ((hConc * 100) / hTotal) : 0;
                            boolean concluido = progresso >= 100;

                            totalAlunos++;
                            totalPendentes  += pend;
                            if (concluido) totalConcluidos++;

                            adicionarCardAluno(uid, nome, empresa, curso,
                                    hConc, hTotal, progresso, concluido);
                        }

                        if (totalAlunos == 0) {
                            tvSemAlunos.setVisibility(View.VISIBLE);
                        } else {
                            tvSemAlunos.setVisibility(View.GONE);
                        }

                        // Atualiza stats do header
                        ((TextView) findViewById(R.id.tv_alunos_count))
                                .setText(String.valueOf(totalAlunos));
                        ((TextView) findViewById(R.id.tv_pendentes_count))
                                .setText(String.valueOf(totalPendentes));
                        ((TextView) findViewById(R.id.tv_concluidos_count))
                                .setText(String.valueOf(totalConcluidos));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    private void adicionarCardAluno(String uid, String nome, String empresa,
                                    String curso, long hConc, long hTotal,
                                    int progresso, boolean concluido) {
        LinearLayout card = new LinearLayout(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dp(16), dp(6), dp(16), dp(6));
        card.setLayoutParams(cardParams);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.bg_card);
        card.setElevation(dp(2));
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        // Linha topo: avatar + info + badge
        LinearLayout linhaTop = new LinearLayout(this);
        linhaTop.setOrientation(LinearLayout.HORIZONTAL);
        linhaTop.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        linhaTop.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Avatar
        LinearLayout avatar = new LinearLayout(this);
        avatar.setLayoutParams(new LinearLayout.LayoutParams(dp(46), dp(46)));
        avatar.setBackgroundResource(R.drawable.bg_avatar_orange);
        avatar.setGravity(android.view.Gravity.CENTER);

        String[] partes = nome.split(" ");
        String iniciais = partes.length >= 2
                ? String.valueOf(partes[0].charAt(0)) + partes[1].charAt(0)
                : String.valueOf(partes[0].charAt(0));

        TextView tvIniciais = new TextView(this);
        tvIniciais.setText(iniciais.toUpperCase());
        tvIniciais.setTextColor(0xFFFFFFFF);
        tvIniciais.setTextSize(16);
        tvIniciais.setTypeface(null, android.graphics.Typeface.BOLD);
        avatar.addView(tvIniciais);
        linhaTop.addView(avatar);

        // Info nome + empresa
        LinearLayout info = new LinearLayout(this);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        infoParams.leftMargin = dp(12);
        info.setLayoutParams(infoParams);
        info.setOrientation(LinearLayout.VERTICAL);

        TextView tvNome = new TextView(this);
        tvNome.setText(nome);
        tvNome.setTextColor(0xFF1A1A1A);
        tvNome.setTextSize(15);
        tvNome.setTypeface(null, android.graphics.Typeface.BOLD);
        info.addView(tvNome);

        if (empresa != null) {
            TextView tvEmpresa = new TextView(this);
            tvEmpresa.setText(empresa);
            tvEmpresa.setTextColor(0xFF888888);
            tvEmpresa.setTextSize(13);
            LinearLayout.LayoutParams epParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            epParams.topMargin = dp(2);
            tvEmpresa.setLayoutParams(epParams);
            info.addView(tvEmpresa);
        }
        linhaTop.addView(info);

        // Badge status
        TextView tvBadge = new TextView(this);
        tvBadge.setText(concluido ? "Concluído" : "Em curso");
        tvBadge.setTextColor(concluido ? 0xFF888888 : 0xFFFFFFFF);
        tvBadge.setTextSize(11);
        tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBadge.setBackgroundResource(concluido
                ? R.drawable.bg_badge_done : R.drawable.bg_badge_pending);
        tvBadge.setPadding(dp(10), dp(4), dp(10), dp(4));
        tvBadge.setGravity(android.view.Gravity.CENTER);
        linhaTop.addView(tvBadge);

        card.addView(linhaTop);

        // Texto progresso
        TextView tvProgresso = new TextView(this);
        tvProgresso.setText("Progresso: " + hConc + "h de " + hTotal + "h (" + progresso + "%)");
        tvProgresso.setTextColor(0xFF888888);
        tvProgresso.setTextSize(12);
        LinearLayout.LayoutParams progTextoParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        progTextoParams.topMargin = dp(12);
        tvProgresso.setLayoutParams(progTextoParams);
        card.addView(tvProgresso);

        // Barra de progresso
        ProgressBar progressBar = new ProgressBar(this,
                null, android.R.attr.progressBarStyleHorizontal);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(8));
        pbParams.topMargin = dp(6);
        progressBar.setLayoutParams(pbParams);
        progressBar.setMax(100);
        progressBar.setProgress(progresso);
        progressBar.setProgressDrawable(
                getResources().getDrawable(R.drawable.progress_drawable, null));
        card.addView(progressBar);

        // Botões ver atividades + avaliar
        LinearLayout linhaBotoes = new LinearLayout(this);
        LinearLayout.LayoutParams botoesParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        botoesParams.topMargin = dp(12);
        linhaBotoes.setLayoutParams(botoesParams);
        linhaBotoes.setOrientation(LinearLayout.HORIZONTAL);

        TextView btnAtividades = new TextView(this);
        LinearLayout.LayoutParams btnAtivParams = new LinearLayout.LayoutParams(
                0, dp(40), 1);
        btnAtivParams.rightMargin = dp(8);
        btnAtividades.setLayoutParams(btnAtivParams);
        btnAtividades.setBackgroundResource(R.drawable.bg_chip_inactive);
        btnAtividades.setText("Ver atividades");
        btnAtividades.setTextColor(0xFFFF6B00);
        btnAtividades.setTextSize(13);
        btnAtividades.setTypeface(null, android.graphics.Typeface.BOLD);
        btnAtividades.setGravity(android.view.Gravity.CENTER);
        btnAtividades.setOnClickListener(v ->
                Toast.makeText(this, "Atividades de " + nome, Toast.LENGTH_SHORT).show());

        TextView btnAvaliar = new TextView(this);
        btnAvaliar.setLayoutParams(new LinearLayout.LayoutParams(0, dp(40), 1));
        btnAvaliar.setBackgroundResource(R.drawable.bg_button_orange);
        btnAvaliar.setText("Avaliar");
        btnAvaliar.setTextColor(0xFFFFFFFF);
        btnAvaliar.setTextSize(13);
        btnAvaliar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnAvaliar.setGravity(android.view.Gravity.CENTER);
        btnAvaliar.setOnClickListener(v ->
                Toast.makeText(this, "Avaliando " + nome, Toast.LENGTH_SHORT).show());

        linhaBotoes.addView(btnAtividades);
        linhaBotoes.addView(btnAvaliar);
        card.addView(linhaBotoes);

        containerAlunos.addView(card);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(value * density);
    }
}