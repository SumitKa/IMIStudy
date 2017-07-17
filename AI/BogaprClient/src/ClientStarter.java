import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// java -Djava.library.path=lib/native -jar bogapr.jar

public class ClientStarter {
    private static final String HOST_NAME = "141.45.214.64";
    private static final String[] TEAM_NAME = {"KaHo", "GamerTwo", "GamerThree"};
    private static final String[] LOGO_PATH = {"resources/logo3.png", "resources/logo3.png", "resources/logo3.png"};

    //TODO: Korrektes setzen der namen/Logos (low prio)
    public static void main(String[] args) {
        BufferedImage logo;
        List<Client> clients = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 1; i++) {
            logo = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR);
            try {
                logo = ImageIO.read(new File(LOGO_PATH[i]));
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.add(new Client(HOST_NAME, TEAM_NAME[i], logo));
        }
        try {
            executor.invokeAll(clients);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}