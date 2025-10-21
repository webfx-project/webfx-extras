package dev.webfx.extras.visual.controls.charts.peers.elemental2.charba;

import org.pepstock.charba.client.BarChart;
import org.pepstock.charba.client.configuration.BarOptions;
import org.pepstock.charba.client.configuration.ConfigurationOptions;
import org.pepstock.charba.client.configuration.Scales;
import dev.webfx.extras.visual.controls.charts.VisualBarChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualBarChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualBarChartPeerMixin;

/**
 * @author Bruno Salmon
 */
public final class Elemental2CharbaVisualBarChartPeer
        <C, N extends VisualBarChart, NB extends VisualBarChartPeerBase<C, N, NB, NM>, NM extends VisualBarChartPeerMixin<C, N, NB, NM>>
        extends Elemental2CharbaVisualChartPeer<C, N, NB, NM>
        implements VisualBarChartPeerMixin<C, N, NB, NM> {

    public Elemental2CharbaVisualBarChartPeer() {
        this((NB) new VisualBarChartPeerBase());
    }

    public Elemental2CharbaVisualBarChartPeer(NB base) {
        super(base);
    }

    @Override
    protected BarChart createChartWidget() {
        return new BarChart();
    }

    @Override
    protected Scales getScales(ConfigurationOptions options) {
        return ((BarOptions) options).getScales();
    }

}
