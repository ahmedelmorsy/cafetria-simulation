package main;

import simulation.global.SimulationClk;
import simulation.queue.CustomServer;
import simulation.queue.InfServersQueueSystem;
import simulation.queue.QueueSystem;
import simulation.queue.UniformServer;

public class Main {

    //90-minute = 6300 sec
    private static final int MAX_TIME = 6300;
    
    public static void main(String[] args) {
        //set simulation clock to 0
        SimulationClk.clock = 0;
        
        UniformServer hotfoodServ = new UniformServer(50, 120, 20, 40, 1);
        UniformServer sandwichesServ = new UniformServer(60, 180, 5, 15, 1);
        UniformServer drinksServ = new UniformServer(5, 20, 5, 10, 1);
        CustomServer cashier1 = new CustomServer();
        CustomServer cashier2 = new CustomServer();
        
        QueueSystem hotfoodSys = new QueueSystem(hotfoodServ, -1);
        QueueSystem sandwichesSys = new QueueSystem(sandwichesServ, -1);
        QueueSystem drinkSys = new InfServersQueueSystem(drinksServ, -1);
        QueueSystem cashierSys1 = new QueueSystem(cashier1, -1);
        QueueSystem cashierSys2 = new QueueSystem(cashier2, -1);
        
        
        
        
        while (SimulationClk.clock < MAX_TIME) {
            
        }
    }
}
