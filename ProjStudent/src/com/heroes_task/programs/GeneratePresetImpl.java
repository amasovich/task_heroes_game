package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

/**
 * Реализация интерфейса GeneratePreset для генерации армии компьютера.
 *
 * Алгоритм:
 * 1. Сортирует юниты по комбинированной эффективности (атака/стоимость + здоровье/стоимость).
 * 2. Добавляет юниты в армию с учетом ограничений по очкам и количеству.
 * 3. Генерирует уникальные координаты для каждого юнита с использованием множества для проверки.
 *
 * Доказательство эффективности:
 * - Сортировка списка юнитов: O(n log n), где n — количество типов юнитов.
 * - Перебор юнитов и добавление: O(n * m), где m — максимальное число юнитов.
 * - Проверка уникальности координат с использованием множества: O(1) на каждую проверку.
 * Итоговая сложность: O(n log n + n * m).
 */
public class GeneratePresetImpl implements GeneratePreset {

    /**
     * Генерация армии компьютера на основе предоставленного списка юнитов и ограничения по очкам.
     *
     * @param unitList  список типов юнитов.
     * @param maxPoints максимальное количество очков для армии.
     * @return объект Army, содержащий список юнитов.
     */
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army(); // Армия компьютера
        List<Unit> sortedUnits = new ArrayList<>(unitList);

        // Сортируем юниты по комбинированной эффективности (атака/стоимость + здоровье/стоимость)
        sortedUnits.sort(Comparator.comparingDouble(unit -> -((double) unit.getBaseAttack() / unit.getCost() + (double) unit.getHealth() / unit.getCost())));

        Map<String, Integer> unitCount = new HashMap<>(); // Счетчик юнитов по типам
        Set<String> usedCoordinates = new HashSet<>(); // Занятые координаты на поле
        Random random = new Random(); // Для генерации случайных координат

        // Добавляем юниты в армию
        for (Unit unit : sortedUnits) {
            String unitType = unit.getUnitType();
            int unitCost = unit.getCost();

            // Проверяем лимиты: очки и количество юнитов данного типа
            if (maxPoints < unitCost) continue;
            unitCount.putIfAbsent(unitType, 0);
            if (unitCount.get(unitType) >= 11) continue;

            // Генерируем уникальные координаты для юнита
            String coordinates = generateUniqueCoordinates(usedCoordinates, random);
            if (coordinates == null) {
                System.out.println("Не удалось найти свободные координаты для юнита: " + unitType);
                continue;
            }

            // Создаем новый юнит с уникальными координатами
            String[] coordParts = coordinates.split(",");
            Unit newUnit = new Unit(
                    unitType + " " + (unitCount.get(unitType) + 1), // Уникальное имя
                    unitType,
                    unit.getHealth(),
                    unit.getBaseAttack(),
                    unitCost,
                    unit.getAttackType(),
                    unit.getAttackBonuses(),
                    unit.getDefenceBonuses(),
                    Integer.parseInt(coordParts[0]), // x-координата
                    Integer.parseInt(coordParts[1])  // y-координата
            );

            // Добавляем юнит в армию
            computerArmy.getUnits().add(newUnit);
            usedCoordinates.add(coordinates);
            unitCount.put(unitType, unitCount.get(unitType) + 1);
            maxPoints -= unitCost;

            System.out.println("Добавлен юнит: " + newUnit.getName() + ", оставшиеся очки: " + maxPoints);
        }

        return computerArmy;
    }

    /**
     * Генерация уникальных координат на игровом поле.
     *
     * @param usedCoordinates множество занятых координат.
     * @param random          объект Random для генерации случайных чисел.
     * @return строка координат в формате "x,y" или null, если уникальные координаты не найдены.
     */
    private String generateUniqueCoordinates(Set<String> usedCoordinates, Random random) {
        int attempts = 100; // Максимальное количество попыток
        while (attempts-- > 0) {
            int x = random.nextInt(3); // Координата X (ширина поля: 0-2)
            int y = random.nextInt(21); // Координата Y (высота поля: 0-20)
            String coordinates = x + "," + y;
            if (!usedCoordinates.contains(coordinates)) {
                return coordinates;
            }
        }
        return null; // Если уникальные координаты не найдены
    }
}