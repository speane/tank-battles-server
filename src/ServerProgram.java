import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Speane on 09.03.2016.
 */
public class ServerProgram {
    public static Connection client;
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

        server.addListener(new Listener() {
            public void connected (Connection connection) {
                Player newPlayer = new Player();
                newPlayer.connection = connection;
                newPlayer.x = 0;
                newPlayer.y = 0;
                newPlayer.rotation = 0;
                newPlayer.id = connection.getID();

                System.out.println("Connected: " + connection.getRemoteAddressTCP());

                CreatePlayer createPlayer = new CreatePlayer();
                createPlayer.id = connection.getID();
                createPlayer.x = 0;
                createPlayer.y = 0;
                createPlayer.rotation = 0;

                server.sendToAllExceptTCP(connection.getID(), createPlayer);

                for (Player player : players.values()) {
                    CreatePlayer tempPlayer = new CreatePlayer();
                    tempPlayer.id = player.id;
                    tempPlayer.x = player.x;
                    tempPlayer.y = player.y;
                    tempPlayer.rotation = player.rotation;

                    connection.sendTCP(tempPlayer);
                }

                players.put(connection.getID(), newPlayer);
            }
            @Override
            public void received(Connection c, Object o) {
                if (o instanceof MoveTank) {
                    MoveTank moveTank = (MoveTank) o;
                    Player player = players.get(c.getID());
                    moveTank.id = player.id;
                    player.x = moveTank.x;
                    player.y = moveTank.y;
                    player.rotation = moveTank.rotation;
                    server.sendToAllExceptTCP(player.id, moveTank);
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
                    server.sendToAllExceptTCP(deadTank.id, deadTank);
                }
            }
            @Override
            public void disconnected(Connection c) {
                players.remove(c.getID());
                System.out.println(players.size());
                if (players.size() == 0) {
                        server.stop();
                        server.close();
                }
            }
        });
    }
}
