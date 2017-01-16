package sample.service;

import lombok.Getter;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by maciek on 07.01.17.
 */
@Getter
public class RSAService {
    BigInteger n, d, k;
    public String encrypt(String message, BigInteger k, BigInteger n) {
        return (new BigInteger(message.getBytes())).modPow(k, n).toString();
    }

    public String encrypt(String message) {
        return (new BigInteger(message.getBytes())).modPow(k, n).toString();
    }

    public String decrypt(String message, BigInteger d, BigInteger n) {
        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }

    public String decrypt(String message) {
        return new String((new BigInteger(message)).modPow(d, n).toByteArray());
    }

    public void generateKeys() {
        SecureRandom r = new SecureRandom();
        BigInteger p = new BigInteger(1024 / 2, 100, r);
        BigInteger q = new BigInteger(1024 / 2, 100, r);
        n = p.multiply(q);
        BigInteger m = (p.subtract(BigInteger.ONE)).multiply(q
                .subtract(BigInteger.ONE));
        k = new BigInteger("3");
        while (m.gcd(k).intValue() > 1) {
            k = k.add(new BigInteger("2"));
        }
        d = k.modInverse(m);
    }
}
