package com.speane.tankbattles.server.network.transfers;

/**
 * Created by Evgeny Shilov on 19.05.2016.
 */
public class HitTank implements NetworkTransfer {
    public int id;
    public int damage;
    public int shooterID;

    public HitTank() {

    }

    public HitTank(int id, int damage, int shooterID) {
        this.id = id;
        this.damage = damage;
        this.shooterID = shooterID;
    }
}
