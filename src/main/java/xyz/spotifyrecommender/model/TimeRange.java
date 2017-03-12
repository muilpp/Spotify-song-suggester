package xyz.spotifyrecommender.model;

public enum TimeRange {
    LONG_TERM("long_term"), MEDIUM_TERM("medium_term"), SHORT_TERM("short_term");
    
    private String termType;
    
    TimeRange(String range) {
        termType = range;
    }
    
    public String getTimeRange() {
        return termType;
    }
}
