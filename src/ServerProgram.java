import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.speane.tankbattles.network.transfers.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Speane on 09.03.2016.
 */
public class ServerProgram {
    private static Map<Integer, Player> players;
    public static void main(String[] args) throws IOException, InterruptedException {
        players = new HashMap<Integer, Player>();

        final Server server = new Server();
        server.start();
        server.bind(7777);

        Kryo kryo = server.getKryo();
        kryo.register(MoveTank.class);
        kryo.register(CreatePlayer.class);
        kryo.register(ShootTank.class);
        kryo.register(DeadTank.class);
        kryo.register(HitTank.class);
        kryo.register(LevelUp.class);

        server.addListener(new Listener() {
            public void connected (Connection connection) {
                System.out.println("Connected: " + connection.getRemoteAddressTCP());
            }
            @Override
            public void received(Connection c, Object o) {
                if (o instanceof MoveTank) {
                    MoveTank moveTank = (MoveTank) o;
                    Player player = players.get(c.getID());
                    moveTank.id = c.getID();
                    player.x = moveTank.x;
                    player.y = moveTank.y;
                    player.rotation = moveTank.rotation;
                    server.sendToAllExceptTCP(c.getID(), moveTank);
                }
                else if (o instanceof ShootTank) {
                    System.out.println("Shoot " + c.getID());
                    ShootTank shootTank = (ShootTank) o;
                    shootTank.id = c.getID();
                    server.sendToAllExceptTCP(shootTank.id, shootTank);
                }
                else if (o instanceof DeadTank) {
                    System.out.println("Dead " + c.getID());
                    DeadTank deadTank = (DeadTank) o;
                    deadTank.id = c.getID();
                    players.remove(deadTank.id);
                    server.sendToAllExceptTCP(deadTank.id, deadTank);
                }
                else if (o instanceof LevelUp) {
                    LevelUp levelUp = (LevelUp) o;
                    levelUp.id = c.getID();
                    System.out.println("level up " + levelUp.id + " " + levelUp.level);
                    server.sendToAllExceptTCP(c.getID(), levelUp);
                }
                else if (o instanceof HitTank) {
                    System.out.println("HIT " + c.getID());
                        HitTank hitTank = (HitTank) o;
                        hitTank.id = c.getID();
                        server.sendToAllExceptTCP(hitTank.id, hitTank);

                }
                else if (o instanceof CreatePlayer) {
                    CreatePlayer createPlayer = (CreatePlayer) o;
                    Player newPlayer = new Player();
                    newPlayer.x = createPlayer.x;
                    newPlayer.y = createPlayer.y;
                    newPlayer.rotation = createPlayer.rotation;
                    newPlayer.healthPoints = createPlayer.healthPoints;
                    newPlayer.level = createPlayer.level;

                    createPlayer.id = c.getID();

                    server.sendToAllExceptTCP(c.getID(), createPlayer);

                    for (Integer key : players.keySet()) {
                        Player player = players.get(key);
                        CreatePlayer tempPlayer = new CreatePlayer();
                        tempPlayer.id = key;
                        tempPlayer.x = player.x;
                        tempPlayer.y = player.y;
                        tempPlayer.rotation = player.rotation;
                        tempPlayer.healthPoints = player.healthPoints;
                        tempPlayer.level = player.level;

                        c.sendTCP(tempPlayer);
                    }
                    players.put(c.getID(), newPlayer);
                }
            }
            @Override
            public void disconnected(Connection c) {
                players.remove(c.getID());
                DeadTank deadTank = new DeadTank();
                deadTank.id = c.getID();
                server.sendToAllExceptTCP(deadTank.id, deadTank);
                System.out.println(players.size());
                if (players.size() == 0) {
                        server.stop();
                        server.close();
                }
            }
        });
    }
}
