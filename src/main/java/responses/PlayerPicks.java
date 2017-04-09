package responses;

import java.util.List;

public class PlayerPicks {
    public List<PickDates> pick_dates;

    public static class PickDates {
        public List<Pick> picks;
    }

    public static class Pick {
        public String name_display_first_last;
        public String game_date;
    }
}
