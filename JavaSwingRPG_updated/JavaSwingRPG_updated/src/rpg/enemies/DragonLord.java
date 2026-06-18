package rpg.enemies;

import rpg.*;
import java.util.Random;
public class DragonLord extends Enemy {
    private static final long serialVersionUID = 2L;
    private int turnCount = 0;
    public DragonLord(int playerLvl) {
        super("Dragon Lord", 800 + playerLvl * 35, 65 + playerLvl * 4, 30 + playerLvl * 3, 8 + playerLvl, playerLvl + 5);
        this.isBoss = true;
        dropPool.add(new Item("Dragon Scale Armor", "ARMOR", 30 + playerLvl));
        dropPool.add(new Item("Dragon Fang Blade", "WEAPON", 35 + playerLvl));
        dropPool.add(new Item("Ancient Dragon Eye", "ARMOR", 28 + playerLvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        turnCount++;
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        // Every 2nd turn: Fire Breath (ignores defense)
        if (turnCount % 2 == 0) {
            int dmg = Math.max(10, getAtk() - atkReduction + 25 + new Random().nextInt(8));
            target.setHp(target.getHp() - dmg);
            taunted = false;
            return -(dmg); // signal: fire breath
        }
        int dmg = Math.max(12, getAtk() - atkReduction - target.getDef() + new Random().nextInt(10));
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return dmg;
    }
}