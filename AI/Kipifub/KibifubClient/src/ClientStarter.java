import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// java -jar kipifub.jar

public class ClientStarter {
    private static final String HOST_NAME = "";
    private static final String[] TEAM_NAME = {"KaHo", "GamerTwo", "GamerThree"};

    public static void main(String[] args) {
        List<Client> clients = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            clients.add(new Client(HOST_NAME, TEAM_NAME[i].toString()));
        }
        try {
            executor.invokeAll(clients);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
