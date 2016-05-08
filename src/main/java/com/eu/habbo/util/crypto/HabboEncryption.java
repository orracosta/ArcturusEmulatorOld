package com.eu.habbo.util.crypto;

import java.math.BigInteger;

class HabboEncryption
{
    private static final BigInteger n = new BigInteger("86851DD364D5C5CECE3C883171CC6DDC5760779B992482BD1E20DD296888DF91B33B936A7B93F06D29E8870F703A216257DEC7C81DE0058FEA4CC5116F75E6EFC4E9113513E45357DC3FD43D4EFAB5963EF178B78BD61E81A14C603B24C8BCCE0A12230B320045498EDC29282FF0603BC7B7DAE8FC1B05B52B2F301A9DC783B7", 16);
    private static final int e = 3;
    private static final BigInteger d = new BigInteger("59AE13E243392E89DED305764BDD9E92E4EAFA67BB6DAC7E1415E8C645B0950BCCD26246FD0D4AF37145AF5FA026C0EC3A94853013EAAE5FF1888360F4F9449EE023762EC195DFF3F30CA0B08B8C947E3859877B5D7DCED5C8715C58B53740B84E11FBC71349A27C31745FCEFEEEA57CFF291099205E230E0C7C27E8E1C0512B", 16);
    private Boolean Initialized;
    private final RC4 RC4;
    private final RSA RSA;
    private final DiffieHellman diffeHellman;
    
    public HabboEncryption()
    {
        this.RSA = new RSA(n, e, d, BigInteger.valueOf(0L), BigInteger.valueOf(0L), BigInteger.valueOf(0L), BigInteger.valueOf(0L), BigInteger.valueOf(0L));
        
        this.RC4 = new RC4();
        this.diffeHellman = new DiffieHellman(200);
        this.Initialized = false;
    }
    
    public Boolean InitializeRC4(String ctext)
    {
        try
        {
            String publickey = this.RSA.Decrypt(ctext);
            if (publickey == null) {
                return false;
            }
            this.diffeHellman.GenerateSharedKey(publickey.replace(Character.toString('\000'), ""));

            this.RC4.init(this.getSharedKey().getBytes());
            
            this.Initialized = true;
            
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
    
    public DiffieHellman getDiffieHellman()
    {
        return this.diffeHellman;
    }
    
    String getSharedKey()
    {
        return this.diffeHellman.SharedKey.toString();
    }
    
    public String getPublicKey()
    {
        return this.diffeHellman.PublicKey.toString();
    }
    
    public String getPrivateKey()
    {
        return this.diffeHellman.PrivateKey.toString();
    }
    
    public RC4 getRC4()
    {
        return this.RC4;
    }
}