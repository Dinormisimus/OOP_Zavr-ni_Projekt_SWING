package rpg;

import rpg.enemies.*;
import rpg.mage.*;
import rpg.warrior.*;
import rpg.rogue.*;
import rpg.ui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JavaSwingRPG extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardsPanel;
    private PlayerCharacter activePlayer;
    private Enemy activeEnemy;
    private boolean isBossMode = false;
    private boolean playerDodgeNext = false;
    private boolean enemySlowed = false;
    private int enemyPoisonTurns = 0;
    private int enemyPoisonDmg = 0;

    private int currentSaveSlot = 0;

    public static final Color COLOR_BG     = new Color(11, 15, 28);
    public static final Color COLOR_CARD   = new Color(20, 24, 43);
    public static final Color COLOR_RED    = new Color(239, 68, 68);
    public static final Color COLOR_BLUE   = new Color(59, 130, 246);
    public static final Color COLOR_GREEN  = new Color(16, 185, 129);
    public static final Color COLOR_YELLOW = new Color(245, 158, 11);
    public static final Color COLOR_PURPLE = new Color(168, 85, 247);
    public static final Color COLOR_TXT_WHITE = new Color(243, 244, 246);
    public static final Color COLOR_TXT_GRAY  = new Color(156, 163, 175);

    private JLabel lblPlayerName, lblPlayerClass, lblEnemyName, lblEnemyLevel, lblEnemyStatusValue, lblAnimacija;
    private JProgressBar barPlayerHP, barPlayerMana, barPlayerXP, barEnemyHP;
    private JTextPane logPane;

    private JLabel lblPlayerSprite;
    private JLabel lblEnemySprite;
    private JPanel battleAnimationPanel;

    private JButton btnAttack, btnSpecial, btnItem, btnFlee;
    private JButton btnContinueBattle;
    private JButton btnMainMenuSave;
    private JButton btnMainMenuInventory;
    private JButton btnBossMode;

    // Character screen refs
    private JPanel characterScreenPanel = null;

    public JavaSwingRPG() {
        setTitle("Sveučilište u Zadru - OOP - Dino Grgić");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardsPanel = new JPanel(cardLayout);
        cardsPanel.setBackground(COLOR_BG);

        cardsPanel.add(buildMainMenu(),      "MAIN_MENU");
        cardsPanel.add(buildCharCreation(),  "CHAR_CREATION");
        cardsPanel.add(buildBattleScreen(),  "BATTLE_SCREEN");
        cardsPanel.add(buildSaveLoadScreen(),"SAVE_LOAD");
        cardsPanel.add(buildCharacterScreen(),"CHARACTER_SCREEN");
        cardsPanel.add(buildBossModeScreen(),"BOSS_MODE");

        add(cardsPanel);
        cardLayout.show(cardsPanel, "MAIN_MENU");
        updateMainMenuButtons();
    }

    // ==================== MAIN MENU ====================
    private JPanel buildMainMenu() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBackground(COLOR_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("BATTLE RPG");
        lblTitle.setFont(new Font("Monospaced", Font.BOLD, 72));
        lblTitle.setForeground(COLOR_RED);
        gbc.gridy = 0; menuPanel.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Turn-Based Fantasy Adventure — Dino Grgić");
        lblSubtitle.setFont(new Font("SansSerif", Font.ITALIC, 16));
        lblSubtitle.setForeground(COLOR_TXT_GRAY);
        gbc.gridy = 1; menuPanel.add(lblSubtitle, gbc);

        JButton btnNewGame = createStyledButton("NEW GAME", COLOR_RED, 280, 45);
        btnNewGame.addActionListener(e -> cardLayout.show(cardsPanel, "CHAR_CREATION"));
        gbc.gridy = 2; menuPanel.add(btnNewGame, gbc);

        btnContinueBattle = createStyledButton("ENTER BATTLE", COLOR_GREEN, 280, 45);
        btnContinueBattle.addActionListener(e -> {
            isBossMode = false;
            spawnNewEnemy();
            refreshBattleStats();
            cardLayout.show(cardsPanel, "BATTLE_SCREEN");
        });
        gbc.gridy = 3; menuPanel.add(btnContinueBattle, gbc);

        btnBossMode = createStyledButton("⚔ BOSS CHALLENGE", COLOR_PURPLE, 280, 45);
        btnBossMode.addActionListener(e -> {
            cardLayout.show(cardsPanel, "BOSS_MODE");
        });
        gbc.gridy = 4; menuPanel.add(btnBossMode, gbc);

        btnMainMenuInventory = createStyledButton("CHARACTER & INVENTORY", COLOR_YELLOW, 280, 45);
        btnMainMenuInventory.addActionListener(e -> {
            refreshCharacterScreen();
            cardLayout.show(cardsPanel, "CHARACTER_SCREEN");
        });
        gbc.gridy = 5; menuPanel.add(btnMainMenuInventory, gbc);

        btnMainMenuSave = createStyledButton("LOAD / SAVE SYSTEM", COLOR_BLUE, 280, 45);
        btnMainMenuSave.addActionListener(e -> {
            cardLayout.show(cardsPanel, "SAVE_LOAD");
            refreshSaveLoadScreen();
        });
        gbc.gridy = 6; menuPanel.add(btnMainMenuSave, gbc);

        JButton btnExit = createStyledButton("EXIT GAME", new Color(75, 85, 99), 280, 45);
        btnExit.addActionListener(e -> System.exit(0));
        gbc.gridy = 7; menuPanel.add(btnExit, gbc);

        return menuPanel;
    }

    private void updateMainMenuButtons() {
        boolean hasHero = (activePlayer != null);
        btnContinueBattle.setEnabled(hasHero);
        btnMainMenuInventory.setEnabled(hasHero);
        btnBossMode.setEnabled(hasHero);
        btnContinueBattle.setText(hasHero ? "CONTINUE ADVENTURE" : "ENTER BATTLE");
    }

    // ==================== CHAR CREATION ====================
    private JPanel buildCharCreation() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel lblHeader = new JLabel("CREATOR HEROES");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblHeader.setForeground(COLOR_TXT_WHITE);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblHeader, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;

        JLabel lblName = new JLabel("Hero Name:");
        lblName.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblName.setForeground(COLOR_TXT_WHITE);
        gbc.gridy = 0; inputPanel.add(lblName, gbc);

        JTextField tfName = new JTextField("Arthas");
        tfName.setBackground(COLOR_CARD);
        tfName.setForeground(COLOR_TXT_WHITE);
        tfName.setCaretColor(COLOR_TXT_WHITE);
        tfName.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tfName.setBorder(new BubbleBorder(COLOR_BLUE, 2, 8));
        gbc.gridy = 1; inputPanel.add(tfName, gbc);

        JLabel lblClass = new JLabel("Select Class:");
        lblClass.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblClass.setForeground(COLOR_TXT_WHITE);
        gbc.gridy = 2; inputPanel.add(lblClass, gbc);

        String[] classes = {"Warrior", "Mage", "Rogue"};
        JComboBox<String> cbClass = new JComboBox<>(classes);
        cbClass.setBackground(COLOR_CARD);
        cbClass.setForeground(COLOR_TXT_WHITE);
        cbClass.setFont(new Font("SansSerif", Font.BOLD, 16));
        gbc.gridy = 3; inputPanel.add(cbClass, gbc);

        centerPanel.add(inputPanel);

        JTextArea areaStats = new JTextArea();
        areaStats.setEditable(false);
        areaStats.setBackground(COLOR_CARD);
        areaStats.setForeground(COLOR_TXT_WHITE);
        areaStats.setFont(new Font("Monospaced", Font.PLAIN, 15));
        areaStats.setBorder(new BubbleBorder(COLOR_RED, 2, 12));
        areaStats.setMargin(new Insets(20, 20, 20, 20));

        Runnable updateStatDisplay = () -> {
            String selected = (String) cbClass.getSelectedItem();
            if ("Warrior".equals(selected)) {
                areaStats.setText("⚔️  WARRIOR\n\n HP:   130\n Mana: 50\n ATK:  20\n DEF:  16\n SPD:  8\n\nAbilities:\n• Strike (basic)\n• Shield Bash  [20 MP]\n• War Cry      [15 MP]\n• Whirlwind    [35 MP]\n• Taunt        [10 MP]\n• Execute      [45 MP]\n\nEvolves → Elite Warrior @ Lvl 10");
            } else if ("Mage".equals(selected)) {
                areaStats.setText("🧙  MAGE\n\n HP:   75\n Mana: 140\n ATK:  25\n DEF:  6\n SPD:  11\n\nAbilities:\n• Fire Bolt (basic)\n• Fireball    [30 MP]\n• Ice Bolt    [20 MP]\n• Mana Drain  [15 MP]\n• Lightning   [40 MP]\n• Heal        [35 MP]\n\nEvolves → Arch Mage @ Lvl 10");
            } else {
                areaStats.setText("🗡️  ROGUE\n\n HP:   90\n Mana: 70\n ATK:  17\n DEF:  10\n SPD:  18\n\nAbilities:\n• Stab (basic, 28% crit)\n• Backstab    [20 MP]\n• Smoke Bomb  [15 MP]\n• Poison      [25 MP]\n• Shadow Step [30 MP]\n• Fan of Knives[40 MP]\n\nEvolves → Shadow Rogue @ Lvl 10");
            }
        };

        cbClass.addActionListener(e -> updateStatDisplay.run());
        updateStatDisplay.run();

        centerPanel.add(areaStats);
        p.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setOpaque(false);

        JButton btnBack = createStyledButton("BACK", new Color(107, 114, 128), 120, 40);
        btnBack.addActionListener(e -> cardLayout.show(cardsPanel, "MAIN_MENU"));
        bottomPanel.add(btnBack);

        JButton btnCreate = createStyledButton("LAUNCH HERO", COLOR_GREEN, 180, 40);
        btnCreate.addActionListener(e -> {
            String name = tfName.getText().trim();
            if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Unesite ime heroja!", "Greška", JOptionPane.ERROR_MESSAGE); return; }
            String sel = (String) cbClass.getSelectedItem();
            if ("Warrior".equals(sel)) activePlayer = new Warrior(name);
            else if ("Mage".equals(sel)) activePlayer = new Mage(name);
            else activePlayer = new Rogue(name);

            currentSaveSlot = 0;
            updateMainMenuButtons();
            logPane.setText("");
            isBossMode = false;
            spawnNewEnemy();
            refreshBattleStats();
            logMessage("<font color='#10B981'>Heroj " + activePlayer.getName() + " (" + activePlayer.getClassName() + ") je spreman za borbu!</font>");
            cardLayout.show(cardsPanel, "BATTLE_SCREEN");
        });
        bottomPanel.add(btnCreate);
        p.add(bottomPanel, BorderLayout.SOUTH);
        return p;
    }

    // ==================== BATTLE SCREEN ====================
    private JPanel buildBattleScreen() {
        JPanel p = new JPanel(new BorderLayout(15, 15));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // TOP: Player card | VS | Enemy card
        JPanel topSection = new JPanel(new GridLayout(1, 3, 20, 0));
        topSection.setOpaque(false);

        // --- Player card ---
        RoundedPanel playerCard = new RoundedPanel(15, COLOR_CARD);
        playerCard.setLayout(new GridBagLayout());
        playerCard.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        GridBagConstraints pcGbc = new GridBagConstraints();
        pcGbc.fill = GridBagConstraints.HORIZONTAL; pcGbc.weightx = 1.0;

        lblPlayerName = new JLabel("Hero Name");
        lblPlayerName.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblPlayerName.setForeground(COLOR_TXT_WHITE);
        pcGbc.gridy = 0; pcGbc.gridx = 0; playerCard.add(lblPlayerName, pcGbc);

        lblPlayerClass = new JLabel("WARRIOR");
        lblPlayerClass.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPlayerClass.setForeground(COLOR_BLUE);
        lblPlayerClass.setHorizontalAlignment(SwingConstants.RIGHT);
        pcGbc.gridx = 1; playerCard.add(lblPlayerClass, pcGbc);

        JLabel lblPHP = new JLabel("HP");
        lblPHP.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPHP.setForeground(COLOR_TXT_GRAY);
        pcGbc.gridy = 1; pcGbc.gridx = 0; pcGbc.insets = new Insets(8, 0, 0, 0); playerCard.add(lblPHP, pcGbc);

        barPlayerHP = new JProgressBar(0, 100);
        barPlayerHP.setForeground(COLOR_RED);
        barPlayerHP.setBackground(new Color(40, 20, 20));
        barPlayerHP.setStringPainted(true);
        barPlayerHP.setBorderPainted(false);
        barPlayerHP.setPreferredSize(new Dimension(0, 18));
        pcGbc.gridy = 2; pcGbc.gridwidth = 2; pcGbc.insets = new Insets(2, 0, 0, 0); playerCard.add(barPlayerHP, pcGbc);

        JLabel lblPMana = new JLabel("MANA");
        lblPMana.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPMana.setForeground(COLOR_TXT_GRAY);
        pcGbc.gridy = 3; pcGbc.gridwidth = 1; pcGbc.insets = new Insets(6, 0, 0, 0); playerCard.add(lblPMana, pcGbc);

        barPlayerMana = new JProgressBar(0, 100);
        barPlayerMana.setForeground(COLOR_BLUE);
        barPlayerMana.setBackground(new Color(20, 20, 40));
        barPlayerMana.setStringPainted(true);
        barPlayerMana.setBorderPainted(false);
        barPlayerMana.setPreferredSize(new Dimension(0, 18));
        pcGbc.gridy = 4; pcGbc.gridwidth = 2; pcGbc.insets = new Insets(2, 0, 0, 0); playerCard.add(barPlayerMana, pcGbc);

        JLabel lblPXP = new JLabel("XP");
        lblPXP.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblPXP.setForeground(COLOR_TXT_GRAY);
        pcGbc.gridy = 5; pcGbc.gridwidth = 1; pcGbc.insets = new Insets(6, 0, 0, 0); playerCard.add(lblPXP, pcGbc);

        barPlayerXP = new JProgressBar(0, 100);
        barPlayerXP.setForeground(COLOR_YELLOW);
        barPlayerXP.setBackground(new Color(40, 30, 0));
        barPlayerXP.setStringPainted(true);
        barPlayerXP.setBorderPainted(false);
        barPlayerXP.setPreferredSize(new Dimension(0, 14));
        pcGbc.gridy = 6; pcGbc.gridwidth = 2; pcGbc.insets = new Insets(2, 0, 0, 0); playerCard.add(barPlayerXP, pcGbc);

        topSection.add(playerCard);

        JLabel lblVs = new JLabel("VS");
        lblVs.setFont(new Font("Monospaced", Font.BOLD, 48));
        lblVs.setForeground(COLOR_RED);
        lblVs.setHorizontalAlignment(SwingConstants.CENTER);
        topSection.add(lblVs);

        // --- Enemy card ---
        RoundedPanel enemyCard = new RoundedPanel(15, COLOR_CARD);
        enemyCard.setLayout(new GridBagLayout());
        enemyCard.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        GridBagConstraints ecGbc = new GridBagConstraints();
        ecGbc.fill = GridBagConstraints.HORIZONTAL; ecGbc.weightx = 1.0;

        lblEnemyName = new JLabel("Enemy");
        lblEnemyName.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblEnemyName.setForeground(COLOR_TXT_WHITE);
        ecGbc.gridy = 0; ecGbc.gridx = 0; enemyCard.add(lblEnemyName, ecGbc);

        lblEnemyLevel = new JLabel("LVL 1");
        lblEnemyLevel.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEnemyLevel.setForeground(COLOR_BLUE);
        lblEnemyLevel.setHorizontalAlignment(SwingConstants.RIGHT);
        ecGbc.gridx = 1; enemyCard.add(lblEnemyLevel, ecGbc);

        JLabel lblEHP = new JLabel("HP");
        lblEHP.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEHP.setForeground(COLOR_TXT_GRAY);
        ecGbc.gridy = 1; ecGbc.gridx = 0; ecGbc.insets = new Insets(8, 0, 0, 0); enemyCard.add(lblEHP, ecGbc);

        barEnemyHP = new JProgressBar(0, 100);
        barEnemyHP.setForeground(COLOR_RED);
        barEnemyHP.setBackground(new Color(40, 20, 20));
        barEnemyHP.setStringPainted(true);
        barEnemyHP.setBorderPainted(false);
        barEnemyHP.setPreferredSize(new Dimension(0, 18));
        ecGbc.gridy = 2; ecGbc.gridwidth = 2; ecGbc.insets = new Insets(2, 0, 0, 0); enemyCard.add(barEnemyHP, ecGbc);

        JLabel lblEnemyStatus = new JLabel("STATUS");
        lblEnemyStatus.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblEnemyStatus.setForeground(COLOR_TXT_GRAY);
        ecGbc.gridy = 3; ecGbc.gridwidth = 1; ecGbc.insets = new Insets(8, 0, 0, 0); enemyCard.add(lblEnemyStatus, ecGbc);

        lblEnemyStatusValue = new JLabel("Normal");
        lblEnemyStatusValue.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblEnemyStatusValue.setForeground(COLOR_GREEN);
        lblEnemyStatusValue.setHorizontalAlignment(SwingConstants.RIGHT);
        ecGbc.gridx = 1; enemyCard.add(lblEnemyStatusValue, ecGbc);

        topSection.add(enemyCard);
        p.add(topSection, BorderLayout.NORTH);

        // --- CENTER: Sprites ---
        battleAnimationPanel = new RoundedPanel(15, COLOR_CARD);
        battleAnimationPanel.setLayout(new GridLayout(1, 3, 10, 0));
        battleAnimationPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        lblPlayerSprite = new JLabel();
        lblPlayerSprite.setHorizontalAlignment(SwingConstants.CENTER);
        battleAnimationPanel.add(lblPlayerSprite);

        lblAnimacija = new JLabel("[ STATUS BORBE ]");
        lblAnimacija.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblAnimacija.setForeground(COLOR_TXT_GRAY);
        lblAnimacija.setHorizontalAlignment(SwingConstants.CENTER);
        battleAnimationPanel.add(lblAnimacija);

        lblEnemySprite = new JLabel();
        lblEnemySprite.setHorizontalAlignment(SwingConstants.CENTER);
        battleAnimationPanel.add(lblEnemySprite);

        p.add(battleAnimationPanel, BorderLayout.CENTER);

        // --- BOTTOM: Log + Buttons ---
        JPanel bottomSection = new JPanel(new BorderLayout(15, 15));
        bottomSection.setOpaque(false);
        bottomSection.setPreferredSize(new Dimension(1200, 250));

        logPane = new JTextPane();
        logPane.setContentType("text/html");
        logPane.setEditable(false);
        logPane.setBackground(COLOR_CARD);
        logPane.setForeground(COLOR_TXT_WHITE);
        JScrollPane logScroll = new JScrollPane(logPane);
        logScroll.setBorder(new BubbleBorder(COLOR_CARD, 2, 12));
        logScroll.setPreferredSize(new Dimension(1200, 155));
        bottomSection.add(logScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(1200, 55));

        btnAttack = createStyledButton("NAPAD", COLOR_RED, 230, 55);
        btnAttack.addActionListener(e -> executePlayerTurn("ATTACK", -1));
        buttonPanel.add(btnAttack);

        btnSpecial = createStyledButton("SPECIJALI", COLOR_BLUE, 230, 55);
        btnSpecial.addActionListener(e -> openSpecialMenu());
        buttonPanel.add(btnSpecial);

        btnItem = createStyledButton("ITEM", COLOR_GREEN, 200, 55);
        btnItem.addActionListener(e -> openBattleItemMenu());
        buttonPanel.add(btnItem);

        btnFlee = createStyledButton("BJEŽI", new Color(75, 85, 99), 200, 55);
        btnFlee.addActionListener(e -> attemptEscape());
        buttonPanel.add(btnFlee);

        bottomSection.add(buttonPanel, BorderLayout.SOUTH);
        p.add(bottomSection, BorderLayout.SOUTH);
        return p;
    }

    // ==================== BOSS MODE SCREEN ====================
    private JPanel buildBossModeScreen() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(50, 80, 50, 80));

        JLabel lblTitle = new JLabel("⚔ BOSS CHALLENGE ⚔");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 36));
        lblTitle.setForeground(COLOR_PURPLE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblTitle, BorderLayout.NORTH);

        JLabel lblSubtitle = new JLabel("Bossi su izrazito teški. Pobijedi ih za SPECIAL LOOT!");
        lblSubtitle.setFont(new Font("SansSerif", Font.ITALIC, 14));
        lblSubtitle.setForeground(COLOR_TXT_GRAY);
        lblSubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bossListPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        bossListPanel.setOpaque(false);

        String[][] bossInfo = {
                {"💀 LICH KING", "Undead sorcerer. Soul Drain healing, massive HP.", "LICH_KING", "#A855F7"},
                {"🐉 DRAGON LORD", "Ancient dragon. Fire Breath every 2nd turn, ignores DEF.", "DRAGON_LORD", "#EF4444"},
                {"👤 SHADOW LORD", "Shadow assassin. Fastest boss, double strikes.", "SHADOW_LORD", "#6B7280"}
        };

        for (String[] boss : bossInfo) {
            RoundedPanel bossRow = new RoundedPanel(12, COLOR_CARD);
            bossRow.setLayout(new BorderLayout(20, 0));
            bossRow.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            JLabel nameLabel = new JLabel(boss[0]);
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            nameLabel.setForeground(Color.decode(boss[3]));
            JLabel descLabel = new JLabel(boss[1]);
            descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            descLabel.setForeground(COLOR_TXT_GRAY);
            infoPanel.add(nameLabel);
            infoPanel.add(descLabel);
            bossRow.add(infoPanel, BorderLayout.CENTER);

            JButton btnChallenge = createStyledButton("CHALLENGE", COLOR_PURPLE, 130, 40);
            final String bossKey = boss[2];
            btnChallenge.addActionListener(e -> startBossFight(bossKey));
            bossRow.add(btnChallenge, BorderLayout.EAST);
            bossListPanel.add(bossRow);
        }

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(lblSubtitle, BorderLayout.NORTH);
        center.add(bossListPanel, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        JButton btnBack = createStyledButton("BACK TO MENU", COLOR_RED, 220, 42);
        btnBack.addActionListener(e -> cardLayout.show(cardsPanel, "MAIN_MENU"));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setOpaque(false);
        south.add(btnBack);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private void startBossFight(String bossKey) {
        if (activePlayer == null) return;
        int playerLvl = activePlayer.getLevel();
        switch (bossKey) {
            case "LICH_KING":    activeEnemy = new LichKing(playerLvl);   break;
            case "DRAGON_LORD":  activeEnemy = new DragonLord(playerLvl); break;
            case "SHADOW_LORD":  activeEnemy = new ShadowLord(playerLvl); break;
            default: return;
        }
        isBossMode = true;
        enemyPoisonTurns = 0;
        playerDodgeNext = false;
        enemySlowed = false;
        logPane.setText("");
        refreshBattleStats();
        logMessage("<font color='#A855F7'><b>⚔ BOSS BATTLE STARTED! " + activeEnemy.getName() + " pojavljuje se pred tobom!</b></font>");
        cardLayout.show(cardsPanel, "BATTLE_SCREEN");
    }

    // ==================== CHARACTER SCREEN ====================
    private JPanel buildCharacterScreen() {
        characterScreenPanel = new JPanel(new BorderLayout(20, 20));
        characterScreenPanel.setBackground(COLOR_BG);
        characterScreenPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        return characterScreenPanel;
    }

    private void refreshCharacterScreen() {
        if (characterScreenPanel == null || activePlayer == null) return;
        characterScreenPanel.removeAll();

        // --- TITLE ---
        JLabel lblTitle = new JLabel("CHARACTER & INVENTORY");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TXT_WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        characterScreenPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        mainPanel.setOpaque(false);

        // === LEFT: Character portrait + stats ===
        RoundedPanel statsCard = new RoundedPanel(15, COLOR_CARD);
        statsCard.setLayout(new BorderLayout(0, 10));
        statsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Character image
        JLabel charImage = new JLabel();
        charImage.setHorizontalAlignment(SwingConstants.CENTER);
        String pClass = activePlayer.getClassName().toLowerCase().replace(" ", "_");
        ImageIcon sprite = loadSprite("/resources/" + pClass + ".png");
        if (sprite != null) {
            Image img = sprite.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            charImage.setIcon(new ImageIcon(img));
        } else {
            charImage.setText("[ " + activePlayer.getClassName() + " ]");
            charImage.setFont(new Font("SansSerif", Font.BOLD, 16));
            charImage.setForeground(COLOR_YELLOW);
        }

        JPanel portraitPanel = new JPanel(new BorderLayout());
        portraitPanel.setOpaque(false);
        portraitPanel.add(charImage, BorderLayout.CENTER);

        JLabel nameTitle = new JLabel(activePlayer.getName(), SwingConstants.CENTER);
        nameTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameTitle.setForeground(COLOR_YELLOW);
        portraitPanel.add(nameTitle, BorderLayout.SOUTH);

        statsCard.add(portraitPanel, BorderLayout.NORTH);

        // Stats text
        int expNeeded = activePlayer.getExpNeeded();
        String statsText = "<html><div style='font-family:monospace; font-size:13px; color:#F3F4F6;'>"
                + "<b style='color:#F59E0B'>" + activePlayer.getClassName().toUpperCase() + "  — Lv. " + activePlayer.getLevel() + "</b><br><br>"
                + "❤ HP:    <b>" + activePlayer.getHp() + " / " + activePlayer.getMaxHp() + "</b><br>"
                + "💧 Mana:  <b>" + activePlayer.getMana() + " / " + activePlayer.getMaxMana() + "</b><br>"
                + "⭐ XP:    <b>" + activePlayer.getExp() + " / " + expNeeded + "</b><br><br>"
                + "⚔ ATK:   <b>" + activePlayer.getAtk() + "</b><br>"
                + "🛡 DEF:   <b>" + activePlayer.getDef() + "</b><br>"
                + "💨 SPD:   <b>" + activePlayer.getSpd() + "</b><br>"
                + "</div></html>";

        JLabel lblStats = new JLabel(statsText);
        lblStats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        statsCard.add(lblStats, BorderLayout.CENTER);

        mainPanel.add(statsCard);

        // === CENTER: Equipment slots ===
        RoundedPanel equipCard = new RoundedPanel(15, COLOR_CARD);
        equipCard.setLayout(new GridBagLayout());
        equipCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints eqGbc = new GridBagConstraints();
        eqGbc.fill = GridBagConstraints.HORIZONTAL;
        eqGbc.weightx = 1.0;
        eqGbc.insets = new Insets(5, 0, 5, 0);

        JLabel lblEquipTitle = new JLabel("EQUIPPED ITEMS");
        lblEquipTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblEquipTitle.setForeground(COLOR_YELLOW);
        eqGbc.gridy = 0; equipCard.add(lblEquipTitle, eqGbc);

        // Weapon slot
        JLabel lblWeaponSlotTitle = new JLabel("⚔ WEAPON SLOT");
        lblWeaponSlotTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblWeaponSlotTitle.setForeground(COLOR_TXT_GRAY);
        eqGbc.gridy = 1; eqGbc.insets = new Insets(12, 0, 2, 0); equipCard.add(lblWeaponSlotTitle, eqGbc);

        JButton btnWeaponSlot = createEquipSlotButton(activePlayer.getEquippedWeapon(), "WEAPON");
        eqGbc.gridy = 2; eqGbc.insets = new Insets(2, 0, 5, 0);
        equipCard.add(btnWeaponSlot, eqGbc);
        btnWeaponSlot.addActionListener(e -> openEquipPicker("WEAPON", btnWeaponSlot));

        // Armor slot
        JLabel lblArmorSlotTitle = new JLabel("🛡 ARMOR SLOT");
        lblArmorSlotTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblArmorSlotTitle.setForeground(COLOR_TXT_GRAY);
        eqGbc.gridy = 3; eqGbc.insets = new Insets(12, 0, 2, 0); equipCard.add(lblArmorSlotTitle, eqGbc);

        JButton btnArmorSlot = createEquipSlotButton(activePlayer.getEquippedArmor(), "ARMOR");
        eqGbc.gridy = 4; eqGbc.insets = new Insets(2, 0, 5, 0);
        equipCard.add(btnArmorSlot, eqGbc);
        btnArmorSlot.addActionListener(e -> openEquipPicker("ARMOR", btnArmorSlot));

        // Filler
        JPanel filler = new JPanel(); filler.setOpaque(false);
        eqGbc.gridy = 5; eqGbc.weighty = 1.0; equipCard.add(filler, eqGbc);
        eqGbc.weighty = 0;

        JButton btnUnequipWeapon = createStyledButton("Ukloni oružje", new Color(80, 40, 40), 200, 30);
        btnUnequipWeapon.setEnabled(activePlayer.getEquippedWeapon() != null);
        btnUnequipWeapon.addActionListener(e -> {
            if (activePlayer.getEquippedWeapon() != null) {
                activePlayer.unequipItem(activePlayer.getEquippedWeapon());
                refreshCharacterScreen();
                cardLayout.show(cardsPanel, "CHARACTER_SCREEN");
            }
        });
        eqGbc.gridy = 6; equipCard.add(btnUnequipWeapon, eqGbc);

        JButton btnUnequipArmor = createStyledButton("Ukloni oklop", new Color(80, 40, 40), 200, 30);
        btnUnequipArmor.setEnabled(activePlayer.getEquippedArmor() != null);
        btnUnequipArmor.addActionListener(e -> {
            if (activePlayer.getEquippedArmor() != null) {
                activePlayer.unequipItem(activePlayer.getEquippedArmor());
                refreshCharacterScreen();
                cardLayout.show(cardsPanel, "CHARACTER_SCREEN");
            }
        });
        eqGbc.gridy = 7; equipCard.add(btnUnequipArmor, eqGbc);

        mainPanel.add(equipCard);

        // === RIGHT: Inventory ===
        RoundedPanel invCard = new RoundedPanel(15, COLOR_CARD);
        invCard.setLayout(new BorderLayout(0, 10));
        invCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblInvTitle = new JLabel("INVENTORY (" + activePlayer.getInventory().size() + " items)");
        lblInvTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblInvTitle.setForeground(COLOR_GREEN);
        invCard.add(lblInvTitle, BorderLayout.NORTH);

        JPanel invList = new JPanel();
        invList.setOpaque(false);
        invList.setLayout(new BoxLayout(invList, BoxLayout.Y_AXIS));

        if (activePlayer.getInventory().isEmpty()) {
            JLabel emptyLbl = new JLabel("Inventar je prazan.");
            emptyLbl.setForeground(COLOR_TXT_GRAY);
            emptyLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            invList.add(emptyLbl);
        } else {
            for (Item item : new ArrayList<>(activePlayer.getInventory())) {
                JPanel itemRow = new JPanel(new BorderLayout(8, 0));
                itemRow.setOpaque(false);
                itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                itemRow.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));

                Color itemColor = item.getType().equals("WEAPON") ? COLOR_RED :
                        item.getType().equals("ARMOR")  ? COLOR_BLUE :
                                item.getType().equals("HEALTH_POTION") ? COLOR_GREEN : COLOR_PURPLE;

                JLabel itemLbl = new JLabel(item.toString());
                itemLbl.setForeground(itemColor);
                itemLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
                itemRow.add(itemLbl, BorderLayout.CENTER);

                if (!item.isConsumable()) {
                    JButton btnEquip = createStyledButton("Opremi", COLOR_GREEN, 80, 28);
                    btnEquip.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    btnEquip.addActionListener(e -> {
                        activePlayer.equipItem(item);
                        refreshCharacterScreen();
                        cardLayout.show(cardsPanel, "CHARACTER_SCREEN");
                    });
                    itemRow.add(btnEquip, BorderLayout.EAST);
                }
                invList.add(itemRow);
            }
        }

        JScrollPane invScroll = new JScrollPane(invList);
        invScroll.setOpaque(false);
        invScroll.getViewport().setOpaque(false);
        invScroll.setBorder(null);
        invCard.add(invScroll, BorderLayout.CENTER);
        mainPanel.add(invCard);

        characterScreenPanel.add(mainPanel, BorderLayout.CENTER);

        JButton btnBack = createStyledButton("BACK TO MENU", COLOR_RED, 220, 42);
        btnBack.addActionListener(e -> cardLayout.show(cardsPanel, "MAIN_MENU"));
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setOpaque(false);
        southPanel.add(btnBack);
        characterScreenPanel.add(southPanel, BorderLayout.SOUTH);

        characterScreenPanel.revalidate();
        characterScreenPanel.repaint();
    }

    private JButton createEquipSlotButton(Item item, String slotType) {
        String txt = item != null ? item.toString() : "[ " + (slotType.equals("WEAPON") ? "Nema oružja" : "Nema oklopa") + " — klikni za opremu ]";
        Color bg = item != null ? (slotType.equals("WEAPON") ? new Color(60, 20, 20) : new Color(20, 30, 60)) : new Color(30, 33, 55);
        JButton btn = new JButton(txt);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(item != null ? COLOR_TXT_WHITE : COLOR_TXT_GRAY);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 42));
        return btn;
    }

    private void openEquipPicker(String slotType, JButton slotBtn) {
        ArrayList<Item> candidates = new ArrayList<>();
        for (Item it : activePlayer.getInventory()) {
            if (it.getType().equals(slotType)) candidates.add(it);
        }
        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nema predmeta tog tipa u inventaru!", "Inventar", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] opts = candidates.stream().map(Item::toString).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this, "Odaberi što opremiti:", "Oprema — " + slotType, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
        if (choice != null) {
            for (Item it : candidates) {
                if (it.toString().equals(choice)) { activePlayer.equipItem(it); break; }
            }
            refreshCharacterScreen();
            cardLayout.show(cardsPanel, "CHARACTER_SCREEN");
        }
    }

    // ==================== SAVE/LOAD SCREEN ====================
    private JPanel buildSaveLoadScreen() {
        JPanel p = new JPanel(new BorderLayout(20, 20));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JLabel lblTitle = new JLabel("SAVE & LOAD SYSTEM");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitle.setForeground(COLOR_TXT_WHITE);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lblTitle, BorderLayout.NORTH);

        JPanel slotsPanel = new JPanel(new GridLayout(5, 1, 0, 15));
        slotsPanel.setOpaque(false);
        p.add(slotsPanel, BorderLayout.CENTER);

        JButton btnBack = createStyledButton("RETURN TO MAIN MENU", COLOR_RED, 300, 45);
        btnBack.addActionListener(e -> cardLayout.show(cardsPanel, "MAIN_MENU"));
        p.add(btnBack, BorderLayout.SOUTH);

        return p;
    }

    private void refreshSaveLoadScreen() {
        JPanel saveLoadPanel = (JPanel) cardsPanel.getComponent(3);
        BorderLayout layout = (BorderLayout) saveLoadPanel.getLayout();
        JPanel slotsPanel = (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
        slotsPanel.removeAll();

        for (int i = 1; i <= 5; i++) {
            final int slotIndex = i;
            PlayerCharacter loadedHero = SaveManager.load(slotIndex);

            JPanel slotRow = new JPanel(new BorderLayout(20, 0));
            slotRow.setBackground(COLOR_CARD);
            slotRow.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            JLabel lblSlotInfo = new JLabel();
            lblSlotInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
            lblSlotInfo.setForeground(COLOR_TXT_WHITE);
            if (loadedHero != null) {
                lblSlotInfo.setText("Slot " + slotIndex + ": " + loadedHero.getName() + " — " + loadedHero.getClassName() + " (Level " + loadedHero.getLevel() + ")");
            } else {
                lblSlotInfo.setText("Slot " + slotIndex + ": [ EMPTY SLOT ]");
            }
            slotRow.add(lblSlotInfo, BorderLayout.WEST);

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actionPanel.setOpaque(false);

            JButton btnSave = createStyledButton("SAVE", COLOR_GREEN, 100, 35);
            btnSave.setEnabled(activePlayer != null);
            btnSave.addActionListener(e -> {
                if (loadedHero != null) {
                    int ans = JOptionPane.showConfirmDialog(this, "Slot već ima podatke. Prepisati?", "Potvrda", JOptionPane.YES_NO_OPTION);
                    if (ans != JOptionPane.YES_OPTION) return;
                }
                if (SaveManager.save(activePlayer, slotIndex)) {
                    currentSaveSlot = slotIndex;
                    JOptionPane.showMessageDialog(this, "Igra spremljena na Slot " + slotIndex + "!");
                    refreshSaveLoadScreen();
                }
            });
            actionPanel.add(btnSave);

            JButton btnLoad = createStyledButton("LOAD", COLOR_BLUE, 100, 35);
            btnLoad.setEnabled(loadedHero != null);
            btnLoad.addActionListener(e -> {
                activePlayer = SaveManager.load(slotIndex);
                if (activePlayer != null) {
                    currentSaveSlot = slotIndex;
                    JOptionPane.showMessageDialog(this, "Igra učitana sa Slota " + slotIndex + "!");
                    updateMainMenuButtons();
                    cardLayout.show(cardsPanel, "MAIN_MENU");
                }
            });
            actionPanel.add(btnLoad);

            slotRow.add(actionPanel, BorderLayout.EAST);
            slotsPanel.add(slotRow);
        }
        slotsPanel.revalidate();
        slotsPanel.repaint();
    }

    // ==================== ENEMY SPAWNING ====================
    private void spawnNewEnemy() {
        int playerLvl = (activePlayer != null) ? activePlayer.getLevel() : 1;
        Random r = new Random();
        switch (r.nextInt(5)) {
            case 0: activeEnemy = new Skeleton(playerLvl);   break;
            case 1: activeEnemy = new Goblin(playerLvl);     break;
            case 2: activeEnemy = new Orc(playerLvl);        break;
            case 3: activeEnemy = new DarkElf(playerLvl);    break;
            default: activeEnemy = new DragonWhelp(playerLvl); break;
        }
        enemyPoisonTurns = 0; enemyPoisonDmg = 0;
        playerDodgeNext = false; enemySlowed = false;
    }

    private ImageIcon loadSprite(String path) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL);
        return null;
    }

    // ==================== BATTLE STATS REFRESH ====================
    private void refreshBattleStats() {
        if (activePlayer == null || activeEnemy == null) return;

        lblPlayerName.setText(activePlayer.getName());
        lblPlayerClass.setText(activePlayer.getClassName().toUpperCase() + "  [Lvl " + activePlayer.getLevel() + "]");

        barPlayerHP.setMaximum(activePlayer.getMaxHp());
        barPlayerHP.setValue(activePlayer.getHp());
        barPlayerHP.setString(activePlayer.getHp() + " / " + activePlayer.getMaxHp());

        barPlayerMana.setMaximum(activePlayer.getMaxMana());
        barPlayerMana.setValue(activePlayer.getMana());
        barPlayerMana.setString(activePlayer.getMana() + " / " + activePlayer.getMaxMana());

        int expNeeded = activePlayer.getExpNeeded();
        barPlayerXP.setMaximum(expNeeded);
        barPlayerXP.setValue(Math.min(activePlayer.getExp(), expNeeded));
        barPlayerXP.setString(activePlayer.getExp() + " / " + expNeeded + " XP");

        lblEnemyName.setText(activeEnemy.getName() + (activeEnemy.isBoss() ? " 👑" : ""));
        lblEnemyLevel.setText("LVL " + activeEnemy.getLevel());
        barEnemyHP.setMaximum(activeEnemy.getMaxHp());
        barEnemyHP.setValue(activeEnemy.getHp());
        barEnemyHP.setString(activeEnemy.getHp() + " / " + activeEnemy.getMaxHp());

        // Enemy status
        String statusText = "Normal";
        Color statusColor = COLOR_GREEN;
        if (enemyPoisonTurns > 0) { statusText = "☠ Trovan (" + enemyPoisonTurns + " poteza)"; statusColor = COLOR_PURPLE; }
        else if (enemySlowed)     { statusText = "❄ Usporen"; statusColor = COLOR_BLUE; }
        lblEnemyStatusValue.setText(statusText);
        lblEnemyStatusValue.setForeground(statusColor);

        // Sprites
        String pClass = activePlayer.getClassName().toLowerCase().replace(" ", "_");
        ImageIcon pSprite = loadSprite("/resources/" + pClass + ".png");
        if (pSprite != null) {
            Image img = pSprite.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            lblPlayerSprite.setIcon(new ImageIcon(img));
            lblPlayerSprite.setText("");
        } else {
            lblPlayerSprite.setIcon(null);
            lblPlayerSprite.setText("🧙");
            lblPlayerSprite.setFont(new Font("SansSerif", Font.PLAIN, 80));
        }

        String eName = activeEnemy.getName().toLowerCase().replace(" ", "_");
        ImageIcon eSprite = loadSprite("/resources/" + eName + ".png");
        if (eSprite != null) {
            Image img = eSprite.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
            lblEnemySprite.setIcon(new ImageIcon(img));
            lblEnemySprite.setText("");
        } else {
            lblEnemySprite.setIcon(null);
            String emojiMap;
            switch (activeEnemy.getName()) {
                case "Skeleton":     emojiMap = "💀"; break;
                case "Goblin":       emojiMap = "👺"; break;
                case "Orc":          emojiMap = "👹"; break;
                case "Dark Elf":     emojiMap = "🧝"; break;
                case "Dragon Whelp": emojiMap = "🐉"; break;
                case "Lich King":    emojiMap = "☠";  break;
                case "Dragon Lord":  emojiMap = "🔥"; break;
                case "Shadow Lord":  emojiMap = "👤"; break;
                default:             emojiMap = "👾"; break;
            }
            lblEnemySprite.setText(emojiMap);
            lblEnemySprite.setFont(new Font("SansSerif", Font.PLAIN, 80));
        }

        int baseAtk = activePlayer.getAtk();
        String attackName = activePlayer instanceof Warrior ? "Strike" : activePlayer instanceof Mage ? "Fire Bolt" : "Stab";
        btnAttack.setText(attackName + " (" + baseAtk + " DMG)");
        btnSpecial.setText("✦ SPECIJALI");
        btnSpecial.setEnabled(true);
        toggleControls(true);
    }

    // ==================== PLAYER TURN ====================
    private void openSpecialMenu() {
        List<String[]> abilities = activePlayer.getExtraAbilities();
        if (abilities.isEmpty()) { JOptionPane.showMessageDialog(this, "Nema specijalnih sposobnosti!"); return; }

        JDialog dialog = new JDialog(this, "Specijalne sposobnosti", true);
        dialog.setSize(480, 420);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(COLOR_BG);

        JLabel title = new JLabel("Odaberi specijalnu sposobnost:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(COLOR_TXT_WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        dialog.add(title, BorderLayout.NORTH);

        JPanel abilityPanel = new JPanel(new GridLayout(abilities.size(), 1, 0, 8));
        abilityPanel.setBackground(COLOR_BG);
        abilityPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        final int[] chosenIndex = {-1};

        for (int i = 0; i < abilities.size(); i++) {
            String[] ab = abilities.get(i);
            int cost = Integer.parseInt(ab[1]);
            boolean canAfford = activePlayer.getMana() >= cost;

            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setBackground(COLOR_CARD);
            row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            JLabel nameLbl = new JLabel(ab[0]);
            nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLbl.setForeground(canAfford ? COLOR_TXT_WHITE : COLOR_TXT_GRAY);
            JLabel descLbl = new JLabel(ab[2]);
            descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
            descLbl.setForeground(COLOR_TXT_GRAY);
            textPanel.add(nameLbl);
            textPanel.add(descLbl);
            row.add(textPanel, BorderLayout.CENTER);

            JLabel costLbl = new JLabel(ab[1] + " MP");
            costLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
            costLbl.setForeground(canAfford ? COLOR_BLUE : COLOR_RED);
            row.add(costLbl, BorderLayout.EAST);

            final int idx = i;
            row.setCursor(canAfford ? new Cursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
            if (canAfford) {
                row.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        chosenIndex[0] = idx;
                        dialog.dispose();
                    }
                    public void mouseEntered(java.awt.event.MouseEvent e) { row.setBackground(new Color(35, 40, 70)); }
                    public void mouseExited(java.awt.event.MouseEvent e)  { row.setBackground(COLOR_CARD); }
                });
            }
            abilityPanel.add(row);
        }

        JScrollPane sp = new JScrollPane(abilityPanel);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        dialog.add(sp, BorderLayout.CENTER);

        JButton btnCancel = createStyledButton("Odustani", new Color(75, 85, 99), 150, 38);
        btnCancel.addActionListener(e -> dialog.dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.setBackground(COLOR_BG);
        south.add(btnCancel);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.setVisible(true);

        if (chosenIndex[0] >= 0) {
            executePlayerTurn("SPECIAL", chosenIndex[0]);
        }
    }

    private void executePlayerTurn(String actionType, int specialIndex) {
        toggleControls(false);
        int damageDealt = 0;
        String logMsg = "";

        if ("ATTACK".equals(actionType)) {
            triggerHitAndShakeAnimation(lblEnemySprite, "SWORD_SLASH");
            int raw = activePlayer.basicAttack(activeEnemy);
            boolean isCrit = raw < 0;
            damageDealt = Math.abs(raw);
            if (isCrit) {
                logMsg = "<font color='#EF4444'><b>" + activePlayer.getName() + "</b></font> udara kritikal! <font color='#F59E0B'>⚡ " + damageDealt + " CRIT DMG!</font>";
            } else {
                logMsg = "<font color='#EF4444'><b>" + activePlayer.getName() + "</b></font> napada za <font color='#F59E0B'>" + damageDealt + " dmg</font>.";
            }
        } else if ("SPECIAL".equals(actionType) && specialIndex >= 0) {
            triggerHitAndShakeAnimation(lblEnemySprite, "SPECIAL_BURST");
            List<String[]> abilities = activePlayer.getExtraAbilities();
            String abilityName = abilities.get(specialIndex)[0];
            int result = activePlayer.useExtraAbility(specialIndex, activeEnemy);

            if (result == -1) {
                logMessage("<font color='#EF4444'>Nedovoljno mane!</font>");
                toggleControls(true);
                return;
            } else if (result == 0) {
                // War Cry / buff
                if (abilityName.equals("War Cry")) {
                    logMsg = "<font color='#F59E0B'><b>" + activePlayer.getName() + "</b> izvikuje War Cry! +ATK za ovaj potez!</font>";
                } else {
                    logMsg = "<font color='#3B82F6'><b>" + activePlayer.getName() + "</b> koristi <b>" + abilityName + "</b>!</font>";
                }
            } else if (result == -1) {
                // Taunt
                activeEnemy.applyTaunt();
                logMsg = "<font color='#F59E0B'><b>" + activePlayer.getName() + "</b> provocira neprijatelja! Smanji napad za 30%!</font>";
            } else if (result == -2) {
                // Mana Drain
                int drained = Math.min(25, activeEnemy.getAtk());
                activePlayer.setMana(activePlayer.getMana() + drained);
                logMsg = "<font color='#A855F7'><b>" + activePlayer.getName() + "</b> krade " + drained + " mane od " + activeEnemy.getName() + "!</font>";
            } else if (result == -3) {
                // Heal
                logMsg = "<font color='#10B981'><b>" + activePlayer.getName() + "</b> se liječi s <b>" + abilityName + "</b>!</font>";
            } else if (result == -4) {
                // Smoke Bomb — dodge next
                playerDodgeNext = true;
                logMsg = "<font color='#6B7280'><b>" + activePlayer.getName() + "</b> baca dimnu bombu! Iduće izbjegava napad!</font>";
            } else if (result == -5) {
                // Poison
                enemyPoisonTurns = 3;
                enemyPoisonDmg = 5 + activePlayer.getLevel();
                logMsg = "<font color='#A855F7'><b>" + activePlayer.getName() + "</b> truje " + activeEnemy.getName() + "! (" + enemyPoisonDmg + " dmg/potez, 3 poteza)</font>";
                damageDealt = Math.abs(result);
            } else if (result < 0) {
                // Slow (Ice Bolt)
                damageDealt = Math.abs(result);
                enemySlowed = true;
                logMsg = "<font color='#3B82F6'><b>" + activePlayer.getName() + "</b> koristi <b>" + abilityName + "</b> i nanosi <font color='#F59E0B'>" + damageDealt + " dmg</font>! Neprijatelj usporen!</font>";
            } else {
                damageDealt = result;
                logMsg = "<font color='#3B82F6'><b>" + activePlayer.getName() + "</b> koristi <b>" + abilityName + "</b> i nanosi <font color='#F59E0B'>" + damageDealt + " dmg</font>!</font>";
            }
        }

        if (!logMsg.isEmpty()) logMessage(logMsg);

        // Poison tick on enemy
        if (enemyPoisonTurns > 0) {
            int poisonDmg = enemyPoisonTurns > 0 ? enemyPoisonDmg : 0;
            activeEnemy.setHp(activeEnemy.getHp() - poisonDmg);
            enemyPoisonTurns--;
            logMessage("<font color='#A855F7'>☠ " + activeEnemy.getName() + " prima " + poisonDmg + " otrovan dmg! (" + enemyPoisonTurns + " poteza preostalo)</font>");
        }

        refreshBattleStats();

        if (!activeEnemy.isAlive()) {
            Timer victoryDelay = new Timer(1500, e -> { handleVictory(); ((Timer)e.getSource()).stop(); });
            victoryDelay.start();
            return;
        }

        Timer timer = new Timer(2000, e -> { executeEnemyTurn(); ((Timer)e.getSource()).stop(); });
        timer.start();
    }

    private void executeEnemyTurn() {
        if (!activeEnemy.isAlive()) return;

        if (playerDodgeNext) {
            playerDodgeNext = false;
            logMessage("<font color='#6B7280'>💨 " + activePlayer.getName() + " izbjegava napad " + activeEnemy.getName() + "!</font>");
        } else {
            triggerHitAndShakeAnimation(lblPlayerSprite, "ENEMY_ATTACK");
            int raw = activeEnemy.basicAttack(activePlayer);
            boolean isSpecialAttack = raw < 0;
            int dmg = Math.abs(raw);

            // Slow reduces enemy damage
            if (enemySlowed) {
                dmg = Math.max(1, (int)(dmg * 0.7));
                enemySlowed = false;
                // Apply the reduced damage
                activePlayer.setHp(activePlayer.getHp() + Math.abs(raw) - dmg); // undo full dmg, apply reduced
            }

            String attackLabel = "";
            if (isSpecialAttack) {
                if (activeEnemy instanceof LichKing)   attackLabel = "<font color='#A855F7'> [SOUL DRAIN + liječi se 15 HP]</font>";
                if (activeEnemy instanceof DragonLord)  attackLabel = "<font color='#EF4444'> [FIRE BREATH — ignorira DEF!]</font>";
                if (activeEnemy instanceof ShadowLord)  attackLabel = "<font color='#6B7280'> [SHADOW BURST — dvostruki udarac!]</font>";
                if (activeEnemy instanceof DarkElf)    attackLabel = "<font color='#3B82F6'> [DOUBLE STRIKE!]</font>";
                if (activeEnemy instanceof DragonWhelp) attackLabel = "<font color='#EF4444'> [FIRE BREATH]</font>";
            }
            logMessage("<font color='#EF4444'><b>" + activeEnemy.getName() + "</b></font> uzvraća za <font color='#F59E0B'>" + dmg + " dmg</font>." + attackLabel);
        }

        activePlayer.setMana(Math.min(activePlayer.getMaxMana(), activePlayer.getMana() + 5));
        logMessage("<font color='#3B82F6'>Regenerirano +5 mane.</font>");
        refreshBattleStats();

        if (!activePlayer.isAlive()) {
            Timer d = new Timer(1500, e -> { handleDefeat(); ((Timer)e.getSource()).stop(); });
            d.start();
            return;
        }

        Timer resetTimer = new Timer(1500, e -> {
            lblAnimacija.setText("[ STATUS BORBE ]");
            lblAnimacija.setForeground(COLOR_TXT_GRAY);
            ((Timer)e.getSource()).stop();
        });
        resetTimer.start();

        toggleControls(true);
    }

    // ==================== ITEM MENU (in battle) ====================
    private void openBattleItemMenu() {
        ArrayList<Item> usable = new ArrayList<>();
        for (Item it : activePlayer.getInventory()) {
            if (it.isConsumable()) usable.add(it);
        }

        if (usable.isEmpty()) {
            logMessage("<font color='#9CA3AF'>Nema poticija u inventaru!</font>");
            return;
        }

        String[] opts = usable.stream().map(Item::toString).toArray(String[]::new);
        String choice = (String) JOptionPane.showInputDialog(this, "Odaberi potiju:", "Potije", JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

        if (choice != null) {
            for (Item it : usable) {
                if (it.toString().equals(choice)) {
                    toggleControls(false);
                    int amount = activePlayer.usePotion(it);
                    if (it.getType().equals("HEALTH_POTION")) {
                        logMessage("<font color='#10B981'><b>" + activePlayer.getName() + "</b> pije Health Potion i liječi " + amount + " HP!</font>");
                    } else {
                        logMessage("<font color='#3B82F6'><b>" + activePlayer.getName() + "</b> pije Mana Potion i obnavlja " + amount + " mane!</font>");
                    }
                    refreshBattleStats();
                    // Using item still costs a turn
                    Timer timer = new Timer(1500, e -> { executeEnemyTurn(); ((Timer)e.getSource()).stop(); });
                    timer.start();
                    break;
                }
            }
        }
    }

    // ==================== VICTORY / DEFEAT ====================
    private void handleVictory() {
        int baseXP = isBossMode ? 150 + (activeEnemy.getLevel() * 15) : 35 + (activeEnemy.getLevel() * 5);
        logMessage("<font color='#10B981'><b>Pobjeda!</b></font> " + activeEnemy.getName() + " je poražen!");
        logMessage("Dobivaš <font color='#3B82F6'>" + baseXP + " XP</font>.");

        if (activePlayer.gainExp(baseXP)) {
            logMessage("<font color='#F59E0B'><b>LEVEL UP! Sada si razina " + activePlayer.getLevel() + "!</b></font>");
            if (activePlayer.getLevel() == 10) triggerEvolution();
        }

        Item dropped;
        if (isBossMode) {
            dropped = activeEnemy.rollBossLoot();
            logMessage("<font color='#A855F7'>⭐ BOSS LOOT! Poseban predmet ispada!</font>");
        } else {
            dropped = activeEnemy.rollLoot();
        }

        if (dropped != null) {
            String desc = dropped.isConsumable()
                    ? "Pronašao si: " + dropped.toString() + "\nDodati u inventar?"
                    : "Loot: " + dropped.toString() + "\nOpremi odmah ili spremi u inventar?";

            if (dropped.isConsumable()) {
                int ch = JOptionPane.showConfirmDialog(this, desc, "Drop!", JOptionPane.YES_NO_OPTION);
                if (ch == JOptionPane.YES_OPTION) activePlayer.getInventory().add(dropped);
            } else {
                Object[] options = {"Opremi odmah", "Spremi u inventar"};
                int choice = JOptionPane.showOptionDialog(this, desc, "Drop!", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (choice == JOptionPane.YES_OPTION) activePlayer.equipItem(dropped);
                else activePlayer.getInventory().add(dropped);
            }
        }

        autoSaveAndReturn();
    }

    private void autoSaveAndReturn() {
        String autoSaveStatus = "";
        if (currentSaveSlot == 0) {
            String[] slotOptions = new String[5];
            for (int i = 1; i <= 5; i++) {
                PlayerCharacter checkHero = SaveManager.load(i);
                slotOptions[i-1] = checkHero != null
                        ? "Slot " + i + " - " + checkHero.getName() + " (Lvl " + checkHero.getLevel() + ")"
                        : "Slot " + i + " - [ EMPTY ]";
            }
            String odabraniSlotText = (String) JOptionPane.showInputDialog(this,
                    "Pobijedili ste! Na kojem slotu želite spremiti ovog heroja?",
                    "Odabir Save Slota", JOptionPane.QUESTION_MESSAGE, null, slotOptions, slotOptions[0]);
            if (odabraniSlotText != null) {
                currentSaveSlot = Character.getNumericValue(odabraniSlotText.charAt(5));
                PlayerCharacter existingHero = SaveManager.load(currentSaveSlot);
                if (existingHero != null) {
                    int pots = JOptionPane.showConfirmDialog(this,
                            "Pažnja! Odabrali ste slot na kojem je već heroj " + existingHero.getName() + ".\nŽelite li ga prepisati?",
                            "Potvrda prepisivanja", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (pots != JOptionPane.YES_OPTION) { currentSaveSlot = 0; autoSaveStatus = " (Spremanje otkazano)"; }
                }
                if (currentSaveSlot != 0) {
                    SaveManager.save(activePlayer, currentSaveSlot);
                    autoSaveStatus = " (Spremljeno na Slot " + currentSaveSlot + ")";
                }
            } else {
                autoSaveStatus = " (Niste odabrali slot)";
            }
        } else {
            SaveManager.save(activePlayer, currentSaveSlot);
            autoSaveStatus = " (Auto-spravljeno na Slot " + currentSaveSlot + ")";
        }

        JOptionPane.showMessageDialog(this, "Vraćate se u Main Menu kako biste se odmorili." + autoSaveStatus, "Bitka završena", JOptionPane.INFORMATION_MESSAGE);
        lblAnimacija.setText("[ STATUS BORBE ]");
        lblAnimacija.setForeground(COLOR_TXT_GRAY);
        isBossMode = false;
        updateMainMenuButtons();
        cardLayout.show(cardsPanel, "MAIN_MENU");
    }

    private void handleDefeat() {
        JOptionPane.showMessageDialog(this, "Tvoj heroj je poražen! Vraćaš se u Main Menu. Progress NIJE izgubljen!", "Poraz", JOptionPane.WARNING_MESSAGE);
        activePlayer.setHp(activePlayer.getMaxHp() / 2);
        activePlayer.setMana(activePlayer.getMaxMana());
        lblAnimacija.setText("[ STATUS BORBE ]");
        lblAnimacija.setForeground(COLOR_TXT_GRAY);
        isBossMode = false;
        updateMainMenuButtons();
        cardLayout.show(cardsPanel, "MAIN_MENU");
    }

    private void attemptEscape() {
        if (isBossMode) {
            logMessage("<font color='#EF4444'>Ne možeš pobjeći iz Boss bitke!</font>");
            return;
        }
        if (new Random().nextDouble() < 0.6) {
            JOptionPane.showMessageDialog(this, "Uspješno bježiš iz borbe natrag u Main Menu!", "Bijeg uspio", JOptionPane.INFORMATION_MESSAGE);
            isBossMode = false;
            updateMainMenuButtons();
            cardLayout.show(cardsPanel, "MAIN_MENU");
        } else {
            logMessage("<font color='#EF4444'>Bijeg neuspješan! Neprijatelj te blokira!</font>");
            toggleControls(false);
            Timer timer = new Timer(1000, e -> { executeEnemyTurn(); ((Timer)e.getSource()).stop(); });
            timer.start();
        }
    }

    private void triggerEvolution() {
        if (activePlayer instanceof Warrior && !(activePlayer instanceof EliteWarrior)) {
            activePlayer = new EliteWarrior((Warrior) activePlayer);
            JOptionPane.showMessageDialog(this, "⚔ Evolucija u Elite Warrior! Otključane sve napredne sposobnosti!");
        } else if (activePlayer instanceof Mage && !(activePlayer instanceof ArchMage)) {
            activePlayer = new ArchMage((Mage) activePlayer);
            JOptionPane.showMessageDialog(this, "🔥 Evolucija u Arch Mage! Otključane sve napredne sposobnosti!");
        } else if (activePlayer instanceof Rogue && !(activePlayer instanceof ShadowRogue)) {
            activePlayer = new ShadowRogue((Rogue) activePlayer);
            JOptionPane.showMessageDialog(this, "🗡 Evolucija u Shadow Rogue! Otključane sve napredne sposobnosti!");
        }
    }

    private void toggleControls(boolean enabled) {
        btnAttack.setEnabled(enabled);
        btnSpecial.setEnabled(enabled);
        btnItem.setEnabled(enabled);
        btnFlee.setEnabled(enabled && !isBossMode);
    }

    private void logMessage(String rawHtmlText) {
        String currentHtml = logPane.getText();
        int bodyStart = currentHtml.indexOf("<body>");
        int bodyEnd   = currentHtml.indexOf("</body>");
        String bodyContent = (bodyStart != -1 && bodyEnd != -1) ? currentHtml.substring(bodyStart + 6, bodyEnd) : "";
        logPane.setText("<html><head><style>body { font-family: sans-serif; font-size: 13px; color: #E5E7EB; margin: 5px; }</style></head><body>" + bodyContent + "• " + rawHtmlText + "<br></body></html>");
        logPane.setCaretPosition(logPane.getDocument().getLength());
    }

    private void triggerHitAndShakeAnimation(JLabel targetLabel, String animType) {
        if ("SWORD_SLASH".equals(animType)) {
            lblAnimacija.setText("⚔️ SWORD SLASH! ⚔️");
            lblAnimacija.setForeground(COLOR_RED);
        } else if ("SPECIAL_BURST".equals(animType)) {
            lblAnimacija.setText("🔥 SPECIAL ABILITY! 🔥");
            lblAnimacija.setForeground(COLOR_YELLOW);
        } else {
            lblAnimacija.setText("💀 PROTIVNIK NAPADA! 💀");
            lblAnimacija.setForeground(COLOR_RED);
        }

        final Point originalPos = targetLabel.getLocation();
        Timer shakeTimer = new Timer(75, new ActionListener() {
            private int count = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (count >= 10) {
                    targetLabel.setLocation(originalPos);
                    targetLabel.setVisible(true);
                    ((Timer)e.getSource()).stop();
                } else {
                    if (count % 2 == 0) { targetLabel.setLocation(originalPos.x + 12, originalPos.y); targetLabel.setVisible(false); }
                    else                { targetLabel.setLocation(originalPos.x - 12, originalPos.y); targetLabel.setVisible(true); }
                    count++;
                }
            }
        });
        shakeTimer.start();
    }

    private JButton createStyledButton(String text, Color bg, int width, int height) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(COLOR_TXT_WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { if (btn.isEnabled()) btn.setBackground(bg.brighter()); }
            public void mouseExited (java.awt.event.MouseEvent evt) { if (btn.isEnabled()) btn.setBackground(bg); }
        });
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new JavaSwingRPG().setVisible(true));
    }
}