package main;

import java.util.Random;

import simulation.global.Event;
import simulation.global.SimulationClk;
import simulation.queue.CustomServer;
import simulation.queue.Customer;
import simulation.queue.InfServersQueueSystem;
import simulation.queue.QueueSystem;
import simulation.queue.UniformServer;
import simulation.random.ExponentialRandom;

public class Main {

    // 90-minute = 6300 sec
    private static final int MAX_TIME = 6300;
    private static Random groupsRand = new Random();
    private static Random routeRand = new Random();

    public static void main(String[] args) {
        // set simulation clock to 0
        SimulationClk.clock = 0;

        ExponentialRandom arrivalRand = new ExponentialRandom(30);

        UniformServer hotfoodServ = new UniformServer(50, 120, 20, 40, 1);
        UniformServer sandwichesServ = new UniformServer(60, 180, 5, 15, 1);
        UniformServer drinksServ = new UniformServer(5, 20, 5, 10, 1);
        CustomServer cashier1 = new CustomServer();
        CustomServer cashier2 = new CustomServer();

        QueueSystem hotfoodSys = new QueueSystem(hotfoodServ, -1);
        QueueSystem sandwichesSys = new QueueSystem(sandwichesServ, -1);
        final QueueSystem drinkSys = new InfServersQueueSystem(drinksServ, -1);
        final QueueSystem cashierSys1 = new QueueSystem(cashier1, -1);
        final QueueSystem cashierSys2 = new QueueSystem(cashier2, -1);

        while (SimulationClk.clock < MAX_TIME) {
            int nextArrival = arrivalRand.nextInt();
            SimulationClk.clock += nextArrival;
            int group = getCustomerNum();
            for (int i = 0; i < group; i++) {
                final Customer cust = new Customer();
                int route = getRoute();
                switch (route) {
                case 1:
                    hotfoodSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }
                    });
                    break;
                case 2:
                    sandwichesSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }
                    });
                    break;
                case 3:
                    drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                    break;
                default:
                    break;
                }
            }
        }
    }

    private static int getRoute() {
        double routeProb = routeRand.nextDouble();
        if (routeProb <= 0.8) {
            return 1;
        } else if (routeProb <= 0.95) {
            return 2;
        } else {
            return 3;
        }
    }

    private static int getCustomerNum() {
        double groupsProb = groupsRand.nextDouble();
        if (groupsProb <= 0.5) {
            return 1;
        } else if (groupsProb <= 0.8) {
            return 2;
        } else if (groupsProb <= 0.9) {
            return 3;
        } else {
            return 4;
        }
    }

    private static class JoiningCashierEvent implements Event {

        private QueueSystem cashierSys1;
        private QueueSystem cashierSys2;
        private Customer cust;

        public JoiningCashierEvent(QueueSystem cashierSys1,
                QueueSystem cashierSys2, Customer cust) {
            this.cashierSys1 = cashierSys1;
            this.cashierSys2 = cashierSys2;
            this.cust = cust;
        }

        @Override
        public void execute() {
            if (cashierSys1.getQueueLength() <= cashierSys2.getQueueLength()) {
                cashierSys1.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                    }
                });
            } else {
                cashierSys2.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                    }
                });
            }

        }

    }
}
