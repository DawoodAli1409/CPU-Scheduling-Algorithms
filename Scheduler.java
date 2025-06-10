package osproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Scheduler {

    private static List<Process> getProcessesFromUser(Scanner sc) {
        List<Process> processes = new ArrayList<>();
        int n = 0;

        // Input number of processes with simple validation
        while (true) {
            System.out.print("Enter number of processes: ");
            String input = sc.nextLine().trim();  // Read the whole line
            try {
                n = Integer.parseInt(input);  // Convert to integer
                if (n > 0) break;
                else System.out.println("❌ Number of processes must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid integer.");
            }
        }

        // Input arrival and burst times with basic checks
        for (int i = 0; i < n; i++) {
            int at = -1, bt = -1;
            System.out.println("Process " + (i + 1) + ":");

            // Arrival Time
            while (true) {
                System.out.print("  Arrival Time: ");
                String input = sc.nextLine().trim();
                try {
                    at = Integer.parseInt(input);
                    if (at >= 0) break;
                    else System.out.println("  ❌ Arrival Time must be 0 or greater.");
                } catch (NumberFormatException e) {
                    System.out.println("  ❌ Invalid input. Please enter a valid integer.");
                }
            }

            // Burst Time
            while (true) {
                System.out.print("  Burst Time: ");
                String input = sc.nextLine().trim();
                try {
                    bt = Integer.parseInt(input);
                    if (bt > 0) break;
                    else System.out.println("  ❌ Burst Time must be greater than 0.");
                } catch (NumberFormatException e) {
                    System.out.println("  ❌ Invalid input. Please enter a valid integer.");
                }
            }

            processes.add(new Process(i + 1, at, bt));
        }

        return processes;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== CPU Scheduling Menu =====");
            System.out.println("1. First Come First Serve (FCFS)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Shortest Remaining Time First (SRTF)");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            String choiceInput = sc.nextLine().trim();  // Read the whole line for choice
            int choice = -1;
            try {
                choice = Integer.parseInt(choiceInput);
                if (choice < 0 || choice > 3) {
                    System.out.println("❌ Invalid choice. Please select from 0 to 3.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
                continue;
            }

            if (choice == 0) {
                System.out.println("Exiting program. Goodbye!");
                break;
            }

            List<Process> processes = getProcessesFromUser(sc);
            SchedulingAlgorithm algorithm;

            switch (choice) {
                case 1 -> algorithm = new FCFSAlgorithm();
                case 2 -> algorithm = new SJFAlgorithm();
                case 3 -> algorithm = new SRTFAlgorithm();
                default -> {
                    System.out.println("❌ Unknown error occurred.");
                    continue;
                }
            }

            algorithm.run(processes);
            
            // Modified to only show the execution log and results without PID table
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

        sc.close();
    }
}