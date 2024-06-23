package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.scene.chart.AreaChart;
import dev.webfx.extras.visual.controls.charts.VisualAreaChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class OpenJFXVisualAreaChartPeer
        <FxN extends AreaChart, N extends VisualAreaChart, NB extends VisualAreaChartPeerBase<FxN, N, NB, NM>, NM extends VisualAreaChartPeerMixin<FxN, N, NB, NM>>

        extends OpenJFXVisualXYChartPeer<FxN, N, NB, NM>
        implements VisualAreaChartPeerMixin<FxN, N, NB, NM> {

    public OpenJFXVisualAreaChartPeer() {
        super((NB) new VisualAreaChartPeerBase());
    }

    @Override
    protected FxN createFxChart() {
        AreaChart<String, Number> areaChart = new AreaChart<>(xAxis, yAxis);
        areaChart.setCreateSymbols(false);
        return (FxN) areaChart;
    }
}
