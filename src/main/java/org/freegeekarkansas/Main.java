package org.freegeekarkansas;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.nio.file.Files;
import java.util.List;

public class Main {
    static void main() throws Exception {
//        SystemInfo si = new SystemInfo();
//        OperatingSystem os = si.getOperatingSystem();
//        HardwareAbstractionLayer hw = si.getHardware();
//        CentralProcessor cpu = hw.getProcessor();
//        List<GraphicsCard> gpus = hw.getGraphicsCards();
//        GlobalMemory mem = hw.getMemory();
//        List<HWDiskStore> disks = hw.getDiskStores();
//
//        BuildInfo build = new BuildInfo();
//
//        PassmarkInfo pmi = new PassmarkInfo(cpu);
        PassmarkInfo.loadPassmarkInfo();
    }

}
