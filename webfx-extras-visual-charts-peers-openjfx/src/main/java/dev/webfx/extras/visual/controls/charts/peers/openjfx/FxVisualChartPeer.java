package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import dev.webfx.extras.visual.SelectionMode;
import dev.webfx.extras.visual.VisualSelection;
import dev.webfx.extras.visual.controls.charts.VisualChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.SceneRequester;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.openjfx.FxRegionPeer;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.BorderPane;

/**
 * @author Bruno Salmon
 */
abstract class FxVisualChartPeer
        <FxN extends Chart, N extends VisualChart, NB extends VisualChartPeerBase<FxN, N, NB, NM>, NM extends VisualChartPeerMixin<FxN, N, NB, NM>>
        extends FxRegionPeer<BorderPane, N, NB, NM>
        implements VisualChartPeerMixin<FxN, N, NB, NM>, FxLayoutMeasurable {

    protected FxN fxChart;

    FxVisualChartPeer(NB base) {
        super(base);
    }

    @Override
    public void bind(N node, SceneRequester sceneRequester) {
        super.bind(node, sceneRequester);
    }

    @Override
    protected final BorderPane createFxNode() {
        // We embed the JavaFX chart in a container (BorderPane). The reason for this is that the JavaFX API is rigid
        // regarding the definition of the axis: they can't be changed once the chart is created (the axis are passed in
        // the constructor and can't be changed anymore). But in the WebFX Visual API the axis definition depends on the
        // VisualResult. So, before receiving the first VisualResult, we don't know the axis definition. In addition,
        // the VisualResult may completely change over the time (including the axis definition). So will create the
        // actual JavaFX chart only once we have the date from VisualResult.
        return new BorderPane();
    }

    protected FxN getOrCreateFxChart() {
        if (fxChart == null) {
            fxChart = createFxChart();
            fxChart.setAnimated(false);
            getFxNode().setCenter(fxChart);
        }
        return fxChart;
    }

    protected abstract FxN createFxChart();

    @Override
    public void updateSelectionMode(SelectionMode mode) {
    }

    @Override
    public void updateVisualSelection(VisualSelection selection) {
    }

    static NumberAxis createNumberAxis() {
        NumberAxis axis = new NumberAxis();
        axis.setForceZeroInRange(false);
        return axis;
    }

    static CategoryAxis createCategoryAxis() {
        CategoryAxis axis = new CategoryAxis();
        return axis;
    }
}
