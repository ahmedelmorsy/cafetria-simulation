package simulation.queue;

import simulation.global.Event;

public class InfServersQueueSystem extends QueueSystem {

    public InfServersQueueSystem(Server server, int queueLen) {
        super(server, queueLen);
    }

    @Override
    public boolean enqueue(Customer customer, Event afterService) {
        this.server.serve(customer, afterService);
        return true;
    }
}
