package com.example.estagioapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DetalhesAlunoActivity extends AppCompatActivity {

    private TextView tvNome;
    private TextView tvCurso;
    private RecyclerView recycler;

    private final List<Atividade> lista =
            new ArrayList<>();

    private AtividadesAdapter adapter;

    private String alunoUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_aluno);

        alunoUid =
                getIntent().getStringExtra("alunoUid");

        tvNome = findViewById(R.id.tv_nome_aluno);
        tvCurso = findViewById(R.id.tv_curso_aluno);
        recycler = findViewById(R.id.recycler_atividades);

        recycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AtividadesAdapter(
                lista,
                FirebaseHelper.getDatabase().child("atividades")
        );

        recycler.setAdapter(adapter);

        carregarAluno();

        carregarAtividades();
    }

    private void carregarAluno() {

        FirebaseHelper.refUsuarios()
                .child(alunoUid)
                .addListenerForSingleValueEvent(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                String nome =
                                        snapshot.child("nome")
                                                .getValue(String.class);

                                String curso =
                                        snapshot.child("curso")
                                                .getValue(String.class);

                                tvNome.setText(nome);
                                tvCurso.setText(curso);
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {
                            }
                        });
    }

    private void carregarAtividades() {

        FirebaseHelper.getDatabase()
                .child("atividades")
                .addValueEventListener(
                        new ValueEventListener() {

                            @Override
                            public void onDataChange(
                                    @NonNull DataSnapshot snapshot
                            ) {

                                lista.clear();

                                for (DataSnapshot ds :
                                        snapshot.getChildren()) {

                                    Atividade atividade =
                                            ds.getValue(Atividade.class);

                                    if (atividade == null)
                                        continue;

                                    if (!alunoUid.equals(
                                            atividade.getAlunoUid()
                                    )) {
                                        continue;
                                    }

                                    atividade.setId(ds.getKey());

                                    lista.add(atividade);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(
                                    @NonNull DatabaseError error
                            ) {
                            }
                        });
    }
}