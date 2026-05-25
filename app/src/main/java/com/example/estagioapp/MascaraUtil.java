package com.example.estagioapp;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MascaraUtil {

    // Aplica qualquer máscara a um EditText
    public static void aplicar(final EditText campo, final String mascara) {
        campo.addTextChangedListener(new TextWatcher() {

            boolean editando = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editando) return;
                editando = true;

                // Remove tudo que não é dígito
                String digits = s.toString().replaceAll("[^\\d]", "");

                StringBuilder resultado = new StringBuilder();
                int i = 0;

                for (char m : mascara.toCharArray()) {
                    if (i >= digits.length()) break;
                    if (m == '#') {
                        resultado.append(digits.charAt(i));
                        i++;
                    } else {
                        resultado.append(m);
                    }
                }

                campo.setText(resultado.toString());
                campo.setSelection(resultado.length());

                editando = false;
            }
        });
    }

    // Máscaras prontas
    public static void cpf(EditText campo) {
        aplicar(campo, "###.###.###-##");
    }

    public static void telefone(EditText campo) {
        // Detecta celular (9 dígitos) ou fixo (8 dígitos) dinamicamente
        campo.addTextChangedListener(new TextWatcher() {

            boolean editando = false;

            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editando) return;
                editando = true;

                String digits = s.toString().replaceAll("[^\\d]", "");

                String mascara = digits.length() <= 10
                        ? "(##) ####-####"
                        : "(##) #####-####";

                StringBuilder resultado = new StringBuilder();
                int i = 0;

                for (char m : mascara.toCharArray()) {
                    if (i >= digits.length()) break;
                    if (m == '#') {
                        resultado.append(digits.charAt(i));
                        i++;
                    } else {
                        resultado.append(m);
                    }
                }

                campo.setText(resultado.toString());
                campo.setSelection(resultado.length());

                editando = false;
            }
        });
    }

    public static void data(EditText campo) {
        aplicar(campo, "##/##/####");
    }

    public static void cnpj(EditText campo) {
        aplicar(campo, "##.###.###/####-##");
    }
}