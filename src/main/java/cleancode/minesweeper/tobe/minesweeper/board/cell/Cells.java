package cleancode.minesweeper.tobe.minesweeper.board.cell;

import java.util.Arrays;
import java.util.List;

public class Cells {

    // 단 하나의 필드
    private final List<Cell> cells;

    private Cells(List<Cell> cells) {
        this.cells = cells;
    }

    public static Cells of(List<Cell> cells) {
        return new Cells(cells);
    }

    /**
     * 컬렉션의 가공에 대한 책임을 가지는 메서드
     * @param cells
     * @return
     */
    public static Cells from(Cell[][] cells) {
        List<Cell> cellList = Arrays.stream(cells) // Stream<String[]>
            .flatMap(Arrays::stream) // Stream<String>
            .toList();
        return new Cells(cellList);
    }

    public boolean isAllChecked() {
        return cells.stream()
            .allMatch(Cell::isChecked);
    }
}
