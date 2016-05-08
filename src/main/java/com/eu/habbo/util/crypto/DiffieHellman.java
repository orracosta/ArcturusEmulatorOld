package com.eu.habbo.util.crypto;

import java.math.BigInteger;
import java.util.Random;

public class DiffieHellman
{
    private int BITLENGTH = 30;
    private static BigInteger Prime = new BigInteger("114670925920269957593299136150366957983142588366300079186349531");
    private static BigInteger Generator = new BigInteger("1589935137502239924254699078669119674538324391752663931735947");
    public BigInteger PrivateKey;
    public BigInteger PublicKey;
    private BigInteger PublicClientKey;
    public BigInteger SharedKey;
    
    public DiffieHellman()
    {
        InitDH();
    }
    
    public DiffieHellman(int b)
    {
        this.BITLENGTH = b;
        
        InitDH();
    }
    
    private void InitDH()
    {
        this.PublicKey = BigInteger.valueOf(0L);
        while (this.PublicKey.intValue() == 0)
        {

            this.PrivateKey = new BigInteger(GenerateRandomHexString(this.BITLENGTH), 16);
            if (Generator.intValue() > Prime.intValue())
            {
                BigInteger temp = Prime;
                Prime = Generator;
                Generator = temp;
            }
            this.PublicKey = Generator.modPow(this.PrivateKey, Prime);
        }
    }
    
    public DiffieHellman(BigInteger prime, BigInteger generator)
    {
        Prime = prime;
        Generator = generator;
        
        this.PrivateKey = new BigInteger(GenerateRandomHexString(this.BITLENGTH), 16);
        if (Generator.intValue() > Prime.intValue())
        {
            BigInteger temp = Prime;
            Prime = Generator;
            Generator = temp;
        }
        this.PublicKey = Generator.modPow(this.PrivateKey, Prime);
    }
    
    public void GenerateSharedKey(String ckey)
    {
        this.PublicClientKey = new BigInteger(ckey, 10);
        
        this.SharedKey = this.PublicClientKey.modPow(this.PrivateKey, Prime);
    }
    
    private static String GenerateRandomHexString(int len)
    {
        int rand = 0;
        String result = "";
        
        Random rnd = new Random();
        for (int i = 0; i < len; i++)
        {
            rand = 1 + (int)(rnd.nextDouble() * 254.0D);
            result = result + Integer.toString(rand, 16);
        }
        return result;
    }
}