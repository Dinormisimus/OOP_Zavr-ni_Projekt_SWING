package rpg.enemies;

import rpg.*;
import java.util.Random;
public class Skeleton extends Enemy {
    private static final long serialVersionUID = 2L;
    public Skeleton(int lvl) {
        super("Skeleton", 45 + (lvl * 10), 11 + (lvl * 2), 4 + lvl, 6 + lvl, lvl);
        dropPool.add(new Item("Bone Shard", "WEAPON", 4 + lvl));
        dropPool.add(new Item("Rusted Sword", "WEAPON", 6 + lvl));
        dropPool.add(new Item("Worn Shield", "ARMOR", 4 + lvl));
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