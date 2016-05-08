package com.eu.habbo.util.crypto;

public class RC4
{
    /*
    private int i = 0;
    private int j = 0;
    private int[] Table;
    
    public RC4()
    {
        this.Table = new int[256];
    }
    
    public RC4(byte[] key)
    {
        this.Table = new int[256];
        
        init(key);
    }
    
    public void init(byte[] key)
    {
        int k = key.length;
        this.i = 0;
        while (this.i < 256)
        {
            this.Table[this.i] = this.i;
            this.i += 1;
        }
        this.i = 0;
        this.j = 0;
        while (this.i < 256)
        {
            this.j = ((this.j + this.Table[this.i] + key[(this.i % k)]) % 256);
            Swap(this.i, this.j);
            this.i += 1;
        }
        this.i = 0;
        this.j = 0;
    }
    
    public void Swap(int a, int b)
    {
        int k = this.Table[a];
        this.Table[a] = this.Table[b];
        this.Table[b] = k;
    }
    
    public byte[] parse(byte[] bytes)
    {
        int k = 0;
        byte[] result = new byte[bytes.length];
        int pos = 0;
        for (int a = 0; a < bytes.length; a++)
        {
            this.i = ((this.i + 1) % 256);
            this.j = ((this.j + this.Table[this.i]) % 256);
            Swap(this.i, this.j);
            k = (this.Table[this.i] + this.Table[this.j]) % 256;
            result[(pos++)] = ((byte)(bytes[a] ^ this.Table[k]));
        }
        return result;
    }*/

    private int i = 0;
    private int j = 0;
    private final int[] Table = new int[256];
    public void init(byte[] a)
    {
            int k = a.length;
            this.i = 0;
            while (this.i < 256)
            {
                    this.Table[this.i] = this.i;
                    this.i++;
            }
            this.j = 0;
            this.i = 0;
            while (this.i < 256)
            {
                    this.j = ((this.j + this.Table[this.i]) + (a[this.i % k] & 0xff)) % 256;
                    this.Swamp(this.i, this.j);
                    this.i++;
            }
            this.i = 0;
            this.j = 0;
    }

    public byte[] parse(byte[] b) // encrypt and decrypt
    {
            for(int a = 0;a<b.length;a++)
            {
                    this.i = (this.i + 1) % 256;
                    this.j = (this.j + this.Table[this.i]) % 256;
                    this.Swamp(this.i, this.j);
                    b[a] = (byte) ((b[a] & 0xff) ^ this.Table[(this.Table[this.i] + this.Table[this.j]) % 256]);
            }
            return b;
    }

    private void Swamp(int a1, int a2)
    {
            int k = this.Table[a1];
            this.Table[a1] = this.Table[a2];
            this.Table[a2] = k;
    }
}