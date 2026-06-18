package rpg.warrior;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class EliteWarrior extends Warrior {
    private static final long serialVersionUID = 2L;

    public EliteWarrior(Warrior old) {
        super(old.getName(), old.getMaxHp() + 50, old.getMaxMana() + 20, old.getAtk() + 10, old.getDef() + 8, old.getSpd() + 3, "Elite Warrior");
        this.level = old.getLevel();
        this.exp = old.getExp();
        this.inventory = old.getInventory();
        this.equippedWeapon = old.getEquippedWeapon();
        this.equippedArmor = old.getEquippedArmor();
        this.hp = this.maxHp;
        this.mana = this.maxMana;
    }

    @Override
    public List<String[]> getExtraAbilities() {
        return Arrays.asList(
            new String[]{"Shield Bash", "40", "Stunna + " + Math.max(15, getAtk() * 2 - 5) + " dmg"},
            new String[]{"War Cry", "25", "Boost ATK +8 za 1 potez"},
            new String[]{"Whirlwind", "50", "Čisti napad " + Math.max(25, getAtk() + 20) + " dmg"},
            new String[]{"Taunt", "20", "Smanjuje dmg neprijatelja -40%"},
            new String[]{"Execute", "50", "Masivni napad " + Math.max(40, getAtk() * 3 - 5) + " dmg"},
            new String[]{"Berserker Rage", "75", "ULTRA napad " + Math.max(50, getAtk() * 2 - 10) + " dmg"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {40, 25, 50, 20, 50, 75};
        if (index >= costs.length || getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: dmg = Math.max(15, getAtk() * 2 - target.getDef() + new Random().nextInt(8)); target.setHp(target.getHp() - dmg); break;
            case 1: this.atk += 8; dmg = 0; break;
            case 2: dmg = Math.max(25, getAtk() + 20 + new Random().nextInt(12)); target.setHp(target.getHp() - dmg); break;
            case 3: dmg = -1; break;
            case 4: dmg = Math.max(40, getAtk() * 3 - target.getDef() + new Random().nextInt(15)); target.setHp(target.getHp() - dmg); break;
            case 5: dmg = Math.max(50, getAtk() * 4 - target.getDef() + new Random().nextInt(20)); target.setHp(target.getHp() - dmg); break;
        }
        return dmg;
    }

    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - 55);
        int dmg = Math.max(50, (getAtk() * 4) - target.getDef() + new Random().nextInt(20));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public String getSpecialName() { return "SPECIJALI"; }
}