package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.io.ConsoleInputHandler;
import cleancode.minesweeper.tobe.io.ConsoleOutputHandler;

public class Minesweeper {

	public static final int BOARD_ROW_SIZE = 8;
	public static final int BOARD_COL_SIZE = 10;

	private final GameBoard gameBoard = new GameBoard(BOARD_ROW_SIZE, BOARD_COL_SIZE);
	private final ConsoleInputHandler consoleInputHandler =  new ConsoleInputHandler();
	private final ConsoleOutputHandler consoleOutputHandler = new ConsoleOutputHandler();

	private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

	public void run() {
		consoleOutputHandler.showGameStartComments();

		// 게임 초기화
		gameBoard.initializeGame();

		while (true) {
			try {
				consoleOutputHandler.showBoard(gameBoard);

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
			gameBoard.flag(selectedRowIndex, selectedColIndex);
			checkIfGameIsOver();
			return;
		}

		// cell 오픈을 선택한 경우
		if (doesUserChooseToOpenCell(userActionInput)) {
			// 지뢰 cell을 선택한 경우
			if (gameBoard.isLandMineCell(selectedRowIndex, selectedColIndex)) {
				gameBoard.open(selectedRowIndex, selectedColIndex);
				changeGameStatusToLose();
				return;
			}

			// 일반 cell을 선택한 경우
			gameBoard.openSurroundedCells(selectedRowIndex, selectedColIndex);
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
		if (gameBoard.isAllCellChecked()) {
			changeGameStatusToWin();
		}
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
}
