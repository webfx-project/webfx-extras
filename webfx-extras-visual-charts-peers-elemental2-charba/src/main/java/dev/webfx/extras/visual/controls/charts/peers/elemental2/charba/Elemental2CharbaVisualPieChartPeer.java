package dev.webfx.extras.visual.controls.charts.peers.elemental2.charba;

import org.pepstock.charba.client.PieChart;
import org.pepstock.charba.client.configuration.ConfigurationOptions;
import org.pepstock.charba.client.configuration.Scales;
import dev.webfx.extras.visual.controls.charts.VisualPieChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualPieChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualPieChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class Elemental2CharbaVisualPieChartPeer
        <C, N extends VisualPieChart, NB extends VisualPieChartPeerBase<C, N, NB, NM>, NM extends VisualPieChartPeerMixin<C, N, NB, NM>>
        extends Elemental2CharbaVisualChartPeer<C, N, NB, NM>
        implements VisualPieChartPeerMixin<C, N, NB, NM> {

    public Elemental2CharbaVisualPieChartPeer() {
        this((NB) new VisualPieChartPeerBase());
    }

    public Elemental2CharbaVisualPieChartPeer(NB base) {
        super(base);
    }

    @Override
    protected PieChart createChartWidget() {
        return new PieChart();
    }

    @Override
    protected Scales getScales(ConfigurationOptions options) {
        return null;
    }

}
