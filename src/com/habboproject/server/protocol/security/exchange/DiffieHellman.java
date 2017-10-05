package com.habboproject.server.protocol.security.exchange;

import java.math.BigInteger;
import java.util.Random;


public class DiffieHellman {
    public int BITLENGTH = 30;

    private BigInteger prime;
    private BigInteger generator;

    private BigInteger privateKey;
    private BigInteger publicKey;

    private BigInteger publicClientKey;

    private BigInteger sharedKey;

    public DiffieHellman() {
        this.initDH();
    }

    public DiffieHellman(int b) {
        this.BITLENGTH = b;

        this.initDH();
    }

    public DiffieHellman(BigInteger prime, BigInteger generator) {
        this.prime = prime;
        this.generator = generator;

        this.privateKey = new BigInteger(generateRandomHexString(BITLENGTH), 16);

        if (this.generator.intValue() > this.prime.intValue()) {
            BigInteger temp = this.prime;
            this.prime = this.generator;
            this.generator = temp;
        }

        this.publicKey = this.generator.modPow(this.privateKey, this.prime);
    }

    public static String generateRandomHexString(int len) {
        int rand = 0;
        String result = "";

        Random rnd = new Random();

        for (int i = 0; i < len; i++) {
            rand = 1 + (int) (rnd.nextDouble() * 254); // 1 - 255
            result += Integer.toString(rand, 16);
        }
        return result;
    }

    private void initDH() {
        this.publicKey = BigInteger.valueOf(0);
        Random random = new Random();
        while (this.publicKey.intValue() == 0) {
            this.prime = BigInteger.probablePrime(BITLENGTH, random);
            this.generator = BigInteger.probablePrime(BITLENGTH, random);

            this.privateKey = new BigInteger(generateRandomHexString(BITLENGTH), 16);

            if (this.privateKey.intValue() < 1) {
                continue;
            }

            if (this.generator.intValue() > this.prime.intValue()) {
                BigInteger temp = this.prime;
                this.prime = this.generator;
                this.generator = temp;
            }

            this.publicKey = this.generator.modPow(this.privateKey, this.prime);
        }
    }

    public void generateSharedKey(String ckey) {
        this.publicClientKey = new BigInteger(ckey);

        this.sharedKey = this.publicClientKey.modPow(this.privateKey, this.prime);
    }

    public BigInteger getPrime() {
        return prime;
    }

    public BigInteger getGenerator() {
        return generator;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    public BigInteger getPublicKey() {
        return publicKey;
    }

    public BigInteger getPublicClientKey() {
        return publicClientKey;
    }

    public BigInteger getSharedKey() {
        return sharedKey;
    }
}