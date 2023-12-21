package kotlinx.coroutines;
public final class NonDisposableHandle implements DisposableHandle, ChildHandle {
    public static final NonDisposableHandle INSTANCE = new NonDisposableHandle();

    @Override
    public boolean childCancelled(Throwable th) {
        return false;
    }

    @Override
    public void dispose() {
    }

    public String toString() {
        return "NonDisposableHandle";
    }

    private NonDisposableHandle() {
    }
}