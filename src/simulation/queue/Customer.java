package simulation.queue;

import simulation.global.SimulationClk;

public class Customer {
    
    private static int idGen = 1;
    
    private int id;
    private int accumlatedTime;
    private int queueEnteranceTime;
    
    public Customer() {
        this.id = idGen++;
        this.accumlatedTime = 0;
    }
    
    public void accumlateTime(int time) {
        this.accumlatedTime += time;
    }
    
    public int getAccumlatedTime() {
        return this.accumlatedTime;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void enterQueue() {
        this.queueEnteranceTime = SimulationClk.clock;
    }
}
