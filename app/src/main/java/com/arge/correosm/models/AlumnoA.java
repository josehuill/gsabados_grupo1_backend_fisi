package com.arge.correosm.models;

public class AlumnoA {
    String id;
    String name;
    String email;
    String addres;
    String codigo;

    public AlumnoA(String id, String name, String email, String addres, String codigo) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.addres = addres;
        this.codigo = codigo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAddres() {
        return addres;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddres(String addres) {
        this.addres = addres;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /*
    public AlumnoA(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

 */
}
