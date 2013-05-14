package simulation.queue;

import simulation.global.Event;

public class InfServersQueueSystem extends QueueSystem {

    public InfServersQueueSystem(String name, Server server, int queueLen) {
        super(name,server, queueLen);
    }

    @Override
    public boolean enqueue(Customer customer, Event afterService) {
        this.server.serve(customer, afterService);
        return true;
    }
}
