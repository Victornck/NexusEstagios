package com.example.estagioapp;

public class Atividade {

    private String id;
    private String titulo;
    private String descricao;
    private String data;
    private boolean concluida;

    private String alunoUid;
    private String alunoNome;

    private String supervisorUid;
    private String supervisorNome;

    private String criadoPor;

    private int horas;

    private long timestamp;

    // CONSTRUTOR VAZIO FIREBASE
    public Atividade() {
    }

    // CONSTRUTOR COMPLETO
    public Atividade(
            String id,
            String titulo,
            String descricao,
            String data,
            boolean concluida,
            String alunoUid,
            String alunoNome,
            String supervisorUid,
            String supervisorNome,
            String criadoPor,
            int horas,
            long timestamp
    ) {

        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.data = data;
        this.concluida = concluida;

        this.alunoUid = alunoUid;
        this.alunoNome = alunoNome;

        this.supervisorUid = supervisorUid;
        this.supervisorNome = supervisorNome;

        this.criadoPor = criadoPor;

        this.horas = horas;

        this.timestamp = timestamp;
    }

    // GETTERS

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getData() {
        return data;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public String getAlunoUid() {
        return alunoUid;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public String getSupervisorUid() {
        return supervisorUid;
    }

    public String getSupervisorNome() {
        return supervisorNome;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public int getHoras() {
        return horas;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // SETTERS

    public void setId(String id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    public void setAlunoUid(String alunoUid) {
        this.alunoUid = alunoUid;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public void setSupervisorUid(String supervisorUid) {
        this.supervisorUid = supervisorUid;
    }

    public void setSupervisorNome(String supervisorNome) {
        this.supervisorNome = supervisorNome;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
