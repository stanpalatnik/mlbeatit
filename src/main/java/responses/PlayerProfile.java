package responses;

import java.util.Map;

public class PlayerProfile {
    public ProfileWrapper bts_profile_cmpsd;

    public static class ProfileWrapper {
        public QueryWrapper bts_profile_basic;
    }

    public static class QueryWrapper {
        public Profile queryResults;
    }

    public static class Profile {
        public Map<String, String> row;
    }
}
