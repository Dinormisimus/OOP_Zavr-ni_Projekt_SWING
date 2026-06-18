package rpg;

import java.io.Serializable;

public abstract class GameCharacter implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String name;
    protected int hp;
    protected int maxHp;
    protected int atk;
    protected int def;
    protected int spd;
    protected int level;

    public GameCharacter(String name, int hp, int atk, int def, int spd) {
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.atk = atk;
        this.def = def;
        this.spd = spd;
        this.level = 1;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public int getMaxHp() { return maxHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getSpd() { return spd; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public boolean isAlive() { return this.hp > 0; }

    public abstract int basicAttack(GameCharacter target);
}