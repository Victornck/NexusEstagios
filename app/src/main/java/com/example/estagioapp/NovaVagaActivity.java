package com.example.estagioapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class NovaVagaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_vaga);

        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());

        findViewById(R.id.btn_publicar).setOnClickListener(v -> {
            EditText etTitulo     = findViewById(R.id.et_titulo);
            EditText etLocal      = findViewById(R.id.et_local);
            EditText etModalidade = findViewById(R.id.et_modalidade);
            EditText etCarga      = findViewById(R.id.et_carga);
            EditText etDescricao  = findViewById(R.id.et_descricao);

            String titulo     = etTitulo.getText().toString().trim();
            String local      = etLocal.getText().toString().trim();
            String modalidade = etModalidade.getText().toString().trim();
            String carga      = etCarga.getText().toString().trim();
            String descricao  = etDescricao.getText().toString().trim();

            if (titulo.isEmpty())    { etTitulo.setError("Obrigatório"); etTitulo.requestFocus(); return; }
            if (local.isEmpty())     { etLocal.setError("Obrigatório"); etLocal.requestFocus(); return; }
            if (modalidade.isEmpty()){ etModalidade.setError("Obrigatório"); etModalidade.requestFocus(); return; }
            if (carga.isEmpty())     { etCarga.setError("Obrigatório"); etCarga.requestFocus(); return; }
            if (descricao.isEmpty()) { etDescricao.setError("Obrigatório"); etDescricao.requestFocus(); return; }

            Toast.makeText(this, "Vaga publicada com sucesso!", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}