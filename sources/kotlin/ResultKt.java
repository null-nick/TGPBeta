package kotlin;

import kotlin.Result;
import kotlin.jvm.internal.Intrinsics;
public final class ResultKt {
    public static final Object createFailure(Throwable exception) {
        Intrinsics.checkNotNullParameter(exception, "exception");
        return new Result.Failure(exception);
    }
}
