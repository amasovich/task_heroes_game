package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

/**
 * Реализация интерфейса GeneratePreset для генерации армии компьютера.
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

        // Сортируем юниты по эффективности (атака/стоимость, здоровье/стоимость)
        sortedUnits.sort((u1, u2) -> {
            double effectiveness1 = (double) u1.getBaseAttack() / u1.getCost();
            double effectiveness2 = (double) u2.getBaseAttack() / u2.getCost();
            if (Double.compare(effectiveness2, effectiveness1) == 0) {
                return Double.compare((double) u2.getHealth() / u2.getCost(),
                        (double) u1.getHealth() / u1.getCost());
            }
            return Double.compare(effectiveness2, effectiveness1);
        });

        Map<String, Integer> unitCount = new HashMap<>(); // Счетчик юнитов по типам
        List<int[]> usedCoordinates = new ArrayList<>(); // Занятые координаты на поле
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
            int[] coordinates = generateUniqueCoordinates(usedCoordinates, random);
            if (coordinates == null) {
                System.out.println("Не удалось найти свободные координаты для юнита: " + unitType);
                continue;
            }

            // Создаем новый юнит с уникальными координатами
            Unit newUnit = new Unit(
                    unitType + " " + (unitCount.get(unitType) + 1), // Уникальное имя
                    unitType,
                    unit.getHealth(),
                    unit.getBaseAttack(),
                    unitCost,
                    unit.getAttackType(),
                    unit.getAttackBonuses(),
                    unit.getDefenceBonuses(),
                    coordinates[0], // x-координата
                    coordinates[1]  // y-координата
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
     * @param usedCoordinates список занятых координат.
     * @param random          объект Random для генерации случайных чисел.
     * @return массив из двух элементов [x, y] или null, если уникальные координаты не найдены.
     */
    private int[] generateUniqueCoordinates(List<int[]> usedCoordinates, Random random) {
        int attempts = 100; // Максимальное количество попыток
        while (attempts-- > 0) {
            int x = random.nextInt(3); // Координата X (ширина поля: 0-2)
            int y = random.nextInt(21); // Координата Y (высота поля: 0-20)
            int[] newCoordinates = {x, y};
            boolean isUnique = usedCoordinates.stream().noneMatch(coords ->
                    Arrays.equals(coords, newCoordinates));
            if (isUnique) {
                return newCoordinates;
            }
        }
        return null; // Если уникальные координаты не найдены
    }
}