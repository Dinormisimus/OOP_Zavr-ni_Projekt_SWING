package rpg.mage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class Mage extends PlayerCharacter {
    private static final long serialVersionUID = 2L;

    public Mage(String name) {
        super(name, 75, 140, 25, 6, 11, "Mage");
    }

    protected Mage(String name, int hp, int mana, int atk, int def, int spd, String className) {
        super(name, hp, mana, atk, def, spd, className);
    }

    @Override
    public int basicAttack(GameCharacter target) {
        int dmg = Math.max(5, getAtk() - target.getDef() + new Random().nextInt(4));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - 30);
        int dmg = Math.max(15, (getAtk() + 25) - target.getDef() + new Random().nextInt(10));
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
            new String[]{"Fireball", "30", "Vatrana kugla " + Math.max(15, getAtk() + 25) + " dmg"},
            new String[]{"Ice Bolt", "20", "Uspori neprijatelja + " + Math.max(10, getAtk() + 10) + " dmg"},
            new String[]{"Mana Drain", "15", "Krade 20 mane od neprijatelja"},
            new String[]{"Lightning", "40", "Munja " + Math.max(25, getAtk() + 35) + " dmg"},
            new String[]{"Heal", "35", "Liječi " + (30 + level * 3) + " HP"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {30, 20, 15, 40, 35};
        if (getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: // Fireball
                dmg = Math.max(15, getAtk() + 25 - target.getDef() + new Random().nextInt(10));
                target.setHp(target.getHp() - dmg);
                break;
            case 1: // Ice Bolt
                dmg = Math.max(10, getAtk() + 10 - target.getDef() + new Random().nextInt(6));
                target.setHp(target.getHp() - dmg);
                dmg = -(dmg); // negative signals slow debuff in main
                break;
            case 2: // Mana Drain — signal -2
                dmg = -2;
                break;
            case 3: // Lightning
                dmg = Math.max(25, getAtk() + 35 - target.getDef() + new Random().nextInt(15));
                target.setHp(target.getHp() - dmg);
                break;
            case 4: // Heal self
                int healed = Math.min(30 + level * 3, maxHp - hp);
                setHp(hp + 30 + level * 3);
                dmg = -3; // signal heal
                break;
        }
        return dmg;
    }
}