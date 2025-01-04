package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Реализация интерфейса SimulateBattle, отвечающего за симуляцию боя между армией игрока и компьютера.
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
        // Отбираем только живых юнитов из обеих армий
        List<Unit> playerUnits = playerArmy.getUnits().stream().filter(Unit::isAlive).toList();
        List<Unit> computerUnits = computerArmy.getUnits().stream().filter(Unit::isAlive).toList();

        // Проверяем, есть ли живые юниты в обеих армиях
        if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
            System.out.println("Бой невозможен, так как одна из армий не имеет юнитов.");
            return;
        }

        int round = 1;

        // Основной цикл боя
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            System.out.println("Раунд " + round);
            System.out.println("Живые юниты армии игрока: " + playerUnits.size());
            System.out.println("Живые юниты армии компьютера: " + computerUnits.size());

            // Сортируем юнитов по убыванию baseAttack
            playerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            computerUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // Объединяем юнитов в очередь по приоритету (сильнейшие ходят первыми)
            PriorityQueue<Unit> turnQueue = new PriorityQueue<>(Comparator.comparingInt(Unit::getBaseAttack).reversed());
            turnQueue.addAll(playerUnits);
            turnQueue.addAll(computerUnits);

            // Обрабатываем ходы юнитов
            while (!turnQueue.isEmpty()) {
                Unit currentUnit = turnQueue.poll();

                // Пропускаем мёртвых юнитов
                if (!currentUnit.isAlive()) {
                    continue;
                }

                Unit target;
                if (playerUnits.contains(currentUnit)) {
                    // Атакует юнит игрока
                    target = currentUnit.getProgram().attack();
                    printBattleLog.printBattleLog(currentUnit, target);
                    if (target != null && !target.isAlive()) {
                        computerUnits.remove(target);
                    }
                } else {
                    // Атакует юнит компьютера
                    target = currentUnit.getProgram().attack();
                    printBattleLog.printBattleLog(currentUnit, target);
                    if (target != null && !target.isAlive()) {
                        playerUnits.remove(target);
                    }
                }

                // Завершаем бой, если одна из армий полностью уничтожена
                if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
                    break;
                }
            }

            // Завершаем раунд
            System.out.println("Раунд " + round + " завершён.");
            round++;
        }

        // Определяем и выводим результат боя
        System.out.println("Бой окончен!");
        if (playerUnits.isEmpty() && computerUnits.isEmpty()) {
            System.out.println("Ничья!");
        } else if (playerUnits.isEmpty()) {
            System.out.println("Победила армия компьютера!");
        } else {
            System.out.println("Победила армия игрока!");
        }
    }
}