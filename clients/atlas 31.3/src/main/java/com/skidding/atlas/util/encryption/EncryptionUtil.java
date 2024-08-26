package com.skidding.atlas.util.encryption;

import com.skidding.atlas.util.minecraft.IMinecraft;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;


public final class EncryptionUtil implements IMinecraft {

    public static final EncryptionUtil INSTANCE = new EncryptionUtil();

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public String getKey(KeyLevel keyLevel) {
        return switch (keyLevel) {
            case LOW -> "AtlasKlientNaTopuBráško?DerBesteClientVonAllen!SmrtVšemNechutnýmUkrajincům.EsmuMelnsUnBīstams,KasEsEsmu?SponsoredByDrogiUsing:ReflectionBasedRandomPredictionVerteciesCalculationComputationLookupTableHandlerStringArrayLengthNumberSwitchGotoStringEnumReferenceTransformerV3";
            case MEDIUM -> STR."\{System.getProperty("user.name")}-Atlas-Párek-\{System.getProperty("os.name")}-\{System.getenv("COMPUTERNAME")}-\{System.getenv("PROCESSOR_IDENTIFIER")}-\{System.getenv("PROCESSOR_LEVEL")}-\{getKey(KeyLevel.LOW)}";
            default ->  STR."\{System.getProperty("user.name")} \{System.getenv("COMPUTERNAME")} \{System.getenv("os")} \{System.getProperty("os.name")} \{System.getenv("PROCESSOR_IDENTIFIER")} \{System.getProperty("os.arch")} \{System.getProperty("os.version")} \{System.getProperty("user.language")} \{System.getenv("SystemRoot")} \{System.getenv("HOMEDRIVE")} \{System.getenv("PROCESSOR_LEVEL")} \{System.getenv("PROCESSOR_REVISION")} \{System.getenv("PROCESSOR_IDENTIFIER")} \{System.getenv("PROCESSOR_ARCHITECTURE")} \{System.getenv("PROCESSOR_ARCHITEW6432")} \{System.getenv("NUMBER_OF_PROCESSORS")} Atlas-\{getKey(KeyLevel.MEDIUM)}";
        };
    }

    public String encrypt(String plainText, KeyLevel keyLevel) throws Exception {
        return encrypt(plainText, getKey(keyLevel));
    }

    public String decrypt(String encryptedText, KeyLevel keyLevel) throws Exception {
        return decrypt(encryptedText, getKey(keyLevel));
    }

    public static String encrypt(String plainText, String password) throws Exception {
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(cipherText);
    }

    public static String decrypt(String encryptedText, String password) throws Exception {
        SecretKey secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        return new String(decryptedBytes);
    }

    private static SecretKey generateKey(String password) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), password.getBytes(), ITERATIONS, KEY_LENGTH);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), ALGORITHM);
    }

    @RequiredArgsConstructor
    public enum KeyLevel {
        MAXIMUM("1!"), MEDIUM("2!"), LOW("3!");

        public final String name;

        @Override
        public String toString() {
            return name;
        }
    }

}
