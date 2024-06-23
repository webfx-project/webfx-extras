package dev.webfx.extras.visual.controls.charts.registry.spi.impl.openjfx;

import dev.webfx.extras.visual.controls.charts.*;
import dev.webfx.extras.visual.controls.charts.peers.openjfx.*;
import dev.webfx.extras.visual.controls.charts.registry.spi.VisualChartsRegistryProvider;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public class OpenJFXVisualChartsRegistryProvider implements VisualChartsRegistryProvider {

    public void registerLineChart() {
        registerNodePeerFactory(VisualLineChart.class, OpenJFXVisualLineChartPeer::new);
    }

    public void registerAreaChart() {
        registerNodePeerFactory(VisualAreaChart.class, OpenJFXVisualAreaChartPeer::new);
    }

    public void registerBarChart() {
        registerNodePeerFactory(VisualBarChart.class, OpenJFXVisualBarChartPeer::new);
    }

    public void registerPieChart() {
        registerNodePeerFactory(VisualPieChart.class, OpenJFXVisualPieChartPeer::new);
    }

    public void registerScatterChart() {
        registerNodePeerFactory(VisualScatterChart.class, OpenJFXVisualScatterChartPeer::new);
    }

}
