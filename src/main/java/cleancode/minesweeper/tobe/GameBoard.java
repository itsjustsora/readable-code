package cleancode.minesweeper.tobe;

import java.util.Arrays;
import java.util.Random;

public class GameBoard {

	public static final int LAND_MINE_COUNT = 10;

	private final Cell[][] board;

	public GameBoard(int rowSize, int colSize) {
		board = new Cell[rowSize][colSize];
	}

	public void flag(int rowIndex, int colIndex) {
		Cell cell = findCell(rowIndex, colIndex);
		cell.flag();
	}

	public void open(int rowIndex, int colIndex) {
		Cell cell = findCell(rowIndex, colIndex);
		cell.open();
	}

	public void openSurroundedCells(int row, int col) {
		// 칸을 벗어났는지 검증
		if (row < 0 || row >= getRowSize() || col < 0 || col >= getColSize()) {
			return;
		}

		// 기존 cell이 이미 선택되었었는지 검증
		if (isOpened(row, col)) {
			return;
		}

		// 지뢰 cell인지 검증
		if (isLandMineCell(row, col)) {
			return;
		}

		open(row, col);

		// 지뢰 count를 가지고 있는 cell인지 검증
		if (doesCellHaveLandMineCount(row, col)) {
			return;
		}

		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		openSurroundedCells(row - 1, col - 1);
		openSurroundedCells(row - 1, col);
		openSurroundedCells(row - 1, col + 1);
		openSurroundedCells(row, col - 1);
		openSurroundedCells(row, col + 1);
		openSurroundedCells(row + 1, col - 1);
		openSurroundedCells(row + 1, col);
		openSurroundedCells(row + 1, col + 1);
	}

	private boolean doesCellHaveLandMineCount(int row, int col) {
		return findCell(row, col).hasLandMineCount();
	}

	private boolean isOpened(int row, int col) {
		return findCell(row, col).isOpened();
	}

	public boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
		Cell cell = findCell(selectedRowIndex, selectedColIndex);
		return cell.isLandMine();
	}

	public void initializeGame() {
		int rowSize = board.length;
		int colSize = board[0].length;

		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				// 행 8, 열 10
				board[row][col] = Cell.create();
			}
		}

		// 랜덤 자리에 지뢰를 10개 설정하여 해당 위치를 true로 변경
		for (int i = 0; i < LAND_MINE_COUNT; i++) {
			int landMineCol = new Random().nextInt(colSize);
			int landMineRow = new Random().nextInt(rowSize);
			Cell landMineCell = findCell(landMineRow, landMineCol);
			landMineCell.turnOnLandMine();
		}

		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				// 지뢰가 있는 경우
				if (isLandMineCell(row, col)) {
					continue;
				}

				// 지뢰가 아닌 경우
				int count = countNearbyLandMines(row, col);
				Cell cell = findCell(row, col);
				cell.updateNearbyLandMineCount(count);
			}
		}
	}

	public int getRowSize() {
		return board.length;
	}

	public int getColSize() {
		return board[0].length;
	}

	public String getSign(int rowIndex, int colIndex) {
		Cell cell = findCell(rowIndex, colIndex);
		return cell.getSign();
	}

	private Cell findCell(int rowIndex, int colIndex) {
		return board[rowIndex][colIndex];
	}

	/**
	 * 셀이 모두 열려있는지 확인
	 * @return
	 */
	public boolean isAllCellChecked() {
		return Arrays.stream(board) // Stream<String[]>
			.flatMap(Arrays::stream) // Stream<String>
			.allMatch(Cell::isChecked);
	}

	/**
	 * 근처에 위치한 지뢰의 수 조회
	 * @param row
	 * @param col
	 * @return
	 */
	private int countNearbyLandMines(int row, int col) {
		int rowSize = getRowSize();
		int colSize = getColSize();

		int count = 0;
		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		if (row - 1 >= 0 && col - 1 >= 0 && isLandMineCell(row - 1, col - 1)) { // 왼쪽 위 대각선
			count++;
		}
		if (row - 1 >= 0 && isLandMineCell(row - 1, col)) {
			count++;
		}
		if (row - 1 >= 0 && col + 1 < colSize && isLandMineCell(row - 1, col + 1)) {
			count++;
		}
		if (col - 1 >= 0 && isLandMineCell(row, col - 1)) {
			count++;
		}
		if (col + 1 < colSize && isLandMineCell(row, col + 1)) {
			count++;
		}
		if (row + 1 < 8 && col - 1 >= 0 && isLandMineCell(row + 1, col - 1)) {
			count++;
		}
		if (row + 1 < rowSize && isLandMineCell(row + 1, col)) {
			count++;
		}
		if (row + 1 < rowSize && col + 1 < colSize && isLandMineCell(row + 1, col + 1)) {
			count++;
		}
		return count;
	}
}
