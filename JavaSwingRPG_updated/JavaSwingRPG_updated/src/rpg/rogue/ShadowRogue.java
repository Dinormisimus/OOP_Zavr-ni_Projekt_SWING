package rpg.rogue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class ShadowRogue extends Rogue {
    private static final long serialVersionUID = 2L;

    public ShadowRogue(Rogue old) {
        super(old.getName(), old.getMaxHp() + 35, old.getMaxMana() + 30, old.getAtk() + 12, old.getDef() + 5, old.getSpd() + 10, "Shadow Rogue");
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
            new String[]{"Backstab", "20", "Napad iz sjene " + Math.max(20, getAtk() * 2 + 15) + " dmg"},
            new String[]{"Smoke Bomb", "15", "Izbjegava sljedeći napad"},
            new String[]{"Poison Strike", "25", "Truje neprijatelja 8 dmg/potez"},
            new String[]{"Shadow Step", "30", "Brzi udarac " + Math.max(25, getAtk() + 20) + " dmg + dodge"},
            new String[]{"Fan of Knives", "40", "Salva bodova " + Math.max(30, getAtk() + 28) + " dmg"},
            new String[]{"Assassination", "50", "INSTANT ubojica " + Math.max(40, getAtk() * 3 + 5) + " dmg"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {20, 15, 25, 30, 40, 50};
        if (index >= costs.length || getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: dmg = Math.max(20, getAtk() * 2 - target.getDef() + 15 + new Random().nextInt(10)); target.setHp(target.getHp() - dmg); break;
            case 1: dmg = -4; break;
            case 2: dmg = Math.max(6, getAtk() - target.getDef()); target.setHp(target.getHp() - dmg); dmg = -5; break;
            case 3: dmg = Math.max(25, getAtk() + 20 - target.getDef() + new Random().nextInt(12)); target.setHp(target.getHp() - dmg); break;
            case 4: dmg = Math.max(30, getAtk() + 28 - target.getDef() + new Random().nextInt(15)); target.setHp(target.getHp() - dmg); break;
            case 5: dmg = Math.max(40, getAtk() * 3 - target.getDef() + 5 + new Random().nextInt(18)); target.setHp(target.getHp() - dmg); break;
        }
        return dmg;
    }

    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - 50);
        int dmg = Math.max(40, (getAtk() * 3) - target.getDef() + new Random().nextInt(18));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public String getSpecialName() { return "SPECIJALI"; }
}