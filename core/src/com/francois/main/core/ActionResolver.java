package com.francois.main.core;

public interface ActionResolver {
    public boolean getSignedInGPGS();
    public void getUserHighScoreGPGS(String score_leaderboard);
    public void loginGPGS();
    public void submitScoreGPGS(int score, String score_leaderboard);
    public void submitTimeGPGS(int time, String time_leaderboard);
    public void unlockAchievementGPGS(String achievementId);
    public void getLeaderboardGPGS();
    public void getAchievementsGPGS();
}
