package rpg.enemies;

import rpg.*;
import java.util.Random;

public class DragonWhelp extends Enemy {
    private static final long serialVersionUID = 2L;
    public DragonWhelp(int lvl) {
        super("Dragon Whelp", 85 + (lvl * 18), 23 + (lvl * 4), 10 + lvl, 9 + lvl, lvl);
        dropPool.add(new Item("Dragonic Claw", "WEAPON", 19 + lvl));
        dropPool.add(new Item("Obsidian Scale", "ARMOR", 15 + lvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        // Dragon has fire breath variant (30% chance)
        boolean fireBreath = new Random().nextDouble() < 0.30;
        int dmg;
        if (fireBreath) {
            dmg = Math.max(12, getAtk() - atkReduction + 10 + new Random().nextInt(8)); // ignores some def
        } else {
            dmg = Math.max(8, getAtk() - atkReduction - target.getDef() + new Random().nextInt(6));
        }
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return fireBreath ? -(dmg) : dmg;
    }
}