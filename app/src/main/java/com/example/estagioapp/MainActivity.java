package com.example.estagioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView tvNomeUsuario;
    private TextView tvLetraUsuario;
    private TextView tvProgresso;
    private TextView tvHoras;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Se não está logado, vai pro login
        if (!FirebaseHelper.estaLogado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        tvNomeUsuario = findViewById(R.id.tv_nome_usuario);
        tvLetraUsuario = findViewById(R.id.tv_letra_usuario);
        tvProgresso    = findViewById(R.id.tv_progresso_percent);
        tvHoras        = findViewById(R.id.tv_horas_progresso);
        progressBar    = findViewById(R.id.progress_bar);

        tvLetraUsuario.setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));

        findViewById(R.id.nav_inicio).setOnClickListener(v -> {});
        findViewById(R.id.nav_vagas).setOnClickListener(v ->
                NavHelper.navigate(this, VagasActivity.class));
        findViewById(R.id.nav_atividades).setOnClickListener(v ->
                NavHelper.navigate(this, AtividadesActivity.class));
        findViewById(R.id.nav_perfil).setOnClickListener(v ->
                NavHelper.navigate(this, PerfilActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseHelper.estaLogado()) {
            carregarUsuario();
        }
    }

    private void carregarUsuario() {
        String uid = FirebaseHelper.getUidAtual();
        if (uid == null) return;

        FirebaseHelper.refUsuarios().child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        String nome = snapshot.child("nome").getValue(String.class);
                        if (nome == null) nome = "Usuário";

                        // Nome e avatar
                        tvNomeUsuario.setText(nome);
                        tvLetraUsuario.setText(
                                String.valueOf(nome.charAt(0)).toUpperCase());

                        // Salva localmente para outras telas
                        AppData.setNome(MainActivity.this, nome);

                        String curso = snapshot.child("curso").getValue(String.class);
                        if (curso != null) AppData.setCurso(MainActivity.this, curso);

                        String empresa = snapshot.child("empresa").getValue(String.class);
                        if (empresa != null) AppData.setEmpresa(MainActivity.this, empresa);

                        String supervisor = snapshot.child("supervisorUid").getValue(String.class);
                        if (supervisor != null)
                            AppData.setSupervisor(MainActivity.this, supervisor);

                        // Progresso
                        Long horasConc  = snapshot.child("horasConcluidas").getValue(Long.class);
                        Long horasTotal = snapshot.child("horasTotal").getValue(Long.class);

                        long hConc  = horasConc  != null ? horasConc  : 0;
                        long hTotal = horasTotal != null ? horasTotal : 600;
                        int  perc   = hTotal > 0 ? (int) ((hConc * 100) / hTotal) : 0;

                        if (tvProgresso != null) tvProgresso.setText(perc + "%");
                        if (tvHoras != null)
                            tvHoras.setText(hConc + "h de " + hTotal + "h concluídas");
                        if (progressBar != null) progressBar.setProgress(perc);

                        // Salva horas localmente
                        AppData.setHorasConcluidas(MainActivity.this, String.valueOf(hConc));
                        AppData.setHorasTotal(MainActivity.this, String.valueOf(hTotal));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
    }
}