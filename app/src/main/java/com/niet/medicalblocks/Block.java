package com.niet.medicalblocks;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Block {

	private String aID;
	private String pName;
	private String pUID;
	private String hash;
	private String previousHash;
	private String data;
	private String dName;
	private String dUID;

	public Block(String aID, String pName, String pUID, String hash, String previousHash, String data, String dName, String dUID) {
		this.aID = aID;
		this.pName = pName;
		this.pUID = pUID;
		this.hash = hash;
		this.previousHash = previousHash;
		this.data = data;
		this.dName = dName;
		this.dUID = dUID;
	}

	public Block(String aID, String pName, String pUID, String data, String dName, String dUID) {
		this.aID = aID;
		this.pName = pName;
		this.pUID = pUID;
		this.data = data;
		this.dName = dName;
		this.dUID = dUID;
		this.hash = computeHash();
	}

	public String computeHash() {
		
		String dataToHash = "" + this.aID + this.pName + this.pUID + this.data + this.dName + this.dUID;
		MessageDigest digest;
		String encoded = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		this.hash = encoded;
		Log.d("TAG1" ,"Hash Computed " + encoded);
		return encoded;
		
	}
	public String computeHashForValid() {

		String dataToHash = "" + this.aID + this.pName + this.pUID + this.data + this.dName + this.dUID;
		MessageDigest digest;
		String encoded = null;

		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		Log.d("TAG1" ,"Hash Computed for Validation " + encoded);
		return encoded;

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
