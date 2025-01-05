package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

/**
 * Реализация интерфейса UnitTargetPathFinder для поиска кратчайшего пути между юнитами.
 * Этот класс использует алгоритм A* с эвристикой Манхэттена для нахождения пути на игровом поле
 * с учетом препятствий, представленных в виде занятых клеток.
 *
 * Алгоритмическая сложность:
 * 1. Инициализация структур данных: O(WIDTH * HEIGHT), где WIDTH и HEIGHT — размеры игрового поля.
 *    - Обработка всех клеток для создания начальных значений занимает линейное время от числа клеток.
 * 2. Основной цикл:
 *    - Каждый узел обрабатывается один раз, а его соседи проверяются в количестве DIRECTIONS (4 направления).
 *    - Использование PriorityQueue добавляет логарифмическую сложность на каждую операцию извлечения.
 *    - Итоговая сложность цикла: O((WIDTH * HEIGHT) * log(WIDTH * HEIGHT)).
 * 3. Построение пути:
 *    - Реконструкция пути из карты cameFrom занимает время, пропорциональное длине пути, т.е. O(WIDTH + HEIGHT) в худшем случае.
 * Итоговая сложность алгоритма: O((WIDTH * HEIGHT) * log(WIDTH * HEIGHT)), что соответствует требованиям задачи.
 */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;  // Ширина игрового поля, убедитесь, что эта константа соответствует реальным настройкам игрового процесса
    private static final int HEIGHT = 21; // Высота игрового поля, убедитесь, что эта константа соответствует реальным настройкам игрового процесса
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // Возможные направления перемещения

    /**
     * Метод находит кратчайший путь между атакующим и атакуемым юнитами.
     * @param attackUnit атакующий юнит
     * @param targetUnit атакуемый юнит
     * @param existingUnitList список всех существующих юнитов
     * @return список объектов Edge, представляющих координаты клеток пути
     */
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        // Инициализация структур данных
        Map<Edge, Integer> gScore = new HashMap<>(); // Стоимость пути от начальной до текущей клетки
        Map<Edge, Integer> fScore = new HashMap<>(); // Оценка полной стоимости пути через клетку (g + эвристика)
        Set<Edge> occupiedCells = new HashSet<>(); // Занятые клетки
        for (Unit unit : existingUnitList) {
            if (unit.isAlive()) {
                occupiedCells.add(new Edge(unit.getxCoordinate(), unit.getyCoordinate()));
            }
        }

        // Начальная и целевая клетки
        Edge start = new Edge(startX, startY); // Стартовая клетка
        Edge goal = new Edge(targetX, targetY); // Целевая клетка

        // Очередь для обработки узлов на основе приоритета (fScore)
        PriorityQueue<Edge> openSet = new PriorityQueue<>(Comparator.comparingInt(fScore::get));
        openSet.add(start); // Добавляем стартовую клетку в очередь

        // Инициализация начальных значений для gScore и fScore
        gScore.put(start, 0); // Расстояние до стартовой клетки = 0
        fScore.put(start, heuristic(start, goal)); // Эвристическая оценка пути до цели

        // Карта переходов для восстановления пути
        Map<Edge, Edge> cameFrom = new HashMap<>();

        // Основной цикл обработки узлов
        while (!openSet.isEmpty()) {
            Edge current = openSet.poll(); // Извлекаем узел с наименьшим fScore

            // Если достигли цели, восстанавливаем путь
            if (current.equals(goal)) {
                return reconstructPath(cameFrom, current);
            }

            // Обработка соседей текущей клетки
            for (Edge neighbor : getNeighbors(current, WIDTH, HEIGHT)) {
                if (occupiedCells.contains(neighbor)) continue; // Пропускаем занятые клетки

                // Вычисляем новую стоимость пути до соседней клетки
                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + 1;

                // Если новый путь короче, обновляем значения
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current); // Обновляем путь
                    gScore.put(neighbor, tentativeGScore); // Обновляем gScore
                    fScore.put(neighbor, tentativeGScore + heuristic(neighbor, goal)); // Обновляем fScore

                    // Добавляем клетку в очередь, если её там ещё нет
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        // Если путь не найден, выводим сообщение
        System.out.println("Путь от юнита \"" + attackUnit.getName() + "\" к юниту \"" + targetUnit.getName() + "\" не найден.");
        return new ArrayList<>();
    }

    /**
     * Оценка эвристики (Манхэттенское расстояние)
     * @param a текущая клетка
     * @param b целевая клетка
     * @return оценка расстояния
     */
    private int heuristic(Edge a, Edge b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY());
    }

    /**
     * Восстановление пути из карты cameFrom
     * @param cameFrom карта переходов
     * @param current конечная клетка
     * @return список клеток пути
     */
    private List<Edge> reconstructPath(Map<Edge, Edge> cameFrom, Edge current) {
        List<Edge> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        return path;
    }

    /**
     * Получение соседних клеток
     * @param edge текущая клетка
     * @param width ширина поля
     * @param height высота поля
     * @return список соседних клеток
     */
    private List<Edge> getNeighbors(Edge edge, int width, int height) {
        List<Edge> neighbors = new ArrayList<>();
        int x = edge.getX();
        int y = edge.getY();

        for (int[] direction : DIRECTIONS) {
            int newX = x + direction[0];
            int newY = y + direction[1];
            if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                neighbors.add(new Edge(newX, newY));
            }
        }

        return neighbors;
    }
}