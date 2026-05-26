package com.example.estagioapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class NovaVagaActivity extends AppCompatActivity {

    private Spinner spinnerArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nova_vaga);

        // SPINNER ÁREA

        spinnerArea =
                findViewById(R.id.spinner_area);

        String[] areas = {
                "Tech",
                "Design",
                "Marketing",
                "Finanças"
        };

        ArrayAdapter<String> adapterArea =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        areas
                );

        spinnerArea.setAdapter(adapterArea);

        // VOLTAR

        findViewById(R.id.btn_voltar)
                .setOnClickListener(v -> finish());

        // PUBLICAR

        findViewById(R.id.btn_publicar)
                .setOnClickListener(v -> {

                    EditText etTitulo =
                            findViewById(R.id.et_titulo);

                    EditText etLocal =
                            findViewById(R.id.et_local);

                    EditText etModalidade =
                            findViewById(R.id.et_modalidade);

                    EditText etCarga =
                            findViewById(R.id.et_carga);

                    EditText etDescricao =
                            findViewById(R.id.et_descricao);

                    String titulo =
                            etTitulo.getText()
                                    .toString()
                                    .trim();

                    String area =
                            spinnerArea.getSelectedItem()
                                    .toString();

                    String local =
                            etLocal.getText()
                                    .toString()
                                    .trim();

                    String modalidade =
                            etModalidade.getText()
                                    .toString()
                                    .trim();

                    String carga =
                            etCarga.getText()
                                    .toString()
                                    .trim();

                    String descricao =
                            etDescricao.getText()
                                    .toString()
                                    .trim();

                    // VALIDAÇÕES

                    if (titulo.isEmpty()) {

                        etTitulo.setError("Obrigatório");

                        etTitulo.requestFocus();

                        return;
                    }

                    if (local.isEmpty()) {

                        etLocal.setError("Obrigatório");

                        etLocal.requestFocus();

                        return;
                    }

                    if (modalidade.isEmpty()) {

                        etModalidade.setError("Obrigatório");

                        etModalidade.requestFocus();

                        return;
                    }

                    if (carga.isEmpty()) {

                        etCarga.setError("Obrigatório");

                        etCarga.requestFocus();

                        return;
                    }

                    if (descricao.isEmpty()) {

                        etDescricao.setError("Obrigatório");

                        etDescricao.requestFocus();

                        return;
                    }

                    // BOTÃO

                    findViewById(R.id.btn_publicar)
                            .setEnabled(false);

                    ((TextView) findViewById(R.id.btn_publicar))
                            .setText("Publicando...");

                    // USUÁRIO

                    String uid =
                            FirebaseHelper.getUidAtual();

                    FirebaseHelper.refUsuarios()
                            .child(uid)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                String nomeEmpresa =
                                        snapshot.child("razaoSocial")
                                                .getValue(String.class);

                                if (nomeEmpresa == null) {

                                    nomeEmpresa =
                                            snapshot.child("nome")
                                                    .getValue(String.class);
                                }

                                // OBJETO

                                Map<String, Object> vaga =
                                        new HashMap<>();

                                vaga.put("titulo", titulo);

                                vaga.put("area", area);

                                vaga.put("local", local);

                                vaga.put("modalidade", modalidade);

                                vaga.put("carga", carga);

                                vaga.put("descricao", descricao);

                                vaga.put("empresa", nomeEmpresa);

                                vaga.put("empresaUid", uid);

                                vaga.put("status", "ativa");

                                vaga.put("candidatos", 0);

                                vaga.put(
                                        "timestamp",
                                        System.currentTimeMillis()
                                );

                                // FIREBASE

                                DatabaseReference novaVaga =
                                        FirebaseHelper
                                                .refVagas()
                                                .push();

                                novaVaga.setValue(vaga)

                                        .addOnSuccessListener(unused -> {

                                            Toast.makeText(
                                                    this,
                                                    "Vaga publicada com sucesso!",
                                                    Toast.LENGTH_SHORT
                                            ).show();

                                            finish();
                                        })

                                        .addOnFailureListener(e -> {

                                            findViewById(R.id.btn_publicar)
                                                    .setEnabled(true);

                                            ((TextView) findViewById(R.id.btn_publicar))
                                                    .setText("Publicar vaga");

                                            Toast.makeText(
                                                    this,
                                                    "Erro ao publicar: "
                                                            + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                            })

                            .addOnFailureListener(e -> {

                                findViewById(R.id.btn_publicar)
                                        .setEnabled(true);

                                ((TextView) findViewById(R.id.btn_publicar))
                                        .setText("Publicar vaga");

                                Toast.makeText(
                                        this,
                                        "Erro ao buscar dados da empresa.",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                });
    }
}