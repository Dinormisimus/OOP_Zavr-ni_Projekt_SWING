package rpg;


import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String type; // "WEAPON", "ARMOR", "HEALTH_POTION", "MANA_POTION"
    private int value;   // Stat bonus or heal amount

    public Item(String name, String type, int value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getValue() { return value; }

    public boolean isConsumable() {
        return type.equals("HEALTH_POTION") || type.equals("MANA_POTION");
    }

    @Override
    public String toString() {
        if (type.equals("WEAPON")) return name + " (+" + value + " ATK)";
        if (type.equals("ARMOR")) return name + " (+" + value + " DEF)";
        if (type.equals("HEALTH_POTION")) return name + " (Restore " + value + " HP)";
        if (type.equals("MANA_POTION")) return name + " (Restore " + value + " MP)";
        return name;
    }
}
