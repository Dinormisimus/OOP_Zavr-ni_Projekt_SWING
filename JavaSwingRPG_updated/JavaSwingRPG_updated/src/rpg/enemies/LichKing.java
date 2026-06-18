package rpg.enemies;

import rpg.*;
import java.util.Random;
public class LichKing extends Enemy {
    private static final long serialVersionUID = 2L;
    private int turnCount = 0;
    public LichKing(int playerLvl) {
        super("Lich King", 600 + playerLvl * 35, 40 + playerLvl * 7, 25 + playerLvl * 2, 15 + playerLvl, playerLvl + 5);
        this.isBoss = true;
        dropPool.add(new Item("Crown of Undeath", "ARMOR", 25 + playerLvl));
        dropPool.add(new Item("Frostmourne Shard", "WEAPON", 30 + playerLvl));
        dropPool.add(new Item("Soul Lantern", "WEAPON", 28 + playerLvl));
    }
    @Override
    public int basicAttack(GameCharacter target) {
        turnCount++;
        int atkReduction = taunted ? (int)(getAtk() * 0.30) : 0;
        // Every 3rd turn: Soul Drain (big hit + heals self)
        if (turnCount % 3 == 0) {
            int dmg = Math.max(20, getAtk() - atkReduction + 20 - target.getDef() + new Random().nextInt(10));
            target.setHp(target.getHp() - dmg);
            setHp(Math.min(maxHp, hp + 15));
            taunted = false;
            return -(dmg); // signal: soul drain
        }
        int dmg = Math.max(10, getAtk() - atkReduction - target.getDef() + new Random().nextInt(8));
        target.setHp(target.getHp() - dmg);
        taunted = false;
        return dmg;
    }
}