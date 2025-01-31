package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;

public class Minesweeper implements GameInitializable, GameRunnable {

	private final GameBoard gameBoard;
	private final BoardIndexConverter boardIndexConverter = new BoardIndexConverter();
	private final InputHandler inputHandler;
	private final OutputHandler outputHandler;

	private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

	public Minesweeper(GameLevel gameLevel, InputHandler inputHandler, OutputHandler outputHandler) {
		gameBoard = new GameBoard(gameLevel);
		this.inputHandler = inputHandler;
		this.outputHandler = outputHandler;
	}

	@Override
	public void initialize() {
		// 게임 초기화
		gameBoard.initializeGame();
	}

	@Override
	public void run() {
		outputHandler.showGameStartComments();

		while (true) {
			try {
				outputHandler.showBoard(gameBoard);

				if (doesUserWinTheGame()) {
					outputHandler.showGameWinningComment();
					break;
				}
				if (doesUserLoseTheGame()) {
					outputHandler.showGameLosingComment();
					break;
				}

				String cellInput = getCellInputFromUser();
				String userActionInput = getUserActionInputFromUser();

				actOnCell(cellInput, userActionInput);
			} catch (GameException e) {
				outputHandler.showExceptionMessage(e);
			} catch (Exception e) {
				outputHandler.showSimpleMessage("프로그램에 문제가 생겼습니다.");
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
		outputHandler.showSimpleMessage("잘못된 번호를 선택하셨습니다.");
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
		outputHandler.showCommentForSelectingCell();
		return inputHandler.getUserInput();
	}

	private String getCellInputFromUser() {
		outputHandler.showCommentForUserAction();
		return inputHandler.getUserInput();
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
