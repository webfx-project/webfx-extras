package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.scene.chart.LineChart;
import dev.webfx.extras.visual.controls.charts.VisualLineChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualLineChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualLineChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class FxVisualLineChartPeer
        <FxN extends LineChart, N extends VisualLineChart, NB extends VisualLineChartPeerBase<FxN, N, NB, NM>, NM extends VisualLineChartPeerMixin<FxN, N, NB, NM>>

        extends FxVisualXYChartPeer<FxN, N, NB, NM>
        implements VisualLineChartPeerMixin<FxN, N, NB, NM> {

    public FxVisualLineChartPeer() {
        super((NB) new VisualLineChartPeerBase());
    }

    @Override
    protected FxN createFxChart() {
        LineChart lineChart = new LineChart(xAxis, yAxis);
        lineChart.setCreateSymbols(false);
        return (FxN) lineChart;
    }

}
