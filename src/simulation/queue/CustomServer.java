package simulation.queue;

import simulation.global.Event;
import simulation.global.EventsQueue;
import simulation.global.SimulationClk;
import simulation.queue.QueueSystem.QueueEntry;

public class CustomServer extends Server {

    private int finishTime;
    
    @Override
    public boolean isBusy() {
        if (SimulationClk.clock > finishTime) return true;
        return false;
    }

    @Override
    public void serve(Customer customer, Event e) {
        int serviceTime = customer.getAccumlatedTime();
        finishTime = SimulationClk.clock + serviceTime;
        EventsQueue.enqueue(finishTime, new Event() {
            
            @Override
            public void execute() {
                QueueEntry next;
                if ((next = getQueueSystem().dequeue()) != null){
                    serve(next.customer, next.afterService);
                } else {
                    finishTime = -1;
                }
            }
        });
    }
}
