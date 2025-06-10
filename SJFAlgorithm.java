package osproject;

import java.util.*;

public class SJFAlgorithm extends SchedulingAlgorithm {

    @Override
    public void run(List<Process> processes) {
        List<Process> completed = new ArrayList<>();
        List<Process> waiting = new ArrayList<>(processes);
        int currentTime = 0;

        System.out.println("\n Starting Shortest Job First (SJF) Scheduling...\n");

        while (!waiting.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : waiting) {
                if (p.getArrivalTime() <= currentTime) {
                    available.add(p);
                }
            }

            // Show Ready Queue
            System.out.print(" Time " + currentTime + " - Ready Queue: ");
            if (available.isEmpty()) {
                System.out.println("[Empty]");
                currentTime++;
                continue;
            } else {
                for (Process p : available) {
                    System.out.print("P" + p.getId() + "(BT=" + p.getBurstTime() + ") ");
                }
                System.out.println();
            }

            // Use same logic as before — pick shortest from available
            Process shortest = available.stream()
                    .min(Comparator.comparingInt(Process::getBurstTime))
                    .get();

            // Display execution info (doesn't affect logic)
            System.out.println(" Executing P" + shortest.getId() +
                    " from Time " + currentTime + " to " + (currentTime + shortest.getBurstTime()));

            // Your original logic (unchanged)
            currentTime += shortest.getBurstTime();
            shortest.setCompletionTime(currentTime);
            shortest.setTurnaroundTime(currentTime - shortest.getArrivalTime());
            shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());

            completed.add(shortest);
            waiting.remove(shortest);
        }

        processes.clear();
        processes.addAll(completed);

        System.out.println("\n All processes have been scheduled.\n");
    }
}
