package dev.webfx.extras.visual.controls.charts.peers.gwt.charba;

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
public final class GwtJ2clCharbaVisualAreaChartPeer
        <C, N extends VisualAreaChart, NB extends VisualAreaChartPeerBase<C, N, NB, NM>, NM extends VisualAreaChartPeerMixin<C, N, NB, NM>>
        extends GwtJ2clCharbaVisualChartPeer<C, N, NB, NM>
        implements VisualAreaChartPeerMixin<C, N, NB, NM> {

    public GwtJ2clCharbaVisualAreaChartPeer() {
        this((NB) new VisualAreaChartPeerBase());
    }

    public GwtJ2clCharbaVisualAreaChartPeer(NB base) {
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
