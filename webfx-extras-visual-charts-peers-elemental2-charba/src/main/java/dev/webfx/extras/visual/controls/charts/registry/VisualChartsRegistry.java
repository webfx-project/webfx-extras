package dev.webfx.extras.visual.controls.charts.registry;

import dev.webfx.extras.visual.controls.charts.*;
import dev.webfx.extras.visual.controls.charts.peers.elemental2.charba.*;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public class VisualChartsRegistry {

    public static void registerLineChart() {
        registerNodePeerFactory(VisualLineChart.class, Elemental2CharbaVisualLineChartPeer::new);
    }

    public static void registerAreaChart() {
        registerNodePeerFactory(VisualAreaChart.class, Elemental2CharbaVisualAreaChartPeer::new);
    }

    public static void registerBarChart() {
        registerNodePeerFactory(VisualBarChart.class, Elemental2CharbaVisualBarChartPeer::new);
    }

    public static void registerPieChart() {
        registerNodePeerFactory(VisualPieChart.class, Elemental2CharbaVisualPieChartPeer::new);
    }

    public static void registerScatterChart() {
        registerNodePeerFactory(VisualScatterChart.class, Elemental2CharbaVisualScatterChartPeer::new);
    }
}
