package org.freegeekarkansas;

import java.util.List;

public class BuildInfo {
    public String description; //add input for description
    public String sn; //add input for serial number
    public String screensize; //add input for screen size
    public String batteryhealth; //add input for battery health
    public String batterylife; //add input for battery use time
    public String hardwarenotes; //add input for hardware notes, i.e. Discounts for battery health, screen condition, etc, defaut to "Nothing to note"
    public String softwarenotes; //add input for software notes, default to "Nothing to note"
    public String builddate; //add input for build date, maybe just call the current date?
    public String builder; //add input for who built the machine
    public String discounts // add input for discounts applied to the build, (i.e. bad battery discount, bad screen) default to "No discounts applied"

    public CPU cpu;

    public double ram;
    public String ramType;

    public List<Double> diskSizes;
    public List<Integer> diskTypes;

    public String gpu;

    public String os;

    public int price;
}

