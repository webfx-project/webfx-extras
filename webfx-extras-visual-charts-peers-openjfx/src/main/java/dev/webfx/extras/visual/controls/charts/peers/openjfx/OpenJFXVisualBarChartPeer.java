package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.scene.chart.BarChart;
import dev.webfx.extras.visual.controls.charts.VisualBarChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualBarChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualBarChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class OpenJFXVisualBarChartPeer
        <FxN extends BarChart, N extends VisualBarChart, NB extends VisualBarChartPeerBase<FxN, N, NB, NM>, NM extends VisualBarChartPeerMixin<FxN, N, NB, NM>>

        extends OpenJFXVisualXYChartPeer<FxN, N, NB, NM>
        implements VisualBarChartPeerMixin<FxN, N, NB, NM> {

    public OpenJFXVisualBarChartPeer() {
        super((NB) new VisualBarChartPeerBase());
    }

    @Override
    protected FxN createFxChart() {
        return (FxN) new BarChart(xAxis, yAxis);
    }
}
