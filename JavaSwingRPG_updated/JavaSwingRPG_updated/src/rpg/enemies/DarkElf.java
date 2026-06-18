package rpg.enemies;

import rpg.*;
import java.util.Random;
public class DarkElf extends Enemy {
    private static final long serialVersionUID = 2L;
    public DarkElf(int lvl) {
        super("Dark Elf", 58 + (lvl * 12), 19 + (lvl * 3), 5 + lvl, 13 + lvl, lvl);
        dropPool.add(new Item("Shadow Blade", "WEAPON", 13 + lvl));
        dropPool.add(new Item("Elven Cloak", "ARMOR", 8 + lvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        // Dark Elf has chance to double-strike
        boolean doubleStrike = new Random().nextDouble() < 0.20;
        int dmg = Math.max(4, getAtk() - atkReduction - target.getDef() + new Random().nextInt(5));
        if (doubleStrike) dmg = (int)(dmg * 1.5);
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return doubleStrike ? -(dmg) : dmg; // negative = double strike signal
    }
}