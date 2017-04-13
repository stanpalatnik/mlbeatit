package responses;

import java.util.List;

public class LeaderBoard {
    public String league_name;
    public String page;
    public String yesterdays_game_date;
    public String startRow;
    public String game_date;
    public String total_pages;
    public String league_id;
    public String locked;
    public String src;
    public Standings standings;

    public static class Standings {
        public String game_date;
        public List<Player> standing;
    }

    public static class Player {

        public Player(){}

        public Player(String guid){
            this.guid = guid;
        }

        public String guid;
        public String rank;
        public String cur;
        public String userName;
        public String high;
    }
}