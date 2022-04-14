package ru.netology.lessons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameProgress implements Serializable {
    private static final long serialVersionUID = 1L;
    public static List<GameProgress> listGameProgress = new ArrayList<>();

    private int health;
    private int weapons;
    private int lvl;
    private double distance;

    public GameProgress(int health, int weapons, int lvl, double distance) {
        this.health = health;
        this.weapons = weapons;
        this.lvl = lvl;
        this.distance = distance;
        listGameProgress.add(this);
    }

    @Override
    public String toString() {
        return "GameProgress{" +
                "health=" + health +
                ", weapons=" + weapons +
                ", lvl=" + lvl +
                ", distance=" + distance +
                '}';
    }
}