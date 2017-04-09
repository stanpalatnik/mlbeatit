package util;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import responses.LeaderBoard;
import responses.PlayerPicks;
import responses.PlayerProfile;

import java.io.IOException;
import java.util.List;

public class LeaderBoardUtil {
    private static final String LEADERBOARD_ENTRYPOINT = "http://mlb.mlb.com/fantasylookup/rawjson/named.bts_hitdd_standings.bam?bts_game_id=12&pg=%d&ns=mlb&year=2017&order=high";
    private static final String PLAYER_INFO_ENDPOINT = "http://mlb.mlb.com/fantasylookup/json/named.bts_profile_cmpsd.bam?bts_game_id=12&bts_user_recent_results.maxRows=0&timeframe=365&fntsy_game_id=10&bts_mulligan_status.game_id=bts2017&guid=";
    private static final String PLAYER_PICKS_ENDPOINT = "http://mlb.mlb.com/fantasylookup/rawjson/named.bts_hitdd_picks.bam?ns=mlb&ipid=%s&max_days_back=1&max_days_ahead=1&bts_game_id=12&year=2017&focus_date=%s&ts=1491670980042";
    private static final Moshi MOSHI = new Moshi.Builder().build();
    private static final JsonAdapter<LeaderBoard> LEADERBOARD_JSON_ADAPTOR = MOSHI.adapter(
            Types.newParameterizedType(LeaderBoard.class, LeaderBoard.Standings.class, LeaderBoard.Player.class));

    private static final JsonAdapter<PlayerProfile> PLAYER_PROFILE_JSON_ADAPTOR = MOSHI.adapter(
            Types.newParameterizedType(PlayerProfile.class, PlayerProfile.ProfileWrapper.class, PlayerProfile.QueryWrapper.class, PlayerProfile.Profile.class));

    private static final JsonAdapter<PlayerPicks> PLAYER_PICKS_JSON_ADAPTOR = MOSHI.adapter(
            Types.newParameterizedType(PlayerPicks.class, PlayerPicks.Pick.class, PlayerPicks.PickDates.class));

    public static LeaderBoard getLeaderboard(OkHttpClient client, int page) throws IOException {
        // Create request for remote resource.
        Request request = new Request.Builder()
                .url(String.format(LEADERBOARD_ENTRYPOINT, page))
                .build();

        // Execute the request and retrieve the response.
        Response response = client.newCall(request).execute();

        // Deserialize HTTP response to concrete type.
        ResponseBody body = response.body();
        LeaderBoard leaderboard = LEADERBOARD_JSON_ADAPTOR.fromJson(body.source());
        body.close();
        return leaderboard;
    }

    public static List<PlayerPicks.Pick> getPlayerPicks(OkHttpClient client, String pid, String currentDate) throws IOException {
        Request request = new Request.Builder()
                .url(String.format(PLAYER_PICKS_ENDPOINT, pid, currentDate))
                .build();

        // Execute the request and retrieve the response.
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        PlayerPicks playerPicks = PLAYER_PICKS_JSON_ADAPTOR.fromJson(body.source());
        return playerPicks.pick_dates.get(playerPicks.pick_dates.size() -2).picks;
    }

    public static String getPid(OkHttpClient client, String guid) throws IOException {
        Request request = new Request.Builder()
                .url(PLAYER_INFO_ENDPOINT + guid)
                .build();

        // Execute the request and retrieve the response.
        Response response = client.newCall(request).execute();

        // Deserialize HTTP response to concrete type.
        ResponseBody body = response.body();
        PlayerProfile profile = PLAYER_PROFILE_JSON_ADAPTOR.fromJson(body.source());
        body.close();
        return profile.bts_profile_cmpsd.bts_profile_basic.queryResults.row.get("ipid");
    }
}
