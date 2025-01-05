package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Реализация интерфейса SimulateBattle, отвечающего за симуляцию боя между армией игрока и компьютера.
 *
 * <p>Доказательство эффективности алгоритма:</p>
 * <ul>
 *     <li><b>Сложность одного раунда:</b>
 *         <ul>
 *             <li>Инициализация очередей ходов: O(n * log n), где n — количество юнитов.</li>
 *             <li>Обработка ходов: Каждый юнит извлекается из очереди за O(log n) и атакует за O(1). Для n юнитов это O(n * log n).</li>
 *             <li>Удаление мёртвых юнитов: O(n).</li>
 *         </ul>
 *         Итоговая сложность одного раунда: O(n * log n).</li>
 *     <li><b>Общее количество раундов:</b> В худшем случае равно количеству юнитов (n), так как в каждом раунде может быть уничтожен только один юнит.</li>
 *     <li><b>Итоговая сложность:</b> O(n * (n * log n)) = O(n^2 * log n).</li>
 * </ul>
 */
public class SimulateBattleImpl implements SimulateBattle {

    /**
     * Логгер для вывода событий боя после каждой атаки.
     */
    private final PrintBattleLog printBattleLog;

    /**
     * Конструктор, инициализирующий логгер для боя.
     *
     * @param printBattleLog экземпляр PrintBattleLog для логирования событий боя
     */
    public SimulateBattleImpl(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    /**
     * Выполняет симуляцию боя между армией игрока и армией компьютера.
     *
     * @param playerArmy   армия игрока, содержащая список юнитов
     * @param computerArmy армия компьютера, содержащая список юнитов
     * @throws InterruptedException если симуляция была прервана
     */
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Инициализируем очереди ходов для обеих армий
        PriorityQueue<Unit> playerQueue = new PriorityQueue<>(Comparator.comparingInt(Unit::getBaseAttack).reversed());
        PriorityQueue<Unit> computerQueue = new PriorityQueue<>(Comparator.comparingInt(Unit::getBaseAttack).reversed());

        // Добавляем в очереди только живых юнитов
        playerQueue.addAll(playerArmy.getUnits().stream().filter(Unit::isAlive).toList());
        computerQueue.addAll(computerArmy.getUnits().stream().filter(Unit::isAlive).toList());

        // Проверяем, есть ли живые юниты в обеих армиях
        if (playerQueue.isEmpty() || computerQueue.isEmpty()) {
            System.out.println("Бой невозможен, так как одна из армий не имеет юнитов.");
            return;
        }

        int round = 1;

        // Основной цикл боя
        while (!playerQueue.isEmpty() && !computerQueue.isEmpty()) {
            System.out.println("Раунд " + round);
            System.out.println("Живые юниты армии игрока: " + playerQueue.size());
            System.out.println("Живые юниты армии компьютера: " + computerQueue.size());

            // Очередь для текущего раунда
            PriorityQueue<Unit> turnQueue = new PriorityQueue<>(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            turnQueue.addAll(playerQueue);
            turnQueue.addAll(computerQueue);

            // Обрабатываем ходы юнитов
            while (!turnQueue.isEmpty()) {
                Unit currentUnit = turnQueue.poll();

                // Пропускаем мёртвых юнитов
                if (!currentUnit.isAlive()) {
                    continue;
                }

                Unit target;
                if (playerQueue.contains(currentUnit)) {
                    // Атакует юнит игрока
                    target = currentUnit.getProgram().attack();
                    printBattleLog.printBattleLog(currentUnit, target);
                    if (target != null && !target.isAlive()) {
                        computerQueue.remove(target);
                    }
                } else {
                    // Атакует юнит компьютера
                    target = currentUnit.getProgram().attack();
                    printBattleLog.printBattleLog(currentUnit, target);
                    if (target != null && !target.isAlive()) {
                        playerQueue.remove(target);
                    }
                }

                // Завершаем бой, если одна из армий полностью уничтожена
                if (playerQueue.isEmpty() || computerQueue.isEmpty()) {
                    break;
                }
            }

            // Удаляем мёртвых юнитов из очередей
            playerQueue.removeIf(unit -> !unit.isAlive());
            computerQueue.removeIf(unit -> !unit.isAlive());

            // Завершаем раунд
            System.out.println("Раунд " + round + " завершён.");
            round++;
        }

        // Определяем и выводим результат боя
        System.out.println("Бой окончен!");
        if (playerQueue.isEmpty() && computerQueue.isEmpty()) {
            System.out.println("Ничья!");
        } else if (playerQueue.isEmpty()) {
            System.out.println("Победила армия компьютера!");
        } else {
            System.out.println("Победила армия игрока!");
        }
    }
}