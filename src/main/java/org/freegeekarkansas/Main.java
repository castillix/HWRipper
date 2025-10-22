package org.freegeekarkansas;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main(String[] args) throws Exception {
//        PassmarkInfo.loadPassmarkInfo();
//        ArrayList<CPU> test = DBManager.getCPUs();
//        System.out.println(test.size());
        if(args.length > 0 && args[0].equals("-i")) {
            PassmarkInfo.loadPassmarkInfo();
        } else {
            BuildInfo build = HardwareGrabber.getHardware();
            System.out.println("Calculated price: $" + HardwareGrabber.getPrice(build));
            HttpManager.servePage(build);
        }
    }

}
