package simulation.global;

import java.util.PriorityQueue;

public class EventsQueue {

    private static PriorityQueue<Node> queue = new PriorityQueue<Node>();

    public static void enqueue(int time, Event event) {
        Node n = new Node();
        n.time = time;
        n.e = event;
        queue.add(n);
    }
    
    private static class Node {
        public int time;
        public Event e;
    }
}
