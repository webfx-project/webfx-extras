package dev.webfx.extras.visual.controls.charts.registry;

import dev.webfx.extras.visual.controls.charts.*;
import dev.webfx.extras.visual.controls.charts.peers.gwt.charba.*;

import static dev.webfx.kit.mapper.peers.javafxgraphics.NodePeerFactoryRegistry.registerNodePeerFactory;

public class VisualChartsRegistry {

    public static void registerLineChart() {
        registerNodePeerFactory(VisualLineChart.class, GwtJ2clCharbaVisualLineChartPeer::new);
    }

    public static void registerAreaChart() {
        registerNodePeerFactory(VisualAreaChart.class, GwtJ2clCharbaVisualAreaChartPeer::new);
    }

    public static void registerBarChart() {
        registerNodePeerFactory(VisualBarChart.class, GwtJ2clCharbaVisualBarChartPeer::new);
    }

    public static void registerPieChart() {
        registerNodePeerFactory(VisualPieChart.class, GwtJ2clCharbaVisualPieChartPeer::new);
    }

    public static void registerScatterChart() {
        registerNodePeerFactory(VisualScatterChart.class, GwtJ2clCharbaVisualScatterChartPeer::new);
    }
}
