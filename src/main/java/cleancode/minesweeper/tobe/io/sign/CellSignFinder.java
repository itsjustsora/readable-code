package cleancode.minesweeper.tobe.io.sign;

import java.util.List;

import cleancode.minesweeper.tobe.cell.CellSnapshot;

public class CellSignFinder {

    public static final List<CellSignProvidable> CELL_SIGN_PROVIDERS = List.of(
        new EmptyCellSignProvider(),
        new FlagCellSignProvider(),
        new LandMineCellSignProvider(),
        new NumberCellSignProvider(),
        new UncheckedCellSignProvider()
    );

    public String findCellSign(CellSnapshot snapshot) {
        return CELL_SIGN_PROVIDERS.stream()
            .filter(provider -> provider.supports(snapshot)) // 스냅샷에 맞는 Provider 선택
            .findFirst()
            .map(provider -> provider.provide(snapshot)) // 문양
            .orElseThrow(() -> new IllegalArgumentException("확인할 수 없는 셀입니다."));
    }
}
