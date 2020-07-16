package com.ppdai.auth.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * a tool to get the md5 sum value of string/file
 *
 */
@Slf4j
public class Md5Tool {

    public static String getStringMd5(String target) {
        String md5 = null;

        if (target != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] md5sum = md.digest(target.getBytes());
                md5 = String.format("%032X", new BigInteger(1, md5sum));
            } catch (NoSuchAlgorithmException e) {
                log.error("failed to calculate the string md5sum.", e);
            }
        }

        return md5;
    }

    public static String getFileMd5(String filePath) {
        String md5 = null;
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = fis.read(buffer, 0, 1024)) != -1) {
                md.update(buffer, 0, length);
            }
            BigInteger bigInt = new BigInteger(1, md.digest());
            md5 = bigInt.toString(16);
        } catch (FileNotFoundException e) {
            log.error("The files doesn't exist and not able to calculate its md5sum.", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("failed to calculate the file's md5sum.", e);
        } catch (IOException e) {
            log.error("failed to open input file.", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    log.error("failed to close input stream during md5sum calculation.", e);
                }
            }
        }

        return md5;
    }

}
