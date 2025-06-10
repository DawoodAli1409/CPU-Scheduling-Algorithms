package osproject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class CPUSchedulingGUI extends JFrame {
    private JPanel chartPanel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JLabel avgTATLabel;
    private JLabel avgWTLabel;
    private JComboBox<String> algorithmComboBox;
    private JTextField arrivalField;
    private JTextField burstField;
    private JTextField processNameField;

    public CPUSchedulingGUI() {
        setTitle("CPU Scheduling Algorithms");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        processNameField = new JTextField();
        arrivalField = new JTextField();
        burstField = new JTextField();

        // Algorithm selection dropdown
        String[] algorithms = {"FCFS", "SJF", "SRTF"};
        algorithmComboBox = new JComboBox<>(algorithms);

        JButton addButton = new JButton("Add Process");
        JButton removeButton = new JButton("Remove Selected");
        JButton computeButton = new JButton("Compute Schedule");
        JButton clearButton = new JButton("Clear All");

        inputPanel.add(new JLabel("Process ID:"));
        inputPanel.add(processNameField);
        inputPanel.add(new JLabel("Arrival Time:"));
        inputPanel.add(arrivalField);
        inputPanel.add(new JLabel("Burst Time:"));
        inputPanel.add(burstField);
        inputPanel.add(new JLabel("Algorithm:"));
        inputPanel.add(algorithmComboBox);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(computeButton);
        inputPanel.add(clearButton);

        add(inputPanel, BorderLayout.NORTH);

        // Gantt chart panel
        chartPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGanttChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 200));
        chartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        add(chartPanel, BorderLayout.CENTER);

        // Table for process details
        String[] columnNames = {"Process", "Arrival Time", "Burst Time", "Completion Time", "Turnaround Time", "Waiting Time"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        processTable = new JTable(tableModel);
        processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(processTable);
        scrollPane.setPreferredSize(new Dimension(880, 150));

        // Average labels with enhanced appearance
        avgTATLabel = new JLabel("Average Turnaround Time: ");
        avgTATLabel.setFont(new Font("Arial", Font.BOLD, 14));
        avgTATLabel.setForeground(new Color(0, 100, 0)); // Dark green
        
        avgWTLabel = new JLabel("Average Waiting Time: ");
        avgWTLabel.setFont(new Font("Arial", Font.BOLD, 14));
        avgWTLabel.setForeground(new Color(0, 100, 0)); // Dark green
        
        JPanel avgPanel = new JPanel(new GridLayout(1, 2));
        avgPanel.add(avgTATLabel);
        avgPanel.add(avgWTLabel);

        // Bottom panel to hold table and averages
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(scrollPane, BorderLayout.CENTER);
        bottomPanel.add(avgPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action listeners
        addButton.addActionListener(e -> addProcess());
        removeButton.addActionListener(e -> removeSelectedProcess());
        computeButton.addActionListener(e -> computeSchedule());
        clearButton.addActionListener(e -> clearAll());
    }

    private java.util.List<ExecutionSegment> executionSegments = new ArrayList<>();
    private java.util.List<Process> processStats = new ArrayList<>();
    private Map<String, String> processDisplayNames = new HashMap<>();

    private void addProcess() {
        try {
            String processId = processNameField.getText().trim();
            int arrival = Integer.parseInt(arrivalField.getText().trim());
            int burst = Integer.parseInt(burstField.getText().trim());

            if (processId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a process ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (burst <= 0) {
                JOptionPane.showMessageDialog(this, "Burst time must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.addRow(new Object[]{processId, arrival, burst, "", "", ""});
            
            // Clear input fields
            processNameField.setText("");
            arrivalField.setText("");
            burstField.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for arrival and burst times", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedProcess() {
        int selectedRow = processTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a process to remove", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        executionSegments.clear();
        processStats.clear();
        processDisplayNames.clear();
        avgTATLabel.setText("Average Turnaround Time: ");
        avgWTLabel.setText("Average Waiting Time: ");
        chartPanel.repaint();
    }

    private void computeSchedule() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No processes to schedule", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String algorithm = (String) algorithmComboBox.getSelectedItem();
        
        // Get process data from table
        int n = tableModel.getRowCount();
        int[] arrivals = new int[n];
        int[] bursts = new int[n];
        String[] processIds = new String[n];
        
        // Generate display names (P1, P2, etc.) for the Gantt chart
        processDisplayNames.clear();
        for (int i = 0; i < n; i++) {
            processIds[i] = (String) tableModel.getValueAt(i, 0);
            processDisplayNames.put(processIds[i], "P" + (i + 1));
            arrivals[i] = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            bursts[i] = Integer.parseInt(tableModel.getValueAt(i, 2).toString());
        }

        executionSegments.clear();
        processStats.clear();

        switch (algorithm) {
            case "SRTF":
                runSRTF(processIds, arrivals, bursts);
                break;
            case "FCFS":
                runFCFS(processIds, arrivals, bursts);
                break;
            case "SJF":
                runSJF(processIds, arrivals, bursts);
                break;
        }

        updateProcessTable(processIds);
        chartPanel.repaint();
    }

    private void runFCFS(String[] processIds, int[] arrivals, int[] bursts) {
        int n = processIds.length;
        
        // Sort processes by arrival time (FCFS)
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        
        Arrays.sort(indices, Comparator.comparingInt(i -> arrivals[i]));
        
        int[] completionTime = new int[n];
        int currentTime = 0;
        
        for (int i = 0; i < n; i++) {
            int idx = indices[i];
            if (arrivals[idx] > currentTime) {
                currentTime = arrivals[idx];
            }
            
            executionSegments.add(new ExecutionSegment(processIds[idx], currentTime, currentTime + bursts[idx]));
            completionTime[idx] = currentTime + bursts[idx];
            currentTime = completionTime[idx];
        }
        
        // Calculate TAT and WT
        for (int i = 0; i < n; i++) {
            int tat = completionTime[i] - arrivals[i];
            int wt = tat - bursts[i];
            processStats.add(new Process(processIds[i], arrivals[i], bursts[i], completionTime[i], tat, wt));
        }
    }

    private void runSJF(String[] processIds, int[] arrivals, int[] bursts) {
        int n = processIds.length;
        int[] completionTime = new int[n];
        boolean[] completed = new boolean[n];
        int currentTime = 0;
        int completedCount = 0;
        
        while (completedCount < n) {
            int shortestIndex = -1;
            int shortestBurst = Integer.MAX_VALUE;
            
            // Find the process with shortest burst time that has arrived and not completed
            for (int i = 0; i < n; i++) {
                if (!completed[i] && arrivals[i] <= currentTime && bursts[i] < shortestBurst) {
                    shortestBurst = bursts[i];
                    shortestIndex = i;
                }
            }
            
            if (shortestIndex == -1) {
                currentTime++;
                continue;
            }
            
            executionSegments.add(new ExecutionSegment(processIds[shortestIndex], currentTime, currentTime + bursts[shortestIndex]));
            completionTime[shortestIndex] = currentTime + bursts[shortestIndex];
            currentTime = completionTime[shortestIndex];
            completed[shortestIndex] = true;
            completedCount++;
        }
        
        // Calculate TAT and WT
        for (int i = 0; i < n; i++) {
            int tat = completionTime[i] - arrivals[i];
            int wt = tat - bursts[i];
            processStats.add(new Process(processIds[i], arrivals[i], bursts[i], completionTime[i], tat, wt));
        }
    }

    private void runSRTF(String[] processIds, int[] arrivals, int[] bursts) {
        int n = processIds.length;
        int[] remainingTime = bursts.clone();
        int[] completionTime = new int[n];
        boolean[] isComplete = new boolean[n];

        int complete = 0, t = 0, minIndex = -1;
        int prevIndex = -1; // To track context switches

        while (complete != n) {
            minIndex = -1;
            int minRemaining = Integer.MAX_VALUE;

            // Find process with shortest remaining time
            for (int i = 0; i < n; i++) {
                if (arrivals[i] <= t && !isComplete[i] && remainingTime[i] < minRemaining && remainingTime[i] > 0) {
                    minRemaining = remainingTime[i];
                    minIndex = i;
                }
            }

            if (minIndex == -1) {
                t++;
                continue;
            }

            // Check if this is a continuation of the same process
            if (prevIndex != minIndex && prevIndex != -1 && !isComplete[prevIndex]) {
                // End the previous segment
                executionSegments.get(executionSegments.size() - 1).end = t;
            }

            if (prevIndex != minIndex) {
                // Start new segment
                executionSegments.add(new ExecutionSegment(processIds[minIndex], t, t + 1));
            } else {
                // Extend current segment
                executionSegments.get(executionSegments.size() - 1).end = t + 1;
            }

            remainingTime[minIndex]--;
            prevIndex = minIndex;
            t++;

            if (remainingTime[minIndex] == 0) {
                isComplete[minIndex] = true;
                completionTime[minIndex] = t;
                complete++;
            }
        }

        // Calculate TAT and WT
        for (int i = 0; i < n; i++) {
            int tat = completionTime[i] - arrivals[i];
            int wt = tat - bursts[i];
            processStats.add(new Process(processIds[i], arrivals[i], bursts[i], completionTime[i], tat, wt));
        }
    }

    private void updateProcessTable(String[] processIds) {
        double totalTAT = 0, totalWT = 0;
        int n = processStats.size();

        // Update the table with results
        for (int i = 0; i < n; i++) {
            Process p = processStats.get(i);
            for (int j = 0; j < tableModel.getRowCount(); j++) {
                if (tableModel.getValueAt(j, 0).equals(p.pid)) {
                    tableModel.setValueAt(p.completion, j, 3);
                    tableModel.setValueAt(p.tat, j, 4);
                    tableModel.setValueAt(p.wt, j, 5);
                    break;
                }
            }
            totalTAT += p.tat;
            totalWT += p.wt;
        }

        avgTATLabel.setText("Average Turnaround Time: " + String.format("%.2f", totalTAT / n));
        avgWTLabel.setText("Average Waiting Time: " + String.format("%.2f", totalWT / n));
    }

    private void drawGanttChart(Graphics g) {
        if (executionSegments.isEmpty()) {
            g.drawString("No chart to display. Compute a schedule first.", 50, 50);
            return;
        }

        int x = 50;
        int y = 50;
        int height = 50;
        int scale = 30; // pixels per time unit

        // Draw the chart
        for (ExecutionSegment segment : executionSegments) {
            int width = (segment.end - segment.start) * scale;
            
            // Set color to sky blue
            g.setColor(new Color(135, 206, 235)); // Sky blue color
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
            
            // Center the process display name (P1, P2, etc.) in the box
            String displayName = processDisplayNames.get(segment.pid);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(displayName);
            g.drawString(displayName, x + (width - textWidth)/2, y + height/2 + fm.getAscent()/2 - 2);
            
            // Draw time markers
            g.drawString(Integer.toString(segment.start), x - 5, y + height + 15);
            x += width;
        }
        
        // Draw the final time marker
        g.drawString(Integer.toString(executionSegments.get(executionSegments.size()-1).end), 
                   x - 5, y + height + 15);
    }

    // Execution segment for Gantt chart
    class ExecutionSegment {
        String pid;
        int start, end;

        ExecutionSegment(String pid, int start, int end) {
            this.pid = pid;
            this.start = start;
            this.end = end;
        }
    }

    // Process stat holder
    class Process {
        String pid;
        int arrival, burst, completion, tat, wt;

        Process(String pid, int arrival, int burst, int completion, int tat, int wt) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
            this.completion = completion;
            this.tat = tat;
            this.wt = wt;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CPUSchedulingGUI gui = new CPUSchedulingGUI();
            gui.setVisible(true);
        });
    }
}