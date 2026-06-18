package rpg;

import java.util.ArrayList;

public abstract class PlayerCharacter extends GameCharacter {
    private static final long serialVersionUID = 2L;
    protected int mana;
    protected int maxMana;
    protected int exp;
    protected ArrayList<Item> inventory;
    protected Item equippedWeapon;
    protected Item equippedArmor;
    protected String className;

    public PlayerCharacter(String name, int hp, int mana, int atk, int def, int spd, String className) {
        super(name, hp, atk, def, spd);
        this.mana = mana;
        this.maxMana = mana;
        this.exp = 0;
        this.inventory = new ArrayList<>();
        this.className = className;
    }

    public int getMana() { return mana; }
    public void setMana(int mana) { this.mana = Math.max(0, Math.min(mana, maxMana)); }
    public int getMaxMana() { return maxMana; }
    public int getExp() { return exp; }
    public ArrayList<Item> getInventory() { return inventory; }
    public String getClassName() { return className; }
    public Item getEquippedWeapon() { return equippedWeapon; }
    public Item getEquippedArmor() { return equippedArmor; }

    @Override
    public int getAtk() {
        int bonus = (equippedWeapon != null) ? equippedWeapon.getValue() : 0;
        return this.atk + bonus;
    }

    @Override
    public int getDef() {
        int bonus = (equippedArmor != null) ? equippedArmor.getValue() : 0;
        return this.def + bonus;
    }

    public boolean gainExp(int amount) {
        this.exp += amount;
        if (this.exp >= getExpNeeded()) {
            levelUp();
            return true;
        }
        return false;
    }

    public int getExpNeeded() {
        return 100 + (level - 1) * 20;
    }

    protected void levelUp() {
        this.level++;
        this.exp = 0;
        this.maxHp += 10;
        this.hp = this.maxHp;
        this.maxMana += 5;
        this.mana = this.maxMana;
        this.atk += 2;
        this.def += 1;
        this.spd += 2;
    }

    public void equipItem(Item item) {
        if (item.getType().equals("WEAPON")) {
            if (equippedWeapon != null) inventory.add(equippedWeapon);
            equippedWeapon = item;
            inventory.remove(item);
        } else if (item.getType().equals("ARMOR")) {
            if (equippedArmor != null) inventory.add(equippedArmor);
            equippedArmor = item;
            inventory.remove(item);
        }
    }

    public void unequipItem(Item item) {
        if (item == equippedWeapon) {
            inventory.add(equippedWeapon);
            equippedWeapon = null;
        } else if (item == equippedArmor) {
            inventory.add(equippedArmor);
            equippedArmor = null;
        }
    }

    // Use a consumable potion from inventory, returns heal amount or -1 if not usable
    public int usePotion(Item item) {
        if (!item.isConsumable() || !inventory.contains(item)) return -1;
        inventory.remove(item);
        if (item.getType().equals("HEALTH_POTION")) {
            int healed = Math.min(item.getValue(), maxHp - hp);
            setHp(hp + item.getValue());
            return healed;
        } else if (item.getType().equals("MANA_POTION")) {
            int restored = Math.min(item.getValue(), maxMana - mana);
            setMana(mana + item.getValue());
            return restored;
        }
        return -1;
    }

    public abstract int specialAttack(GameCharacter target);
    public abstract String getSpecialName();
    public abstract int getSpecialCost();

    // Extra abilities — classes override this to provide sub-menu abilities
    public java.util.List<String[]> getExtraAbilities() {
        return new ArrayList<>(); // [name, manaCost, description]
    }
    public int useExtraAbility(int index, GameCharacter target) { return 0; }
}
