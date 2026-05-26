package com.example.estagioapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    private EditText etNome;
    private EditText etCurso;
    private EditText etEstagio;
    private EditText etEmpresa;
    private EditText etSupervisor;

    // HORAS EXTRAS MANUAIS
    private EditText etHorasExtras;

    // CARGA HORÁRIA TOTAL
    private EditText etHorasTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_editar_perfil
        );

        iniciarViews();

        carregarDados();

        configurarSalvar();

        configurarCancelar();
    }

    private void iniciarViews() {

        etNome =
                findViewById(R.id.et_nome);

        etCurso =
                findViewById(R.id.et_curso);

        etEstagio =
                findViewById(R.id.et_estagio);

        etEmpresa =
                findViewById(R.id.et_empresa);

        etSupervisor =
                findViewById(R.id.et_supervisor);

        // AGORA ESSE CAMPO REPRESENTA
        // AS HORAS EXTRAS MANUAIS
        etHorasExtras =
                findViewById(R.id.et_horas);

        etHorasTotal =
                findViewById(R.id.et_horas_total);

        // SUPERVISOR SOMENTE LEITURA

        etSupervisor.setEnabled(false);

        etSupervisor.setFocusable(false);

        etSupervisor.setClickable(false);

        etSupervisor.setCursorVisible(false);
    }

    private void carregarDados() {

        etNome.setText(
                AppData.getNome(this)
        );

        etCurso.setText(
                AppData.getCurso(this)
        );

        etEstagio.setText(
                AppData.getEstagio(this)
        );

        etEmpresa.setText(
                AppData.getEmpresa(this)
        );

        etHorasTotal.setText(
                AppData.getHorasTotal(this)
        );

        carregarHorasExtras();

        carregarSupervisor();
    }

    private void carregarHorasExtras() {

        String uid =
                FirebaseHelper.getUidAtual();

        if (uid == null) return;

        FirebaseHelper.refUsuarios()
                .child(uid)
                .child("horasExtras")
                .get()
                .addOnSuccessListener(snapshot -> {

                    Long horas =
                            snapshot.getValue(Long.class);

                    if (horas != null) {

                        etHorasExtras.setText(
                                String.valueOf(
                                        horas.intValue()
                                )
                        );

                    } else {

                        etHorasExtras.setText("0");
                    }
                });
    }

    private void carregarSupervisor() {

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

                        etSupervisor.setText(
                                "Sem supervisor"
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

                                    nomeSupervisor =
                                            "Supervisor";
                                }

                                AppData.setSupervisor(
                                        EditarPerfilActivity.this,
                                        nomeSupervisor
                                );

                                etSupervisor.setText(
                                        nomeSupervisor
                                );
                            });
                });
    }

    private void configurarSalvar() {

        findViewById(R.id.btn_salvar)
                .setOnClickListener(v -> {

                    salvarPerfil();
                });
    }

    private void salvarPerfil() {

        String nome =
                etNome.getText()
                        .toString()
                        .trim();

        String curso =
                etCurso.getText()
                        .toString()
                        .trim();

        String estagio =
                etEstagio.getText()
                        .toString()
                        .trim();

        String empresa =
                etEmpresa.getText()
                        .toString()
                        .trim();

        String horasExtrasTexto =
                etHorasExtras.getText()
                        .toString()
                        .trim();

        String horasTotalTexto =
                etHorasTotal.getText()
                        .toString()
                        .trim();

        int horasExtras =
                horasExtrasTexto.isEmpty()
                        ? 0
                        : Integer.parseInt(
                        horasExtrasTexto
                );

        int horasTotal =
                horasTotalTexto.isEmpty()
                        ? 600
                        : Integer.parseInt(
                        horasTotalTexto
                );

        // SALVA LOCAL

        AppData.setNome(
                this,
                nome
        );

        AppData.setCurso(
                this,
                curso
        );

        AppData.setEstagio(
                this,
                estagio
        );

        AppData.setEmpresa(
                this,
                empresa
        );

        AppData.setHorasTotal(
                this,
                String.valueOf(
                        horasTotal
                )
        );

        String uid =
                FirebaseHelper.getUidAtual();

        if (uid == null) return;

        // FIREBASE

        Map<String, Object> updates =
                new HashMap<>();

        updates.put(
                "nome",
                nome
        );

        updates.put(
                "curso",
                curso
        );

        updates.put(
                "estagio",
                estagio
        );

        updates.put(
                "empresa",
                empresa
        );

        // HORAS MANUAIS
        // FICAM SEPARADAS
        // DAS ATIVIDADES

        updates.put(
                "horasExtras",
                horasExtras
        );

        // CARGA HORÁRIA TOTAL

        updates.put(
                "horasTotal",
                horasTotal
        );

        FirebaseHelper.refUsuarios()
                .child(uid)
                .updateChildren(updates)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Perfil atualizado!",
                            Toast.LENGTH_SHORT
                    ).show();

                    finish();
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Erro ao atualizar perfil",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    private void configurarCancelar() {

        findViewById(R.id.btn_cancelar)
                .setOnClickListener(v -> finish());
    }
}