package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

        btnAluno.setOnClickListener(v -> {

            tipoSelecionado = "aluno";

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
        });

        btnSupervisor.setOnClickListener(v -> {

            tipoSelecionado = "supervisor";

            selecionarTipo(
                    btnSupervisor,
                    btnAluno,
                    btnEmpresa,
                    txtSupervisor,
                    txtAluno,
                    txtEmpresa,
                    camposSupervisor,
                    camposAluno,
                    camposEmpresa
            );
        });

        btnEmpresa.setOnClickListener(v -> {

            tipoSelecionado = "empresa";

            selecionarTipo(
                    btnEmpresa,
                    btnAluno,
                    btnSupervisor,
                    txtEmpresa,
                    txtAluno,
                    txtSupervisor,
                    camposEmpresa,
                    camposAluno,
                    camposSupervisor
            );
        });

        findViewById(R.id.btn_voltar)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btn_cadastrar)
                .setOnClickListener(v -> cadastrarUsuario());

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

        MascaraUtil.cpf(findViewById(R.id.et_cpf));
        MascaraUtil.data(findViewById(R.id.et_nascimento));
        MascaraUtil.telefone(findViewById(R.id.et_telefone));
        MascaraUtil.cnpj(findViewById(R.id.et_cnpj));
    }

    private void cadastrarUsuario() {

        EditText etNome           = findViewById(R.id.et_nome);
        EditText etCpf            = findViewById(R.id.et_cpf);
        EditText etNascimento     = findViewById(R.id.et_nascimento);
        EditText etTelefone       = findViewById(R.id.et_telefone);
        EditText etEmail          = findViewById(R.id.et_email);
        EditText etSenha          = findViewById(R.id.et_senha);
        EditText etConfirmarSenha = findViewById(R.id.et_confirmar_senha);

        String nome           = etNome.getText().toString().trim();
        String cpf            = etCpf.getText().toString().trim();
        String nascimento     = etNascimento.getText().toString().trim();
        String telefone       = etTelefone.getText().toString().trim();
        String email          = etEmail.getText().toString().trim();
        String senha          = etSenha.getText().toString().trim();
        String confirmarSenha = etConfirmarSenha.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Obrigatório");
            return;
        }

        if (cpf.isEmpty()) {
            etCpf.setError("Obrigatório");
            return;
        }
        if (nascimento.isEmpty()) {
            etNascimento.setError("Obrigatório");
            return;
        }

        if (!maiorDe16(nascimento)) {
            etNascimento.setError("Necessário ter 16 anos ou mais");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Obrigatório");
            return;
        }

        if (senha.length() < 6) {
            etSenha.setError("Mínimo 6 caracteres");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            etConfirmarSenha.setError("Senhas não conferem");
            return;
        }

        Map<String, Object> dadosExtras = new HashMap<>();

        // =========================
        // ALUNO
        // =========================

        if (tipoSelecionado.equals("aluno")) {

            EditText etCurso =
                    findViewById(R.id.et_curso);

            EditText etSemestre =
                    findViewById(R.id.et_semestre);

            EditText etCodigoSupervisor =
                    findViewById(R.id.et_codigo_supervisor);

            String curso =
                    etCurso.getText().toString().trim();

            String semestre =
                    etSemestre.getText().toString().trim();

            String codigoSupervisor =
                    etCodigoSupervisor.getText().toString().trim().toUpperCase();

            if (curso.isEmpty()) {
                etCurso.setError("Obrigatório");
                return;
            }

            if (semestre.isEmpty()) {
                etSemestre.setError("Obrigatório");
                return;
            }

            if (!semestre.matches("\\d{1,2}")) {
                etSemestre.setError("Máximo 2 números");
                return;
            }

            if (codigoSupervisor.isEmpty()) {
                etCodigoSupervisor.setError("Informe o código");
                return;
            }

            dadosExtras.put(
                    "curso",
                    curso + " · " + semestre + "° sem"
            );

            validarSupervisorERegistrar(
                    codigoSupervisor,
                    nome,
                    cpf,
                    nascimento,
                    telefone,
                    email,
                    senha,
                    dadosExtras
            );

            return;
        }

        // =========================
        // SUPERVISOR
        // =========================

        if (tipoSelecionado.equals("supervisor")) {

            EditText etMatricula =
                    findViewById(R.id.et_matricula);

            EditText etInstituicao =
                    findViewById(R.id.et_instituicao);

            String matricula =
                    etMatricula.getText().toString().trim();

            String instituicao =
                    etInstituicao.getText().toString().trim();

            if (matricula.isEmpty()) {
                etMatricula.setError("Obrigatório");
                return;
            }

            if (instituicao.isEmpty()) {
                etInstituicao.setError("Obrigatório");
                return;
            }

            String codigoSupervisor =
                    gerarCodigoSupervisor();

            dadosExtras.put("matricula", matricula);
            dadosExtras.put("instituicao", instituicao);
            dadosExtras.put("codigoSupervisor", codigoSupervisor);
        }

        // =========================
        // EMPRESA
        // =========================

        if (tipoSelecionado.equals("empresa")) {

            EditText etCnpj =
                    findViewById(R.id.et_cnpj);

            EditText etRazaoSocial =
                    findViewById(R.id.et_razao_social);

            String cnpj =
                    etCnpj.getText().toString().trim();

            String razaoSocial =
                    etRazaoSocial.getText().toString().trim();

            if (cnpj.isEmpty()) {
                etCnpj.setError("Obrigatório");
                return;
            }

            if (razaoSocial.isEmpty()) {
                etRazaoSocial.setError("Obrigatório");
                return;
            }

            dadosExtras.put("cnpj", cnpj);
            dadosExtras.put("razaoSocial", razaoSocial);
        }

        registrarUsuario(
                nome,
                cpf,
                nascimento,
                telefone,
                email,
                senha,
                dadosExtras
        );
    }

    private void validarSupervisorERegistrar(
            String codigoSupervisor,
            String nome,
            String cpf,
            String nascimento,
            String telefone,
            String email,
            String senha,
            Map<String, Object> dadosExtras
    ) {

        FirebaseHelper.refUsuarios()
                .get()
                .addOnSuccessListener(snapshot -> {

                    String supervisorUid = null;
                    String supervisorNome = null;

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        String perfil =
                                ds.child("perfil")
                                        .getValue(String.class);

                        String codigo =
                                ds.child("codigoSupervisor")
                                        .getValue(String.class);

                        if ("supervisor".equals(perfil)
                                && codigoSupervisor.equals(codigo)) {

                            supervisorUid = ds.getKey();

                            supervisorNome =
                                    ds.child("nome")
                                            .getValue(String.class);

                            break;
                        }
                    }

                    if (supervisorUid == null) {

                        Toast.makeText(
                                this,
                                "Código do supervisor inválido",
                                Toast.LENGTH_LONG
                        ).show();

                        return;
                    }

                    dadosExtras.put("supervisorUid", supervisorUid);
                    dadosExtras.put("supervisorNome", supervisorNome);
                    dadosExtras.put("codigoSupervisor", codigoSupervisor);

                    registrarUsuario(
                            nome,
                            cpf,
                            nascimento,
                            telefone,
                            email,
                            senha,
                            dadosExtras
                    );
                });
    }

    private void registrarUsuario(
            String nome,
            String cpf,
            String nascimento,
            String telefone,
            String email,
            String senha,
            Map<String, Object> dadosExtras
    ) {

        findViewById(R.id.btn_cadastrar)
                .setEnabled(false);

        ((TextView) findViewById(R.id.btn_cadastrar))
                .setText("Criando conta...");

        FirebaseHelper.getAuth()
                .createUserWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {

                    String uid =
                            FirebaseHelper.getUidAtual();

                    Map<String, Object> usuario =
                            new HashMap<>();

                    usuario.put("nome", nome);
                    usuario.put("email", email);
                    usuario.put("cpf", cpf);
                    usuario.put("nascimento", nascimento);
                    usuario.put("telefone", telefone);
                    usuario.put("perfil", tipoSelecionado);

                    usuario.putAll(dadosExtras);

                    FirebaseHelper.refUsuarios()
                            .child(uid)
                            .setValue(usuario)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        "Conta criada com sucesso!",
                                        Toast.LENGTH_SHORT
                                ).show();

                                startActivity(
                                        new Intent(
                                                this,
                                                LoginActivity.class
                                        )
                                );

                                finishAffinity();
                            });
                })
                .addOnFailureListener(e -> {

                    findViewById(R.id.btn_cadastrar)
                            .setEnabled(true);

                    ((TextView) findViewById(R.id.btn_cadastrar))
                            .setText("Criar conta");

                    Toast.makeText(
                            this,
                            e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String gerarCodigoSupervisor() {

        Random random = new Random();

        int numero = 1000 + random.nextInt(9000);

        return "SUP-" + numero;
    }

    private void selecionarTipo(
            LinearLayout btnAtivo,
            LinearLayout btnInativo1,
            LinearLayout btnInativo2,
            TextView txtAtivo,
            TextView txtInativo1,
            TextView txtInativo2,
            LinearLayout camposAtivo,
            LinearLayout camposInativo1,
            LinearLayout camposInativo2
    ) {

        btnAtivo.setBackgroundResource(
                R.drawable.bg_perfil_selected
        );

        btnInativo1.setBackgroundResource(
                R.drawable.bg_perfil_unselected
        );

        btnInativo2.setBackgroundResource(
                R.drawable.bg_perfil_unselected
        );

        txtAtivo.setTextColor(0xFFFF6B00);

        txtInativo1.setTextColor(0xFFFFD4B3);

        txtInativo2.setTextColor(0xFFFFD4B3);

        camposAtivo.setVisibility(View.VISIBLE);

        camposInativo1.setVisibility(View.GONE);

        camposInativo2.setVisibility(View.GONE);
    }

    private boolean maiorDe16(String dataNascimento) {

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Calendar nascimento =
                    Calendar.getInstance();

            nascimento.setTime(
                    sdf.parse(dataNascimento)
            );

            Calendar hoje =
                    Calendar.getInstance();

            int idade =
                    hoje.get(Calendar.YEAR)
                            - nascimento.get(Calendar.YEAR);

            if (hoje.get(Calendar.DAY_OF_YEAR)
                    < nascimento.get(Calendar.DAY_OF_YEAR)) {

                idade--;
            }

            return idade >= 16;

        } catch (ParseException e) {

            return false;
        }
    }
}