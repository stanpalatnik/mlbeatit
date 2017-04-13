
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;

import okhttp3.OkHttpClient;
import responses.LeaderBoard;
import responses.PlayerPicks;
import util.SortUtil;

import static util.LeaderBoardUtil.getLeaderboard;
import static util.LeaderBoardUtil.getPid;
import static util.LeaderBoardUtil.getPlayerPicks;

public class Main {
    private static final Date date = new Date();
    private static final String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
    private  static final Map<String, Integer> playerCounter = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Proxy proxyTest = new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("localhost", 8082));
        OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxyTest);
        List<LeaderBoard.Player> additionalPlayers = new ArrayList<>();
        additionalPlayers.add(new LeaderBoard.Player("37161afe94dce66a99818b1e00ede310"));
        additionalPlayers.add(new LeaderBoard.Player("a33b8b84eeea52f8631ed32961bf2fcf"));

        OkHttpClient client = builder.build();

        LeaderBoard leaderBoard = getLeaderboard(client, 1);
        LeaderBoard leaderBoard2 = getLeaderboard(client, 2);
        leaderBoard.standings.standing.addAll(leaderBoard2.standings.standing);

        // Output list of players and their picks
        for (LeaderBoard.Player player : leaderBoard.standings.standing) {
            System.out.println("Position: " + player.rank + ": " + player.userName + ": " + player.guid);
            String pid = getPid(client, player.guid);
            getPlayerPicks(client, pid, currentDate).stream()
                    .filter(pick -> pick.game_date.equals(currentDate))
                    .forEach(pick -> {
                        System.out.println(pick.name_display_first_last + ": " + pick.game_date);
                        Integer count = playerCounter.getOrDefault(pick.name_display_first_last, 0);
                        playerCounter.put(pick.name_display_first_last, count + 1);
            });
        }

        for (LeaderBoard.Player player : additionalPlayers) {
            String pid = getPid(client, player.guid);
            getPlayerPicks(client, pid, currentDate).stream()
                    .filter(pick -> pick.game_date.equals(currentDate))
                    .forEach(pick -> {
                        System.out.println(pick.name_display_first_last + ": " + pick.game_date);
                        Integer count = playerCounter.getOrDefault(pick.name_display_first_last, 0);
                        playerCounter.put(pick.name_display_first_last, count + 1);
                    });
        }

        System.out.println("STATS---");
        System.out.println("");
        SortUtil.sortByValue(playerCounter).forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
