package dev.webfx.extras.util.masterslave;

/**
 * @author Bruno Salmon
 */
public abstract class AbstractSlaveEditor<T> implements SlaveEditor<T> {

    private T slave;

    @Override
    public void setSlave(T approvedSlave) {
        slave = approvedSlave;
    }

    @Override
    public T getSlave() {
        return slave;
    }
}
