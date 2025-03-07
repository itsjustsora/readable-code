package cleancode.minesweeper.tobe;

import java.util.List;

import cleancode.minesweeper.tobe.cell.Cell;
import cleancode.minesweeper.tobe.cell.Cells;
import cleancode.minesweeper.tobe.cell.EmptyCell;
import cleancode.minesweeper.tobe.cell.LandMineCell;
import cleancode.minesweeper.tobe.cell.NumberCell;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.position.CellPositions;
import cleancode.minesweeper.tobe.position.RelativePosition;

public class GameBoard {

	private final Cell[][] board;
	private final int landMineCount;

	public GameBoard(GameLevel gameLevel) {
		int rowSize = gameLevel.getRowSize();
		int colSize = gameLevel.getColSize();
		board = new Cell[rowSize][colSize];

		landMineCount = gameLevel.getLandMineCount();
	}

	public void flagAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		cell.flag();
	}

	public void openAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		cell.open();
	}

	public void openSurroundedCells(CellPosition cellPosition) {
		// 기존 cell이 이미 선택되었었는지 검증
		if (isOpenedCell(cellPosition)) {
			return;
		}

		// 지뢰 cell인지 검증
		if (isLandMineCellAt(cellPosition)) {
			return;
		}

		openAt(cellPosition);

		// 지뢰 count를 가지고 있는 cell인지 검증
		if (doesCellHaveLandMineCount(cellPosition)) {
			return;
		}

		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		List<CellPosition> surroundedPosition = calculateSurroundedPosition(cellPosition, getRowSize(), getColSize());
		surroundedPosition.forEach(this::openSurroundedCells);
	}

	private boolean doesCellHaveLandMineCount(CellPosition cellPosition) {
		return findCell(cellPosition).hasLandMineCount();
	}

	private boolean isOpenedCell(CellPosition cellPosition) {
		return findCell(cellPosition).isOpened();
	}

	public boolean isLandMineCellAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		return cell.isLandMine();
	}

	/**
	 * 셀이 모두 열려있는지 확인
	 * @return
	 */
	public boolean isAllCellChecked() {
		Cells cells = Cells.from(board);
		return cells.isAllChecked();
	}

	public boolean isInvalidCellPosition(CellPosition cellPosition) {
		int rowSize = getRowSize();
		int colSize = getColSize();

		return cellPosition.isRowIndexMoreThanOrEqual(rowSize)
			|| cellPosition.isColIndexMoreThanOrEqual(colSize);
	}

	public void initializeGame() {
		CellPositions cellPositions = CellPositions.from(board);

		initializeEmptyCells(cellPositions);

		// 랜덤 자리에 지뢰를 10개 설정하여 해당 위치를 true로 변경
		List<CellPosition> landMinePositions = cellPositions.extractRandomPositions(landMineCount);
		initializeLandMineCells(landMinePositions);

		List<CellPosition> numberPositionCandidates = cellPositions.subtract(landMinePositions);
		initializeNumberCells(numberPositionCandidates);
	}

	private void initializeNumberCells(List<CellPosition> numberPositionCandidates) {
		for (CellPosition candidatePosition : numberPositionCandidates) {
			int count = countNearbyLandMines(candidatePosition);
			if (count != 0) {
				updateCellAt(candidatePosition, new NumberCell(count));
			}
		}
	}

	private void initializeEmptyCells(CellPositions cellPositions) {
		List<CellPosition> allPositions = cellPositions.getPositions();
		for (CellPosition position : allPositions) {
			updateCellAt(position, new EmptyCell());
		}
	}

	private void initializeLandMineCells(List<CellPosition> landMinePositions) {
		for (CellPosition position : landMinePositions) {
			updateCellAt(position, new LandMineCell());
		}
	}

	private void updateCellAt(CellPosition position, Cell cell) {
		board[position.getRowIndex()][position.getColIndex()] = cell;
	}

	public int getRowSize() {
		return board.length;
	}

	public int getColSize() {
		return board[0].length;
	}

	public String getSign(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		return cell.getSign();
	}

	private Cell findCell(CellPosition cellPosition) {
		return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
	}

	/**
	 * 근처에 위치한 지뢰의 수 조회
	 * @return
	 */
	private int countNearbyLandMines(CellPosition cellPosition) {
		int rowSize = getRowSize();
		int colSize = getColSize();

		long count = calculateSurroundedPosition(cellPosition, rowSize, colSize).stream()
			.filter(this::isLandMineCellAt)
			.count();

		return (int) count;
	}

	private List<CellPosition> calculateSurroundedPosition(CellPosition cellPosition, int rowSize, int colSize) {
		return RelativePosition.SURROUNDED_POSITION.stream()
			.filter(cellPosition::canCalculatePositionBy)
			.map(cellPosition::calculatePositionBy)
			.filter(position -> position.isRowIndexLessThan(rowSize))
			.filter(position -> position.isColIndexLessThan(colSize))
			.toList();
	}

}
