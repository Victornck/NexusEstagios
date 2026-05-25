package com.example.estagioapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private static FirebaseAuth auth;

    private static DatabaseReference database;

    // AUTH

    public static FirebaseAuth getAuth() {

        if (auth == null) {

            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }

    // DATABASE

    public static DatabaseReference getDatabase() {

        if (database == null) {

            database =
                    FirebaseDatabase
                            .getInstance()
                            .getReference();
        }

        return database;
    }

    // USUÁRIO ATUAL

    public static FirebaseUser getUsuarioAtual() {

        return getAuth().getCurrentUser();
    }

    public static String getUidAtual() {

        FirebaseUser user =
                getUsuarioAtual();

        return user != null
                ? user.getUid()
                : null;
    }

    public static boolean estaLogado() {

        return getUsuarioAtual() != null;
    }

    // REFERÊNCIAS

    public static DatabaseReference refUsuarios() {

        return getDatabase()
                .child("usuarios");
    }

    public static DatabaseReference refVagas() {

        return getDatabase()
                .child("vagas");
    }

    public static DatabaseReference refCandidaturas() {

        return getDatabase()
                .child("candidaturas");
    }

    public static DatabaseReference refAtividades() {

        return getDatabase()
                .child("atividades");
    }
}