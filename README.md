# Real-Time Scheduling Simulator

This project is a Java-based simulator for real-time task scheduling, managed with Maven.

Descriptions of the implementation choices are available in the [`report`](report.pdf) document.

### Features
The main goal is generate execution traces where each trace is a sequence of pairs $<time,event>$ and an event can be job release of a task job semaphore acquire/release by a task job chunk completion or job completion of a task
- It supports Rate Monotonic (RM).
- It support Priority Ceiling Protocol (PCP).

### Getting Started
- Clone the repository:
```bash
git clone https://github.com/edoardosarri24/real-time-scheduling-simulator
cd real-time-scheduling-simulator
```
- Test: `mvn test`
- Eseguire: `mvn compile exec:java`. To run the simulator, you need to define all the necessary objects in the *main.java* file, including tasks, jobs, resources, and eventually scheduling protocols.

### Future developments
The code is designed to be modular enough to allow the implementation of additional schedulers and resource access protocols with relative ease.