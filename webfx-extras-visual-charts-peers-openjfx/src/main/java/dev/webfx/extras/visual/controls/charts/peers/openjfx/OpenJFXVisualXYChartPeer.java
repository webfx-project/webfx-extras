package dev.webfx.extras.visual.controls.charts.peers.openjfx;

import dev.webfx.extras.type.PrimType;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.visual.controls.charts.VisualChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerMixin;
import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
public abstract class OpenJFXVisualXYChartPeer
        <FxN extends XYChart, N extends VisualChart, NB extends VisualChartPeerBase<FxN, N, NB, NM>, NM extends VisualChartPeerMixin<FxN, N, NB, NM>>
        extends OpenJFXVisualChartPeer<FxN, N, NB, NM> {

    protected Axis xAxis;
    protected Axis yAxis;
    private List<XYChart.Series> seriesList;
    private Object xValue;

    public OpenJFXVisualXYChartPeer(NB base) {
        super(base);
    }

    @Override
    public void createChartData(Type xType, Type yType, int pointPerSeriesCount, int seriesCount, Function<Integer, String> seriesNameGetter) {
        xAxis = createAxisFromType(xType);
        yAxis = createAxisFromType(yType);
        seriesList = new ArrayList<>();
        for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
            XYChart.Series series = new XYChart.Series<>();
            series.setName(seriesNameGetter.apply(seriesIndex));
            seriesList.add(series);
        }
    }

    private Axis createAxisFromType(Type type) {
        if (type instanceof PrimType && ((PrimType) type).isNumber())
            return createNumberAxis();
        return createCategoryAxis();
    }

    @Override
    public void setChartDataX(Object xValue, int pointIndex) {
        this.xValue = xValue;
    }

    @Override
    public void setChartDataY(Object yValue, int pointIndex, int seriesIndex) {
        seriesList.get(seriesIndex).getData().add(new XYChart.Data<>(xValue, yValue));
    }

    @Override
    public void applyChartData() {
        getOrCreateFxChart().getData().setAll(seriesList);
    }
}
