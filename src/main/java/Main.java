
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
    private static Date date = new Date();
    private static String currentDate= new SimpleDateFormat("yyyy-MM-dd").format(date);
    public  static Map<String, Integer> playerCounter = new HashMap<>();

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        LeaderBoard leaderBoard = getLeaderboard(client, 1);
        LeaderBoard leaderBoard2 = getLeaderboard(client, 2);
        leaderBoard.standings.standing.addAll(leaderBoard2.standings.standing);

        // Output list of players and their picks
        for (LeaderBoard.Player player : leaderBoard.standings.standing) {
            System.out.println("Position: " + player.rank + ": " + player.userName + ": " + player.guid);
            String pid = getPid(client, player.guid);
            List<PlayerPicks.Pick> picks = getPlayerPicks(client, pid, currentDate);
            picks.stream().filter(pick -> pick.game_date.equals(currentDate)).forEach(pick -> {
                System.out.println(pick.name_display_first_last + ": " + pick.game_date);
                Integer count = playerCounter.containsKey(pick.name_display_first_last) ? playerCounter.get(pick.name_display_first_last) : 0;
                playerCounter.put(pick.name_display_first_last, count + 1);
            });
        }
        System.out.println("STATS---");
        System.out.println("");
        playerCounter = SortUtil.sortByValue(playerCounter);
        for(Map.Entry<String, Integer> player : playerCounter.entrySet()) {
            System.out.println(player.getKey() + ": " + player.getValue());
        }
    }
}
