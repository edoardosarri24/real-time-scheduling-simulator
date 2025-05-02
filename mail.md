# Obiettivo
Generazione di tracce di esecuzione di task-set real-time in ambiente simulato (in Java)

##### Task
- Pattern di rilascio (distribuzione del tempo fra due arrivi)
- Numero di chunk (computazioni) che costituiscono un job del task
- Ogni chunk è caratterizzato da una distribuzione del tempo di esecuzione, eventuale richiesta di risorse da usare in mutua esclusione (prima dell'esecuzione del chunk acquisisco il semaforo, e dopo l'esecuzione lo rilascio)
- Deadline
- Rappresentiamo distribuzioni di probabilità (per tempo fra due rilasci e tempo di esecuzione) direttamente con il loro [sampler](https://github.com/oris-tool/sirio/blob/master/sirio/src/main/java/org/oristool/simulator/samplers/Sampler.java).
- Aggiungere il deterministic sampler che campiona sempre lo stesso valore
##### TaskSet: è un insieme di Task
##### Risorsa da usare in mutua esclusione + semaforo che la gestisce
##### CPU: assumiamo che sia una sola
##### Scheduler
- Rate monotonic.
- Earliest deadline first.
##### Protocollo di accesso alle risorse
- Priority Ceiling Protocol.
- (Priority Inheritance Protocol).
##### Possibilità di iniettare fault
- Task aggiuntivo che fa cycle stealing (si aggiungono uno o più task a priorità alta allo scenario).
- Task programming defect (gestione errata della priorità o dell'acquisizione delle risorse).
##### Fallimenti osservati
- Deadline miss
- Violazione del tempo di computazione di un chunk (troppo basso o troppo alto).
##### Generazione di tracce dell'esecuzione: ogni traccia è una sequenza di coppie <evento, tempo> dove evento può essere:
- Rilascio di un job di un task.
- Acquisizione/rilascio di un semaforo da parte di un job di un task.
- Completamento di un chunk.
- Completamento di un job di un task (in realtà coincide con il completamento dell'ultimo chunk del task).
---
##### info
- Per la descrizione del fault & failure model([link](https://stlab.dinfo.unifi.it/carnevali/papers/11_CRV_TSE.pdf)).
- Per una descrizione di Sirio/ORIS ([link](https://www.oris-tool.org/papers/2019_tse_oris_tool.pdf)).