package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

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
        // Целевой ряд, который не проверяется на блокировку
        int targetRow = isLeftArmyTarget ? 2 : 0;

        // Перебор всех рядов
        for (int rowIndex = 0; rowIndex < unitsByRow.size(); rowIndex++) {
            // Перебор всех юнитов в текущем ряду
            for (Unit unit : unitsByRow.get(rowIndex)) {
                // Проверяем, жив ли юнит
                if (unit.isAlive()) {
                    // Ряд, с которого может быть блокировка
                    int blockingRow = rowIndex + direction;

                    // Проверяем, блокирован ли юнит
                    boolean isBlocked = blockingRow >= 0 && blockingRow < unitsByRow.size() &&
                            unitsByRow.get(blockingRow).stream()
                                    .anyMatch(otherUnit -> otherUnit.getyCoordinate() == unit.getyCoordinate() && otherUnit.isAlive());

                    // Если юнит не блокирован или находится в крайнем ряду, добавляем его
                    if (!isBlocked || rowIndex == targetRow) {
                        suitableUnits.add(unit);
                    }
                }
            }
        }

        // Если подходящих юнитов нет, выводим сообщение
        if (suitableUnits.isEmpty()) {
            System.out.println("Подходящие юниты для атаки не найдены!");
        }

        return suitableUnits;
    }
}
