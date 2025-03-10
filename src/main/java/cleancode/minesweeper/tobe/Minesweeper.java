package cleancode.minesweeper.tobe;

import cleancode.minesweeper.tobe.config.GameConfig;
import cleancode.minesweeper.tobe.game.GameInitializable;
import cleancode.minesweeper.tobe.game.GameRunnable;
import cleancode.minesweeper.tobe.gamelevel.GameLevel;
import cleancode.minesweeper.tobe.io.InputHandler;
import cleancode.minesweeper.tobe.io.OutputHandler;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.user.UserAction;

public class Minesweeper implements GameInitializable, GameRunnable {

	private final GameBoard gameBoard;
	private final InputHandler inputHandler;
	private final OutputHandler outputHandler;

	private int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

	public Minesweeper(GameConfig gameConfig) {
		gameBoard = new GameBoard(gameConfig.getGameLevel());
		this.inputHandler = gameConfig.getInputHandler();
		this.outputHandler = gameConfig.getOutputHandler();
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

				CellPosition cellPosition = getCellInputFromUser();
				UserAction userAction = getUserActionInputFromUser();

				actOnCell(cellPosition, userAction);
			} catch (GameException e) {
				outputHandler.showExceptionMessage(e);
			} catch (Exception e) {
				outputHandler.showSimpleMessage("프로그램에 문제가 생겼습니다.");
			}
		}
	}

	private void actOnCell(CellPosition cellPosition, UserAction userAction) {
		// 깃발 꽂기를 선택한 경우
		if (doesUserChooseToPlantFlag(userAction)) {
			gameBoard.flagAt(cellPosition);
			checkIfGameIsOver();
			return;
		}

		// cell 오픈을 선택한 경우
		if (doesUserChooseToOpenCell(userAction)) {
			// 지뢰 cell을 선택한 경우
			if (gameBoard.isLandMineCellAt(cellPosition)) {
				gameBoard.openAt(cellPosition);
				changeGameStatusToLose();
				return;
			}

			// 일반 cell을 선택한 경우
			gameBoard.openSurroundedCells(cellPosition);
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

	private boolean doesUserChooseToOpenCell(UserAction userAction) {
		return userAction == UserAction.OPEN;
	}

	private boolean doesUserChooseToPlantFlag(UserAction userAction) {
		return userAction == UserAction.FLAG;
	}

	private UserAction getUserActionInputFromUser() {
		outputHandler.showCommentForSelectingCell();
		return inputHandler.getUserActionFromUser();
	}

	private CellPosition getCellInputFromUser() {
		outputHandler.showCommentForUserAction();
		CellPosition cellPosition = inputHandler.getCellPositionFromUser();
		if (gameBoard.isInvalidCellPosition(cellPosition)) {
			throw new GameException("잘못된 좌표를 선택하셨습니다.");
		}
		return cellPosition;
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
