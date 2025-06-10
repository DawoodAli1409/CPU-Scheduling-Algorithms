package osproject;

public class Process implements Comparable<Process>{
    private int id;
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int completionTime;
    private int waitingTime;
    private int turnaroundTime;

    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }

    // Getters and setters
    public int getId() { 
    	return id; }
    public int getArrivalTime() { 
    	return arrivalTime; 
    	}
    public int getBurstTime() { 
    	return burstTime; 
    	}
    public int getRemainingTime() { 
    	return remainingTime; 
    	}
    public void setRemainingTime(int remainingTime) { 
    	this.remainingTime = remainingTime; 
    	}
    public int getCompletionTime() { 
    	return completionTime; 
    	}
    public void setCompletionTime(int completionTime) { 
    	this.completionTime = completionTime; 
    	}
    public int getWaitingTime() { 
    	return waitingTime; 
    	}
    public void setWaitingTime(int waitingTime) { 
    	this.waitingTime = waitingTime; 
    	}
    public int getTurnaroundTime() { 
    	return turnaroundTime; 
    	}
    public void setTurnaroundTime(int turnaroundTime) { 
    	this.turnaroundTime = turnaroundTime; 
    	}
    public int compareTo(Process p) {
        return Integer.compare(this.arrivalTime, p.arrivalTime);
    }
}
