import lenz.htw.kipifub.net.NetworkClient;

import java.util.Random;
import java.util.concurrent.Callable;


/**
 * Created by kapoor on 12.06.17.
 */
public class Client implements Callable<NetworkClient> {
    static private String hostName, teamName;

    public Client(String hostName, String teamName) {
        this.hostName = hostName;
        this.teamName = teamName;
    }

    @Override
    public NetworkClient call() throws Exception {

        NetworkClient networkClient = new NetworkClient(hostName, teamName);
        send(networkClient);
        return networkClient;
    }

    public NetworkClient send(NetworkClient client) {
        Random rng = new Random();
        int test;
        for (int i = 0; i < 3; i++) {
            test = (rng.nextInt() * 2) - 1;
            if (test < 1) {
                client.setMoveDirection(i, -1, -1);
            }
            else if (test > -1) {
                client.setMoveDirection(i, 1, 1);
            }
        }
        return client;
    }

}
