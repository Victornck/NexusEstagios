package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvNomeUsuario;
    private TextView tvLetraUsuario;
    private TextView tvProgresso;
    private TextView tvHoras;

    private TextView tvStatVagas;
    private TextView tvStatCurriculos;
    private TextView tvStatAprovadas;

    private ProgressBar progressBar;

    private EditText etBusca;

    private RecyclerView rvVagasRecomendadas;

    private final List<Vaga> todasVagas =
            new ArrayList<>();

    private final List<Vaga> vagasFiltradas =
            new ArrayList<>();

    private VagaRecomendadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FirebaseHelper.estaLogado()) {

            startActivity(
                    new Intent(
                            this,
                            LoginActivity.class
                    )
            );

            finish();

            return;
        }

        setContentView(R.layout.activity_main);

        iniciarViews();

        configurarRecycler();

        configurarNavegacao();

        configurarBusca();
    }

    private void iniciarViews() {

        tvNomeUsuario =
                findViewById(R.id.tv_nome_usuario);

        tvLetraUsuario =
                findViewById(R.id.tv_letra_usuario);

        tvProgresso =
                findViewById(R.id.tv_progresso_percent);

        tvHoras =
                findViewById(R.id.tv_horas_progresso);

        progressBar =
                findViewById(R.id.progress_bar);

        tvStatVagas =
                findViewById(R.id.tv_stat_vagas);

        tvStatCurriculos =
                findViewById(R.id.tv_stat_curriculos);

        tvStatAprovadas =
                findViewById(R.id.tv_stat_aprovadas);

        etBusca =
                findViewById(R.id.et_busca);

        rvVagasRecomendadas =
                findViewById(R.id.rv_vagas_recomendadas);
    }

    private void configurarRecycler() {

        rvVagasRecomendadas.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );

        adapter =
                new VagaRecomendadaAdapter(
                        vagasFiltradas,
                        this
                );

        rvVagasRecomendadas.setAdapter(
                adapter
        );
    }

    private void configurarNavegacao() {

        tvLetraUsuario.setOnClickListener(v ->

                NavHelper.navigate(
                        this,
                        PerfilActivity.class
                )
        );

        findViewById(R.id.nav_inicio)
                .setOnClickListener(v -> {
                });

        findViewById(R.id.nav_vagas)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                VagasActivity.class
                        )
                );

        findViewById(R.id.nav_atividades)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                AtividadesActivity.class
                        )
                );

        findViewById(R.id.nav_perfil)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                PerfilActivity.class
                        )
                );
    }

    private void configurarBusca() {

        etBusca.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after
                    ) {
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s
                    ) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count
                    ) {

                        filtrarVagas(
                                s.toString().trim()
                        );
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FirebaseHelper.estaLogado()) {

            carregarUsuario();

            carregarStats();

            carregarVagasRecomendadas();

            carregarHorasAtividades();
        }
    }

    private void carregarUsuario() {

        String uid =
                FirebaseHelper.getUidAtual();

        if (uid == null) return;

        FirebaseHelper.refUsuarios()
                .child(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot
                            ) {

                                String nome =
                                        snapshot.child("nome")
                                                .getValue(String.class);

                                if (nome == null
                                        || nome.isEmpty()) {

                                    nome = "Usuário";
                                }

                                tvNomeUsuario.setText(
                                        nome
                                );

                                tvLetraUsuario.setText(
                                        String.valueOf(
                                                nome.charAt(0)
                                        ).toUpperCase()
                                );

                                AppData.setNome(
                                        MainActivity.this,
                                        nome
                                );
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error
                            ) {
                            }
                        });
    }

    private void carregarHorasAtividades() {

        String uidAtual =
                FirebaseHelper.getUidAtual();

        if (uidAtual == null) return;

        FirebaseHelper.refUsuarios()
                .child(uidAtual)
                .get()
                .addOnSuccessListener(userSnapshot -> {

                    final int horasExtras;

                    Long extras =
                            userSnapshot
                                    .child("horasExtras")
                                    .getValue(Long.class);

                    if (extras != null) {

                        horasExtras =
                                extras.intValue();

                    } else {

                        horasExtras = 0;
                    }

                    final int cargaHoraria;

                    Long cargaFirebase =
                            userSnapshot
                                    .child("horasTotal")
                                    .getValue(Long.class);

                    if (cargaFirebase != null) {

                        cargaHoraria =
                                cargaFirebase.intValue();

                    } else {

                        cargaHoraria = 600;
                    }

                    FirebaseHelper.refAtividades()
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                int horasAtividades = 0;

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    Atividade atividade =
                                            ds.getValue(
                                                    Atividade.class
                                            );

                                    if (atividade == null)
                                        continue;

                                    if (!uidAtual.equals(
                                            atividade.getAlunoUid()
                                    )) {
                                        continue;
                                    }

                                    if (atividade.isConcluida()) {

                                        horasAtividades +=
                                                atividade.getHoras();
                                    }
                                }

                                int horasTotais =
                                        horasExtras
                                                + horasAtividades;

                                if (horasTotais > cargaHoraria) {

                                    horasTotais =
                                            cargaHoraria;
                                }

                                int progresso = 0;

                                if (cargaHoraria > 0) {

                                    progresso =
                                            (horasTotais * 100)
                                                    / cargaHoraria;
                                }

                                tvProgresso.setText(
                                        progresso + "%"
                                );

                                tvHoras.setText(
                                        horasTotais
                                                + "h de "
                                                + cargaHoraria
                                                + "h concluídas"
                                );

                                progressBar.setProgress(
                                        progresso
                                );

                                AppData.setHorasConcluidas(
                                        MainActivity.this,
                                        String.valueOf(
                                                horasTotais
                                        )
                                );

                                AppData.setHorasTotal(
                                        MainActivity.this,
                                        String.valueOf(
                                                cargaHoraria
                                        )
                                );
                            });
                });
    }

    private void carregarStats() {

        String uid =
                FirebaseHelper.getUidAtual();

        if (uid == null) return;

        FirebaseHelper.refVagas()
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot
                            ) {

                                if (tvStatVagas != null) {

                                    tvStatVagas.setText(
                                            String.valueOf(
                                                    snapshot.getChildrenCount()
                                            )
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error
                            ) {
                            }
                        });

        FirebaseHelper.refCandidaturas()
                .orderByChild("candidatoUid")
                .equalTo(uid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot
                            ) {

                                long total =
                                        snapshot.getChildrenCount();

                                long aprovadas = 0;

                                for (DataSnapshot c :
                                        snapshot.getChildren()) {

                                    String status =
                                            c.child("status")
                                                    .getValue(String.class);

                                    if ("aprovado"
                                            .equalsIgnoreCase(status)) {

                                        aprovadas++;
                                    }
                                }

                                tvStatCurriculos.setText(
                                        String.valueOf(total)
                                );

                                tvStatAprovadas.setText(
                                        String.valueOf(aprovadas)
                                );
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error
                            ) {
                            }
                        });
    }

    private void carregarVagasRecomendadas() {

        FirebaseHelper.refVagas()
                .limitToLast(5)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    DataSnapshot snapshot
                            ) {

                                todasVagas.clear();

                                for (DataSnapshot vSnap :
                                        snapshot.getChildren()) {

                                    Vaga vaga =
                                            vSnap.getValue(
                                                    Vaga.class
                                            );

                                    if (vaga != null) {

                                        vaga.setId(
                                                vSnap.getKey()
                                        );

                                        todasVagas.add(vaga);
                                    }
                                }

                                filtrarVagas(
                                        etBusca.getText()
                                                .toString()
                                                .trim()
                                );
                            }

                            @Override
                            public void onCancelled(
                                    DatabaseError error
                            ) {
                            }
                        });
    }

    private void filtrarVagas(String query) {

        vagasFiltradas.clear();

        if (query.isEmpty()) {

            vagasFiltradas.addAll(
                    todasVagas
            );

        } else {

            String lower =
                    query.toLowerCase();

            for (Vaga vaga : todasVagas) {

                boolean titulo =
                        vaga.getTitulo() != null
                                && vaga.getTitulo()
                                .toLowerCase()
                                .contains(lower);

                boolean empresa =
                        vaga.getEmpresa() != null
                                && vaga.getEmpresa()
                                .toLowerCase()
                                .contains(lower);

                boolean local =
                        vaga.getLocal() != null
                                && vaga.getLocal()
                                .toLowerCase()
                                .contains(lower);

                if (titulo
                        || empresa
                        || local) {

                    vagasFiltradas.add(vaga);
                }
            }
        }

        if (adapter != null) {

            adapter.notifyDataSetChanged();
        }
    }
}