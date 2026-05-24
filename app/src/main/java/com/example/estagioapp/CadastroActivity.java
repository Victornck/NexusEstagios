package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    private String tipoSelecionado = "aluno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        LinearLayout btnAluno      = findViewById(R.id.btn_tipo_aluno);
        LinearLayout btnSupervisor = findViewById(R.id.btn_tipo_supervisor);
        LinearLayout btnEmpresa    = findViewById(R.id.btn_tipo_empresa);
        TextView txtAluno          = findViewById(R.id.txt_tipo_aluno);
        TextView txtSupervisor     = findViewById(R.id.txt_tipo_supervisor);
        TextView txtEmpresa        = findViewById(R.id.txt_tipo_empresa);

        LinearLayout camposAluno      = findViewById(R.id.campos_aluno);
        LinearLayout camposSupervisor = findViewById(R.id.campos_supervisor);
        LinearLayout camposEmpresa    = findViewById(R.id.campos_empresa);

        // Seleção de tipo de conta
        btnAluno.setOnClickListener(v -> {
            tipoSelecionado = "aluno";
            selecionarTipo(btnAluno, btnSupervisor, btnEmpresa,
                    txtAluno, txtSupervisor, txtEmpresa,
                    camposAluno, camposSupervisor, camposEmpresa);
        });

        btnSupervisor.setOnClickListener(v -> {
            tipoSelecionado = "supervisor";
            selecionarTipo(btnSupervisor, btnAluno, btnEmpresa,
                    txtSupervisor, txtAluno, txtEmpresa,
                    camposSupervisor, camposAluno, camposEmpresa);
        });

        btnEmpresa.setOnClickListener(v -> {
            tipoSelecionado = "empresa";
            selecionarTipo(btnEmpresa, btnAluno, btnSupervisor,
                    txtEmpresa, txtAluno, txtSupervisor,
                    camposEmpresa, camposAluno, camposSupervisor);
        });

        // Voltar
        findViewById(R.id.btn_voltar).setOnClickListener(v -> finish());

        // Cadastrar
        findViewById(R.id.btn_cadastrar).setOnClickListener(v -> {

            EditText etNome            = findViewById(R.id.et_nome);
            EditText etCpf             = findViewById(R.id.et_cpf);
            EditText etNascimento      = findViewById(R.id.et_nascimento);
            EditText etTelefone        = findViewById(R.id.et_telefone);
            EditText etEmail           = findViewById(R.id.et_email);
            EditText etSenha           = findViewById(R.id.et_senha);
            EditText etConfirmarSenha  = findViewById(R.id.et_confirmar_senha);

            String nome           = etNome.getText().toString().trim();
            String cpf            = etCpf.getText().toString().trim();
            String nascimento     = etNascimento.getText().toString().trim();
            String telefone       = etTelefone.getText().toString().trim();
            String email          = etEmail.getText().toString().trim();
            String senha          = etSenha.getText().toString().trim();
            String confirmarSenha = etConfirmarSenha.getText().toString().trim();

            // Validações base
            if (nome.isEmpty())      { etNome.setError("Obrigatório"); etNome.requestFocus(); return; }
            if (cpf.isEmpty())       { etCpf.setError("Obrigatório"); etCpf.requestFocus(); return; }
            if (nascimento.isEmpty()){ etNascimento.setError("Obrigatório"); etNascimento.requestFocus(); return; }
            if (telefone.isEmpty())  { etTelefone.setError("Obrigatório"); etTelefone.requestFocus(); return; }
            if (email.isEmpty())     { etEmail.setError("Obrigatório"); etEmail.requestFocus(); return; }
            if (senha.length() < 6)  { etSenha.setError("Mínimo 6 caracteres"); etSenha.requestFocus(); return; }
            if (!senha.equals(confirmarSenha)) {
                etConfirmarSenha.setError("Senhas não conferem");
                etConfirmarSenha.requestFocus();
                return;
            }

            // Validações e salvamento por tipo
            if (tipoSelecionado.equals("aluno")) {
                EditText etCurso    = findViewById(R.id.et_curso);
                EditText etSemestre = findViewById(R.id.et_semestre);
                String curso    = etCurso.getText().toString().trim();
                String semestre = etSemestre.getText().toString().trim();
                if (curso.isEmpty())    { etCurso.setError("Obrigatório"); etCurso.requestFocus(); return; }
                if (semestre.isEmpty()) { etSemestre.setError("Obrigatório"); etSemestre.requestFocus(); return; }
                AppData.setCurso(this, curso + " · " + semestre + "° sem");

            } else if (tipoSelecionado.equals("supervisor")) {
                EditText etMatricula   = findViewById(R.id.et_matricula);
                EditText etInstituicao = findViewById(R.id.et_instituicao);
                String matricula   = etMatricula.getText().toString().trim();
                String instituicao = etInstituicao.getText().toString().trim();
                if (matricula.isEmpty())   { etMatricula.setError("Obrigatório"); etMatricula.requestFocus(); return; }
                if (instituicao.isEmpty()) { etInstituicao.setError("Obrigatório"); etInstituicao.requestFocus(); return; }
                AppData.setSupervisor(this, nome + " · " + instituicao);

            } else if (tipoSelecionado.equals("empresa")) {
                EditText etCnpj       = findViewById(R.id.et_cnpj);
                EditText etRazaoSocial = findViewById(R.id.et_razao_social);
                String cnpj       = etCnpj.getText().toString().trim();
                String razaoSocial = etRazaoSocial.getText().toString().trim();
                if (cnpj.isEmpty())       { etCnpj.setError("Obrigatório"); etCnpj.requestFocus(); return; }
                if (razaoSocial.isEmpty()){ etRazaoSocial.setError("Obrigatório"); etRazaoSocial.requestFocus(); return; }
                AppData.setEmpresa(this, razaoSocial);
            }

            // Salva dados comuns
            AppData.setNome(this, nome);
            AppData.setEmailLogado(this, email);
            AppData.setPerfilLogado(this, tipoSelecionado);

            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();

            // Vai direto pro login
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity();
        });
        selecionarTipo(
                btnAluno,
                btnSupervisor,
                btnEmpresa,
                txtAluno,
                txtSupervisor,
                txtEmpresa,
                camposAluno,
                camposSupervisor,
                camposEmpresa
        );
    }

    private void selecionarTipo(
            LinearLayout btnAtivo, LinearLayout btnInativo1, LinearLayout btnInativo2,
            TextView txtAtivo, TextView txtInativo1, TextView txtInativo2,
            LinearLayout camposAtivo, LinearLayout camposInativo1, LinearLayout camposInativo2) {

        btnAtivo.setBackgroundResource(R.drawable.bg_perfil_selected);
        btnInativo1.setBackgroundResource(R.drawable.bg_perfil_unselected);
        btnInativo2.setBackgroundResource(R.drawable.bg_perfil_unselected);

        txtAtivo.setTextColor(0xFFFF6B00);
        txtInativo1.setTextColor(0xFFFFD4B3);
        txtInativo2.setTextColor(0xFFFFD4B3);

        camposAtivo.setVisibility(View.VISIBLE);
        camposInativo1.setVisibility(View.GONE);
        camposInativo2.setVisibility(View.GONE);
    }
}