package com.desafio.integrados.criptografia.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Converter
public class AesCryptoConverter implements AttributeConverter<String, String> {

    @Value("${crypto.aes.key}")
    private String aesKey;

    private StandardPBEStringEncryptor encryptor;

    @PostConstruct
    public void init() {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(aesKey);
        encryptor.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
        encryptor.setIvGenerator(new org.jasypt.iv.RandomIvGenerator());
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptor.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return encryptor.decrypt(dbData);
        } catch (Exception e) {
            return dbData; 
        }
    }
}
