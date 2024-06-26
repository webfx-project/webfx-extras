package dev.webfx.extras.visual.controls.charts;

import dev.webfx.extras.visual.controls.SelectableVisualResultControl;

/**
 *
 * A chart that can display one or several series of values. Each series has a name and points. For charts with 2 axis,
 * each point is defined by 2 values: one for the X axis (major axis) and one for the Y axis (minor axis).
 * For pie charts that don't have 2 axis, each point is defines by 1 single value.
 * <p>
 * The chart uses the VisualResult to read the data (series names and points). The format depends on the type of
 * chart. And for each type of chart, there are 2 possible formats: the column format where series are stored in
 * columns and the row format where series are stored in rows (formats are just transposed - they both produce the same
 * visual result).
 *
 *
 * ===================
 * ==== Pie chart ====
 * ===================
 *
 * Each series has 1 single point with 1 single value that represents a slice in the pie circle.
 *
 * 1) Column format: series are stored in columns (better for fixed number of series) and there is one single row
 *    - The columns headers values contain the series names
 *    - The columns values contain the point values (only 1 row - any additional row will be ignored).
 * {@code <pre>
 *    +--------+---------+------+--------+
 *    | Europe | America | Asia | Africa |
 *    +--------+---------+------+--------+
 *    |   10   |    18   |  12  |    7   |
 *    +--------+---------+------+--------+
 *  </pre> }
 * 2) Row format: Series are stored in rows (better for variable number of series) and there are 2 columns
 *    - The first column values contain the series names (the header value is ignored)
 *    - The second columns values contain the point values (the header value is ignored)
 * {@code <pre>
 *    +---------+-------+
 *    | Series  | Value |
 *    +---------+-------+
 *    | Europe  |   10  |
 *    +---------+-------+
 *    | America |   18  |
 *    +---------+-------+
 *    | Asia    |   12  |
 *    +---------+-------+
 *    | Africa |     7  |
 *    +---------+-------+
 * </pre> }
 * ==========================================================
 * ==== Line chart, Area chart, Bar chart, Scatter chart ====
 * ==========================================================
 *
 * All these types of charts are XY charts where series are supposed to share the same value on the X axis and different
 * values on the Y axis. For exceptions (some series may have no point for a specific X), Y value can be set to null.
 *
 * 1) Column format: series are stored in columns (better for fixed number of series but variable number of points in each series)
 *    - The first column values contain the X values (shared by all series). The header value is ignored.
 *    - Other columns headers values contain the series names
 *    - Other columns values contain Y values (specific to the series).
 * {@code <pre>
 *    +--------+--------+---------+------+--------+
 *    | Year   | Europe | America | Asia | Africa |
 *    +--------+--------+---------+------+--------+
 *    |  2014  |   10   |    18   |  12  |    6   |
 *    +--------+--------+---------+------+--------+
 *    |  2015  |    9   |    17   |  11  |    6   |
 *    +--------+--------+---------+------+--------+
 *    |  2016  |   11   |    19   |  13  |    8   |
 *    +--------+--------+---------+------+--------+
 *    <   X   ><              Y                  >
 * </pre> }
 * 2) Row format: series are stored in rows (better for variable number of series but fixed number of points in each series)
 *    - The first column values contain the series names (the header value is ignored).
 *    - Other columns headers values contain the shared point values on the major axis (shared by all series).
 *    - Other columns values contain the point values for the minor axis
 * {@code <pre>
 *    +---------+-------+-------+-------+
 *    | Series  |  2014 |  2015 |  2016 |  X
 *    +---------+-------+-------+-------+  ^
 *    | Europe  |   10  |    9  |   11  |
 *    +---------+-------+-------+-------+
 *    | America |   18  |   17  |   19  |  Y
 *    +---------+-------+-------+-------+
 *    | Asia    |   12  |   11  |   13  |
 *    +---------+-------+-------+-------+
 *    | Africa  |    7  |    6  |    8  |
 *    +---------+-------+-------+-------+  ^
 * </pre> }
 *
 * @author Bruno Salmon
 */

public abstract class VisualChart extends SelectableVisualResultControl {
}
