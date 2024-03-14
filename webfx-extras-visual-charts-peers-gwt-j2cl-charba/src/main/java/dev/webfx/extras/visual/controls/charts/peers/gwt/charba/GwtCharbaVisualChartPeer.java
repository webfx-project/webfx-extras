package dev.webfx.extras.visual.controls.charts.peers.gwt.charba;

import dev.webfx.extras.type.PrimType;
import dev.webfx.extras.type.Type;
import dev.webfx.extras.type.Types;
import dev.webfx.extras.visual.SelectionMode;
import dev.webfx.extras.visual.VisualResult;
import dev.webfx.extras.visual.VisualSelection;
import dev.webfx.extras.visual.controls.charts.VisualBarChart;
import dev.webfx.extras.visual.controls.charts.VisualChart;
import dev.webfx.extras.visual.controls.charts.VisualLineChart;
import dev.webfx.extras.visual.controls.charts.VisualPieChart;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerBase;
import dev.webfx.extras.visual.controls.charts.peers.base.VisualChartPeerMixin;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.html.HtmlRegionPeer;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.html.layoutmeasurable.HtmlLayoutMeasurable;
import dev.webfx.kit.mapper.peers.javafxgraphics.gwtj2cl.util.HtmlUtil;
import dev.webfx.platform.console.Console;
import dev.webfx.platform.uischeduler.UiScheduler;
import dev.webfx.platform.util.time.Times;
import dev.webfx.platform.util.Numbers;
import dev.webfx.platform.util.Strings;
import javafx.scene.paint.Color;
import org.pepstock.charba.client.AbstractChart;
import org.pepstock.charba.client.Charba;
import org.pepstock.charba.client.IsDatasetCreator;
import org.pepstock.charba.client.callbacks.ColorCallback;
import org.pepstock.charba.client.callbacks.DatasetContext;
import org.pepstock.charba.client.configuration.*;
import org.pepstock.charba.client.data.*;
import org.pepstock.charba.client.enums.DefaultInteractionMode;
import org.pepstock.charba.client.enums.Position;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @author Bruno Salmon
 */
abstract class GwtCharbaVisualChartPeer
        <C, N extends VisualChart, NB extends VisualChartPeerBase<C, N, NB, NM>, NM extends VisualChartPeerMixin<C, N, NB, NM>>
        extends HtmlRegionPeer<N, NB, NM>
        implements VisualChartPeerMixin<C, N, NB, NM>, HtmlLayoutMeasurable {

    static {
        Charba.enable(); // For embedded resources
    }

    protected AbstractChart chartWidget;

    GwtCharbaVisualChartPeer(NB base) {
        super(base, HtmlUtil.createDivElement());
        chartWidget = createChartWidget();
        // Disabling animation (by setting its duration to 0) because it's annoying when combined with push updates (ex: Monitor activity)
        //chartWidget.getOptions().getAnimation().setDuration(0);
        HtmlUtil.setChild(getElement(), chartWidget.getChartElement().as());
        if (getNode() != null)
            onNodeAndWidgetReady();
        else
            UiScheduler.scheduleDeferred(this::onNodeAndWidgetReady);
    }

    private void onNodeAndWidgetReady() {
        N node = getNode();
        updateVisualResult(node.getVisualResult());
    }

    protected abstract AbstractChart createChartWidget();

    protected abstract Scales getScales(ConfigurationOptions options);

    @Override
    public void updateSelectionMode(SelectionMode mode) {

    }

    @Override
    public void updateVisualSelection(VisualSelection selection) {

    }

    @Override
    public void updateVisualResult(VisualResult rs) {
        if (chartWidget != null)
            getNodePeerBase().updateResult(rs);
    }

    private final static String[] JAVAFX_CHART_COLOR_PALETTE = {"#f3622d", "#fba71b", "#57b757", "#41a9c9", "#4258c9", "#9a42c8", "#c84164", "#888888"};

    PrimType xPrimType, yPrimType;
    private boolean isPieChart;
    List<Dataset> datasets;
    private SeriesInfo[] seriesInfos;
    private int seriesCount;

    private String[] labels; // used for Pie
    private String[] xLabels; // used for chats with categories on xAxis
    private static class SeriesInfo {
        double[] data;
        List<DataPoint> dataPoints;
    }

    @Override
    public void createChartData(Type xType, Type yType, int pointPerSeriesCount, int seriesCount, Function<Integer, String> seriesNameGetter) {
        Console.log("xType = " + xType + ", yType = " + yType);
        //seriesCount = 1;
        xPrimType = Types.getPrimType(xType);
        yPrimType = Types.getPrimType(yType);

        ConfigurationOptions options = chartWidget.getOptions();
        options.setResponsive(true);
        options.setMaintainAspectRatio(false);
        options.getLegend().setDisplay(true);
        options.getLegend().setPosition(Position.BOTTOM);
        options.getTooltips().setMode(DefaultInteractionMode.INDEX);
        options.getTooltips().setIntersect(false);
        options.getHover().setMode(DefaultInteractionMode.NEAREST);
        options.getHover().setIntersect(true);
        options.getElements().getPoint().setRadius(0.5); // 1px dot
        options.getElements().getLine().setTension(0.0); // No Bezier interpolation
        //options.setShowLines(false);

        Axis xAxis = null;
        Scales scales = getScales(options);
        if (scales != null) {
            if (xPrimType.isNumber())
                xAxis = new CartesianLinearAxis(chartWidget);
            else if (xPrimType.isDate())
                xAxis = new CartesianTimeAxis(chartWidget);
            else
                xAxis = new CartesianCategoryAxis(chartWidget);
            xAxis.setDisplay(true);

            Axis yAxis = new CartesianLinearAxis(chartWidget);
            yAxis.setDisplay(true);

            scales.setAxes(xAxis, yAxis);
        }

        datasets = chartWidget.getData().getDatasets(true);
        datasets.clear();

        N node = getNode();
        isPieChart = node instanceof VisualPieChart;
        if (isPieChart) {
            labels = new String[seriesCount];
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                labels[seriesIndex] = seriesNameGetter.apply(seriesIndex);
            }
            Dataset dataset = newDataset("white", 1, context -> JAVAFX_CHART_COLOR_PALETTE[context.getDataIndex() % JAVAFX_CHART_COLOR_PALETTE.length]);
            SeriesInfo seriesInfo = new SeriesInfo();
            seriesInfo.data = new double[seriesCount];
            seriesInfos = new SeriesInfo[1];
            seriesInfos[0] = seriesInfo;
            datasets.add(dataset);
        } else {
            boolean hasXLabels = xAxis instanceof CartesianCategoryAxis;
            if (hasXLabels)
                xLabels = new String[pointPerSeriesCount];
            seriesInfos = new SeriesInfo[seriesCount];
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                String hexColor = JAVAFX_CHART_COLOR_PALETTE[seriesIndex % JAVAFX_CHART_COLOR_PALETTE.length];
                Dataset dataset = newDataset(hexColor, 1, context -> node instanceof VisualLineChart ? "transparent" : "#" + Color.valueOf(hexColor).deriveColor(1, 1, 1, node instanceof VisualBarChart ? 1 : 0.2).toString().substring(2));
                dataset.setLabel(seriesNameGetter.apply(seriesIndex));
                datasets.add(dataset);
                SeriesInfo seriesInfo = new SeriesInfo();
                if (dataset instanceof HasDataPoints && !hasXLabels)
                    seriesInfo.dataPoints = ((HasDataPoints) dataset).getDataPoints(true);
                else
                    seriesInfo.data = new double[pointPerSeriesCount];
                seriesInfos[seriesIndex] = seriesInfo;
            }
        }
        this.seriesCount = seriesCount;
    }

    private Dataset newDataset(String borderColor, int borderWidth, ColorCallback<DatasetContext> backgroundColorCallback) {
        Dataset dataset = ((IsDatasetCreator) chartWidget).newDataset();
        dataset.setBorderColor(context -> borderColor);
        if (dataset instanceof HoverDataset)
            ((HoverDataset) dataset).setBorderWidth(context -> borderWidth);
        else if (dataset instanceof LiningDataset)
            ((LiningDataset) dataset).setBorderWidth(context -> borderWidth);
        dataset.setBackgroundColor(backgroundColorCallback);
        return dataset;
    }

    private Object xValue;

    @Override
    public void setChartDataX(Object xValue, int pointIndex) {
        this.xValue = xValue;
    }

    @Override
    public void setChartDataY(Object yValue, int pointIndex, int seriesIndex) {
        if (isPieChart) {
            SeriesInfo seriesInfo = seriesInfos[0];
            seriesInfo.data[seriesIndex] = Numbers.toDouble(yValue);
            return;
        }
        if (xLabels != null)
            xLabels[pointIndex] = Strings.toString(xValue);
        if (seriesIndex >= seriesCount)
            return;
        SeriesInfo seriesInfo = seriesInfos[seriesIndex];
        if (seriesInfo.dataPoints != null)
            seriesInfo.dataPoints.add(pointIndex, newDataPoint(xValue, yValue));
        else
            seriesInfo.data[pointIndex] = Numbers.toDouble(yValue);
    }

    private DataPoint newDataPoint(Object xValue, Object yValue) {
        Console.log("x = " + xValue + ", y = " + yValue);
        DataPoint dataPoint = new DataPoint();
        if (xPrimType.isNumber())
            dataPoint.setX(Numbers.toDouble(xValue));
        else if (xPrimType.isDate())
            dataPoint.setX(new Date(Times.toInstant(xValue).toEpochMilli()));
        else
            dataPoint.setX(Strings.toSafeString(xValue));
        dataPoint.setY(Numbers.toDouble(yValue));
        return dataPoint;
    }

    @Override
    public void applyChartData() {
        if (labels != null)
            chartWidget.getData().setLabels(labels);
        if (xLabels != null)
            chartWidget.getData().setXLabels(xLabels);
        if (isPieChart) {
            SeriesInfo seriesInfo = seriesInfos[0];
            Dataset dataset = datasets.get(0);
            if (seriesInfo.data != null)
                dataset.setData(seriesInfo.data);
        } else
            for (int seriesIndex = 0; seriesIndex < seriesCount; seriesIndex++) {
                SeriesInfo seriesInfo = seriesInfos[seriesIndex];
                Dataset dataset = datasets.get(seriesIndex);
                if (seriesInfo.data != null)
                    dataset.setData(seriesInfo.data);
        }
        chartWidget.update();
    }

}
