import lenz.htw.kipifub.net.NetworkClient;

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
        networkClient.pullNextColorChange();
        return networkClient;
    }

}
