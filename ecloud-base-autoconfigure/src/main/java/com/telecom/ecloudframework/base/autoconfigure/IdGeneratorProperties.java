package com.telecom.ecloudframework.base.autoconfigure;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(
        prefix = "ecloud.id-generator"
)
public class IdGeneratorProperties {
    private long machine = 1L;
    private byte machineBits = 3;
    private byte sequenceBits = 15;
    private byte timeSequence = 45;

    public IdGeneratorProperties() {
    }

    public long getMachine() {
        return this.machine;
    }

    public void setMachine(long machine) {
        this.machine = machine;
    }

    public byte getMachineBits() {
        return this.machineBits;
    }

    public void setMachineBits(byte machineBits) {
        this.machineBits = machineBits;
    }

    public byte getSequenceBits() {
        return this.sequenceBits;
    }

    public void setSequenceBits(byte sequenceBits) {
        this.sequenceBits = sequenceBits;
    }

    public byte getTimeSequence() {
        return this.timeSequence;
    }

    public void setTimeSequence(byte timeSequence) {
        this.timeSequence = timeSequence;
    }
}

