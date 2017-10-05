package com.habboproject.server.protocol.security.exchange;

import java.math.BigInteger;

public class RSA {
    public BigInteger exponent; // public exponent
    public BigInteger n; // modulus
    // private key
    public BigInteger privateKey;
    // bools (can encrypt/decrypt?)
    public boolean decryptable;

    public boolean encry;

    private BigInteger zero = new BigInteger("0");

    public RSA() {

    }

    public void init() {
        this.n = new BigInteger("85F5D787695551F8899858DD52C1C19D080308C78C13FD9FBDCF6B8852B6FEF8A9380F5DEB39CC64F65321F94FE415F02E455A9A82B7E55AC3E9DF684347AA04A4A95B798A9C465042CB8EC95C91F0B68415E1A8CD9BCE1473D1397319295E0C7AA362E25992D83289FD4E2DAB39F794D4D779671DF18A898BFDCFE25CD0A5F1", 16);
        this.exponent = new BigInteger("3", 16);
        this.privateKey = new BigInteger("594E8FAF9B8E36A5B1103B3E372BD668B00205DA5D62A9152934F25AE1CF54A5C6255F93F22688434EE216A63542B94AC98391BC57254391D7F13F9AD7851C021703B238CD44EE121992AD950C020B899764A5FDDF9F09D459887AAA26BAAC08450FA6490243CAE1D7E69F372B6CAFE4C5BA0FBC095C9537E33EA795E6A848A3", 16);

        this.encry = (n != null && n != zero && exponent != zero);
        this.decryptable = (encry && privateKey != zero && privateKey != null);
    }

    public int getBlockSize() {
        return (n.bitLength() + 7) / 8;
    }

    public BigInteger doPublic(BigInteger x) {
        if (this.encry) {
            return x.modPow(new BigInteger(this.exponent + ""), this.n);
        }

        return zero;
    }

    public String encrypt(String text) {
        BigInteger m = new BigInteger(this.pkcs1pad2(text.getBytes(), this.getBlockSize()));

        if (m.equals(zero)) {
            return null;
        }

        BigInteger c = this.doPublic(m);

        if (c.equals(zero)) {
            return null;
        }

        String result = c.toString(16);

        if ((result.length() & 1) == 0) {
            return result;
        }

        return "0" + result;
    }

    public String sign(String text) {
        BigInteger m = new BigInteger(this.pkcs1pad2(text.getBytes(), this.getBlockSize()));

        if (m.equals(zero)) {
            return null;
        }

        BigInteger c = this.doPrivate(m);

        if (c.equals(zero)) {
            return null;
        }

        String result = c.toString(16);

        if ((result.length() & 1) == 0) {
            return result;
        }

        return result;
    }

    private byte[] pkcs1pad2(byte[] data, int n) {
        byte[] bytes = new byte[n];

        int i = data.length - 1;

        while (i >= 0 && n > 11) {
            bytes[--n] = data[i--];
        }

        bytes[--n] = 0;

        while (n > 2) {
            bytes[--n] = (byte) 0xFF;
        }

        bytes[--n] = (byte) 1;
        bytes[--n] = 0;

        return bytes;
    }

    public BigInteger doPrivate(BigInteger x) {
        if (this.decryptable) {
            return x.modPow(this.privateKey, this.n);
        }

        return zero;
    }

    public String decrypt(String ctext) {
        BigInteger c = new BigInteger(ctext, 16);
        BigInteger m = this.doPrivate(c);

        if (m.equals(zero)) {
            return null;
        }

        byte[] bytes = this.pkcs1unpad2(m, this.getBlockSize());

        if (bytes == null) {
            return null;
        }

        return new String(bytes);
    }

    /**
     * @param src
     * @param n
     * @return
     */
    private byte[] pkcs1unpad2(BigInteger src, int n) {
        byte[] bytes = src.toByteArray();
        byte[] out;
        int i = 0;

        while (i < bytes.length && bytes[i] == 0) {
            ++i;
        }

        if (bytes.length - i != n - 1 || bytes[i] > 2) {
            return null;
        }

        ++i;

        while (bytes[i] != 0) {
            if (++i >= bytes.length) {
                return null;
            }
        }

        out = new byte[(bytes.length - i) + 1];
        int p = 0;

        while (++i < bytes.length) {
            out[p++] = (bytes[i]);
        }

        return out;
    }
}