package dev.webfx.extras.visual.controls.charts.peers.elemental2.charba;

import dev.webfx.extras.visual.controls.charts.VisualAreaChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualAreaChartPeerMixin;
import org.pepstock.charba.client.StackedAreaChart;
import org.pepstock.charba.client.configuration.ConfigurationOptions;
import org.pepstock.charba.client.configuration.Scales;
import org.pepstock.charba.client.configuration.ScalesOptions;

/**
 * @author Bruno Salmon
 */
public final class Elemental2CharbaVisualAreaChartPeer
        <C, N extends VisualAreaChart, NB extends VisualAreaChartPeerBase<C, N, NB, NM>, NM extends VisualAreaChartPeerMixin<C, N, NB, NM>>
        extends Elemental2CharbaVisualChartPeer<C, N, NB, NM>
        implements VisualAreaChartPeerMixin<C, N, NB, NM> {

    public Elemental2CharbaVisualAreaChartPeer() {
        this((NB) new VisualAreaChartPeerBase());
    }

    public Elemental2CharbaVisualAreaChartPeer(NB base) {
        super(base);
    }

    @Override
    protected StackedAreaChart createChartWidget() {
        return new StackedAreaChart();
    }

    @Override
    protected Scales getScales(ConfigurationOptions options) {
        return ((ScalesOptions) options).getScales();
    }
}
