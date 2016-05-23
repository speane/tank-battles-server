package com.speane.tankbattles.server.network.transfers;

/**
 * Created by Evgeny Shilov on 19.05.2016.
 */
public class LevelUp implements NetworkTransfer {
    public int id;
    public int level;
    public int healthPoints;

    public LevelUp() {

    }

    public LevelUp(int id, int level, int healthPoints) {
        this.id = id;
        this.level = level;
        this.healthPoints = healthPoints;
    }
}
