package dev.webfx.extras.time.layout.impl;

import dev.webfx.extras.geometry.MutableBounds;

/**
 * The mutable bounds associated to an object that needs to be visualised in a TimeLayout (and the TimeLayout will be
 * responsible for the computation of its bounds). The associated object can be a child (in this case it will be a
 * ChildBounds superclass instance) but it can also be an additional decorator object such as a parent or grandparent
 * row or header in a GanttLayout (these objects are automatically created by GanttLayout from the children).
 *
 * All instances will probably use lazy computation (and therefore derive LazyObjectBounds), but the TimeLayoutUtil API
 * can accept any ObjectBounds. TimeLayoutUtil will trigger the lazy computation for the visible objects only (plus other
 * enclosing bounds that may be necessary to determine which bounds are visible) and therefore offer the best possible
 * performance. The only addition that is required by the TimeLayoutUtil (compared to MutableBounds) is the access to
 * the associated object that he will pass to an object processor (some code that will receive the object and its bounds
 * to process it - typically drawing the object in a canvas at this specific bounds).
 *
 * @author Bruno Salmon
 */
public class ObjectBounds<O> extends MutableBounds {

    private O object;

    public O getObject() {
        return object;
    }

    public void setObject(O object) {
        this.object = object;
    }

}
