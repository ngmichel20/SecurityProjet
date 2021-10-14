package esi.security.dto;

/**
 *
 * @author Nguyen Khanh-Michel
 */
public class Dto_Client {
    private final String name;
    private final String hashPasswd;
    private final String sel;
    

    public Dto_Client(String name, String hashPasswd, String sel) {
        this.name = name;
        this.hashPasswd = hashPasswd;
        this.sel = sel;
        
    }


    public String getName() {
        return name;
    }

    public String getSel() {
        return sel;
    }

    public String getHashPasswd() {
        return hashPasswd;
    }

    @Override
    public String toString() {
        return "Dto_Client{" + "name=" + name + ", hashPasswd=" + hashPasswd + ", sel=" + sel + '}';
    }

    
}
