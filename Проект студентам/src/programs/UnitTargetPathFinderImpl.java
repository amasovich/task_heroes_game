package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

/**
 * Реализация интерфейса UnitTargetPathFinder для поиска кратчайшего пути между юнитами.
 * Этот класс использует алгоритм Дейкстры для нахождения пути на игровом поле
 * с учетом препятствий, представленных в виде занятых клеток.
 */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;  // Ширина игрового поля
    private static final int HEIGHT = 21; // Высота игрового поля
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
        int[][] distances = new int[WIDTH][HEIGHT]; // Матрица расстояний
        for (int[] row : distances) Arrays.fill(row, Integer.MAX_VALUE); // Заполнение максимальными значениями
        distances[startX][startY] = 0; // Установка стартовой точки

        boolean[][] visited = new boolean[WIDTH][HEIGHT]; // Матрица посещенных клеток
        Edge[][] previous = new Edge[WIDTH][HEIGHT]; // Матрица предыдущих узлов для восстановления пути

        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance)); // Очередь для обработки
        queue.add(new EdgeDistance(startX, startY, 0)); // Добавляем стартовую точку

        // Множество занятых клеток (препятствий)
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive()) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Алгоритм поиска пути (Дейкстра)
        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll(); // Извлекаем элемент с минимальным расстоянием
            int x = current.getX();
            int y = current.getY();

            if (visited[x][y]) continue; // Пропускаем уже посещенные клетки
            visited[x][y] = true;

            if (x == targetX && y == targetY) break; // Если достигли цели, заканчиваем

            for (int[] direction : DIRECTIONS) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if (isValid(newX, newY, occupiedCells, targetUnit)) {
                    int newDistance = distances[x][y] + 1;
                    if (newDistance < distances[newX][newY]) {
                        distances[newX][newY] = newDistance;
                        queue.add(new EdgeDistance(newX, newY, newDistance)); // Добавляем в очередь
                        previous[newX][newY] = new Edge(x, y); // Обновляем предыдущую клетку
                    }
                }
            }
        }

        // Построение пути от цели к старту
        List<Edge> path = new ArrayList<>();
        if (previous[targetX][targetY] != null) {
            int x = targetX;
            int y = targetY;

            while (x != startX || y != startY) {
                path.add(new Edge(x, y));
                Edge edge = previous[x][y];
                x = edge.getX();
                y = edge.getY();
            }

            path.add(new Edge(startX, startY)); // Добавляем стартовую точку
            Collections.reverse(path); // Разворачиваем путь в правильном порядке
        } else {
            System.out.println("Путь от юнита \"" + attackUnit.getName() + "\" к юниту \"" + targetUnit.getName() + "\" не найден.");
        }

        return path;
    }

    /**
     * Проверяет, является ли клетка допустимой для перемещения.
     * @param x координата X
     * @param y координата Y
     * @param occupiedCells множество занятых клеток
     * @param targetUnit атакуемый юнит (цель)
     * @return true, если клетка допустима для перемещения, иначе false
     */
    private boolean isValid(int x, int y, Set<String> occupiedCells, Unit targetUnit) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT &&
                (!occupiedCells.contains(x + "," + y) || (x == targetUnit.getxCoordinate() && y == targetUnit.getyCoordinate()));
    }
}