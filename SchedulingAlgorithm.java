package osproject;

import java.util.List;

public abstract class SchedulingAlgorithm {
    
    public abstract void run(List<Process> processes);
    
    public void printResults(List<Process> processes) {
        System.out.println("\n📊 Scheduling Results:");
        System.out.println("PID\tArrival\tBurst\tCompletion\tTurnaround\tWaiting");
        
        double totalTurnaround = 0;
        double totalWaiting = 0;
        
        for (Process p : processes) {
            System.out.printf("P%d\t%d\t%d\t%d\t\t%d\t\t%d\n",
                    p.getId(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getCompletionTime(),
                    p.getTurnaroundTime(),
                    p.getWaitingTime());
            
            totalTurnaround += p.getTurnaroundTime();
            totalWaiting += p.getWaitingTime();
        }
        
        double avgTurnaround = totalTurnaround / processes.size();
        double avgWaiting = totalWaiting / processes.size();
        
        System.out.printf("\n📈 Averages:\n");
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaround);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaiting);
    }
}