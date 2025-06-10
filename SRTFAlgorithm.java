package osproject;

import java.util.*;

public class SRTFAlgorithm extends SchedulingAlgorithm {

    @Override
    public void run(List<Process> processes) {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Process previous = null;

        System.out.println("\n Starting Shortest Remaining Time First (SRTF) Scheduling...\n");

        while (completed < n) {
            Process shortest = null;
            List<Process> readyQueue = new ArrayList<>();

            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && p.getRemainingTime() > 0) {
                    readyQueue.add(p);
                    if (shortest == null || p.getRemainingTime() < shortest.getRemainingTime()) {
                        shortest = p;
                    }
                }
            }

            // Ready Queue Display
            System.out.print("Time " + currentTime + " - Ready Queue: ");
            if (readyQueue.isEmpty()) {
                System.out.println("[Empty]");
            } else {
                for (Process p : readyQueue) {
                    System.out.print("P" + p.getId() + "(BT=" + p.getRemainingTime() + ") ");
                }
                System.out.println();
            }

            // Preemption Display
            if (shortest != null && previous != null && shortest != previous) {
                System.out.println("⚠️ Preemption: P" + previous.getId() + " ➡️ P" + shortest.getId());
            }

            if (shortest == null) {
                currentTime++;
                continue;
            }

            shortest.setRemainingTime(shortest.getRemainingTime() - 1);
            currentTime++;

            if (shortest.getRemainingTime() == 0) {
                shortest.setCompletionTime(currentTime);
                shortest.setTurnaroundTime(currentTime - shortest.getArrivalTime());
                shortest.setWaitingTime(shortest.getTurnaroundTime() - shortest.getBurstTime());
                completed++;
            }

            previous = shortest;
        }

        // 🔄 Sort by process ID so result display is consistent and correct
        processes.sort(Comparator.comparingInt(Process::getId));

        System.out.println("\n✅ All processes have been scheduled.\n");
    }
}
