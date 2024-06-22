package com.bsouza.entradaesaida01;

import java.io.Serializable;

public class Order implements Serializable {
    private String unit;
    private String name;
    private String date;
    private String time;
    private String description;
    private String notaFiscal;
    private String plantonista;
    private String status;
    private String orderId;
    private String imageUrl;  // Adicione este campo

    // Construtor padrão
    public Order() {
    }

    // Construtores, getters e setters
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(String notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public String getPlantonista() {
        return plantonista;
    }

    public void setPlantonista(String plantonista) {
        this.plantonista = plantonista;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
