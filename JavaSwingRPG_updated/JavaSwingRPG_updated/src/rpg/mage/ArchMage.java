package rpg.mage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import rpg.*;

public class ArchMage extends Mage {
    private static final long serialVersionUID = 2L;

    public ArchMage(Mage old) {
        super(old.getName(), old.getMaxHp() + 30, old.getMaxMana() + 60, old.getAtk() + 14, old.getDef() + 4, old.getSpd() + 4, "Arch Mage");
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
            new String[]{"Fireball", "30", "Vatrana kugla " + Math.max(20, getAtk() + 30) + " dmg"},
            new String[]{"Ice Bolt", "20", "Uspori + " + Math.max(15, getAtk() + 15) + " dmg"},
            new String[]{"Mana Drain", "15", "Krade 30 mane od neprijatelja"},
            new String[]{"Lightning", "40", "Munja " + Math.max(35, getAtk() + 45) + " dmg"},
            new String[]{"Heal", "35", "Liječi " + (50 + level * 5) + " HP"},
            new String[]{"Arcane Surge", "60", "ULTRA magija " + Math.max(55, getAtk() + 60) + " dmg"}
        );
    }

    @Override
    public int useExtraAbility(int index, GameCharacter target) {
        int[] costs = {30, 20, 15, 40, 35, 60};
        if (index >= costs.length || getMana() < costs[index]) return -1;
        setMana(getMana() - costs[index]);
        int dmg = 0;
        switch (index) {
            case 0: dmg = Math.max(20, getAtk() + 30 - target.getDef() + new Random().nextInt(10)); target.setHp(target.getHp() - dmg); break;
            case 1: dmg = Math.max(15, getAtk() + 15 - target.getDef() + new Random().nextInt(6)); target.setHp(target.getHp() - dmg); dmg = -dmg; break;
            case 2: dmg = -2; break;
            case 3: dmg = Math.max(35, getAtk() + 45 - target.getDef() + new Random().nextInt(15)); target.setHp(target.getHp() - dmg); break;
            case 4: setHp(hp + 50 + level * 5); dmg = -3; break;
            case 5: dmg = Math.max(55, getAtk() + 60 - target.getDef() + new Random().nextInt(20)); target.setHp(target.getHp() - dmg); break;
        }
        return dmg;
    }

    @Override
    public int specialAttack(GameCharacter target) {
        this.setMana(this.getMana() - 60);
        int dmg = Math.max(55, (getAtk() + 60) - target.getDef() + new Random().nextInt(20));
        target.setHp(target.getHp() - dmg);
        return dmg;
    }

    @Override
    public String getSpecialName() { return "SPECIJALI"; }
}