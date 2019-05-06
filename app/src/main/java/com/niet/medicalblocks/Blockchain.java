package com.niet.medicalblocks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
	private List<Block> chain;
	
	public Blockchain() {
		chain = new ArrayList<Block>();
		chain.clear();
		chain.add(generateGenesis());
	}
	
	private Block generateGenesis() {
		Block genesis = new Block("", "", " ", "", "", "", "", "");
		genesis.setPreviousHash(null);
		genesis.computeHash();
		return genesis;
	}
	
	public void addBlock(Block blk) {
		Block newBlock = blk;
		newBlock.setPreviousHash(chain.get(chain.size()-1).getHash());
		newBlock.computeHash();
		this.chain.add(newBlock);
	}
	public void addPreBlocks(Block blk){
		/*Block newBlock = blk;
		newBlock.computeHash();*/
		this.chain.add(blk);
	}
	
	public void displayChain() {

		for(int i=0; i<chain.size(); i++) {
			Log.d("TAG" , "Chain " + i + chain.get(i).getaID() + chain.get(i).getHash() + chain.get(i).getPreviousHash());
			Log.d("TAG" , "");
		}
		
	}
	public  void storeChain (String userType, String uid){
		Log.d("TAG", " Size of the new block" + chain.size());
		Log.d("TAG", " Uid + user type" + uid + userType);
		FirebaseFirestore db = FirebaseFirestore.getInstance();


		for (int i = 0 ; i<chain.size(); i++){
			Map<String, Object> data = new HashMap<>();
			data.put("aID", chain.get(i).getaID());
			data.put("pName", chain.get(i).getpName());
			data.put("pUID", chain.get(i).getpUID());
			data.put("hash", chain.get(i).getHash());
			data.put("previousHash", chain.get(i).getPreviousHash());
			data.put("data", chain.get(i).getData());
			data.put("dName", chain.get(i).getdName());
			data.put("dUID", chain.get(i).getdUID());
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmsssss");
			String doc = df.format(c.getTime()) + i;
			Log.d("TAG", "Doc ID" + doc);
			db.collection(userType).document(uid).collection("BlockChain").document(doc).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
					if (task.isSuccessful()){
						Log.d("TAG", "Chain Updated to db");
					}
				}
			});
		}
	}
	
	public Block getLatestBlock() {
		return this.chain.get(chain.size()-1);
	}
	
	public String isValid() {
		
		for(int i= 1; i< chain.size(); i++) {
			String hash = chain.get(i).computeHashForValid();
			Log.d("TAG1" , "Hash " + hash);
			if( !(chain.get(i).getHash().equals(chain.get(i).computeHashForValid()))   ) {

				return "No";
			}
			
			if(  !(chain.get(i).getPreviousHash().equals(chain.get(i-1).computeHashForValid()))  ) {

				return "No";
			}
			if (!(chain.get(i).getPreviousHash().equals(chain.get(i-1).computeHashForValid()))){
				return "No";
			}
			if (!(chain.get(i).getPreviousHash().equals(chain.get(i-1).getHash()))){
				return "No";
			}
		}
		
		return "Yes";
		
	}
	
	
}
