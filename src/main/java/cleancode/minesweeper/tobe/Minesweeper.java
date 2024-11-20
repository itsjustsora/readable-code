package cleancode.minesweeper.tobe;

import java.util.Arrays;
import java.util.Random;

import cleancode.minesweeper.tobe.io.ConsoleInputHandler;
import cleancode.minesweeper.tobe.io.ConsoleOutputHandler;

public class Minesweeper {

	public static final int BOARD_ROW_SIZE = 8;
	public static final int BOARD_COL_SIZE = 10;
	public static final int LAND_MINE_COUNT = 10;

	private static final Cell[][] BOARD = new Cell[BOARD_ROW_SIZE][BOARD_COL_SIZE];

	private final ConsoleInputHandler consoleInputHandler =  new ConsoleInputHandler();
	private final ConsoleOutputHandler consoleOutputHandler = new ConsoleOutputHandler();

	private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

	public void run() {
		consoleOutputHandler.showGameStartComments();

		// 게임 초기화
		initializeGame();

		while (true) {
			try {
				consoleOutputHandler.showBoard(BOARD);

				if (doesUserWinTheGame()) {
					consoleOutputHandler.printGameWinningComment();
					break;
				}
				if (doesUserLoseTheGame()) {
					consoleOutputHandler.printGameLosingComment();
					break;
				}

				String cellInput = getCellInputFromUser();
				String userActionInput = getUserActionInputFromUser();

				actOnCell(cellInput, userActionInput);
			} catch (GameException e) {
				consoleOutputHandler.printExceptionMessage(e);
			} catch (Exception e) {
				consoleOutputHandler.printSimpleMessage("프로그램에 문제가 생겼습니다.");
			}
		}
	}

	private void actOnCell(String cellInput, String userActionInput) {
		int selectedColIndex = getSelectedColIndex(cellInput);
		int selectedRowIndex = getSelectedRowIndex(cellInput);

		// 깃발 꽂기를 선택한 경우
		if (doesUserChooseToPlantFlag(userActionInput)) {
			BOARD[selectedRowIndex][selectedColIndex].flag();
			checkIfGameIsOver();
			return;
		}

		// cell 오픈을 선택한 경우
		if (doesUserChooseToOpenCell(userActionInput)) {
			// 지뢰 cell을 선택한 경우
			if (isLandMineCell(selectedRowIndex, selectedColIndex)) {
				BOARD[selectedRowIndex][selectedColIndex].open();
				changeGameStatusToLose();
				return;
			}

			// 일반 cell을 선택한 경우
			open(selectedRowIndex, selectedColIndex);
			checkIfGameIsOver();
			return;
		}
		consoleOutputHandler.printSimpleMessage("잘못된 번호를 선택하셨습니다.");
	}

	private void changeGameStatusToLose() {
		gameStatus = -1;
	}

	private void changeGameStatusToWin() {
		gameStatus = 1;
	}

	private boolean isLandMineCell(int selectedRowIndex, int selectedColIndex) {
		return BOARD[selectedRowIndex][selectedColIndex].isLandMine();
	}

	private boolean doesUserChooseToOpenCell(String userActionInput) {
		return userActionInput.equals("1");
	}

	private boolean doesUserChooseToPlantFlag(String userActionInput) {
		return userActionInput.equals("2");
	}

	private int getSelectedRowIndex(String cellInput) {
		char cellInputRow = cellInput.charAt(1); // 숫자
		return convertRowFrom(cellInputRow);
	}

	private int getSelectedColIndex(String cellInput) {
		char cellInputCol = cellInput.charAt(0); // 알파벳
		return convertColFrom(cellInputCol);
	}

	private String getUserActionInputFromUser() {
		consoleOutputHandler.printCommentForSelectiongCell();
		return consoleInputHandler.getUserInput();
	}

	private String getCellInputFromUser() {
		consoleOutputHandler.printCommentForUserAction();
		return consoleInputHandler.getUserInput();
	}

	private boolean doesUserLoseTheGame() {
		return gameStatus == -1;
	}

	private boolean doesUserWinTheGame() {
		return gameStatus == 1;
	}

	/**
	 * 셀이 모두 열려있는지 확인 후 게임 종료 값 설정
	 */
	private void checkIfGameIsOver() {
		// 셀이 모두 열려있는지 확인
		boolean isAllChecked = isAllCellChecked();

		if (isAllChecked) {
			changeGameStatusToWin();
		}
	}

	/**
	 * 셀이 모두 열려있는지 확인
	 * @return
	 */
	private boolean isAllCellChecked() {
		return Arrays.stream(BOARD) // Stream<String[]>
			.flatMap(Arrays::stream) // Stream<String>
			.allMatch(Cell::isChecked);
	}

	private int convertRowFrom(char cellInputRow) {
		int rowIndex = Character.getNumericValue(cellInputRow) - 1;
		if (rowIndex >= BOARD_ROW_SIZE) {
			throw new GameException("잘못된 입력입니다.");
		}

		return rowIndex;
	}

	private int convertColFrom(char cellInputCol) {
		return switch (cellInputCol) {
			case 'a' -> 0;
			case 'b' -> 1;
			case 'c' -> 2;
			case 'd' -> 3;
			case 'e' -> 4;
			case 'f' -> 5;
			case 'g' -> 6;
			case 'h' -> 7;
			case 'i' -> 8;
			case 'j' -> 9;
			default -> throw new GameException("잘못된 입력입니다.");
		};
	}

	private void initializeGame() {
		for (int row = 0; row < BOARD_ROW_SIZE; row++) {
			for (int col = 0; col < BOARD_COL_SIZE; col++) {
				// 행 8, 열 10
				BOARD[row][col] = Cell.create();
			}
		}

		// 랜덤 자리에 지뢰를 10개 설정하여 해당 위치를 true로 변경
		for (int i = 0; i < LAND_MINE_COUNT; i++) {
			int col = new Random().nextInt(BOARD_COL_SIZE);
			int row = new Random().nextInt(BOARD_ROW_SIZE);
			BOARD[row][col].turnOnLandMine();
		}

		for (int row = 0; row < BOARD_ROW_SIZE; row++) {
			for (int col = 0; col < BOARD_COL_SIZE; col++) {
				// 지뢰가 있는 경우
				if (isLandMineCell(row, col)) {
					continue;
				}

				// 지뢰가 아닌 경우
				int count = countNearbyLandMines(row, col);
				BOARD[row][col].updateNearbyLandMineCount(count);
			}
		}
	}

	/**
	 * 근처에 위치한 지뢰의 수 조회
	 * @param row
	 * @param col
	 * @return
	 */
	private int countNearbyLandMines(int row, int col) {
		int count = 0;
		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		if (row - 1 >= 0 && col - 1 >= 0 && isLandMineCell(row - 1, col - 1)) { // 왼쪽 위 대각선
			count++;
		}
		if (row - 1 >= 0 && isLandMineCell(row - 1, col)) {
			count++;
		}
		if (row - 1 >= 0 && col + 1 < BOARD_COL_SIZE && isLandMineCell(row - 1, col + 1)) {
			count++;
		}
		if (col - 1 >= 0 && isLandMineCell(row, col - 1)) {
			count++;
		}
		if (col + 1 < BOARD_COL_SIZE && isLandMineCell(row, col + 1)) {
			count++;
		}
		if (row + 1 < 8 && col - 1 >= 0 && isLandMineCell(row + 1, col - 1)) {
			count++;
		}
		if (row + 1 < BOARD_ROW_SIZE && isLandMineCell(row + 1, col)) {
			count++;
		}
		if (row + 1 < BOARD_ROW_SIZE && col + 1 < BOARD_COL_SIZE && isLandMineCell(row + 1, col + 1)) {
			count++;
		}
		return count;
	}

	private void open(int row, int col) {
		// 칸을 벗어났는지 검증
		if (row < 0 || row >= BOARD_ROW_SIZE || col < 0 || col >= BOARD_COL_SIZE) {
			return;
		}

		// 기존 cell이 이미 선택되었었는지 검증
		if (BOARD[row][col].isOpened()) {
			return;
		}

		// 지뢰 cell인지 검증
		if (isLandMineCell(row, col)) {
			return;
		}

		BOARD[row][col].open();

		// 지뢰 count를 가지고 있는 cell인지 검증
		if (BOARD[row][col].hasLandMineCount()) {
			return;
		}

		// 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
		open(row - 1, col - 1);
		open(row - 1, col);
		open(row - 1, col + 1);
		open(row, col - 1);
		open(row, col + 1);
		open(row + 1, col - 1);
		open(row + 1, col);
		open(row + 1, col + 1);
	}
}
