package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.scene.chart.ScatterChart;
import dev.webfx.extras.visual.controls.charts.VisualScatterChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualScatterChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualScatterChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class OpenJFXVisualScatterChartPeer
        <FxN extends ScatterChart, N extends VisualScatterChart, NB extends VisualScatterChartPeerBase<FxN, N, NB, NM>, NM extends VisualScatterChartPeerMixin<FxN, N, NB, NM>>

        extends OpenJFXVisualXYChartPeer<FxN, N, NB, NM>
        implements VisualScatterChartPeerMixin<FxN, N, NB, NM> {

    public OpenJFXVisualScatterChartPeer() {
        super((NB) new VisualScatterChartPeerBase());
    }

    @Override
    protected FxN createFxChart() {
        return (FxN) new ScatterChart(xAxis, yAxis);
    }
}
