package taskSet;

import org.junit.Before;
import org.junit.Test;
import exeptions.DeadlineMissedException;
import helper.ReflectionUtils;
import utils.MyClock;

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
        MyClock.reset();
    }

    @Test
    public void purelyPeriodicCheckOK() {
        assertThatCode(() -> task.purelyPeriodicCheck())
            .doesNotThrowAnyException();
    }

    @Test
    public void purelyPeriodicCheck() {
        Task task = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(3),
            List.of(this.chunk));
        assertThatThrownBy(() -> task.purelyPeriodicCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task " + task.getId() + " non è puramente periodico: ha periodo PT0.01S e deadline PT0.003S");
    }

    @Test
    public void checkAndResetIf() {
        assertThatThrownBy(() -> this.task.relasePeriodTask())
            .isInstanceOf(DeadlineMissedException.class)
            .hasMessage("Il task " + this.task.getId() + " ha superato la deadline");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void checkAndResetElse() {
        List<Chunk> chunkToExectute = (List<Chunk>) ReflectionUtils.getField(this.task, "chunkToExecute");
        assertThat(chunkToExectute)
            .containsExactly(this.chunk);
        Chunk newChunk = new Chunk(1, Duration.ofMillis(1));
        chunkToExectute.add(newChunk);
        assertThat(chunkToExectute)
            .containsExactly(this.chunk, newChunk);
        ReflectionUtils.setField(
            this.task,
            "isExecuted",
            true);
        assertThat(this.task.getIsExecuted())
            .isTrue();
        assertThatCode(() -> this.task.relasePeriodTask())
            .doesNotThrowAnyException();
        chunkToExectute = (List<Chunk>) ReflectionUtils.getField(this.task, "chunkToExecute");
        assertThat(chunkToExectute)
            .hasSize(1)
            .allMatch(chunk -> chunk.equals(this.chunk));
        assertThat(this.task.getIsExecuted())
            .isFalse();
    }

    @Test
    public void constructorParent() {
        Chunk chunk0 = new Chunk(0, Duration.ofMillis(5));
        Chunk chunk1 = new Chunk(1, Duration.ofMillis(5));
        Task task = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(10),
            List.of(chunk0, chunk1));
        assertThat(chunk0.getParent())
            .isSameAs(task);
        assertThat(chunk1.getParent())
            .isSameAs(task);
    }

    @Test
    public void utilizationFactor() {
        Task task = new Task(
            Duration.ofMillis(10),
            Duration.ofMillis(10),
            List.of(
                new Chunk(0, Duration.ofMillis(2)),
                new Chunk(1, Duration.ofMillis(3))));
        assertThat(task.utilizationFactor())
            .isEqualTo(0.5);
    }

    @Test
    public void periodAndDealineCheckKo() {
        Task task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(12),
            List.of(this.chunk));
        assertThatThrownBy(() -> task.periodAndDealineCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task "+ task.getId()
                + " ha periodo PT10S e deadline PT12S. Il periodo non può essere minore della deadline");
    }

    @Test
    public void periodAndDealineCheckOk() {
        Task task = new Task(
            Duration.ofSeconds(10),
            Duration.ofSeconds(8),
            List.of(this.chunk));
        assertThatCode(() -> task.periodAndDealineCheck())
            .doesNotThrowAnyException();
    }

}