package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.scene.chart.AreaChart;
import dev.webfx.extras.visual.controls.charts.VisualAreaChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class FxVisualAreaChartPeer
        <FxN extends AreaChart, N extends VisualAreaChart, NB extends VisualAreaChartPeerBase<FxN, N, NB, NM>, NM extends VisualAreaChartPeerMixin<FxN, N, NB, NM>>

        extends FxVisualXYChartPeer<FxN, N, NB, NM>
        implements VisualAreaChartPeerMixin<FxN, N, NB, NM> {

    public FxVisualAreaChartPeer() {
        super((NB) new VisualAreaChartPeerBase());
    }

    @Override
    protected FxN createFxNode() {
        // The API requires the axis to be defined now whereas we don't know the structure to display yet
        // So assuming category on x axis and number on y axis (is it possible to generify that?)
        AreaChart<String, Number> areaChart = new AreaChart<>(createCategoryAxis(), createNumberAxis());
        areaChart.setCreateSymbols(false);
        return (FxN) areaChart;
    }
}
