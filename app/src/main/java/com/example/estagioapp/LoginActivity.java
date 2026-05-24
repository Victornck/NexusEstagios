package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private String perfilSelecionado = "aluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LinearLayout btnAluno      = findViewById(R.id.btn_perfil_aluno);
        LinearLayout btnSupervisor = findViewById(R.id.btn_perfil_supervisor);
        LinearLayout btnEmpresa    = findViewById(R.id.btn_perfil_empresa);

        TextView txtAluno      = findViewById(R.id.txt_perfil_aluno);
        TextView txtSupervisor = findViewById(R.id.txt_perfil_supervisor);
        TextView txtEmpresa    = findViewById(R.id.txt_perfil_empresa);

        // Seleção de perfil
        btnAluno.setOnClickListener(v -> {
            perfilSelecionado = "aluno";

            btnAluno.setBackgroundResource(R.drawable.bg_perfil_selected);
            btnSupervisor.setBackgroundResource(R.drawable.bg_perfil_unselected);
            btnEmpresa.setBackgroundResource(R.drawable.bg_perfil_unselected);

            txtAluno.setTextColor(0xFFFF6B00);
            txtSupervisor.setTextColor(0xFFFFD4B3);
            txtEmpresa.setTextColor(0xFFFFD4B3);
        });

        btnSupervisor.setOnClickListener(v -> {
            perfilSelecionado = "supervisor";

            btnSupervisor.setBackgroundResource(R.drawable.bg_perfil_selected);
            btnAluno.setBackgroundResource(R.drawable.bg_perfil_unselected);
            btnEmpresa.setBackgroundResource(R.drawable.bg_perfil_unselected);

            txtSupervisor.setTextColor(0xFFFF6B00);
            txtAluno.setTextColor(0xFFFFD4B3);
            txtEmpresa.setTextColor(0xFFFFD4B3);
        });

        btnEmpresa.setOnClickListener(v -> {
            perfilSelecionado = "empresa";

            btnEmpresa.setBackgroundResource(R.drawable.bg_perfil_selected);
            btnAluno.setBackgroundResource(R.drawable.bg_perfil_unselected);
            btnSupervisor.setBackgroundResource(R.drawable.bg_perfil_unselected);

            txtEmpresa.setTextColor(0xFFFF6B00);
            txtAluno.setTextColor(0xFFFFD4B3);
            txtSupervisor.setTextColor(0xFFFFD4B3);
        });

        // Entrar
        findViewById(R.id.btn_entrar).setOnClickListener(v -> {

            EditText etEmail = findViewById(R.id.et_email);
            EditText etSenha = findViewById(R.id.et_senha);

            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            // Validação do e-mail
            if (email.isEmpty()) {
                etEmail.setError("Informe seu e-mail");
                etEmail.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("E-mail inválido");
                etEmail.requestFocus();
                return;
            }

            // Validação da senha
            if (senha.isEmpty()) {
                etSenha.setError("Informe sua senha");
                etSenha.requestFocus();
                return;
            }

            if (senha.length() < 6) {
                etSenha.setError("A senha deve ter no mínimo 6 caracteres");
                etSenha.requestFocus();
                return;
            }

            // Salva dados do usuário
            AppData.setPerfilLogado(this, perfilSelecionado);
            AppData.setEmailLogado(this, email);

            // Redireciona conforme perfil
            switch (perfilSelecionado) {

                case "supervisor":
                    startActivity(new Intent(this, SupervisorActivity.class));
                    break;

                case "empresa":
                    startActivity(new Intent(this, EmpresaActivity.class));
                    break;

                default:
                    startActivity(new Intent(this, MainActivity.class));
                    break;
            }

            finish();
        });

        // Cadastro
        findViewById(R.id.btn_cadastrar).setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));
    }
}