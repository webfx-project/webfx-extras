package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.visual.controls.charts.VisualPieChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualPieChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualPieChartPeerMixin;
import dev.webfx.platform.util.Numbers;

import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public final class OpenJFXVisualPieChartPeer
        <FxN extends PieChart, N extends VisualPieChart, NB extends VisualPieChartPeerBase<FxN, N, NB, NM>, NM extends VisualPieChartPeerMixin<FxN, N, NB, NM>>

        extends OpenJFXVisualChartPeer<FxN, N, NB, NM>
        implements VisualPieChartPeerMixin<FxN, N, NB, NM> {

    private ObservableList<javafx.scene.chart.PieChart.Data> pieData;
    private Function<Integer, String> seriesNameGetter;

    public OpenJFXVisualPieChartPeer() {
        super((NB) new VisualPieChartPeerBase());
    }

    @Override
    protected FxN createFxChart() {
        PieChart pieChart = new PieChart();
        pieChart.setStartAngle(90);
        pieChart.setLegendVisible(false);
        return (FxN) pieChart;
    }

    @Override
    public void createChartData(Type xType, Type yType, int pointPerSeriesCount, int seriesCount, Function<Integer, String> seriesNameGetter) {
        pieData = FXCollections.observableArrayList();
        this.seriesNameGetter = seriesNameGetter;
    }

    @Override
    public void setChartDataX(Object xValue, int pointIndex) {
    }

    @Override
    public void setChartDataY(Object yValue, int pointIndex, int seriesIndex) {
        pieData.add(new javafx.scene.chart.PieChart.Data(seriesNameGetter.apply(seriesIndex), Numbers.doubleValue(yValue)));
    }

    @Override
    public void applyChartData() {
        getOrCreateFxChart().setData(pieData);
    }
}
