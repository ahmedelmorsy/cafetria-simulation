package cafetria.queue;

import java.util.Random;

import simulation.global.Event;
import simulation.global.EventsQueue;
import simulation.global.SimulationClk;

public class UniformServer extends Server{

    private int serviceTimeMin;
    private int serviceTimeMax;
    private int finishTime;
    private Random randomGen;
    
    public UniformServer(int stMin, int stMax, int emp) {
        this.serviceTimeMin = stMin / emp;
        this.serviceTimeMax = stMax / emp;
        this.finishTime = -1;
        randomGen = new Random();
    }

    @Override
    public boolean isBusy() {
        if (SimulationClk.clock > finishTime) return true;
        return false;
    }

    @Override
    public void serve(final Customer customer) {
        int serviceTime = randomGen.nextInt(serviceTimeMax - serviceTimeMin) + serviceTimeMin;
        finishTime = SimulationClk.clock + serviceTime;
        EventsQueue.enqueue(finishTime, new Event() {
            
            @Override
            public void execute() {
                customer.afterService();
            }
        });
    }
}

