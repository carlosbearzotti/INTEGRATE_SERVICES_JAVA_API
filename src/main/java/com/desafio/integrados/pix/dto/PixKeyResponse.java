package com.desafio.integrados.pix.dto;

public class PixKeyResponse {

    private Long id;
    private String keyValue;
    private String keyType;

    public PixKeyResponse() {}

    public PixKeyResponse(Long id, String keyValue, String keyType) {
        this.id = id;
        this.keyValue = keyValue;
        this.keyType = keyType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getKeyValue() { return keyValue; }
    public void setKeyValue(String keyValue) { this.keyValue = keyValue; }
    public String getKeyType() { return keyType; }
    public void setKeyType(String keyType) { this.keyType = keyType; }
}
