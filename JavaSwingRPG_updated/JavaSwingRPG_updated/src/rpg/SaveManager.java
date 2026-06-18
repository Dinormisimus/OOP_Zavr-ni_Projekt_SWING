package rpg;

import java.io.*;

public class SaveManager {
    private static final String SAVE_DIR = "saves/";

    public static boolean save(PlayerCharacter hero, int slot) {
        try {
            File dir = new File(SAVE_DIR);
            if (!dir.exists()) dir.mkdirs();

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_DIR + "slot_" + slot + ".sav"));
            oos.writeObject(hero);
            oos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PlayerCharacter load(int slot) {
        try {
            File f = new File(SAVE_DIR + "slot_" + slot + ".sav");
            if (!f.exists()) return null;

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
            PlayerCharacter hero = (PlayerCharacter) ois.readObject();
            ois.close();
            return hero;
        } catch (Exception e) {
            return null;
        }
    }
}