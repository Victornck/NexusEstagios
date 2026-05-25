package com.example.estagioapp;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class SupervisorAlunoAtividadesActivity
        extends AppCompatActivity {

    private LinearLayout containerAtividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_supervisor_aluno_atividades
        );

        // IDS

        containerAtividades =
                findViewById(R.id.container_atividades);

        // BOTÃO VOLTAR

        findViewById(R.id.btn_voltar)
                .setOnClickListener(v -> finish());

        // DADOS RECEBIDOS

        String alunoUid =
                getIntent().getStringExtra("alunoUid");

        // VOLTAR NATIVO

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        finish();
                    }
                });

        carregarAtividades(alunoUid);
    }

    private void carregarAtividades(String alunoUid) {

        FirebaseHelper.getDatabase()
                .child("atividades")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                containerAtividades.removeAllViews();

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    Atividade atividade =
                                            ds.getValue(
                                                    Atividade.class
                                            );

                                    if (atividade == null) continue;

                                    atividade.setId(
                                            ds.getKey()
                                    );

                                    if (!alunoUid.equals(
                                            atividade.getAlunoUid()
                                    )) {
                                        continue;
                                    }

                                    adicionarCard(atividade);
                                }
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {

                            }
                        });
    }

    private void adicionarCard(Atividade atividade) {

        LinearLayout card =
                new LinearLayout(this);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(32, 12, 32, 0);

        card.setLayoutParams(params);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setPadding(32, 28, 32, 28);

        card.setBackgroundResource(
                R.drawable.bg_card
        );

        // TÍTULO

        TextView titulo =
                new TextView(this);

        titulo.setText(
                atividade.getTitulo()
        );

        titulo.setTextSize(16);

        titulo.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        titulo.setTextColor(0xFF1A1A1A);

        card.addView(titulo);

        // DESCRIÇÃO

        TextView descricao =
                new TextView(this);

        descricao.setText(
                atividade.getDescricao()
        );

        descricao.setTextSize(13);

        descricao.setTextColor(0xFF777777);

        descricao.setPadding(0, 12, 0, 0);

        card.addView(descricao);

        // DATA

        TextView data =
                new TextView(this);

        data.setText(
                "Data: " + atividade.getData()
        );

        data.setTextSize(12);

        data.setTextColor(0xFF999999);

        data.setPadding(0, 12, 0, 0);

        card.addView(data);

        // STATUS

        TextView status =
                new TextView(this);

        status.setPadding(0, 18, 0, 0);

        status.setText(
                atividade.isConcluida()
                        ? "Concluída"
                        : "Pendente"
        );

        status.setTextColor(
                atividade.isConcluida()
                        ? 0xFF2E7D32
                        : 0xFFFF6B00
        );

        status.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        card.addView(status);

        // BOTÃO CONCLUIR

        TextView btnConcluir =
                new TextView(this);

        btnConcluir.setText(
                atividade.isConcluida()
                        ? "Atividade concluída"
                        : "Marcar como concluída"
        );

        btnConcluir.setTextColor(
                0xFFFFFFFF
        );

        btnConcluir.setTextSize(13);

        btnConcluir.setTypeface(
                null,
                android.graphics.Typeface.BOLD
        );

        btnConcluir.setGravity(
                Gravity.CENTER
        );

        btnConcluir.setPadding(
                0,
                22,
                0,
                22
        );

        LinearLayout.LayoutParams btnParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        btnParams.topMargin = 24;

        btnConcluir.setLayoutParams(
                btnParams
        );

        btnConcluir.setBackgroundResource(
                atividade.isConcluida()
                        ? R.drawable.bg_badge_done
                        : R.drawable.bg_button_orange
        );

        if (!atividade.isConcluida()) {

            btnConcluir.setOnClickListener(v -> {

                FirebaseHelper.getDatabase()
                        .child("atividades")
                        .child(atividade.getId())
                        .child("concluida")
                        .setValue(true);
            });
        }

        card.addView(btnConcluir);

        containerAtividades.addView(card);
    }
}