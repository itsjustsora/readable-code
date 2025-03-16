package cleancode.minesweeper.tobe.io;

import java.util.Scanner;

import cleancode.minesweeper.tobe.BoardIndexConverter;
import cleancode.minesweeper.tobe.position.CellPosition;
import cleancode.minesweeper.tobe.user.UserAction;

public class ConsoleInputHandler implements InputHandler {

	public static final Scanner SCANNER = new Scanner(System.in);

	private final BoardIndexConverter boardIndexConverter = new BoardIndexConverter();

	@Override
	public UserAction getUserActionFromUser() {
		String userInput = SCANNER.nextLine();

		// NPE를 방지하기 위한 "1"이라는 확실합 타입으로 비교
		if ("1".equals(userInput)) {
			return UserAction.OPEN;
		}
		if ("2".equals(userInput)) {
			return UserAction.FLAG;
		}
		return UserAction.UNKNOWN;
	}

	@Override
	public CellPosition getCellPositionFromUser() {
		String userInput = SCANNER.nextLine();

		int rowIndex = boardIndexConverter.getSelectedRowIndex(userInput);
		int colIndex = boardIndexConverter.getSelectedColIndex(userInput);

		return CellPosition.of(rowIndex, colIndex);
	}
}
