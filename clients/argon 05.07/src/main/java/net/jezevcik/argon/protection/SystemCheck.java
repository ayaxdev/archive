package net.jezevcik.argon.protection;

import net.jezevcik.processes.ProcessInformation;
import net.jezevcik.processes.Processes;

import java.io.File;
import java.nio.file.Files;

public class SystemCheck {

    private static final String[] VM_FILES = new String[] {
            "C:\\windows\\system32\\vmGuestLib.dll",
            "C:\\windows\\system32\\vm3dgl.dll",
            "C:\\windows\\system32\\vboxhook.dll",
            "C:\\windows\\system32\\vboxmrxnp.dll",
            "C:\\windows\\system32\\vmsrvc.dll",
            "C:\\windows\\system32\\drivers\\vmsrvc.sys"
    };

    private static final String[] BLOCKED_PROCESSES = new String[] {
            "vmtoolsd.exe",
            "vmwaretray.exe",
            "vmwareuser.exe",
            "fakenet.exe",
            "dumpcap.exe",
            "httpdebuggerui.exe",
            "wireshark.exe",
            "fiddler.exe",
            "vboxservice.exe",
            "df5serv.exe",
            "vboxtray.exe",
            "vmwaretray.exe",
            "ida64.exe",
            "ollydbg.exe",
            "pestudio.exe",
            "vgauthservice.exe",
            "vmacthlp.exe",
            "x96dbg.exe",
            "x32dbg.exe",
            "prl_cc.exe",
            "prl_tools.exe",
            "xenservice.exe",
            "qemu-ga.exe",
            "joeboxcontrol.exe",
            "ksdumperclient.exe",
            "ksdumper.exe",
            "joeboxserver.exe"
    };

    public static void run() {
        try {
            for (ProcessInformation processInformation : Processes.getProcesses()) {
                for (String name : BLOCKED_PROCESSES) {
                    if (processInformation.name().toLowerCase().equals(name))
                        throw new RuntimeException();
                }
            }
        } catch (Exception e) {
            System.exit(0);
        }

        for (String path : VM_FILES) {
            final File file = new File(path);

            if (Files.exists(file.toPath())) {
                System.exit(0);
            }
        }
    }

}
