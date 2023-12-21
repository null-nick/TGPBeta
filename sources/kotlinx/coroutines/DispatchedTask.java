package kotlinx.coroutines;

import kotlin.ExceptionsKt__ExceptionsKt;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.DispatchedContinuation;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;
import kotlinx.coroutines.scheduling.Task;
import kotlinx.coroutines.scheduling.TaskContext;
public abstract class DispatchedTask<T> extends Task {
    public int resumeMode;

    public void cancelCompletedResult$kotlinx_coroutines_core(Object obj, Throwable th) {
    }

    public abstract Continuation<T> getDelegate$kotlinx_coroutines_core();

    public <T> T getSuccessfulResult$kotlinx_coroutines_core(Object obj) {
        return obj;
    }

    public abstract Object takeState$kotlinx_coroutines_core();

    public DispatchedTask(int i) {
        this.resumeMode = i;
    }

    public final void handleFatalException(Throwable th, Throwable th2) {
        if (th == null && th2 == null) {
            return;
        }
        if (th != null && th2 != null) {
            ExceptionsKt__ExceptionsKt.addSuppressed(th, th2);
        }
        if (th == null) {
            th = th2;
        }
        Intrinsics.checkNotNull(th);
        CoroutineExceptionHandlerKt.handleCoroutineException(getDelegate$kotlinx_coroutines_core().getContext(), new CoroutinesInternalError("Fatal exception in coroutines machinery for " + this + ". Please read KDoc to 'handleFatalException' method and report this incident to maintainers", th));
    }

    public Throwable getExceptionalResult$kotlinx_coroutines_core(Object obj) {
        CompletedExceptionally completedExceptionally = obj instanceof CompletedExceptionally ? (CompletedExceptionally) obj : null;
        if (completedExceptionally == null) {
            return null;
        }
        return completedExceptionally.cause;
    }

    @Override
    public final void run() {
        Object m147constructorimpl;
        Object m147constructorimpl2;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(this.resumeMode != -1)) {
                throw new AssertionError();
            }
        }
        TaskContext taskContext = this.taskContext;
        try {
            DispatchedContinuation dispatchedContinuation = (DispatchedContinuation) getDelegate$kotlinx_coroutines_core();
            Continuation<T> continuation = dispatchedContinuation.continuation;
            Object obj = dispatchedContinuation.countOrElement;
            CoroutineContext context = continuation.getContext();
            Object updateThreadContext = ThreadContextKt.updateThreadContext(context, obj);
            UndispatchedCoroutine<?> updateUndispatchedCompletion = updateThreadContext != ThreadContextKt.NO_THREAD_ELEMENTS ? CoroutineContextKt.updateUndispatchedCompletion(continuation, context, updateThreadContext) : null;
            CoroutineContext context2 = continuation.getContext();
            Object takeState$kotlinx_coroutines_core = takeState$kotlinx_coroutines_core();
            Throwable exceptionalResult$kotlinx_coroutines_core = getExceptionalResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core);
            Job job = (exceptionalResult$kotlinx_coroutines_core == null && DispatchedTaskKt.isCancellableMode(this.resumeMode)) ? (Job) context2.get(Job.Key) : null;
            if (job != null && !job.isActive()) {
                Throwable cancellationException = job.getCancellationException();
                cancelCompletedResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core, cancellationException);
                Result.Companion companion = Result.Companion;
                if (DebugKt.getRECOVER_STACK_TRACES() && (continuation instanceof CoroutineStackFrame)) {
                    cancellationException = StackTraceRecoveryKt.access$recoverFromStackFrame(cancellationException, (CoroutineStackFrame) continuation);
                }
                continuation.resumeWith(Result.m147constructorimpl(ResultKt.createFailure(cancellationException)));
            } else if (exceptionalResult$kotlinx_coroutines_core != null) {
                Result.Companion companion2 = Result.Companion;
                continuation.resumeWith(Result.m147constructorimpl(ResultKt.createFailure(exceptionalResult$kotlinx_coroutines_core)));
            } else {
                T successfulResult$kotlinx_coroutines_core = getSuccessfulResult$kotlinx_coroutines_core(takeState$kotlinx_coroutines_core);
                Result.Companion companion3 = Result.Companion;
                continuation.resumeWith(Result.m147constructorimpl(successfulResult$kotlinx_coroutines_core));
            }
            Unit unit = Unit.INSTANCE;
            if (updateUndispatchedCompletion == null || updateUndispatchedCompletion.clearThreadContext()) {
                ThreadContextKt.restoreThreadContext(context, updateThreadContext);
            }
            try {
                Result.Companion companion4 = Result.Companion;
                taskContext.afterTask();
                m147constructorimpl2 = Result.m147constructorimpl(unit);
            } catch (Throwable th) {
                Result.Companion companion5 = Result.Companion;
                m147constructorimpl2 = Result.m147constructorimpl(ResultKt.createFailure(th));
            }
            handleFatalException(null, Result.m148exceptionOrNullimpl(m147constructorimpl2));
        } catch (Throwable th2) {
            try {
                Result.Companion companion6 = Result.Companion;
                taskContext.afterTask();
                m147constructorimpl = Result.m147constructorimpl(Unit.INSTANCE);
            } catch (Throwable th3) {
                Result.Companion companion7 = Result.Companion;
                m147constructorimpl = Result.m147constructorimpl(ResultKt.createFailure(th3));
            }
            handleFatalException(th2, Result.m148exceptionOrNullimpl(m147constructorimpl));
        }
    }
}