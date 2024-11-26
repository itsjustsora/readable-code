package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.ConsoleInputHandler;
import cleancode.minesweeper.tobe.io.ConsoleOutputHandler;

public class Minesweeper implements GameInitializable, GameRunnable {

	private final GameBoard gameBoard;
	private final BoardIndexConverter boardIndexConverter = new BoardIndexConverter();
	private final ConsoleInputHandler consoleInputHandler =  new ConsoleInputHandler();
	private final ConsoleOutputHandler consoleOutputHandler = new ConsoleOutputHandler();

	private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

	public Minesweeper(GameLevel gameLevel) {
		gameBoard = new GameBoard(gameLevel);
	}

	@Override
	public void initialize() {
		// 게임 초기화
		gameBoard.initializeGame();
	}

	@Override
	public void run() {
		consoleOutputHandler.showGameStartComments();

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
		int selectedColIndex = boardIndexConverter.getSelectedColIndex(cellInput, gameBoard.getColSize());
		int selectedRowIndex = boardIndexConverter.getSelectedRowIndex(cellInput, gameBoard.getRowSize());

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
}
