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

            String nome       = etNome.getText().toString().trim();
            String curso      = etCurso.getText().toString().trim();
            String estagio    = etEstagio.getText().toString().trim();
            String empresa    = etEmpresa.getText().toString().trim();
            String supervisor = etSupervisor.getText().toString().trim();
            String horas      = etHoras.getText().toString().trim();
            String horasTotal = etHorasTotal.getText().toString().trim();

            // Salva localmente (AppData)
            AppData.setNome(this, nome);
            AppData.setCurso(this, curso);
            AppData.setEstagio(this, estagio);
            AppData.setEmpresa(this, empresa);
            AppData.setSupervisor(this, supervisor);
            AppData.setHorasConcluidas(this, horas);
            AppData.setHorasTotal(this, horasTotal);

            // Salva no Firebase para manter consistência
            String uid = FirebaseHelper.getUidAtual();
            if (uid != null) {
                java.util.Map<String, Object> updates = new java.util.HashMap<>();
                updates.put("nome",             nome);
                updates.put("curso",            curso);
                updates.put("estagio",          estagio);
                updates.put("empresa",          empresa);
                updates.put("supervisorUid",    supervisor);
                updates.put("horasConcluidas",  horas.isEmpty() ? 0 : Long.parseLong(horas));
                updates.put("horasTotal",       horasTotal.isEmpty() ? 600 : Long.parseLong(horasTotal));

                FirebaseHelper.refUsuarios().child(uid).updateChildren(updates);
            }

            Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // Botão cancelar
        findViewById(R.id.btn_cancelar).setOnClickListener(v -> finish());
    }
}