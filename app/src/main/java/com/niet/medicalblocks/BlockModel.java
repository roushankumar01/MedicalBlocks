package com.niet.medicalblocks;

public class BlockModel {
    private String aID;
    private String pName;
    private String pUID;
    private String hash;
    private String previousHash;
    private String data;
    private String dName;
    private String dUID;

    public BlockModel(String aID, String pName, String pUID, String hash, String previousHash, String data, String dName, String dUID) {
        this.aID = aID;
        this.pName = pName;
        this.pUID = pUID;
        this.hash = hash;
        this.previousHash = previousHash;
        this.data = data;
        this.dName = dName;
        this.dUID = dUID;
    }

    public String getaID() {
        return aID;
    }

    public void setaID(String aID) {
        this.aID = aID;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpUID() {
        return pUID;
    }

    public void setpUID(String pUID) {
        this.pUID = pUID;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName;
    }

    public String getdUID() {
        return dUID;
    }

    public void setdUID(String dUID) {
        this.dUID = dUID;
    }
}
