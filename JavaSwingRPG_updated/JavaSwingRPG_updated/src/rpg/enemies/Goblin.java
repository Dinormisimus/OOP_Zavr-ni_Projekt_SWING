package rpg.enemies;

import rpg.*;
import java.util.Random;
public class Goblin extends Enemy {
    private static final long serialVersionUID = 2L;
    public Goblin(int lvl) {
        super("Goblin", 38 + (lvl * 8), 13 + (lvl * 2), 3 + lvl, 11 + lvl, lvl);
        dropPool.add(new Item("Goblin Dagger", "WEAPON", 7 + lvl));
        dropPool.add(new Item("Leather Bracers", "ARMOR", 4 + lvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        int dmg = Math.max(3, getAtk() - atkReduction - target.getDef() + new Random().nextInt(4));
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return dmg;
    }
}