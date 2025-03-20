package cleancode.minesweeper.tobe.minesweeper.board.cell;

import java.util.Objects;

public class CellSnapshot {

    private final CellSnapshotStatus status;
    private final int nearByLandMineCount;

    private CellSnapshot(CellSnapshotStatus status, int nearByLandMineCount) {
        this.status = status;
        this.nearByLandMineCount = nearByLandMineCount;
    }

    public static CellSnapshot of(CellSnapshotStatus status, int nearByLandMineCount) {
        return new CellSnapshot(status, nearByLandMineCount);
    }

    public static CellSnapshot ofEmpty() {
        return new CellSnapshot(CellSnapshotStatus.EMPTY, 0);
    }

    public static CellSnapshot ofFlag() {
        return new CellSnapshot(CellSnapshotStatus.FLAG, 0);
    }

    public static CellSnapshot ofLandMine() {
        return new CellSnapshot(CellSnapshotStatus.LAND_MIND, 0);
    }

    public static CellSnapshot ofNumber(int nearByLandMineCount) {
        return new CellSnapshot(CellSnapshotStatus.NUMBER, nearByLandMineCount);
    }

    public static CellSnapshot ofUnchecked() {
        return new CellSnapshot(CellSnapshotStatus.UNCHECKED, 0);
    }

    public boolean isSameStatus(CellSnapshotStatus cellSnapshotStatus) {
        return this.status == cellSnapshotStatus;
    }

    public CellSnapshotStatus getStatus() {
        return status;
    }

    public int getNearByLandMineCount() {
        return nearByLandMineCount;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CellSnapshot that))
            return false;
        return nearByLandMineCount == that.nearByLandMineCount && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, nearByLandMineCount);
    }
}
