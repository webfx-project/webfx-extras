package dev.webfx.extras.visual.controls.charts.peers.gwt.charba;

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
public final class GwtJ2clCharbaVisualLineChartPeer
        <C, N extends VisualLineChart, NB extends VisualLineChartPeerBase<C, N, NB, NM>, NM extends VisualLineChartPeerMixin<C, N, NB, NM>>
        extends GwtJ2clCharbaVisualChartPeer<C, N, NB, NM>
        implements VisualLineChartPeerMixin<C, N, NB, NM> {

    public GwtJ2clCharbaVisualLineChartPeer() {
        this((NB) new VisualLineChartPeerBase());
    }

    public GwtJ2clCharbaVisualLineChartPeer(NB base) {
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
