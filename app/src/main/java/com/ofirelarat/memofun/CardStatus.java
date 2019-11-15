package com.ofirelarat.memofun;

public class CardStatus {
    private boolean isCheckedByUser;
    private boolean isCheckedByGame;

    public CardStatus(boolean isCheckedByUser, boolean isCheckedByGame) {
        this.isCheckedByUser = isCheckedByUser;
        this.isCheckedByGame = isCheckedByGame;
    }

    public boolean isCheckedByUser() {
        return isCheckedByUser;
    }

    public void setCheckedByUser(boolean checkedByUser) {
        isCheckedByUser = checkedByUser;
    }

    public boolean isCheckedByGame() {
        return isCheckedByGame;
    }

    public void setCheckedByGame(boolean checkedByGame) {
        isCheckedByGame = checkedByGame;
    }
}

