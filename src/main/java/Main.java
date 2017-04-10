
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        OkHttpClient client = new OkHttpClient();

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
        System.out.println("STATS---");
        System.out.println("");
        SortUtil.sortByValue(playerCounter).forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
