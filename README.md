# Real-Time Scheduling Simulator
This project is a Java-based simulator for real-time task scheduling, managed with Maven.

It allows the generation of execution traces starting from a defined taskset, optionally using shared resources managed through mutual exclusion protocols. Each execution trace is a sequence of pairs $<time, event>$, where an event can be: release of a job from a task; acquisition or release of a semaphore by a task’s job; completion of a chunk; job completion; preemption of a task. Events and faults can be modeled stochastically, and scheduling feasibility can be checked through known analytical tests. Detailed documentation is available in the [`report`](report.pdf) file. A short presentation is in [`slides`](slides.pdf)file.

To use the simulator, define the taskset, resources, chunks, scheduling algorithm, and protocol (if needed) in the main.java file. The datails are in the chapter 2 of the [`report`](report.pdf) file.

### Features
The simulator supports the following capabilities:
- Scheduling Algorithms:
  - Rate Monotonic (RM) with support for shared resources via Priority Ceiling Protocol (PCP).
  - Earliest Deadline First (EDF), only in absence of resource sharing due to dynamic priority management.
- Trace Generation:
  - Create a single execution trace or a dataset of multiple traces from a taskset and scheduling policy.
  - Specify the desired simulation time.
- Fault Injection and Detection:
  - Check for deadline misses: if a task fails to complete before its deadline, the simulation halts immediately to isolate the first failure.
  - Introduce stochastic faults on chunk execution times (i.e., random additional execution time sampled from a distribution).
  - Inject protocol-level faults in PCP: task’s dynamic priority in critical section is increased by a random value.
  - Simulate faults where a chunk probabilistically fails to acquire/release the semaphore protecting the associated critical section.
- Feasibility Checks:
  - RM: Hyperbolic bound test for schedulability.
  - EDF: Utilization-based necessary and sufficient condition.

### Future developments
The code is modular and extensible. Future improvements could include:
	•	Additional scheduling algorithms (e.g., Deadline Monotonic).
	•	Support for more advanced resource access protocols (e.g., Stack Resource Policy).
	•	A graphical interface for taskset configuration and trace visualization.