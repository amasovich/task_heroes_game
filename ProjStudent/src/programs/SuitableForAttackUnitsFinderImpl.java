package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Реализация интерфейса SuitableForAttackUnitsFinder.
 * Определяет список подходящих для атаки юнитов, основываясь на их положении
 * и наличии блокирующих юнитов с противоположного ряда.
 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    /**
     * Метод возвращает список юнитов, подходящих для атаки.
     *
     * @param unitsByRow список рядов юнитов, где каждый ряд представляет собой список юнитов.
     * @param isLeftArmyTarget флаг, указывающий, атакуется ли левая армия (true) или правая (false).
     * @return список подходящих для атаки юнитов.
     */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Направление проверки: -1 для атаки левой армии, 1 для атаки правой армии
        int direction = isLeftArmyTarget ? -1 : 1;

        // Перебор всех рядов
        for (int rowIndex = 0; rowIndex < unitsByRow.size(); rowIndex++) {
            List<Unit> row = unitsByRow.get(rowIndex);
            Unit edgeUnit = isLeftArmyTarget ? getRightmostUnit(row) : getLeftmostUnit(row);

            if (edgeUnit != null && edgeUnit.isAlive()) {
                int blockingRow = rowIndex + direction;
                boolean isBlocked = blockingRow >= 0 && blockingRow < unitsByRow.size() &&
                        unitsByRow.get(blockingRow).stream()
                                .anyMatch(otherUnit -> otherUnit.getyCoordinate() == edgeUnit.getyCoordinate() && otherUnit.isAlive());

                if (!isBlocked) {
                    suitableUnits.add(edgeUnit);
                }
            }
        }

        // Если подходящих юнитов нет, выводим сообщение
        if (suitableUnits.isEmpty()) {
            System.out.println("Подходящие юниты для атаки не найдены!");
        }

        return suitableUnits;
    }

    /**
     * Возвращает правого крайнего живого юнита в ряду.
     *
     * @param row список юнитов в ряду.
     * @return правый крайний живой юнит или null, если такого нет.
     */
    private Unit getRightmostUnit(List<Unit> row) {
        for (int i = row.size() - 1; i >= 0; i--) {
            Unit unit = row.get(i);
            if (unit != null && unit.isAlive()) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Возвращает левого крайнего живого юнита в ряду.
     *
     * @param row список юнитов в ряду.
     * @return левый крайний живой юнит или null, если такого нет.
     */
    private Unit getLeftmostUnit(List<Unit> row) {
        for (Unit unit : row) {
            if (unit != null && unit.isAlive()) {
                return unit;
            }
        }
        return null;
    }
}