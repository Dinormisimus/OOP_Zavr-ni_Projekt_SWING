package rpg.enemies;

import rpg.GameCharacter;
import rpg.Item;

import java.util.ArrayList;
import java.util.Random;

public abstract class Enemy extends GameCharacter {
    private static final long serialVersionUID = 2L;
    protected ArrayList<Item> dropPool;
    protected boolean isBoss;
    protected int poisonTurns = 0;
    protected int poisonDmg = 0;
    protected boolean taunted = false;
    protected boolean slowed = false;

    public Enemy(String name, int hp, int atk, int def, int spd, int level) {
        super(name, hp, atk, def, spd);
        this.level = level;
        this.dropPool = new ArrayList<>();
        this.isBoss = false;
    }

    public boolean isBoss() { return isBoss; }
    public void applyPoison(int dmg, int turns) { this.poisonDmg = dmg; this.poisonTurns = turns; }
    public void applyTaunt() { this.taunted = true; }
    public void applySlow() { this.slowed = true; }
    public void clearDebuffs() { taunted = false; slowed = false; }

    public int tickPoison() {
        if (poisonTurns > 0) {
            setHp(hp - poisonDmg);
            poisonTurns--;
            return poisonDmg;
        }
        return 0;
    }

    public Item rollLoot() {
        Random r = new Random();
        // 50% chance to drop equipment
        if (r.nextDouble() < 0.5 && !dropPool.isEmpty()) {
            return dropPool.get(r.nextInt(dropPool.size()));
        }
        // 30% chance to drop health potion
        if (r.nextDouble() < 0.3) {
            int healAmt = 25 + level * 5;
            return new Item("Health Potion", "HEALTH_POTION", healAmt);
        }
        // 30% chance to drop mana potion
        if (r.nextDouble() < 0.3) {
            int manaAmt = 20 + level * 4;
            return new Item("Mana Potion", "MANA_POTION", manaAmt);
        }
        return null;
    }

    public Item rollBossLoot() {
        Random r = new Random();
        // Boss always drops something good
        Item equipment = dropPool.get(r.nextInt(dropPool.size()));
        return equipment;
    }
}
