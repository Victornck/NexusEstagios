package com.example.estagioapp;

import android.content.Context;
import android.content.SharedPreferences;

public class AppData {

    private static final String PREFS = "estagioapp_data";

    // ───────────────── GETTERS ─────────────────

    public static String getNome(Context ctx) {
        return get(ctx, "nome", "Lucas Almeida");
    }

    public static String getCurso(Context ctx) {
        return get(ctx, "curso", "Ciência da Computação · 6° sem");
    }

    public static String getEstagio(Context ctx) {
        return get(ctx, "estagio", "Estagiando em Nova Tech");
    }

    public static String getSupervisor(Context ctx) {
        return get(ctx, "supervisor", "Profa. Marina Costa");
    }

    public static String getEmpresa(Context ctx) {
        return get(ctx, "empresa", "Nova Tech · Front-end");
    }

    public static String getHorasConcluidas(Context ctx) {
        return get(ctx, "horas_concluidas", "408");
    }

    public static String getHorasTotal(Context ctx) {
        return get(ctx, "horas_total", "600");
    }

    public static int getCurriculosCount(Context ctx) {
        return getInt(ctx, "curriculos", 3);
    }

    public static int getAtividadesConcluidas(Context ctx) {
        return getInt(ctx, "ativ_concluidas", 28);
    }

    public static int getAtividadesPendentes(Context ctx) {
        return getInt(ctx, "ativ_pendentes", 4);
    }

    public static String getPerfilLogado(Context ctx) {
        return get(ctx, "perfil_logado", "aluno");
    }

    public static String getEmailLogado(Context ctx) {
        return get(ctx, "email_logado", "");
    }

    // ───── DADOS DO CURRÍCULO ─────

    public static String getCurriculoEmail(Context ctx) {
        return get(ctx, "curriculo_email", "");
    }

    public static String getCurriculoTelefone(Context ctx) {
        return get(ctx, "curriculo_telefone", "");
    }

    public static String getCurriculoPretensao(Context ctx) {
        return get(ctx, "curriculo_pretensao", "");
    }

    public static String getCurriculoMensagem(Context ctx) {
        return get(ctx, "curriculo_mensagem", "");
    }

    public static boolean getCurriculoExperiencia(Context ctx) {
        return prefs(ctx).getBoolean("curriculo_experiencia", false);
    }

    // ───────────────── SETTERS ─────────────────

    public static void setNome(Context ctx, String v) {
        set(ctx, "nome", v);
    }

    public static void setCurso(Context ctx, String v) {
        set(ctx, "curso", v);
    }

    public static void setEstagio(Context ctx, String v) {
        set(ctx, "estagio", v);
    }

    public static void setSupervisor(Context ctx, String v) {
        set(ctx, "supervisor", v);
    }

    public static void setEmpresa(Context ctx, String v) {
        set(ctx, "empresa", v);
    }

    public static void setHorasConcluidas(Context ctx, String v) {
        set(ctx, "horas_concluidas", v);
    }

    public static void setHorasTotal(Context ctx, String v) {
        set(ctx, "horas_total", v);
    }

    public static void setCurriculoCount(Context ctx, int v) {
        setInt(ctx, "curriculos", v);
    }

    public static void setAtividadesConcluidas(Context ctx, int v) {
        setInt(ctx, "ativ_concluidas", v);
    }

    public static void setAtividadesPendentes(Context ctx, int v) {
        setInt(ctx, "ativ_pendentes", v);
    }

    public static void setPerfilLogado(Context ctx, String perfil) {
        set(ctx, "perfil_logado", perfil);
    }

    public static void setEmailLogado(Context ctx, String email) {
        set(ctx, "email_logado", email);
    }

    // ───── CURRÍCULO ─────

    public static void setCurriculoEmail(Context ctx, String v) {
        set(ctx, "curriculo_email", v);
    }

    public static void setCurriculoTelefone(Context ctx, String v) {
        set(ctx, "curriculo_telefone", v);
    }

    public static void setCurriculoPretensao(Context ctx, String v) {
        set(ctx, "curriculo_pretensao", v);
    }

    public static void setCurriculoMensagem(Context ctx, String v) {
        set(ctx, "curriculo_mensagem", v);
    }

    public static void setCurriculoExperiencia(Context ctx, boolean v) {
        prefs(ctx)
                .edit()
                .putBoolean("curriculo_experiencia", v)
                .apply();
    }

    // ───── SALVAR CURRÍCULO COMPLETO ─────

    public static void salvarCurriculo(
            Context ctx,
            String nome,
            String email,
            String telefone,
            String pretensao,
            String mensagem,
            boolean experiencia
    ) {

        setNome(ctx, nome);
        setCurriculoEmail(ctx, email);
        setCurriculoTelefone(ctx, telefone);
        setCurriculoPretensao(ctx, pretensao);
        setCurriculoMensagem(ctx, mensagem);
        setCurriculoExperiencia(ctx, experiencia);

        int total = getCurriculosCount(ctx);

        setCurriculoCount(ctx, total + 1);
    }

    // ───── SALVAR PERFIL COMPLETO ─────

    public static void salvarPerfil(
            Context ctx,
            String nome,
            String curso,
            String estagio,
            String empresa,
            String supervisor
    ) {

        setNome(ctx, nome);
        setCurso(ctx, curso);
        setEstagio(ctx, estagio);
        setEmpresa(ctx, empresa);
        setSupervisor(ctx, supervisor);
    }

    // ───────────────── INTERNOS ─────────────────

    private static String get(
            Context ctx,
            String key,
            String def
    ) {

        return prefs(ctx).getString(key, def);
    }

    private static int getInt(
            Context ctx,
            String key,
            int def
    ) {

        return prefs(ctx).getInt(key, def);
    }

    private static void set(
            Context ctx,
            String key,
            String value
    ) {

        prefs(ctx)
                .edit()
                .putString(key, value)
                .apply();
    }

    private static void setInt(
            Context ctx,
            String key,
            int value
    ) {

        prefs(ctx)
                .edit()
                .putInt(key, value)
                .apply();
    }

    private static SharedPreferences prefs(Context ctx) {

        return ctx.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        );
    }
}