package dev.webfx.extras.timelayout.gantt;

import dev.webfx.extras.timelayout.impl.TimeLayoutBase;
import dev.webfx.extras.timelayout.impl.TimeProjector;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
public final class GanttLayout <C, T> extends TimeLayoutBase<C, T> implements TimeProjector<T> {

    private final List<List<Block>> packedRows = new ArrayList<>();

    @Override
    protected TimeProjector<T> getTimeProjector() {
        return this;
    }

    @Override
    public double timeToX(T time, boolean start, boolean exclusive, double layoutWidth) {
        long totalDays = ((Temporal) timeWindowStart).until((Temporal) timeWindowEnd, ChronoUnit.DAYS) + 1;
        long daysToTime = ((Temporal) timeWindowStart).until((Temporal) time, ChronoUnit.DAYS);
        if (start && exclusive || !start && !exclusive)
            daysToTime++;
        return layoutWidth * daysToTime / totalDays;
    }

    @Override
    protected int computeChildColumnIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        return 0;
    }

    @Override
    protected int computeChildRowIndex(int childIndex, C child, T startTime, T endTime, double startX, double endX) {
        if (child instanceof Temporal)
            return 0;
        // Tetris block algorithm:
        Block newBlock = new Block(startX, endX);
        if (childIndex == 0)
            packedRows.clear();
        for (int rowIndex = 0; rowIndex < packedRows.size(); rowIndex++) {
            List<Block> row = packedRows.get(rowIndex);
            boolean fitInRow = row.stream().filter(b -> b.intersects(newBlock)).findAny().isEmpty();
            if (fitInRow) {
                row.add(newBlock);
                return rowIndex;
            }
        }
        return createNewRow(newBlock);
    }

    private int createNewRow(Block block) {
        List<Block> row = new ArrayList<>();
        row.add(block);
        packedRows.add(row);
        return packedRows.size() - 1;
    }

    private static final class Block {
        private final double startX;
        private final double endX;

        private Block(double startX, double endX) {
            this.startX = startX;
            this.endX = endX;
        }

        private boolean intersects(Block b) {
            if (startX < b.startX)
                return endX > b.startX;
            return startX <= b.endX;
        }
    }

}
