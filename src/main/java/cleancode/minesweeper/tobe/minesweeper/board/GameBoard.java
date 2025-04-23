package cleancode.minesweeper.tobe.minesweeper.board;

import java.util.List;
import java.util.Stack;

import cleancode.minesweeper.tobe.minesweeper.board.cell.Cell;
import cleancode.minesweeper.tobe.minesweeper.board.cell.CellSnapshot;
import cleancode.minesweeper.tobe.minesweeper.board.cell.Cells;
import cleancode.minesweeper.tobe.minesweeper.board.cell.EmptyCell;
import cleancode.minesweeper.tobe.minesweeper.board.cell.LandMineCell;
import cleancode.minesweeper.tobe.minesweeper.board.cell.NumberCell;
import cleancode.minesweeper.tobe.minesweeper.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.minesweeper.board.position.CellPosition;
import cleancode.minesweeper.tobe.minesweeper.board.position.CellPositions;
import cleancode.minesweeper.tobe.minesweeper.board.position.RelativePosition;

public class GameBoard {

	private final Cell[][] board;
	private final int landMineCount;

	private GameStatus gameStatus;

	public GameBoard(GameLevel gameLevel) {
		int rowSize = gameLevel.getRowSize();
		int colSize = gameLevel.getColSize();
		board = new Cell[rowSize][colSize];

		landMineCount = gameLevel.getLandMineCount();
		initializeGameStatus();
	}

	public void initializeGame() {
		initializeGameStatus();
		CellPositions cellPositions = CellPositions.from(board);

		initializeEmptyCells(cellPositions);

		// 랜덤 자리에 지뢰를 10개 설정하여 해당 위치를 true로 변경
		List<CellPosition> landMinePositions = cellPositions.extractRandomPositions(landMineCount);
		initializeLandMineCells(landMinePositions);

		List<CellPosition> numberPositionCandidates = cellPositions.subtract(landMinePositions);
		initializeNumberCells(numberPositionCandidates);
	}

	public void openAt(CellPosition cellPosition) {
		// 지뢰 cell을 선택한 경우
		if (isLandMineCellAt(cellPosition)) {
			openOneCellAt(cellPosition);
			changeGameStatusToLose();
			return;
		}

		// 일반 cell을 선택한 경우
		openSurroundedCells2(cellPosition);
		checkIfGameIsOver();
	}

	public void flagAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		cell.flag();

		checkIfGameIsOver();
	}

	public boolean isInvalidCellPosition(CellPosition cellPosition) {
		int rowSize = getRowSize();
		int colSize = getColSize();

		return cellPosition.isRowIndexMoreThanOrEqual(rowSize)
			|| cellPosition.isColIndexMoreThanOrEqual(colSize);
	}

	public boolean isInProgress() {
		return gameStatus == GameStatus.IN_PROGRESS;
	}

	public boolean isWinStatus() {
		return gameStatus == GameStatus.WIN;
	}

	public boolean isLoseStatus() {
		return gameStatus == GameStatus.LOSE;
	}

	public CellSnapshot getSnapshot(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		return cell.getSnapshot();
	}

	public int getRowSize() {
		return board.length;
	}

	public int getColSize() {
		return board[0].length;
	}

	private void initializeGameStatus() {
		gameStatus = GameStatus.IN_PROGRESS;
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

	private void initializeNumberCells(List<CellPosition> numberPositionCandidates) {
		for (CellPosition candidatePosition : numberPositionCandidates) {
			int count = countNearbyLandMines(candidatePosition);
			if (count != 0) {
				updateCellAt(candidatePosition, new NumberCell(count));
			}
		}
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

	private void updateCellAt(CellPosition position, Cell cell) {
		board[position.getRowIndex()][position.getColIndex()] = cell;
	}

	private void openSurroundedCells(CellPosition cellPosition) {
		// 기존 cell이 이미 선택되었었는지 검증
		if (isOpenedCell(cellPosition)) {
			return;
		}

		// 지뢰 cell인지 검증
		if (isLandMineCellAt(cellPosition)) {
			return;
		}

		openOneCellAt(cellPosition);

		// 지뢰 count를 가지고 있는 cell인지 검증
		if (doesCellHaveLandMineCount(cellPosition)) {
			return;
		}

		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		List<CellPosition> surroundedPosition = calculateSurroundedPosition(cellPosition, getRowSize(), getColSize());
		surroundedPosition.forEach(this::openSurroundedCells);
	}

	private void openSurroundedCells2(CellPosition cellPosition) {
		Stack<CellPosition> stack = new Stack<>();
		stack.push(cellPosition);

		while (!stack.isEmpty()) {
			openAndPushCellAt(stack);
		}
	}

	private void openAndPushCellAt(Stack<CellPosition> stack) {
		CellPosition currentCellPosition = stack.pop();

		// 기존 cell이 이미 선택되었었는지 검증
		if (isOpenedCell(currentCellPosition)) {
			return;
		}

		// 지뢰 cell인지 검증
		if (isLandMineCellAt(currentCellPosition)) {
			return;
		}

		openOneCellAt(currentCellPosition);

		// 지뢰 count를 가지고 있는 cell인지 검증
		if (doesCellHaveLandMineCount(currentCellPosition)) {
			return;
		}

		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		List<CellPosition> surroundedPosition = calculateSurroundedPosition(currentCellPosition, getRowSize(), getColSize());
		for (CellPosition position : surroundedPosition) {
			stack.push(position);
		}
	}

	private void openOneCellAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		cell.open();
	}

	private boolean isOpenedCell(CellPosition cellPosition) {
		return findCell(cellPosition).isOpened();
	}

	private boolean isLandMineCellAt(CellPosition cellPosition) {
		Cell cell = findCell(cellPosition);
		return cell.isLandMine();
	}

	private boolean doesCellHaveLandMineCount(CellPosition cellPosition) {
		return findCell(cellPosition).hasLandMineCount();
	}

	/**
	 * 셀이 모두 열려있는지 확인 후 게임 종료 값 설정
	 */
	private void checkIfGameIsOver() {
		// 셀이 모두 열려있는지 확인
		if (isAllCellChecked()) {
			changeGameStatusToWin();
		}
	}

	/**
	 * 셀이 모두 열려있는지 확인
	 * @return
	 */
	private boolean isAllCellChecked() {
		Cells cells = Cells.from(board);
		return cells.isAllChecked();
	}

	private void changeGameStatusToWin() {
		gameStatus = GameStatus.WIN;
	}

	private void changeGameStatusToLose() {
		gameStatus = GameStatus.LOSE;
	}

	private Cell findCell(CellPosition cellPosition) {
		return board[cellPosition.getRowIndex()][cellPosition.getColIndex()];
	}
}
