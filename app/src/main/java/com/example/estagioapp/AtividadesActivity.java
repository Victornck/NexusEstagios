package com.example.estagioapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AtividadesActivity extends AppCompatActivity {

    private RecyclerView recyclerAtividades;

    private TextView tvSupervisor;
    private TextView tvConcluidas;
    private TextView tvPendentes;
    private TextView tvHoras;

    private final List<Atividade> listaAtividades =
            new ArrayList<>();

    private AtividadesAdapter adapter;

    private DatabaseReference atividadesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividades);

        iniciarViews();

        configurarFirebase();

        configurarRecycler();

        carregarAtividades();

        configurarBotaoNovaAtividade();

        configurarNavegacao();

        atualizarCabecalho();
    }

    private void iniciarViews() {

        recyclerAtividades =
                findViewById(R.id.recycler_atividades);

        tvSupervisor =
                findViewById(R.id.tv_supervisor);

        tvConcluidas =
                findViewById(R.id.tv_concluidas);

        tvPendentes =
                findViewById(R.id.tv_pendentes);

        tvHoras =
                findViewById(R.id.tv_horas);
    }

    private void configurarFirebase() {

        atividadesRef =
                FirebaseHelper
                        .getDatabase()
                        .child("atividades");
    }

    private void configurarRecycler() {

        recyclerAtividades.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AtividadesAdapter(
                listaAtividades,
                atividadesRef
        );

        recyclerAtividades.setAdapter(adapter);
    }

    private void carregarAtividades() {

        String uidAtual =
                FirebaseHelper.getUidAtual();

        if (uidAtual == null) return;

        atividadesRef.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot
                    ) {

                        listaAtividades.clear();

                        int concluidas = 0;
                        int pendentes = 0;
                        int horasTotal = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Atividade atividade =
                                    ds.getValue(Atividade.class);

                            if (atividade == null) continue;

                            atividade.setId(ds.getKey());

                            // MOSTRA SOMENTE AS DO ALUNO

                            if (!uidAtual.equals(
                                    atividade.getAlunoUid()
                            )) {
                                continue;
                            }

                            listaAtividades.add(atividade);

                            if (atividade.isConcluida()) {

                                concluidas++;

                                horasTotal +=
                                        atividade.getHoras();

                            } else {

                                pendentes++;
                            }
                        }

                        adapter.notifyDataSetChanged();

                        atualizarContadores(
                                concluidas,
                                pendentes,
                                horasTotal
                        );
                    }

                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error
                    ) {

                        Toast.makeText(
                                AtividadesActivity.this,
                                "Erro ao carregar atividades",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void atualizarCabecalho() {

        String supervisor =
                AppData.getSupervisor(this);

        tvSupervisor.setText(
                "Acompanhado por " + supervisor
        );
    }

    private void atualizarContadores(
            int concluidas,
            int pendentes,
            int horas
    ) {

        tvConcluidas.setText(
                String.valueOf(concluidas)
        );

        tvPendentes.setText(
                String.valueOf(pendentes)
        );

        tvHoras.setText(
                horas + "h"
        );

        // SALVA LOCALMENTE

        AppData.setAtividadesConcluidas(
                this,
                concluidas
        );

        AppData.setAtividadesPendentes(
                this,
                pendentes
        );

        AppData.setHorasConcluidas(
                this,
                String.valueOf(horas)
        );
    }

    private void configurarBotaoNovaAtividade() {

        findViewById(R.id.btn_nova_atividade)
                .setOnClickListener(v -> {

                    abrirDialogNovaAtividade();
                });
    }

    private void abrirDialogNovaAtividade() {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setTitle("Nova atividade");

        EditText inputTitulo =
                new EditText(this);

        inputTitulo.setHint(
                "Título da atividade"
        );

        inputTitulo.setInputType(
                InputType.TYPE_CLASS_TEXT
        );

        builder.setView(inputTitulo);

        builder.setPositiveButton(
                "Criar",
                (dialog, which) -> {

                    String titulo =
                            inputTitulo
                                    .getText()
                                    .toString()
                                    .trim();

                    if (titulo.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Digite um título",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    salvarNovaAtividade(titulo);
                });

        builder.setNegativeButton(
                "Cancelar",
                null
        );

        builder.show();
    }

    private void salvarNovaAtividade(
            String titulo
    ) {

        String uidAtual =
                FirebaseHelper.getUidAtual();

        if (uidAtual == null) return;

        String activityId =
                atividadesRef.push().getKey();

        if (activityId == null) return;

        String dataAtual =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());

        Atividade atividade =
                new Atividade(
                        activityId,
                        titulo,
                        "Atividade criada pelo aluno",
                        dataAtual,
                        false,
                        uidAtual,
                        AppData.getNome(this),
                        "",
                        AppData.getSupervisor(this),
                        "aluno",
                        0,
                        System.currentTimeMillis()
                );

        atividadesRef
                .child(activityId)
                .setValue(atividade)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Atividade criada",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Erro ao criar atividade",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void configurarNavegacao() {

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        NavHelper.navigate(
                                AtividadesActivity.this,
                                MainActivity.class
                        );
                    }
                });

        findViewById(R.id.nav_inicio)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                MainActivity.class
                        ));

        findViewById(R.id.nav_vagas)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                VagasActivity.class
                        ));

        findViewById(R.id.nav_atividades)
                .setOnClickListener(v -> {
                });

        findViewById(R.id.nav_perfil)
                .setOnClickListener(v ->

                        NavHelper.navigate(
                                this,
                                PerfilActivity.class
                        ));
    }
}