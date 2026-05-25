package com.example.estagioapp;

public class Vaga {
    private String id;
    private String titulo;
    private String empresa;
    private String local;
    private String modalidade;
    private String carga;
    private String area;
    private String status;

    public Vaga() {} // obrigatório para Firebase

    // Getters
    public String getId()         { return id; }
    public String getTitulo()     { return titulo; }
    public String getEmpresa()    { return empresa; }
    public String getLocal()      { return local; }
    public String getModalidade() { return modalidade; }
    public String getCarga()      { return carga; }
    public String getArea()       { return area; }
    public String getStatus()     { return status; }

    // Setters
    public void setId(String id)             { this.id = id; }
    public void setTitulo(String titulo)     { this.titulo = titulo; }
    public void setEmpresa(String empresa)   { this.empresa = empresa; }
    public void setLocal(String local)       { this.local = local; }
    public void setModalidade(String m)      { this.modalidade = m; }
    public void setCarga(String carga)       { this.carga = carga; }
    public void setArea(String area)         { this.area = area; }
    public void setStatus(String status)     { this.status = status; }
}