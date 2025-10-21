package dev.webfx.extras.visual.controls.charts.peers.elemental2.charba;

import org.pepstock.charba.client.LineChart;
import org.pepstock.charba.client.configuration.ConfigurationOptions;
import org.pepstock.charba.client.configuration.LineOptions;
import org.pepstock.charba.client.configuration.Scales;
import dev.webfx.extras.visual.controls.charts.VisualLineChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualLineChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualLineChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class Elemental2CharbaVisualLineChartPeer
        <C, N extends VisualLineChart, NB extends VisualLineChartPeerBase<C, N, NB, NM>, NM extends VisualLineChartPeerMixin<C, N, NB, NM>>
        extends Elemental2CharbaVisualChartPeer<C, N, NB, NM>
        implements VisualLineChartPeerMixin<C, N, NB, NM> {

    public Elemental2CharbaVisualLineChartPeer() {
        this((NB) new VisualLineChartPeerBase());
    }

    public Elemental2CharbaVisualLineChartPeer(NB base) {
        super(base);
    }

    @Override
    protected LineChart createChartWidget() {
        return new LineChart();
    }

    @Override
    protected Scales getScales(ConfigurationOptions options) {
        return ((LineOptions) options).getScales();
    }

}
