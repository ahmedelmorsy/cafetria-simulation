package simulation.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import simulation.queue.Customer;

public class Statistics {

    private static HashMap<String, ArrayList<QueueInfo>> queuesInfoMap;
    private static HashMap<String, ArrayList<Delay>> delaysInfoMap;
    private static HashMap<Customer, Delay> tempDelays;
    
    private static int timeAvgNumInQueue = -1;
    private static int maxNumInQueue = -1;
    private static int avgDelayInQueue = -1;
    private static int maxDelayInQueue = -1;

    static {
        queuesInfoMap = new HashMap<String, ArrayList<QueueInfo>>();
        ArrayList<QueueInfo> temp = new ArrayList<Statistics.QueueInfo>();
        QueueInfo tempInfo = new QueueInfo();
        tempInfo.from = 0;
        tempInfo.count = 0;
        temp.add(tempInfo);
        queuesInfoMap.put(Const.HOTFOOD_SERVER, temp);
        temp = new ArrayList<Statistics.QueueInfo>();
        tempInfo = new QueueInfo();
        tempInfo.from = 0;
        tempInfo.count = 0;
        temp.add(tempInfo);
        queuesInfoMap.put(Const.SANDWICH_SERVER, temp);
        // queuesInfoMap.put(Const.DRINKS_SERVER, new
        // ArrayList<Statistics.QueueInfo>());
        temp = new ArrayList<Statistics.QueueInfo>();
        tempInfo = new QueueInfo();
        tempInfo.from = 0;
        tempInfo.count = 0;
        temp.add(tempInfo);
        queuesInfoMap.put(Const.CASHIER_SERVER, temp);
        
        delaysInfoMap = new HashMap<String, ArrayList<Delay>>();
        ArrayList<Delay> list = new ArrayList<Statistics.Delay>();
        delaysInfoMap.put(Const.HOTFOOD_SERVER, list);
        list = new ArrayList<Statistics.Delay>();
        delaysInfoMap.put(Const.SANDWICH_SERVER, list);
        list = new ArrayList<Statistics.Delay>();
        delaysInfoMap.put(Const.CASHIER_SERVER, list);
        
        tempDelays = new HashMap<Customer, Statistics.Delay>();
    }

    public static void UpdateQueueLength(int len, String queueType) {
        ArrayList<QueueInfo> temp = queuesInfoMap.get(queueType);
        QueueInfo info = temp.get(temp.size() - 1);
        if (info.from == SimulationClk.clock) {
            info.count = len;
        } else {
            info.to = SimulationClk.clock;
            info = new QueueInfo();
            info.from = SimulationClk.clock;
            info.count = len;
            temp.add(info);
        }
    }

    public static int getTimeAvgNumInQueue(String qType) {
        if (timeAvgNumInQueue > 0)
            return timeAvgNumInQueue;
        ArrayList<QueueInfo> list = queuesInfoMap.get(qType);
        int max = 0;
        int timeAvg = 0;
        int maxTime = 0;
        Iterator<QueueInfo> it = list.iterator();
        while (it.hasNext()) {
            QueueInfo info = it.next();
            if (info.count > max)
                max = info.count;
            if (info.to > maxTime)
                maxTime = info.to;
            timeAvg += info.count * (info.to - info.from);
        }
        maxNumInQueue = max;
        timeAvgNumInQueue = timeAvg / maxTime;
        return timeAvgNumInQueue;
    }

    public static int getMaxNumInQueue(String qType) {
        if (maxNumInQueue > 0)
            return maxNumInQueue;
        ArrayList<QueueInfo> list = queuesInfoMap.get(qType);
        int max = 0;
        int timeAvg = 0;
        int maxTime = 0;
        Iterator<QueueInfo> it = list.iterator();
        while (it.hasNext()) {
            QueueInfo info = it.next();
            if (info.count > max)
                max = info.count;
            if (info.to > maxTime)
                maxTime = info.to;
            timeAvg += info.count * (info.to - info.from);
        }
        maxNumInQueue = max;
        timeAvgNumInQueue = timeAvg / maxTime;
        return maxNumInQueue;
    }

    ///////////////////// Delays ///////////////////////////////////
    public void CustomerEnteredQueue(Customer c, String qType) {
        Delay d = new Delay();
        d.from = SimulationClk.clock;
        d.c = c;
        tempDelays.put(c, d);
    }

    public void CustomerQuitQueue(Customer c, String qType) {
        Delay d = tempDelays.get(c);
        d.to = SimulationClk.clock;
        ArrayList<Delay> list = delaysInfoMap.get(qType);
        list.add(d);
    }
    
    public static int getAvgDelayInQueue(String qType) {
        if (avgDelayInQueue > 0)
            return avgDelayInQueue;
        ArrayList<Delay> list = delaysInfoMap.get(qType);
        int max = 0;
        int average = 0;
        Iterator<Delay> it = list.iterator();
        while (it.hasNext()) {
            Delay info = it.next();
            int delay = info.to - info.from;
            if (delay > max)
                max = delay;
            average += delay;
        }
        maxDelayInQueue = max;
        avgDelayInQueue = average / list.size();
        return avgDelayInQueue;
    }

    public static int getMaxDelayInQueue(String qType) {
        if (maxDelayInQueue > 0)
            return maxDelayInQueue;
        ArrayList<Delay> list = delaysInfoMap.get(qType);
        int max = 0;
        int average = 0;
        Iterator<Delay> it = list.iterator();
        while (it.hasNext()) {
            Delay info = it.next();
            int delay = info.to - info.from;
            if (delay > max)
                max = delay;
            average += delay;
        }
        maxDelayInQueue = max;
        avgDelayInQueue = average / list.size();
        return maxNumInQueue;
    }

    
    private static class Delay {
        public Customer c;
        public int from;
        public int to;
    }

    private static class QueueInfo {
        public int from;
        public int to;
        public int count;
    }
}
