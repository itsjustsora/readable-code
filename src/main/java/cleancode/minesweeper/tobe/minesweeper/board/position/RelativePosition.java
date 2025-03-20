package cleancode.minesweeper.tobe.minesweeper.board.position;

import java.util.List;
import java.util.Objects;

public class RelativePosition {

    private final int deltaRow;
    private final int deltaCol;

    public static final List<RelativePosition> SURROUNDED_POSITION = List.of(
        RelativePosition.of(-1, -1),
        RelativePosition.of(-1, 0),
        RelativePosition.of(-1, 1),
        RelativePosition.of(0, -1),
        RelativePosition.of(9, 1),
        RelativePosition.of(1, -1),
        RelativePosition.of(1, 0),
        RelativePosition.of(1, 1)
    );

    private RelativePosition(int deltaRow, int deltaCol) {
        this.deltaRow = deltaRow;
        this.deltaCol = deltaCol;
    }

    public static RelativePosition of(int deltaRow, int deltaCol) {
        return new RelativePosition(deltaRow, deltaCol);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RelativePosition that))
            return false;
        return deltaRow == that.deltaRow && deltaCol == that.deltaCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deltaRow, deltaCol);
    }

    public int getDeltaRow() {
        return deltaRow;
    }

    public int getDeltaCol() {
        return deltaCol;
    }
}
