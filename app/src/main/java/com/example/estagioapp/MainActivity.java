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

    private TextView tvNomeUsuario, tvLetraUsuario, tvProgresso, tvHoras;
    private TextView tvStatVagas, tvStatCurriculos, tvStatAprovadas;
    private ProgressBar progressBar;
    private EditText etBusca;
    private RecyclerView rvVagasRecomendadas;

    // Lista completa e lista filtrada para a busca
    private final List<Vaga> todasVagas = new ArrayList<>();
    private final List<Vaga> vagasFiltradas = new ArrayList<>();
    private VagaRecomendadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FirebaseHelper.estaLogado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Views existentes
        tvNomeUsuario = findViewById(R.id.tv_nome_usuario);
        tvLetraUsuario = findViewById(R.id.tv_letra_usuario);
        tvProgresso = findViewById(R.id.tv_progresso_percent);
        tvHoras = findViewById(R.id.tv_horas_progresso);
        progressBar = findViewById(R.id.progress_bar);

        // Views novas (stats)
        tvStatVagas = findViewById(R.id.tv_stat_vagas);
        tvStatCurriculos = findViewById(R.id.tv_stat_curriculos);
        tvStatAprovadas = findViewById(R.id.tv_stat_aprovadas);

        // Busca
        etBusca = findViewById(R.id.et_busca);

        // RecyclerView vagas recomendadas
        rvVagasRecomendadas = findViewById(R.id.rv_vagas_recomendadas);
        rvVagasRecomendadas.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new VagaRecomendadaAdapter(vagasFiltradas, this);
        rvVagasRecomendadas.setAdapter(adapter);

        // Navegação (igual ao original)
        tvLetraUsuario.setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));
        findViewById(R.id.nav_inicio).setOnClickListener(v -> {});
        findViewById(R.id.nav_vagas).setOnClickListener(v ->
                NavHelper.navigate(this, VagasActivity.class));
        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
        findViewById(R.id.nav_perfil).setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));

        // Busca dinâmica enquanto digita
        etBusca.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarVagas(s.toString().trim());
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
        }
    }

    // ─── MÉTODO ORIGINAL PRESERVADO ──────────────────────────────────────────
    private void carregarUsuario() {
        String uid = FirebaseHelper.getUidAtual();
        if (uid == null) return;

        FirebaseHelper.refUsuarios().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String nome = snapshot.child("nome").getValue(String.class);
                        if (nome == null) nome = "Usuário";

                        tvNomeUsuario.setText(nome);
                        tvLetraUsuario.setText(
                                String.valueOf(nome.charAt(0)).toUpperCase());

                        AppData.setNome(MainActivity.this, nome);

                        String curso = snapshot.child("curso").getValue(String.class);
                        if (curso != null) AppData.setCurso(MainActivity.this, curso);

                        String empresa = snapshot.child("empresa").getValue(String.class);
                        if (empresa != null) AppData.setEmpresa(MainActivity.this, empresa);

                        String supervisor = snapshot.child("supervisorUid").getValue(String.class);
                        if (supervisor != null)
                            AppData.setSupervisor(MainActivity.this, supervisor);

                        Long horasConc = snapshot.child("horasConcluidas").getValue(Long.class);
                        Long horasTotal = snapshot.child("horasTotal").getValue(Long.class);

                        long hConc = horasConc != null ? horasConc : 0;
                        long hTotal = horasTotal != null ? horasTotal : 600;
                        int perc = hTotal > 0 ? (int) ((hConc * 100) / hTotal) : 0;

                        if (tvProgresso != null) tvProgresso.setText(perc + "%");
                        if (tvHoras != null)
                            tvHoras.setText(hConc + "h de " + hTotal + "h concluídas");
                        if (progressBar != null) progressBar.setProgress(perc);

                        AppData.setHorasConcluidas(MainActivity.this, String.valueOf(hConc));
                        AppData.setHorasTotal(MainActivity.this, String.valueOf(hTotal));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }

    // ─── NOVO: Stats dinâmicos ────────────────────────────────────────────────
    private void carregarStats() {
        String uid = FirebaseHelper.getUidAtual();
        if (uid == null) return;

        // Contar total de vagas
        FirebaseHelper.refVagas()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (tvStatVagas != null)
                            tvStatVagas.setText(String.valueOf(snapshot.getChildrenCount()));
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });

        // Contar currículos enviados pelo usuário atual
        FirebaseHelper.refCandidaturas()
                .orderByChild("candidatoUid").equalTo(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        long total = snapshot.getChildrenCount();
                        long aprovadas = 0;
                        for (DataSnapshot c : snapshot.getChildren()) {
                            String status = c.child("status").getValue(String.class);
                            if ("aprovado".equalsIgnoreCase(status)) aprovadas++;
                        }
                        if (tvStatCurriculos != null)
                            tvStatCurriculos.setText(String.valueOf(total));
                        if (tvStatAprovadas != null)
                            tvStatAprovadas.setText(String.valueOf(aprovadas));
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    // ─── NOVO: Vagas recomendadas reais ──────────────────────────────────────
    private void carregarVagasRecomendadas() {
        FirebaseHelper.refVagas()
                .limitToLast(5) // pega as 5 mais recentes
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        todasVagas.clear();
                        for (DataSnapshot vSnap : snapshot.getChildren()) {
                            Vaga v = vSnap.getValue(Vaga.class);
                            if (v != null) {
                                v.setId(vSnap.getKey());
                                todasVagas.add(v);
                            }
                        }
                        // Mostra tudo inicialmente
                        filtrarVagas(etBusca.getText().toString().trim());
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });
    }

    // ─── NOVO: Filtro de busca ────────────────────────────────────────────────
    private void filtrarVagas(String query) {
        vagasFiltradas.clear();
        if (query.isEmpty()) {
            vagasFiltradas.addAll(todasVagas);
        } else {
            String lower = query.toLowerCase();
            for (Vaga v : todasVagas) {
                boolean titulo  = v.getTitulo()  != null && v.getTitulo().toLowerCase().contains(lower);
                boolean empresa = v.getEmpresa() != null && v.getEmpresa().toLowerCase().contains(lower);
                boolean local   = v.getLocal()   != null && v.getLocal().toLowerCase().contains(lower);
                if (titulo || empresa || local) vagasFiltradas.add(v);
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}