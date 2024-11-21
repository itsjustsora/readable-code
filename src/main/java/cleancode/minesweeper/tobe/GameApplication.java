package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.gamelevel.Advanced;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;

public class GameApplication {
    public static void main(String[] args) {
        // GameLevel gameLevel = new VeryBeginner();
        // GameLevel gameLevel = new Beginner();
        // GameLevel gameLevel = new Middle();
        GameLevel gameLevel = new Advanced();

        Minesweeper minesweeper = new Minesweeper(gameLevel);
        minesweeper.run();
    }
}
