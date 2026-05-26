package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    private String perfilSelecionado = "aluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se já está logado, redireciona direto
        if (FirebaseHelper.estaLogado()) {
            redirecionarPorPerfil();
            return;
        }

        setContentView(R.layout.activity_login);

        LinearLayout btnAluno      = findViewById(R.id.btn_perfil_aluno);
        LinearLayout btnSupervisor = findViewById(R.id.btn_perfil_supervisor);
        LinearLayout btnEmpresa    = findViewById(R.id.btn_perfil_empresa);
        TextView txtAluno          = findViewById(R.id.txt_perfil_aluno);
        TextView txtSupervisor     = findViewById(R.id.txt_perfil_supervisor);
        TextView txtEmpresa        = findViewById(R.id.txt_perfil_empresa);

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

        findViewById(R.id.btn_entrar).setOnClickListener(v -> {
            EditText etEmail = findViewById(R.id.et_email);
            EditText etSenha = findViewById(R.id.et_senha);

            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            if (email.isEmpty()) { etEmail.setError("Informe seu e-mail"); etEmail.requestFocus(); return; }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { etEmail.setError("E-mail inválido"); etEmail.requestFocus(); return; }
            if (senha.isEmpty()) { etSenha.setError("Informe sua senha"); etSenha.requestFocus(); return; }
            if (senha.length() < 6) { etSenha.setError("Mínimo 6 caracteres"); etSenha.requestFocus(); return; }

            // Desabilita botão durante login
            findViewById(R.id.btn_entrar).setEnabled(false);
            ((TextView) findViewById(R.id.btn_entrar)).setText("Entrando...");

            // Login real no Firebase
            FirebaseHelper.getAuth()
                    .signInWithEmailAndPassword(email, senha)
                    .addOnSuccessListener(authResult -> {
                        String uid = FirebaseHelper.getUidAtual();

                        // Busca perfil do usuário no banco
                        FirebaseHelper.refUsuarios().child(uid)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        String perfilBanco = snapshot.child("perfil").getValue(String.class);
                                        if (perfilBanco != null) {
                                            perfilSelecionado = perfilBanco;
                                        }
                                        redirecionarPorPerfil();
                                        finish();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        redirecionarPorPerfil();
                                        finish();
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        findViewById(R.id.btn_entrar).setEnabled(true);
                        ((TextView) findViewById(R.id.btn_entrar)).setText("Entrar");
                        String msg = e.getMessage();
                        if (msg != null && msg.contains("password")) {
                            Toast.makeText(this, "Senha incorreta.", Toast.LENGTH_SHORT).show();
                        } else if (msg != null && msg.contains("no user")) {
                            Toast.makeText(this, "Usuário não encontrado.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Erro ao entrar. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        findViewById(R.id.btn_cadastrar).setOnClickListener(v ->
                startActivity(new Intent(this, CadastroActivity.class)));

        EditText etSenha =
                findViewById(R.id.et_senha);

        ImageView btnToggleSenha =
                findViewById(R.id.btn_toggle_senha);

        final boolean[] senhaVisivel = {false};

        btnToggleSenha.setOnClickListener(v -> {

            senhaVisivel[0] = !senhaVisivel[0];

            if (senhaVisivel[0]) {

                etSenha.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );

                btnToggleSenha.setImageResource(
                        R.drawable.ic_eye_open
                );

            } else {

                etSenha.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );

                btnToggleSenha.setImageResource(
                        R.drawable.ic_eye_closed
                );
            }

            etSenha.setSelection(
                    etSenha.getText().length()
            );
        });
    }

    private void redirecionarPorPerfil() {
        String uid = FirebaseHelper.getUidAtual();
        if (uid == null) return;

        FirebaseHelper.refUsuarios().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        String perfil = snapshot.child("perfil").getValue(String.class);
                        if (perfil == null) perfil = "aluno";
                        switch (perfil) {
                            case "supervisor":
                                startActivity(new Intent(LoginActivity.this, SupervisorActivity.class));
                                break;
                            case "empresa":
                                startActivity(new Intent(LoginActivity.this, EmpresaActivity.class));
                                break;
                            default:
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                break;
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}