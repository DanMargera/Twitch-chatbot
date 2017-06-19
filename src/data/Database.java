package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.jibble.pircbot.User;

/**
 * Classe pra armazenar estatísticas dos usuários,
 * não há necessidade de um banco de dados para algo de porte pequeno.
 * @author Dan
 */
public class Database implements Serializable {
    
    private static final long serialVersionUID = 3967092439058918608L;
    
    HashMap<String, UserData> usersMap;
    
    public Database() {
        usersMap = new HashMap();
    }
    
    public boolean putUser(String user) {
        if (usersMap.containsKey(user))
            return false;
        
        UserData data = new UserData();
        usersMap.put(user, data);
        return true;
    }
    
    public static Database load() {
        Database db = new Database();
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("database.db"));
            db = (Database)(in.readObject());
        } catch (Exception e) {
            System.out.println(e);
        }
        return db;
    }
    
    public void addPoints(User user, int n) {
        usersMap.get(user.getNick()).addPoints(n);
    }
    
    public void addPoints(String user, int n) {
        UserData temp = usersMap.get(user);
        if (temp != null) {
            temp.addPoints(n);
        }
    }
    
    public boolean spendPoints(String userName, int p) {
        return usersMap.get(userName).spendPoints(p);
    }
    
    public long getUserPoints(String user) {
        if (usersMap.containsKey(user))
            return usersMap.get(user).points;
        else
            return 0;
    }
    
    public void save() {
        try {
            File data = new File("database.db");
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(data));
            out.writeObject(this);
            out.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    public void clean() {
        ArrayList<String> noobs = new ArrayList<>();
        
        usersMap.forEach((user, userdata) -> {
            if (userdata.points<20)
                noobs.add(user);
        });
        
        for (String tmp : noobs) {
            System.out.println(usersMap.get(tmp).points);
            usersMap.remove(tmp);
        }
    }
    
    private class UserData implements Serializable {
        long points;
        boolean vip = false;
        
        public UserData(){
            points = 0;
        }
        
        public void addPoints(int p) {
            points += p;
        }
        
        public boolean spendPoints(int p) {
            if (points >= p) {
                points -= p;
                return true;
            }
            else
                return false;
        }
        
        public void setVip(boolean v) {
            vip = v;
        }
        
        public boolean isVip() {
            return vip;
        }
    }
}
