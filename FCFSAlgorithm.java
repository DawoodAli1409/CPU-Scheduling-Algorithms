package osproject;

import java.util.*;

public class FCFSAlgorithm extends SchedulingAlgorithm {

    @Override
    public void run(List<Process> processes) {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));

        int currentTime = 0;
        System.out.println("\n Starting First Come First Serve (FCFS) Scheduling...\n");

        for (Process p : processes) {
            // Build ready queue
            List<Process> readyQueue = new ArrayList<>();
            for (Process q : processes) {
                if (q.getArrivalTime() <= currentTime && q.getCompletionTime() == 0) {
                    readyQueue.add(q);
                }
            }

            // Show Ready Queue
            System.out.print(" Time " + currentTime + " - Ready Queue: ");
            if (readyQueue.isEmpty()) System.out.println("[Empty]");
            else {
                for (Process r : readyQueue) {
                    System.out.print("P" + r.getId() + "(BT=" + r.getBurstTime() + ") ");
                }
                System.out.println();
            }

            if (currentTime < p.getArrivalTime()) {
                currentTime = p.getArrivalTime();
            }

            System.out.println(" Executing P" + p.getId() + " from Time " + currentTime + " to " + (currentTime + p.getBurstTime()));

            currentTime += p.getBurstTime();
            p.setCompletionTime(currentTime);
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        System.out.println("\n All processes have been scheduled.\n");
    }
}
