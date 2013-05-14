package main;

import java.util.Random;

import simulation.global.Const;
import simulation.global.Event;
import simulation.global.EventsQueue;
import simulation.global.SimulationClk;
import simulation.global.Statistics;
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

        UniformServer hotfoodServ = new UniformServer("HotFood Server", 50, 120, 20, 40, 1);
        UniformServer sandwichesServ = new UniformServer("Sandwich Server", 60, 180, 5, 15, 1);
        UniformServer drinksServ = new UniformServer("Drink Server", 5, 20, 5, 10, 1);
        CustomServer cashier1 = new CustomServer("Cashier1 Server");
        CustomServer cashier2 = new CustomServer("Cashier2 Server");

        final QueueSystem hotfoodSys = new QueueSystem("HotFood Server", hotfoodServ, -1);
        final QueueSystem sandwichesSys = new QueueSystem("Sandwiches Server",sandwichesServ, -1);
        final QueueSystem drinkSys = new InfServersQueueSystem("Drinks Server",drinksServ, -1);
        final QueueSystem cashierSys1 = new QueueSystem("Cashier 1", cashier1, -1);
        final QueueSystem cashierSys2 = new QueueSystem("Cashier 2", cashier2, -1);

        int lastArrival = 0;
        System.out.println("Simulation Starts ....");
        while (true) {
            int nextArrival = arrivalRand.nextInt();
            while (EventsQueue.peekTime() < lastArrival + nextArrival) {
                EventsQueue.executeEvent();
            }
            SimulationClk.clock += nextArrival;
            if (SimulationClk.clock > MAX_TIME)
                break;
            lastArrival = SimulationClk.clock;
            int group = getCustomerNum();
            System.out.println("[" + SimulationClk.clock + "] arrival of " + group + " Customers");
            for (int i = 0; i < group; i++) {
                final Customer cust = new Customer();
                int route = getRoute();
                switch (route) {
                case 1:
                    System.out.println("Customer " + cust.getId() + " going to hot food");
                    Statistics.UpdateQueueLength(hotfoodSys.getQueueLength(), Const.HOTFOOD_SERVER);
                    hotfoodSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            System.out.println("Customer " + cust.getId() + " finished hot food and going to drinks");
                            Statistics.UpdateQueueLength(hotfoodSys.getQueueLength(), Const.HOTFOOD_SERVER);
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }

                        @Override
                        public String getDescription() {
                            return "hot food service event";
                        }
                    });
                    break;
                case 2:
                    System.out.println("Customer " + cust.getId() + " going to sandwiches");
                    Statistics.UpdateQueueLength(sandwichesSys.getQueueLength(), Const.SANDWICH_SERVER);
                    sandwichesSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            System.out.println("Customer " + cust.getId() + " finished sandwiches and going to drinks");
                            Statistics.UpdateQueueLength(sandwichesSys.getQueueLength(), Const.SANDWICH_SERVER);
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }
                        
                        @Override
                        public String getDescription() {
                            return "sandwiches service event";
                        }
                    });
                    break;
                case 3:
                    System.out.println("Customer " + cust.getId() + " going to drinks only");
                    drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                    break;
                default:
                    break;
                }
            }
        }
        while (EventsQueue.getSize() > 0) {
            EventsQueue.executeEvent();
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
            System.out.println("Customer " + cust.getId() + " want to go to cashier and cashiers have the following: " 
                    + cashierSys1.getQueueLength() + ", " + cashierSys2.getQueueLength());
            if (cashierSys1.getQueueLength() <= cashierSys2.getQueueLength()) {
                System.out.println("Customer " + cust.getId() + " decided to go to cashier 1");
                Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                cashierSys1.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                        System.out.println("Customer " + cust.getId() + " finished and leaving ...");
                        Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                    }

                    @Override
                    public String getDescription() {
                        return "Cashier 1 event";
                    }
                });
            } else {
                System.out.println("Customer " + cust.getId() + " decided to go to cashier 2");
                Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                cashierSys2.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                        System.out.println("Customer " + cust.getId() + " finished and leaving ...");
                        Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                    }

                    @Override
                    public String getDescription() {
                        return "Cashier 2 event";
                    }
                });
            }
        }

        @Override
        public String getDescription() {
            return "Joining Cashier event";
        }

    }
}
