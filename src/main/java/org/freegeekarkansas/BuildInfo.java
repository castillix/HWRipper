package org.freegeekarkansas;

import java.util.List;

public class BuildInfo {
    public String description = "no description"; //add input for description
    public String sn = "no s/n"; //add input for serial number
    public String screenSize = "N/A"; //add input for screen size
    public String batteryHealth = "N/A"; //add input for battery health
    public String batteryLife = "N/A"; //add input for battery use time
    public String hardwareNotes = "no hardwareNotes"; //add input for hardware notes, i.e. Discounts for battery health, screen condition, etc, defaut to "Nothing to note"
    public String softwareNotes = "no softwareNotes"; //add input for software notes, default to "Nothing to note"
    public String buildDate = "no buildDate"; //add input for build date, maybe just call the current date?
    public String builder = "no builder"; //add input for who built the machine
    public String discounts; // add input for discounts applied to the build, (i.e. bad battery discount, bad screen) default to "No discounts applied"

    public CPU cpu;

    public double ram;
    public String ramType;

    public List<Double> diskSizes;
    public List<Integer> diskTypes;

    public String gpu;

    public String os;

    public int price;
}

