package com.speane.tankbattles.server.network.transfers;

/**
 * Created by Evgeny Shilov on 05.04.2016.
 */
public class DeadTank implements NetworkTransfer {
    public int id;
    public int killerID;

    public DeadTank() {

    }

    public DeadTank(int id, int killerID) {
        this.id = id;
        this.killerID = killerID;
    }
}
