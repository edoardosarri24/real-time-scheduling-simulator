package taskSet;

import org.junit.Before;
import org.junit.Test;

import exeptions.DeadlineMissedException;
import exeptions.PurelyPeriodicException;
import utils.ReflectionUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskTest {

    private Task task;
    private Chunk chunk;

    @Before
    public void setUP() {
        this.chunk = new Chunk(0, Duration.ofMillis(5));
        this.task = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(10),
            List.of(this.chunk));
    }

    @Test
    public void purelyPeriodicCheckOK() {
        assertThatCode(() -> task.purelyPeriodicCheck())
            .doesNotThrowAnyException();
    }

    @Test
    public void purelyPeriodicCheckKO() {
        Task task = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(3),
            List.of(this.chunk));
        assertThatThrownBy(() -> task.purelyPeriodicCheck())
            .isInstanceOf(PurelyPeriodicException.class)
            .hasMessage("Il task " + task.getId() + " non è puramente periocico: ha periodo PT0.01S e deadline PT0.003S");
    }

    @Test
    public void checkAndResetIf() {
        assertThatThrownBy(() -> this.task.checkAndReset())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + this.task.getId() + " ha superato la deadline");
    }

    @Test
    public void checkAndResetElse() {
        assertThat(this.task.getChunkToExecute())
            .containsExactly(this.chunk);
        Chunk newChunk = new Chunk(1, Duration.ofMillis(1));
        this.task.getChunkToExecute().add(newChunk);
        assertThat(this.task.getChunkToExecute())
            .containsExactly(this.chunk, newChunk);
        ReflectionUtils.setField(
            this.task, 
            "isExecuted", 
            true);
        assertThatCode(() -> this.task.checkAndReset())
            .doesNotThrowAnyException();
        assertThat(this.task.getChunkToExecute())
            .containsExactly(this.chunk);
        assertThat((boolean) ReflectionUtils.getField(this.task, "isExecuted"))
            .isFalse();
    }
    
}