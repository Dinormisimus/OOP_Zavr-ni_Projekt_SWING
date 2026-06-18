package rpg.warrior;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class Warrior extends PlayerCharacter {
    private static final long serialVersionUID = 2L;

    public Warrior(String name) {
        super(name, 130, 50, 20, 16, 8, "Warrior");
    }

    protected Warrior(String name, int hp, int mana, int atk, int def, int spd, String className) {
        super(name, hp, mana, atk, def, spd, className);
    }

    @Override
    public int basicAttack(GameCharacter target) {
        int dmg = Math.max(3, (int)(getAtk() * 0.6) - target.getDef() + new Random().nextInt(8));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    // Special opens submenu — but direct call does Shield Bash
    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - getSpecialCost());
        int dmg = Math.max(15, (getAtk() * 2) - target.getDef() + new Random().nextInt(8));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public String getSpecialName() { return "SPECIJALI"; }
    @Override
    public int getSpecialCost() { return 0; } // Sub-menu, no direct cost

    // Extra abilities for sub-menu: [name, manaCost, dmg description]
    @Override
    public List<String[]> getExtraAbilities() {
        return Arrays.asList(
            new String[]{"Shield Bash", "20", "Stunna + " + Math.max(15, getAtk() * 2 - 5) + " dmg"},
            new String[]{"War Cry", "15", "Boost ATK +5 za 1 potez"},
            new String[]{"Whirlwind", "35", "Čisti napad " + Math.max(20, getAtk() + 15) + " dmg"},
            new String[]{"Taunt", "10", "Smanjuje dmg neprijatelja -30%"},
            new String[]{"Execute", "45", "Masivni napad " + Math.max(30, getAtk() * 3 - 10) + " dmg"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {20, 15, 35, 10, 45};
        if (getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: // Shield Bash
                dmg = Math.max(15, getAtk() * 2 - target.getDef() + new Random().nextInt(8));
                target.setHp(target.getHp() - dmg);
                break;
            case 1: // War Cry — handled in main class (buff)
                this.atk += 5;
                dmg = 0;
                break;
            case 2: // Whirlwind
                dmg = Math.max(20, getAtk() + 15 + new Random().nextInt(10));
                target.setHp(target.getHp() - dmg);
                break;
            case 3: // Taunt — handled in main (debuff flag)
                dmg = -1; // signal: taunt
                break;
            case 4: // Execute
                dmg = Math.max(30, getAtk() * 3 - target.getDef() + new Random().nextInt(12));
                target.setHp(target.getHp() - dmg);
                break;
        }
        return dmg;
    }
}