
package sciencerec;

import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class Utilities {
    
    public static final int ADMIN = 1;
    public static final int COORDONATOR = 2;
    public static final int STUDENT = 3;
    public static final int MASTERAND = 4;
    public static final int DOCTORAND = 5;
    public static final int CERCETATOR = 6;
    
    public static final int LICENSE = 1;
    public static final int MASTER = 2;
    public static final int DOCTORATE = 3;
    public static final int RESEARCH = 4;
    
    public static final int BOOK = 1;
    public static final int JOURNAL_ARTICLE = 2;
    public static final int CONFERENCE_ARTICLE = 3;
    public static final int LICENSE_THESIS = 4;
    public static final int MASTER_THESIS = 5;
    public static final int PHD_THESIS = 6;
    
    public static final int ALG_DATA_STRUCT = 1;
    public static final int MACHINE_LEARNING = 2;
    public static final int DATA_MINING = 3;
    public static final int VIRTUAL_REALITY = 4;
    public static final int JAVA_PROGRAMMING = 5;
    
    public static final int READ = 1;
    public static final int NOT_READ = 2;
    public static final int USEFUL = 3;
    public static final int NOT_USEFUL = 4;
    
    // Stringul de conectare a bazei de date la serverul SQL
    protected static final String sqlURL = "jdbc:sqlite:sciencerec.db";
    
    public static final String regexEmail = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    
    // Genereaza hashcode pentru stringul pwd cu algoritmul SHA-256 si returneaza un String in hexa
    public static String HexHash(String pwd) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Utilizam algoritmul SHA-256 pentru generarea parolei(HashCode)
        byte[] byarr = null;
        try {
            byarr = pwd.getBytes("UTF-8"); // transforam stringul in array de bytes 
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest(byarr); // Apelam algoritmul de generare hashcode
        // Transforma datele din digest (array dee bytes) in StringBuffer si in final in String
        StringBuffer sbHexHash = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            sbHexHash.append(String.format("%02x", digest[i])); // Sintetizam HashCode in scriere hexazecimala
        }
        return sbHexHash.toString(); // returneaza hash code in hexa ca String
    }
}

