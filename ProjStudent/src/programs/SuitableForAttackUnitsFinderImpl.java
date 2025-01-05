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
 *
 * Алгоритмическая сложность:
 * - Перебор всех рядов: O(n), где n — количество рядов (фиксировано и равно 3).
 * - Выбор крайнего юнита (правого или левого): O(1), так как анализируется только крайний юнит.
 * - Проверка на блокировку: O(m), где m — среднее количество юнитов в ряду.
 * Итоговая сложность: O(n * m), что соответствует линейной сложности на двумерной плоскости.
 *
 * Доказательство:
 * 1. Метод перебирает каждый ряд в массиве rows, это O(n).
 * 2. В каждом ряду мы проверяем только крайний юнит (левый или правый), что выполняется за O(1).
 * 3. Проверка блокировки проходит через stream().anyMatch(), что имеет сложность O(m).
 * 4. Учитывая, что n фиксировано (3 ряда), итоговая сложность зависит только от m, то есть O(m).
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