package com.hule.dashi.websocket;

/**
 * <b>Package:</b> com.hule.dashi.websocket <br>
 * <b>Create Date:</b> 2019/1/17  11:00 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> Log打印包装，具体打印实例外部决定，并且需要实现LogDelegate接口 <br>
 */
public class Logger {
    private static LogDelegate sDelegate = null;
    private static boolean sEnable = false;

    /**
     * 外部打印对象实现该接口，转调打印
     */
    public interface LogDelegate {
        void e(final String tag, final String msg, final Object... obj);

        void w(final String tag, final String msg, final Object... obj);

        void i(final String tag, final String msg, final Object... obj);

        void d(final String tag, final String msg, final Object... obj);

        void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj);
    }

    public static void setDelegate(LogDelegate delegate) {
        sDelegate = delegate;
    }

    public static void setLogPrintEnable(boolean enable) {
        sEnable = enable;
    }

    public static void e(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null && sEnable) {
            sDelegate.e(tag, msg, obj);
        }
    }

    public static void w(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null && sEnable) {
            sDelegate.w(tag, msg, obj);
        }
    }

    public static void i(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null && sEnable) {
            sDelegate.i(tag, msg, obj);
        }
    }

    public static void d(final String tag, final String msg, final Object... obj) {
        if (sDelegate != null && sEnable) {
            sDelegate.d(tag, msg, obj);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (sDelegate != null && sEnable) {
            sDelegate.printErrStackTrace(tag, tr, format, obj);
        }
    }
}