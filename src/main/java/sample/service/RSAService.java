package sample.service;

import lombok.Getter;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        Integer bitLength = 1024 / 2;
        Integer certainrty = 100;
        List<String> oddRelativelyPrimeNumbers = Arrays.asList("3", "5", "7");

        SecureRandom rs = new SecureRandom();
        BigInteger p = new BigInteger(bitLength, certainrty, rs);
        BigInteger q = new BigInteger(bitLength, certainrty, rs);

        // n = p * q
        n = p.multiply(q);

        // v = (p-1) * (q-1)
        BigInteger v = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        k = new BigInteger(oddRelativelyPrimeNumbers.get(rs.nextInt(oddRelativelyPrimeNumbers.size())));

        //gcd(k, v) = 1
        while (v.gcd(k).intValue() > 1) {
            k = k.add(new BigInteger("2"));
        }

        //compute d by (d*k)%v = (k*d)%v = 1
        d = k.modInverse(v);
    }
}
