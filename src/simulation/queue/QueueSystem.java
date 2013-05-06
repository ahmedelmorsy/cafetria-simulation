package simulation.queue;

import java.util.LinkedList;

import simulation.global.Event;


/**
 * Represent a queue system with queue and server to serve customers.
 * @author Ahmed Elmorsy Khalifa
 *
 */
public class QueueSystem {
    
    /**
     * Queue where customers wait for service.
     */
    private LinkedList<QueueEntry> queue;
    /**
     * Server that serve customers requests.
     */
    protected Server server;
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
    public boolean enqueue(Customer customer, Event afterService) {
        if (queue.size() == maxQLen) return false;
        if (server.isBusy()) {
            QueueEntry entry = new QueueEntry();
            entry.customer = customer;
            entry.afterService = afterService;
            queue.addLast(entry);
        }
        server.serve(customer, afterService);
        return true;
    }
    
    protected QueueEntry dequeue() {
        if (queue.size() > 0) return queue.getFirst();
        return null;
    }
    
    protected class QueueEntry {
        public Customer customer;
        public Event afterService;
    }
}
