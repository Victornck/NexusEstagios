package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvAvatar;
    private TextView tvNome;
    private TextView tvCurso;
    private TextView tvEstagio;
    private TextView tvCurriculos;
    private TextView tvEmpresa;
    private TextView tvSupervisor;

    private TextView btnEditar;
    private TextView btnSair;

    private LinearLayout navInicio;
    private LinearLayout navVagas;
    private LinearLayout navAtividades;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // ───── IDS ─────

        tvAvatar = findViewById(R.id.tv_avatar);
        tvNome = findViewById(R.id.tv_nome);
        tvCurso = findViewById(R.id.tv_curso);
        tvEstagio = findViewById(R.id.tv_estagio);
        tvCurriculos = findViewById(R.id.tv_curriculos);
        tvEmpresa = findViewById(R.id.tv_empresa);
        tvSupervisor = findViewById(R.id.tv_supervisor);

        btnEditar = findViewById(R.id.btn_editar);
        btnSair = findViewById(R.id.btn_sair_perfil);

        navInicio = findViewById(R.id.nav_inicio);
        navVagas = findViewById(R.id.nav_vagas);
        navAtividades = findViewById(R.id.nav_atividades);

        // ───── CARREGAR DADOS ─────

        carregarDados();

        // ───── EDITAR PERFIL ─────

        btnEditar.setOnClickListener(v -> {

            Intent intent =
                    new Intent(this, EditarPerfilActivity.class);

            startActivity(intent);
        });

        // ───── SAIR DA CONTA ─────

        btnSair.setOnClickListener(v -> {

            AppData.setEmailLogado(this, "");

            FirebaseHelper.getAuth().signOut();

            Intent intent =
                    new Intent(this, LoginActivity.class);

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });

        // ───── NAVEGAÇÃO ─────

        navInicio.setOnClickListener(v ->
                NavHelper.navigate(this, MainActivity.class));

        navVagas.setOnClickListener(v ->
                NavHelper.navigate(this, VagasActivity.class));

        navAtividades.setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

        carregarDados();
    }

    // ───── CARREGAR DADOS ─────

    private void carregarDados() {

        String nome =
                AppData.getNome(this);

        String curso =
                AppData.getCurso(this);

        String estagio =
                AppData.getEstagio(this);

        String empresa =
                AppData.getEmpresa(this);

        int curriculos =
                AppData.getCurriculosCount(this);

        // TEXTOS

        tvNome.setText(nome);

        tvCurso.setText(curso);

        tvEstagio.setText(estagio);

        tvEmpresa.setText(empresa);

        String textoCurriculos;

        if (curriculos == 0) {

            textoCurriculos =
                    "Nenhum currículo salvo";

        } else if (curriculos == 1) {

            textoCurriculos =
                    "1 versão salva";

        } else {

            textoCurriculos =
                    curriculos + " versões salvas";
        }

        tvCurriculos.setText(textoCurriculos);

        // ───── SUPERVISOR ─────

        String uid =
                FirebaseHelper.getUidAtual();

        if (uid != null) {

            FirebaseHelper.refUsuarios()
                    .child(uid)
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        String supervisorUid =
                                snapshot.child("supervisorUid")
                                        .getValue(String.class);

                        if (supervisorUid == null
                                || supervisorUid.isEmpty()) {

                            tvSupervisor.setText(
                                    "Não vinculado"
                            );

                            return;
                        }

                        FirebaseHelper.refUsuarios()
                                .child(supervisorUid)
                                .get()
                                .addOnSuccessListener(supervisorSnapshot -> {

                                    String nomeSupervisor =
                                            supervisorSnapshot.child("nome")
                                                    .getValue(String.class);

                                    if (nomeSupervisor == null
                                            || nomeSupervisor.isEmpty()) {

                                        nomeSupervisor =
                                                "Supervisor";
                                    }

                                    // SALVA LOCALMENTE

                                    AppData.setSupervisor(
                                            PerfilActivity.this,
                                            nomeSupervisor
                                    );

                                    tvSupervisor.setText(
                                            nomeSupervisor
                                    );
                                });
                    });
        }

        // ───── AVATAR ─────

        if (nome != null && !nome.isEmpty()) {

            String[] partes =
                    nome.split(" ");

            String iniciais = "";

            if (partes.length >= 2) {

                iniciais =
                        partes[0].substring(0,1)
                                +
                                partes[1].substring(0,1);

            } else {

                iniciais =
                        nome.substring(0,1);
            }

            tvAvatar.setText(
                    iniciais.toUpperCase()
            );
        }
    }
}