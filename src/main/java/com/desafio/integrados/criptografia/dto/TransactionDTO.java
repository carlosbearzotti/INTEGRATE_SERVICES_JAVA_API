package com.desafio.integrados.criptografia.dto;

public class TransactionDTO {
    private Long id;
    private Long userId;
    private String userDocument;
    private String creditCardToken;
    private Long value;

    public TransactionDTO() {
    }

    public TransactionDTO(Long id, String userDocument, String creditCardToken, Long value) {
        this(id, null, userDocument, creditCardToken, value);
    }

    public TransactionDTO(Long id, Long userId, String userDocument, String creditCardToken, Long value) {
        this.id = id;
        this.userId = userId;
        this.userDocument = userDocument;
        this.creditCardToken = creditCardToken;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserDocument() {
        return userDocument;
    }

    public void setUserDocument(String userDocument) {
        this.userDocument = userDocument;
    }

    public String getCreditCardToken() {
        return creditCardToken;
    }

    public void setCreditCardToken(String creditCardToken) {
        this.creditCardToken = creditCardToken;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
