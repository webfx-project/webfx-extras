package dev.webfx.extras.visual.controls.charts.peers.elemental2.charba;

import org.pepstock.charba.client.ScatterChart;
import org.pepstock.charba.client.configuration.ConfigurationOptions;
import org.pepstock.charba.client.configuration.Scales;
import org.pepstock.charba.client.configuration.ScatterOptions;
import dev.webfx.extras.visual.controls.charts.VisualScatterChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualScatterChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualScatterChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class Elemental2CharbaVisualScatterChartPeer
        <C, N extends VisualScatterChart, NB extends VisualScatterChartPeerBase<C, N, NB, NM>, NM extends VisualScatterChartPeerMixin<C, N, NB, NM>>
        extends Elemental2CharbaVisualChartPeer<C, N, NB, NM>
        implements VisualScatterChartPeerMixin<C, N, NB, NM> {

    public Elemental2CharbaVisualScatterChartPeer() {
        this((NB) new VisualScatterChartPeerBase());
    }

    public Elemental2CharbaVisualScatterChartPeer(NB base) {
        super(base);
    }

    @Override
    protected ScatterChart createChartWidget() {
        return new ScatterChart();
    }

    @Override
    protected Scales getScales(ConfigurationOptions options) {
        return ((ScatterOptions) options).getScales();
    }

}
