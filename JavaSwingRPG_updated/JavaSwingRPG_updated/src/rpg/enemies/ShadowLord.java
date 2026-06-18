package rpg.enemies;

import rpg.*;
import java.util.Random;
public class ShadowLord extends Enemy {
    private static final long serialVersionUID = 2L;
    private int turnCount = 0;
    public ShadowLord(int playerLvl) {
        super("Shadow Lord", 250 + playerLvl * 20, 45 + playerLvl * 5, 20 + playerLvl * 2, 20 + playerLvl, playerLvl + 5);
        this.isBoss = true;
        dropPool.add(new Item("Void Cloak", "ARMOR", 22 + playerLvl));
        dropPool.add(new Item("Shadow Reaper", "WEAPON", 33 + playerLvl));
        dropPool.add(new Item("Darkness Ring", "ARMOR", 26 + playerLvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        turnCount++;
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        // Very fast — every 3rd turn: Shadow Burst (hits twice)
        if (turnCount % 3 == 0) {
            int dmg1 = Math.max(8, getAtk() - atkReduction - target.getDef() + new Random().nextInt(6));
            int dmg2 = Math.max(8, getAtk() - atkReduction - target.getDef() + new Random().nextInt(6));
            target.setHp(target.getHp() - dmg1 - dmg2);
            taunted = false;
            return -(dmg1 + dmg2); // signal: double hit
        }
        int dmg = Math.max(8, getAtk() - atkReduction - target.getDef() + new Random().nextInt(7));
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return dmg;
    }
}