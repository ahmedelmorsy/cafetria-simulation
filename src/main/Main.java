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
    private static final int MAX_TIME = 100;
    private static Random groupsRand = new Random();
    private static Random routeRand = new Random();

    public static void main(String[] args) {
        // set simulation clock to 0
        SimulationClk.clock = 0;

        ExponentialRandom arrivalRand = new ExponentialRandom(30);

        UniformServer hotfoodServ = new UniformServer(Const.HOTFOOD_SERVER, 50, 120, 20, 40, 1);
        UniformServer sandwichesServ = new UniformServer(Const.SANDWICH_SERVER, 60, 180, 5, 15, 1);
        UniformServer drinksServ = new UniformServer(Const.DRINKS_SERVER, 5, 20, 5, 10, 1);
        CustomServer cashier1 = new CustomServer(Const.CASHIER_SERVER);
        CustomServer cashier2 = new CustomServer(Const.CASHIER_SERVER);

        final QueueSystem hotfoodSys = new QueueSystem(Const.HOTFOOD_SERVER, hotfoodServ, -1);
        final QueueSystem sandwichesSys = new QueueSystem(Const.SANDWICH_SERVER,sandwichesServ, -1);
        final QueueSystem drinkSys = new InfServersQueueSystem(Const.DRINKS_SERVER,drinksServ, -1);
        final QueueSystem cashierSys1 = new QueueSystem(Const.CASHIER_SERVER, cashier1, -1);
        final QueueSystem cashierSys2 = new QueueSystem(Const.CASHIER_SERVER, cashier2, -1);

        int lastArrival = 0;
        
        Statistics.console.log("Simulation Starts ....");
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
            Statistics.console.log("[" + SimulationClk.clock + "] arrival of " + group + " Customers");
            Statistics.file.log("[" + SimulationClk.clock + "][Arrival]" + group);
            for (int i = 0; i < group; i++) {
                final Customer cust = new Customer();
                int route = getRoute();
                cust.setType(route);
                switch (route) {
                case Const.CUST_HOTFOOD:
                    Statistics.console.log("Customer " + cust.getId() + " going to hot food");
                    Statistics.UpdateQueueLength(hotfoodSys.getQueueLength(), Const.HOTFOOD_SERVER);
//                    Statistics.CustomerEnteredQueue(cust, Const.HOTFOOD_SERVER);
                    hotfoodSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            Statistics.console.log("Customer " + cust.getId() + " finished hot food and going to drinks");
                            Statistics.UpdateQueueLength(hotfoodSys.getQueueLength(), Const.HOTFOOD_SERVER);
//                            Statistics.CustomerQuitQueue(cust, Const.HOTFOOD_SERVER);
//                            Statistics.CustomerEnteredQueue(cust, Const.DRINKS_SERVER);
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }

                        @Override
                        public String getDescription() {
                            return "hot food service event";
                        }
                    });
                    break;
                case Const.CUST_SANDWICHES:
                    Statistics.console.log("Customer " + cust.getId() + " going to sandwiches");
                    Statistics.UpdateQueueLength(sandwichesSys.getQueueLength(), Const.SANDWICH_SERVER);
//                    Statistics.CustomerEnteredQueue(cust, Const.SANDWICH_SERVER);
                    sandwichesSys.enqueue(cust, new Event() {

                        @Override
                        public void execute() {
                            Statistics.console.log("Customer " + cust.getId() + " finished sandwiches and going to drinks");
                            Statistics.UpdateQueueLength(sandwichesSys.getQueueLength(), Const.SANDWICH_SERVER);
//                            Statistics.CustomerQuitQueue(cust, Const.SANDWICH_SERVER);
//                            Statistics.CustomerEnteredQueue(cust, Const.DRINKS_SERVER);
                            drinkSys.enqueue(cust, new JoiningCashierEvent(cashierSys1, cashierSys2, cust));
                        }
                        
                        @Override
                        public String getDescription() {
                            return "sandwiches service event";
                        }
                    });
                    break;
                case Const.CUST_DRINKS:
                    Statistics.console.log("Customer " + cust.getId() + " going to drinks only");
//                    Statistics.CustomerEnteredQueue(cust, Const.DRINKS_SERVER);
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
        Statistics.console.close();
        Statistics.file.close();
//        Statistics.drawQueueLen();
        System.out.println("Simulation Finished ....");
        System.out.println("Average Delay in Hot Food Server= " + Statistics.getAvgDelayInQueue(Const.HOTFOOD_SERVER));
        System.out.println("Average Delay in Sandwiches Server= " + Statistics.getAvgDelayInQueue(Const.SANDWICH_SERVER));
        System.out.println("Average Delay in Drinks Server= " + Statistics.getAvgDelayInQueue(Const.DRINKS_SERVER));
        System.out.println("Average Delay in Cashiers= " + Statistics.getAvgDelayInQueue(Const.CASHIER_SERVER));
        System.out.println("-------------------------------");
        System.out.println("Time average number in queue in Hot Food Server= " + Statistics.getTimeAvgNumInQueue(Const.HOTFOOD_SERVER));
        System.out.println("Time average number in queue in Sandwiches Server= " + Statistics.getTimeAvgNumInQueue(Const.SANDWICH_SERVER));
        System.out.println("Time average number in queue in Cashiers= " + Statistics.getTimeAvgNumInQueue(Const.CASHIER_SERVER));
    }

    private static int getRoute() {
        double routeProb = routeRand.nextDouble();
        if (routeProb <= 0.8) {
            return Const.CUST_HOTFOOD;
        } else if (routeProb <= 0.95) {
            return Const.CUST_SANDWICHES;
        } else {
            return Const.CUST_DRINKS;
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
            Statistics.console.log("Customer " + cust.getId() + " want to go to cashier and cashiers have the following: " 
                    + cashierSys1.getQueueLength() + ", " + cashierSys2.getQueueLength());
//            Statistics.CustomerQuitQueue(cust, Const.DRINKS_SERVER);
//            Statistics.CustomerEnteredQueue(cust, Const.CASHIER_SERVER);
            if (cashierSys1.getQueueLength() <= cashierSys2.getQueueLength()) {
                Statistics.console.log("Customer " + cust.getId() + " decided to go to cashier 1");
                Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                cashierSys1.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                        Statistics.console.log("Customer " + cust.getId() + " finished and leaving ...");
                        Statistics.file.log("[" + SimulationClk.clock + "][Finish]" + cust.getId());
                        Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
//                        Statistics.CustomerQuitQueue(cust, Const.CASHIER_SERVER);
                    }

                    @Override
                    public String getDescription() {
                        return "Cashier 1 event";
                    }
                });
            } else {
                Statistics.console.log("Customer " + cust.getId() + " decided to go to cashier 2");
                Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
                cashierSys2.enqueue(cust, new Event() {

                    @Override
                    public void execute() {
                        // Do nothing
                        Statistics.console.log("Customer " + cust.getId() + " finished and leaving ...");
                        Statistics.file.log("[" + SimulationClk.clock + "][Finish]" + cust.getId());
                        Statistics.UpdateQueueLength(cashierSys1.getQueueLength() + cashierSys2.getQueueLength(), Const.CASHIER_SERVER);
//                        Statistics.CustomerQuitQueue(cust, Const.CASHIER_SERVER);
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
