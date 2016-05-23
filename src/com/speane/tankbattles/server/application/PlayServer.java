package com.speane.tankbattles.server.application;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.speane.tankbattles.server.Player;
import com.speane.tankbattles.server.help.Config;
import com.speane.tankbattles.server.network.transfers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Speane on 09.03.2016.
 */
public class PlayServer {
    private static Map<Integer, Player> players;
    private Server server;

    public PlayServer() {
        players = new HashMap<>();
    }

    private void registerClasses() {
        Kryo kryo = server.getKryo();
        kryo.register(MoveTank.class);
        kryo.register(CreatePlayer.class);
        kryo.register(ShootTank.class);
        kryo.register(DeadTank.class);
        kryo.register(HitTank.class);
        kryo.register(LevelUp.class);
        kryo.register(SendMessage.class);
    }

    private void initServer() throws IOException {
        server = new Server();
        server.start();
        server.bind(Config.PLAY_PORT);
    }

    private void setListener() {
        server.addListener(new Listener() {
            @Override
            public void received(Connection c, Object o) {
                if (o instanceof MoveTank) {
                    moveTankEvent((MoveTank) o, c);
                }
                else if (o instanceof ShootTank) {
                    shootTankEvent((ShootTank) o, c);
                }
                else if (o instanceof DeadTank) {
                    deadTankEvent((DeadTank) o, c);
                }
                else if (o instanceof LevelUp) {
                    levelUpEvent((LevelUp) o, c);
                }
                else if (o instanceof HitTank) {
                    hitTankEvent((HitTank) o, c);
                }
                else if (o instanceof SendMessage) {
                    sendMessageEvent((SendMessage) o, c);
                }
                else if (o instanceof CreatePlayer) {
                    createPlayerEvent((CreatePlayer) o, c);
                }
            }
            @Override
            public void disconnected(Connection c) {
                players.remove(c.getID());
                DeadTank deadTank = new DeadTank(c.getID(), 0);
                //deadTank.id = c.getID();
                server.sendToAllExceptTCP(deadTank.id, deadTank);
            }
        });
    }

    public void start() throws IOException, InterruptedException {
        initServer();
        registerClasses();
        setListener();
    }

    private void shootTankEvent(ShootTank shootTank, Connection connection) {
        shootTank.id = connection.getID();
        server.sendToAllExceptTCP(shootTank.id, shootTank);
    }

    private void moveTankEvent(MoveTank moveTank, Connection connection) {
        Player player = players.get(connection.getID());
        moveTank.id = connection.getID();
        player.x = moveTank.x;
        player.y = moveTank.y;
        player.rotation = moveTank.rotation;
        server.sendToAllExceptTCP(connection.getID(), moveTank);
    }

    private void deadTankEvent(DeadTank deadTank, Connection connection) {
        deadTank.id = connection.getID();
        players.remove(deadTank.id);
        server.sendToAllExceptTCP(deadTank.id, deadTank);
    }

    private void levelUpEvent(LevelUp levelUp, Connection connection) {
        levelUp.id = connection.getID();
        players.get(connection.getID()).level += levelUp.level;
        players.get(connection.getID()).healthPoints += levelUp.healthPoints;
        server.sendToAllExceptTCP(connection.getID(), levelUp);
    }

    private void hitTankEvent(HitTank hitTank, Connection connection) {
        hitTank.id = connection.getID();
        players.get(connection.getID()).healthPoints -= hitTank.damage;
        server.sendToAllExceptTCP(hitTank.id, hitTank);
    }

    private void sendMessageEvent(SendMessage sendMessage, Connection connection) {
        sendMessage.id = connection.getID();
        server.sendToAllExceptTCP(connection.getID(), sendMessage);
    }

    private void createPlayerEvent(CreatePlayer createPlayer, Connection connection) {
        Player newPlayer = new Player();
        newPlayer.x = createPlayer.x;
        newPlayer.y = createPlayer.y;
        newPlayer.rotation = createPlayer.rotation;
        newPlayer.healthPoints = createPlayer.healthPoints;
        newPlayer.level = createPlayer.level;
        newPlayer.name = createPlayer.name;

        createPlayer.id = connection.getID();

        server.sendToAllExceptTCP(connection.getID(), createPlayer);

        for (Integer key : players.keySet()) {
            Player player = players.get(key);
            /*CreatePlayer tempPlayer = new CreatePlayer();
            tempPlayer.id = key;
            tempPlayer.x = player.x;
            tempPlayer.y = player.y;
            tempPlayer.rotation = player.rotation;
            tempPlayer.healthPoints = player.healthPoints;
            tempPlayer.level = player.level;
            tempPlayer.name = player.name;*/
            CreatePlayer tempPlayer = new CreatePlayer(key, player.x, player.y, player.rotation, player.level,
                    player.healthPoints, player.name);
            connection.sendTCP(tempPlayer);
        }
        players.put(connection.getID(), newPlayer);
    }
}
