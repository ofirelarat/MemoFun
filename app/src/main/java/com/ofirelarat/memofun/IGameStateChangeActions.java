package com.ofirelarat.memofun;

public interface IGameStateChangeActions {
    void gameEnd(boolean isUserWin);
    void updateScoreView();
}
