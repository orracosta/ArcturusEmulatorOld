package com.habboproject.server.utilities;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import org.apache.log4j.Logger;


public class CometRuntime {
    private static final Logger log = Logger.getLogger(CometRuntime.class.getName());

    public static final String operatingSystem = System.getProperty("os.name");
    public static final String operatingSystemArchitecture = System.getProperty("os.arch");

    public static int processId = 0;

    static {
        if (operatingSystem.contains("nix") || operatingSystem.contains("nux")) {
            processId = CLibrary.INSTANCE.getpid();
        } else if (operatingSystem.contains("Windows")) {
            processId = Kernel32.INSTANCE.GetCurrentProcessId();
        } else if (operatingSystem.contains("Mac")) {
            // TODO: Mac :P
            processId = /* processid for mac */ -1000;
        }

        if (processId < 1)
            log.warn("Failed to get process identifier - OS: " + operatingSystem + " (" + operatingSystemArchitecture + ")");
    }

    private interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary) Native.loadLibrary("c", CLibrary.class);

        int getpid();
    }
}
