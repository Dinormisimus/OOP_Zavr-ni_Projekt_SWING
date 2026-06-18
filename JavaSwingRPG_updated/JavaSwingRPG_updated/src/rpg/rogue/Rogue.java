package rpg.rogue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class Rogue extends PlayerCharacter {
    private static final long serialVersionUID = 2L;

    public Rogue(String name) {
        super(name, 90, 70, 17, 10, 18, "Rogue");
    }

    protected Rogue(String name, int hp, int mana, int atk, int def, int spd, String className) {
        super(name, hp, mana, atk, def, spd, className);
    }

    @Override
    public int basicAttack(GameCharacter target) {
        boolean crit = new Random().nextDouble() < 0.28;
        int baseDmg = Math.max(4, getAtk() - target.getDef());
        int dmg = crit ? baseDmg * 2 : baseDmg + new Random().nextInt(5);
        target.setHp(target.getHp() - dmg);
        return crit ? -(dmg) : dmg; // negative = crit signal
    }

    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - 20);
        int dmg = Math.max(12, (getAtk() * 2) - target.getDef() + 10 + new Random().nextInt(8));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public String getSpecialName() { return "SPECIJALI"; }
    @Override
    public int getSpecialCost() { return 0; }

    @Override
    public List<String[]> getExtraAbilities() {
        return Arrays.asList(
            new String[]{"Backstab", "20", "Napad iz sjene " + Math.max(12, getAtk() * 2 + 10) + " dmg"},
            new String[]{"Smoke Bomb", "15", "Izbjegava sljedeći napad"},
            new String[]{"Poison Strike", "25", "Truje neprijatelja 5 dmg/potez"},
            new String[]{"Shadow Step", "30", "Brzi udarac " + Math.max(18, getAtk() + 15) + " dmg + dodge"},
            new String[]{"Fan of Knives", "40", "Salva bodova " + Math.max(22, getAtk() + 20) + " dmg"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {20, 15, 25, 30, 40};
        if (getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: // Backstab
                dmg = Math.max(12, getAtk() * 2 - target.getDef() + 10 + new Random().nextInt(8));
                target.setHp(target.getHp() - dmg);
                break;
            case 1: // Smoke Bomb — signal dodge
                dmg = -4;
                break;
            case 2: // Poison Strike
                dmg = Math.max(6, getAtk() - target.getDef() + new Random().nextInt(4));
                target.setHp(target.getHp() - dmg);
                dmg = -5; // signal: poisoned
                break;
            case 3: // Shadow Step
                dmg = Math.max(18, getAtk() + 15 - target.getDef() + new Random().nextInt(10));
                target.setHp(target.getHp() - dmg);
                break;
            case 4: // Fan of Knives
                dmg = Math.max(22, getAtk() + 20 - target.getDef() + new Random().nextInt(12));
                target.setHp(target.getHp() - dmg);
                break;
        }
        return dmg;
    }
}