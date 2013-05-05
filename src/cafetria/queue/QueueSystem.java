package cafetria.queue;

import java.util.LinkedList;


/**
 * Represent a queue system with queue and server to serve customers.
 * @author Ahmed Elmorsy Khalifa
 *
 */
public class QueueSystem {
    
    /**
     * Queue where customers wait for service.
     */
    private LinkedList<Customer> queue;
    /**
     * Server that serve customers requests.
     */
    private Server server;
    /**
     * maximum Queue length
     */
    private int maxQLen;
    
    /**
     * Construct new queue system with the server given
     * @param server the server of the new queue system
     * @param queueLen maximum queue length, -1 for unlimited queues
     */
    public QueueSystem(Server server, int queueLen) {
        this.server = server;
        server.setQueueSystem(this);
    }
    
    /**
     * Enqueue a new customer in system.
     * @param customer customer entering system
     * @return true if it is enqueued successfully, false If queue is empty in limited
     * queue length systems.
     */
    public boolean enqueue(Customer customer, int time) {
        if (queue.size() == maxQLen) return false;
        if (server.isBusy()) queue.addLast(customer);
        server.serve(customer);
        return true;
    }
    
    protected Customer dequeue() {
        if (queue.size() > 0) return queue.getFirst();
        return null;
    }
}
