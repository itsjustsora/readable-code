package cleancode.minesweeper.tobe;

import java.util.Random;
import java.util.Scanner;

public class MinesweeperGame {

    private static String[][] board = new String[8][10];
    private static Integer[][] landMineCounts = new Integer[8][10];
    private static boolean[][] landMines = new boolean[8][10];
    private static int gameStatus = 0; // 0: 게임 중, 1: 승리, -1: 패배

    public static void main(String[] args) {
        showGameStartComments();

        Scanner scanner = new Scanner(System.in);

        // 게임 초기화
        initializeGame();
        
        while (true) {
            showBoard();

            if (gameStatus == 1) {
                System.out.println("지뢰를 모두 찾았습니다. GAME CLEAR!");
                break;
            }
            if (gameStatus == -1) {
                System.out.println("지뢰를 밟았습니다. GAME OVER!");
                break;
            }

            System.out.println("선택할 좌표를 입력하세요. (예: a1)");
            String cellInput = scanner.nextLine();

            System.out.println("선택한 셀에 대한 행위를 선택하세요. (1: 오픈, 2: 깃발 꽂기)");
            String userActionInput = scanner.nextLine();

            char cellInputCol = cellInput.charAt(0); // 알파벳
            char cellInputRow = cellInput.charAt(1); // 숫자

            int selectedColIndex = convertColFrom(cellInputCol);
            int selectedRowIndex = convertRowFrom(cellInputRow);

            if (userActionInput.equals("2")) {
                board[selectedRowIndex][selectedColIndex] = "⚑";

                checkIfGameIsOver();
            } else if (userActionInput.equals("1")) {
                // 지뢰 cell을 선택한 경우
                if (landMines[selectedRowIndex][selectedColIndex]) {
                    board[selectedRowIndex][selectedColIndex] = "☼";
                    gameStatus = -1;
                    continue;
                } else {
                    // 일반 cell을 선택한 경우
                    open(selectedRowIndex, selectedColIndex);
                }

                checkIfGameIsOver();
            } else {
                System.out.println("잘못된 번호를 선택하셨습니다.");
            }
        }
    }

    /**
     * 셀이 모두 열려있는지 확인 후 게임 종료 값 설정
     */
    private static void checkIfGameIsOver() {
        // 셀이 모두 열려있는지 확인
        boolean isAllOpened = isAllCellOpened();

        if (isAllOpened) {
            gameStatus = 1;
        }
    }

    /**
     * 셀이 모두 열려있는지 확인
     * @return
     */
    private static boolean isAllCellOpened() {
        boolean isAllOpened = true; // 전체 보드가 다 열려있는지 나타내는 플래그 값
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                if (board[row][col].equals("□")) {
                    isAllOpened = false;
                }
            }
        }
        return isAllOpened;
    }

    private static int convertRowFrom(char cellInputRow) {
        return Character.getNumericValue(cellInputRow) - 1;
    }

    private static int convertColFrom(char cellInputCol) {
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
			default -> -1;
		};
    }

    private static void showBoard() {
        System.out.println("   a b c d e f g h i j");

        for (int row = 0; row < 8; row++) {
            System.out.printf("%d  ", row + 1);
            for (int col = 0; col < 10; col++) {
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }

        System.out.println();
    }

    private static void initializeGame() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                // 행 8, 열 10
                board[row][col] = "□";
            }
        }

        // 랜덤 자리에 지뢰를 10개 설정하여 해당 위치를 true로 변경
        for (int i = 0; i < 10; i++) {
            int col = new Random().nextInt(10);
            int row = new Random().nextInt(8);
            landMines[row][col] = true;
        }

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                int count = 0;

                // 지뢰가 없는 경우
                if (!landMines[row][col]) {
                    // 현재 칸 기준으로 주위 8칸을 돌면서 지뢰를 몇 개 가지고 있는지 계산
                    if (row - 1 >= 0 && col - 1 >= 0 && landMines[row - 1][col - 1]) { // 왼쪽 위 대각선
                        count++;
                    }
                    if (row - 1 >= 0 && landMines[row - 1][col]) {
                        count++;
                    }
                    if (row - 1 >= 0 && col + 1 < 10 && landMines[row - 1][col + 1]) {
                        count++;
                    }
                    if (col - 1 >= 0 && landMines[row][col - 1]) {
                        count++;
                    }
                    if (col + 1 < 10 && landMines[row][col + 1]) {
                        count++;
                    }
                    if (row + 1 < 8 && col - 1 >= 0 && landMines[row + 1][col - 1]) {
                        count++;
                    }
                    if (row + 1 < 8 && landMines[row + 1][col]) {
                        count++;
                    }
                    if (row + 1 < 8 && col + 1 < 10 && landMines[row + 1][col + 1]) {
                        count++;
                    }
                    landMineCounts[row][col] = count;
                    continue;
                }
                landMineCounts[row][col] = 0;
            }
        }
    }

    private static void showGameStartComments() {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("지뢰찾기 게임 시작!");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
    }

    private static void open(int row, int col) {
        // 칸을 벗어났는지 검증
        if (row < 0 || row >= 8 || col < 0 || col >= 10) {
            return;
        }

        // 기존 cell이 이미 선택되었었는지 검증
        if (!board[row][col].equals("□")) {
            return;
        }

        // 지뢰 cell인지 검증
        if (landMines[row][col]) {
            return;
        }

        // 지뢰 count를 가지고 있는 cell인지 검증
        if (landMineCounts[row][col] != 0) {
            board[row][col] = String.valueOf(landMineCounts[row][col]);
            return;
        } else {
            // 빈 cell 표시
            board[row][col] = "■";
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
