package com.example.estagioapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
                FirebaseHelper.refAtividades();
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
                        int horasAtividades = 0;

                        for (DataSnapshot ds : snapshot.getChildren()) {

                            Atividade atividade =
                                    ds.getValue(Atividade.class);

                            if (atividade == null)
                                continue;

                            atividade.setId(ds.getKey());

                            if (!uidAtual.equals(
                                    atividade.getAlunoUid()
                            )) {
                                continue;
                            }

                            listaAtividades.add(atividade);

                            if (atividade.isConcluida()) {

                                concluidas++;

                                horasAtividades +=
                                        atividade.getHoras();

                            } else {

                                pendentes++;
                            }
                        }

                        adapter.notifyDataSetChanged();

                        atualizarContadores(
                                concluidas,
                                pendentes,
                                horasAtividades
                        );

                        sincronizarHorasPerfil(
                                uidAtual,
                                horasAtividades
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

    private void sincronizarHorasPerfil(
            String uid,
            int horasAtividades
    ) {

        FirebaseHelper.refUsuarios()
                .child(uid)
                .get()
                .addOnSuccessListener(snapshot -> {

                    int horasExtras = 0;

                    Long extras =
                            snapshot.child("horasExtras")
                                    .getValue(Long.class);

                    if (extras != null) {

                        horasExtras =
                                extras.intValue();
                    }

                    int horasTotais =
                            horasExtras + horasAtividades;

                    FirebaseHelper.refUsuarios()
                            .child(uid)
                            .child("horasAtividades")
                            .setValue(horasAtividades);

                    FirebaseHelper.refUsuarios()
                            .child(uid)
                            .child("horasConcluidas")
                            .setValue(horasTotais);

                    AppData.setHorasConcluidas(
                            this,
                            String.valueOf(horasTotais)
                    );
                });
    }
    private void atualizarCabecalho() {

        String uidAtual =
                FirebaseHelper.getUidAtual();

        if (uidAtual == null) return;

        FirebaseHelper.refUsuarios()
                .child(uidAtual)
                .get()
                .addOnSuccessListener(snapshot -> {

                    String supervisorUid =
                            snapshot.child("supervisorUid")
                                    .getValue(String.class);

                    if (supervisorUid == null
                            || supervisorUid.isEmpty()) {

                        tvSupervisor.setText(
                                "Sem supervisor vinculado"
                        );

                        return;
                    }

                    FirebaseHelper.refUsuarios()
                            .child(supervisorUid)
                            .get()
                            .addOnSuccessListener(supervisorSnapshot -> {

                                String nomeSupervisor =
                                        supervisorSnapshot
                                                .child("nome")
                                                .getValue(String.class);

                                if (nomeSupervisor == null
                                        || nomeSupervisor.isEmpty()) {

                                    nomeSupervisor = "Supervisor";
                                }

                                tvSupervisor.setText(
                                        "Acompanhado por "
                                                + nomeSupervisor
                                );
                            });
                });
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

        AppData.setAtividadesConcluidas(
                this,
                concluidas
        );

        AppData.setAtividadesPendentes(
                this,
                pendentes
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

        LinearLayout layout =
                new LinearLayout(this);

        layout.setOrientation(
                LinearLayout.VERTICAL
        );

        layout.setPadding(
                40,
                30,
                40,
                10
        );

        EditText inputTitulo =
                new EditText(this);

        inputTitulo.setHint(
                "Título da atividade"
        );

        inputTitulo.setInputType(
                InputType.TYPE_CLASS_TEXT
        );

        inputTitulo.setMaxLines(1);

        inputTitulo.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(25)
                }
        );

        layout.addView(inputTitulo);

        EditText inputDescricao =
                new EditText(this);

        inputDescricao.setHint(
                "Descrição da atividade"
        );

        inputDescricao.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        inputDescricao.setMinLines(4);

        inputDescricao.setGravity(
                Gravity.TOP
        );

        inputDescricao.setFilters(
                new InputFilter[]{
                        new InputFilter.LengthFilter(180)
                }
        );

        LinearLayout.LayoutParams descricaoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        descricaoParams.topMargin = 24;

        inputDescricao.setLayoutParams(
                descricaoParams
        );

        layout.addView(inputDescricao);

        Spinner spinnerTipo =
                new Spinner(this);

        String[] tipos = {
                "Reunião - 1h",
                "Atendimento - 2h",
                "Relatório - 2h",
                "Pesquisa - 3h",
                "Desenvolvimento - 4h",
                "Treinamento - 5h"
        };

        ArrayAdapter<String> adapterSpinner =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        tipos
                );

        spinnerTipo.setAdapter(adapterSpinner);

        LinearLayout.LayoutParams spinnerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        spinnerParams.topMargin = 24;

        spinnerTipo.setLayoutParams(
                spinnerParams
        );

        layout.addView(spinnerTipo);

        builder.setView(layout);

        builder.setPositiveButton(
                "Criar",
                (dialog, which) -> {

                    String titulo =
                            inputTitulo
                                    .getText()
                                    .toString()
                                    .trim();

                    String descricao =
                            inputDescricao
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

                    if (descricao.isEmpty()) {

                        Toast.makeText(
                                this,
                                "Digite uma descrição",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String tipoSelecionado =
                            spinnerTipo
                                    .getSelectedItem()
                                    .toString();

                    int horas = 1;

                    if (tipoSelecionado.contains("2h")) {

                        horas = 2;

                    } else if (tipoSelecionado.contains("3h")) {

                        horas = 3;

                    } else if (tipoSelecionado.contains("4h")) {

                        horas = 4;

                    } else if (tipoSelecionado.contains("5h")) {

                        horas = 5;
                    }

                    salvarNovaAtividade(
                            titulo,
                            descricao,
                            horas
                    );
                });

        builder.setNegativeButton(
                "Cancelar",
                null
        );

        builder.show();
    }

    private void salvarNovaAtividade(
            String titulo,
            String descricao,
            int horas
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
                        descricao,
                        dataAtual,
                        false,
                        uidAtual,
                        AppData.getNome(this),
                        "",
                        AppData.getSupervisor(this),
                        "aluno",
                        horas,
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