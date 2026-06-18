package rpg.enemies;

import rpg.*;
import java.util.Random;
public class Orc extends Enemy {
    private static final long serialVersionUID = 2L;
    public Orc(int lvl) {
        super("Orc", 65 + (lvl * 14), 16 + (lvl * 3), 7 + lvl, 4 + lvl, lvl);
        dropPool.add(new Item("Orcish Cleaver", "WEAPON", 11 + lvl));
        dropPool.add(new Item("Iron Plate", "ARMOR", 9 + lvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        int dmg = Math.max(5, getAtk() - atkReduction - target.getDef() + new Random().nextInt(5));
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return dmg;
    }
}