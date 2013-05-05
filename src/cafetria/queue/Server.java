package cafetria.queue;

/**
 * Queue System Server
 * @author Ahmed Elmorsy Khalifa
 *
 */
public abstract class Server {
    
    private QueueSystem qs;
    
    /**
     * check if server busy serving a customer
     * @return true if server is busy serving a customer and false otherwise.
     */
    public abstract boolean isBusy();

    /**
     * serve a customer; 
     * @param customer
     */
    public abstract void serve(Customer customer);

    /**
     * set Queue system to be able to pull customers from queue when idle
     * @param queueSystem the queue system in which server is working
     */
    protected void setQueueSystem(QueueSystem qs){
        this.qs = qs;
    }
    
    protected QueueSystem getQueueSystem(){
        return this.qs;
    }
    
    
}
