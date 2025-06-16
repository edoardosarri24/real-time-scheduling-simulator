package taskSet;

import org.junit.Before;
import org.junit.Test;
import exeptions.DeadlineMissedException;
import helper.ReflectionUtils;
import utils.MyClock;
import utils.sampler.ConstantSampler;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class TaskTest {

    private Task task;
    private Chunk chunk;

    @Before
    public void setUP() {
        this.chunk = new Chunk(0, new ConstantSampler(new BigDecimal(5)));
        this.task = new Task(
            10,
            10,
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
            10,
            3,
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
        Chunk newChunk = new Chunk(1, new ConstantSampler(new BigDecimal(1)));
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
        Chunk chunk0 = new Chunk(0, new ConstantSampler(new BigDecimal(5)));
        Chunk chunk1 = new Chunk(1, new ConstantSampler(new BigDecimal(5)));
        Task task = new Task(
            10,
            10,
            List.of(chunk0, chunk1));
        assertThat(chunk0.getParent())
            .isSameAs(task);
        assertThat(chunk1.getParent())
            .isSameAs(task);
    }

    @Test
    public void utilizationFactor() {
        Task task = new Task(
            10,
            10,
            List.of(
                new Chunk(0, new ConstantSampler(new BigDecimal(2))),
                new Chunk(1, new ConstantSampler(new BigDecimal(3)))));
        assertThat(task.utilizationFactor())
            .isEqualTo(0.5);
    }

    @Test
    public void periodAndDealineCheckKo() {
        Task task = new Task(
            10,
            12,
            List.of(this.chunk));
        assertThatThrownBy(() -> task.periodAndDealineCheck())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Il task "+ task.getId()
                + " ha periodo PT0.01S e deadline PT0.012S. Il periodo non può essere minore della deadline");
    }

    @Test
    public void periodAndDealineCheckOk() {
        Task task = new Task(
            10,
            8,
            List.of(this.chunk));
        assertThatCode(() -> task.periodAndDealineCheck())
            .doesNotThrowAnyException();
    }

    @Test
    public void nextDeadline() {
        Task task = new Task(
            5,
            3,
            List.of(this.chunk));
        Duration output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(3));
        MyClock.getInstance().advanceTo(Duration.ofMillis(2));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(3));
        MyClock.getInstance().advanceTo(Duration.ofMillis(3));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(5));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(6));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(8));
        MyClock.getInstance().advanceTo(Duration.ofMillis(8));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(13));
        MyClock.getInstance().advanceTo(Duration.ofMillis(9));
        output = task.nextDeadline();
        assertThat(output)
            .isEqualTo(Duration.ofMillis(13));
    }

}