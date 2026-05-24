package com.example.estagioapp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditarPerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // Preenche os campos com os dados atuais
        EditText etNome       = findViewById(R.id.et_nome);
        EditText etCurso      = findViewById(R.id.et_curso);
        EditText etEstagio    = findViewById(R.id.et_estagio);
        EditText etEmpresa    = findViewById(R.id.et_empresa);
        EditText etSupervisor = findViewById(R.id.et_supervisor);
        EditText etHoras      = findViewById(R.id.et_horas);
        EditText etHorasTotal = findViewById(R.id.et_horas_total);

        etNome.setText(AppData.getNome(this));
        etCurso.setText(AppData.getCurso(this));
        etEstagio.setText(AppData.getEstagio(this));
        etEmpresa.setText(AppData.getEmpresa(this));
        etSupervisor.setText(AppData.getSupervisor(this));
        etHoras.setText(AppData.getHorasConcluidas(this));
        etHorasTotal.setText(AppData.getHorasTotal(this));

        // Botão salvar
        findViewById(R.id.btn_salvar).setOnClickListener(v -> {
            AppData.setNome(this,       etNome.getText().toString().trim());
            AppData.setCurso(this,      etCurso.getText().toString().trim());
            AppData.setEstagio(this,    etEstagio.getText().toString().trim());
            AppData.setEmpresa(this,    etEmpresa.getText().toString().trim());
            AppData.setSupervisor(this, etSupervisor.getText().toString().trim());
            AppData.setHorasConcluidas(this, etHoras.getText().toString().trim());
            AppData.setHorasTotal(this,      etHorasTotal.getText().toString().trim());

            Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
            finish(); // volta para o Perfil
        });

        // Botão cancelar
        findViewById(R.id.btn_cancelar).setOnClickListener(v -> finish());
    }
}