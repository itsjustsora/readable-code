package cleancode.minesweeper.tobe.io;

import java.util.List;
import java.util.stream.IntStream;

import cleancode.minesweeper.tobe.GameBoard;
import cleancode.minesweeper.tobe.cell.CellSnapshot;
import cleancode.minesweeper.tobe.cell.CellSnapshotStatus;
import cleancode.minesweeper.tobe.position.CellPosition;

public class ConsoleOutputHandler implements OutputHandler {

	private static final String EMPTY_SIGN = "■";
	private static final String LAND_MINE_SIGN = "☼";
	private static final String FLAG_SIGN = "⚑";
	private static final String UNCHECKED_SIGN = "□";

	@Override
	public void showGameStartComments() {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		System.out.println("지뢰찾기 게임 시작!");
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
	}

	@Override
	public void showBoard(GameBoard board) {
		String alphabets = generateColAlphabets(board);
		System.out.println("    " + alphabets);

		for (int row = 0; row < board.getRowSize(); row++) {
			System.out.printf("%2d  ", row + 1);
			for (int col = 0; col < board.getColSize(); col++) {
				CellPosition cellPosition = CellPosition.of(row, col);

				CellSnapshot cellSnapshot = board.getSnapshot(cellPosition);
				String cellSign = decideCellSignFrom(cellSnapshot);

				System.out.print(cellSign + " ");
			}
			System.out.println();
		}

		System.out.println();
	}

	private String decideCellSignFrom(CellSnapshot cellSnapshot) {
		CellSnapshotStatus status = cellSnapshot.getStatus();
		if (status == CellSnapshotStatus.EMPTY) {
			return EMPTY_SIGN;
		}
		if (status == CellSnapshotStatus.FLAG) {
			return FLAG_SIGN;
		}
		if (status == CellSnapshotStatus.LAND_MIND) {
			return LAND_MINE_SIGN;
		}
		if (status == CellSnapshotStatus.NUMBER) {
			return String.valueOf(cellSnapshot.getNearByLandMineCount());
		}
		if (status == CellSnapshotStatus.UNCHECKED) {
			return UNCHECKED_SIGN;
		}
		throw new IllegalArgumentException("확인할 수 없는 셀입니다.");
	}

	private String generateColAlphabets(GameBoard board) {
		List<String> alphabets = IntStream.range(0, board.getColSize())
			.mapToObj(index -> (char)('a' + index))
			.map(Object::toString)
			.toList();

		return String.join(" ", alphabets);
	}

	@Override
	public void showGameWinningComment() {
		System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
	}

	@Override
	public void showGameLosingComment() {
		System.out.println("지뢰를 밟았습니다. GAME OVER!");
	}

	@Override
	public void showCommentForSelectingCell() {
		System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
	}

	@Override
	public void showCommentForUserAction() {
		System.out.println("선택할 좌표를 입력하세요. (예: a1)");
	}

	@Override
	public void showExceptionMessage(Exception exception) {
		System.out.println(exception.getMessage());
	}

	@Override
	public void showSimpleMessage(String message) {
		System.out.println(message);
	}
}
