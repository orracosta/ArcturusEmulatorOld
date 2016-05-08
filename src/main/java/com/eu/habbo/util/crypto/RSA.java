package com.eu.habbo.util.crypto;

import java.math.BigInteger;

class RSA
{
  private final int e;
  private final BigInteger n;
  private final BigInteger d;
  private final BigInteger p;
  private final BigInteger q;
  private final BigInteger dmp1;
  private final BigInteger dmq1;
  private final BigInteger coeff;
  private final boolean canDecrypt;
  private final boolean canEncrypt;
  private final BigInteger zero = new BigInteger("0");
  
  public RSA(BigInteger N, int E, BigInteger D, BigInteger P, BigInteger Q, BigInteger DP, BigInteger DQ, BigInteger C)
  {
    this.n = N;
    this.e = E;
    this.d = D;
    this.p = P;
    this.q = Q;
    this.dmp1 = DP;
    this.dmq1 = DQ;
    this.coeff = C;
    
    this.canEncrypt = ((this.n != null) && (this.n != this.zero) && (this.e != 0));
    this.canDecrypt = ((this.canEncrypt) && (this.d != this.zero) && (this.d != null));
  }
  
  int GetBlockSize()
  {
    return (this.n.bitLength() + 7) / 8;
  }
  
  BigInteger DoPublic(BigInteger x)
  {
    if (this.canEncrypt) {
      return x.modPow(new BigInteger(this.e + ""), this.n);
    }
    return this.zero;
  }
  
  public String Encrypt(String text)
  {
    text.length();GetBlockSize();

    BigInteger m = new BigInteger(pkcs1pad2(text.getBytes(), GetBlockSize()));
    if (m.equals(this.zero)) {
      return null;
    }
    BigInteger c = DoPublic(m);
    if (c.equals(this.zero)) {
      return null;
    }
    String result = c.toString(16);
    if ((result.length() & 0x1) == 0) {
      return result;
    }
    return "0" + result;
  }
  
  private byte[] pkcs1pad2(byte[] data, int n)
  {
    byte[] bytes = new byte[n];
    int i = data.length - 1;
    while ((i >= 0) && (n > 11)) {
      bytes[(--n)] = data[(i--)];
    }
    bytes[(--n)] = 0;
    while (n > 2) {
      bytes[(--n)] = 1;
    }
    bytes[(--n)] = 2;
    bytes[(--n)] = 0;
    
    return bytes;
  }
  
  BigInteger DoPrivate(BigInteger x)
  {
    if (this.canDecrypt) {
      return x.modPow(this.d, this.n);
    }
    return this.zero;
  }
  
  public String Decrypt(String ctext)
  {
    BigInteger c = new BigInteger(ctext, 16);
    BigInteger m = DoPrivate(c);
    if (m.equals(this.zero)) {
      return null;
    }
    byte[] bytes = pkcs1unpad2(m, GetBlockSize());
    if (bytes == null) {
      return null;
    }
    return new String(bytes);
  }
  
  private byte[] pkcs1unpad2(BigInteger src, int n)
  {
    byte[] bytes = src.toByteArray();
    
    int i = 0;
    while ((i < bytes.length) && (bytes[i] == 0)) {
      i++;
    }
    if ((bytes.length - i != n - 1) || (bytes[i] > 2))
    {
      return null;
    }
    i++;
    while (bytes[i] != 0)
    {
      i++;
      if (i >= bytes.length)
      {
        return null;
      }
    }
    byte[] out = new byte[bytes.length - i + 1];
    int p = 0;
    do
    {
      out[(p++)] = bytes[i];i++;
    } while (i < bytes.length);
    return out;
  }
}



/* Location:           C:\Users\Admin\Desktop\HabboEmulator.jar

 * Qualified Name:     com.eu.habbo.util.crypto.RSA

 * JD-Core Version:    0.7.0.1

 */