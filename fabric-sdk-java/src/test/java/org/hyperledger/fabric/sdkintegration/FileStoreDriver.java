package org.hyperledger.fabric.sdkintegration;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.ChannelConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

/********************************************************************************************************

USE_CA=false
USE_TLS=false
CREATE_NEW_CHANNEL=false

ORG_NAME=peerOrg1
MSPID=Org1MSP
CHANNEL_NAME=foo
PEER_USER1_NAME=user1

CA_ADMIN_NAME=admin
CA_ADMIN_PASSWORD=adminpw

ORDERER_ORG_DN=example.com
PEER_ORG_DN=org1.example.com

PEER_ORG_BASE=C:/res/test/fixture/sdkintegration/e2e-2Orgs/channel/crypto-config/peerOrganizations
ORDERER_ORG_BASE=C:/res/test/fixture/sdkintegration/e2e-2Orgs/channel/crypto-config/ordererOrganizations

CONFIG_TX=C:/res/test/fixture/sdkintegration/e2e-2Orgs/channel/foo.tx
ENDORSEMENT_POLICY=C:/res/test/fixture/sdkintegration/chaincodeendorsementpolicy.yaml

CHAINCODE_BASE=C:/res/smartcc
CHAINCODE_PATH=example_cc
CHAINCODE_NAME=lvqinghao888_go
CHAINCODE_VERSION=1

PEER_ADMIN_CERT=users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem
PEER_ADMIN_KEY=users/Admin@org1.example.com/msp/keystore/6b32e59640c594cf633ad8c64b5958ef7e5ba2a205cfeefd44a9e982ce624d93_sk
PEER_USER1_CERT=users/User1@org1.example.com/msp/signcerts/User1@org1.example.com-cert.pem
PEER_USER1_KEY=users/User1@org1.example.com/msp/keystore/f3c01db816069a226654d66a023c2260695f71e19b322a6564dad3e32ccf063b_sk

CA_CERT=ca/ca.org1.example.com-cert.pem
CA_NODE=http://192.168.146.239:7054

PEER_NODES=peer0.org1.example.com@grpc://192.168.146.239:7051,peer1.org1.example.com@grpc://192.168.146.239:7056
EVENT_HUBS=peer0.org1.example.com@grpc://192.168.146.239:7053,peer1.org1.example.com@grpc://192.168.146.239:7058
ORDERER_NODES=orderer.example.com@grpc://192.168.146.239:7050

SAMPLE_STORE=C:/res/fabric-sample-store

********************************************************************************************************/


public class FileStoreDriver {
	//日志打印
	private final Log logger = LogFactory.getLog(FileStoreDriver.class);
	
	private String testTxID = null;
	private Collection<ProposalResponse> responses = null;
	private Collection<ProposalResponse> successful = new LinkedList<>();
	private Collection<ProposalResponse> failed = new LinkedList<>();
	
	private Properties cfg = new Properties();
	private SampleOrg peer_org;
	private SampleStore store;
	
	private boolean USE_CA=false;
	private boolean USE_TLS=false;
	private boolean CREATE_NEW_CHANNEL=false;
	
	private String ORG_NAME;
	private String MSPID;
	private String CHANNEL_NAME;
	
	private String PEER_USER1_NAME;
	private String PEER_USER1_CERT;
	private String PEER_USER1_KEY;
    private String PEER_ADMIN_CERT;
    private String PEER_ADMIN_KEY;	
	
	//Fabric-CA admin
    private  String CA_ADMIN_PASSWORD;
	private  String CA_ADMIN_NAME;
    private  String CA_CERT;
    
    private  String ORDERER_ORG_DN;
    private  String PEER_ORG_DN;

    private  String PEER_ORG_BASE;
    private  String ORDERER_ORG_BASE;
    
    private  String CONFIG_TX;
    private  String ENDORSEMENT_POLICY;
    
    private  String CHAINCODE_BASE;
    private  String CHAINCODE_PATH;
    private  String CHAINCODE_NAME;
    private  String CHAINCODE_VERSION;
    
    private  String SAMPLE_STORE;
    
    private  String PEER_NODES;
    private  String EVENT_HUBS;
    private  String ORDERER_NODES;
    private  String CA_NODE;
    
    private  boolean runningFabricCATLS = USE_TLS;
    private  boolean runningFabricTLS = USE_TLS;
    
    private HFClient hf_client;
    private Channel hf_channel;
    private ChaincodeID hf_cc_id;
    
    private void getCfgFromPropertiesFile(String filePath) {
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(filePath));
			cfg = new Properties();
			cfg.load(in);
			
			USE_CA = ((String) cfg.get("USE_CA")).equalsIgnoreCase("true");
			USE_TLS = ((String) cfg.get("USE_TLS")).equalsIgnoreCase("true");
			CREATE_NEW_CHANNEL = ((String) cfg.get("CREATE_NEW_CHANNEL")).equalsIgnoreCase("true");
			
			ORG_NAME = cfg.getProperty("ORG_NAME");
			MSPID = cfg.getProperty("MSPID");
			CHANNEL_NAME = (String) cfg.get("CHANNEL_NAME");
			
			PEER_ORG_BASE = (String) cfg.get("PEER_ORG_BASE");
			ORDERER_ORG_BASE = (String) cfg.get("ORDERER_ORG_BASE");
			if(!PEER_ORG_BASE.endsWith("/")) {
				PEER_ORG_BASE += "/";
			}
			if(!ORDERER_ORG_BASE.endsWith("/")) {
				ORDERER_ORG_BASE +=  "/";
			}
			ORDERER_ORG_DN = (String) cfg.get("ORDERER_ORG_DN");
			PEER_ORG_DN = (String) cfg.get("PEER_ORG_DN");
			
			String base = PEER_ORG_BASE + PEER_ORG_DN + "/";
			PEER_USER1_NAME = base + (String) cfg.get("PEER_USER1_NAME");
			PEER_USER1_CERT = base + (String) cfg.get("PEER_USER1_CERT");
			PEER_USER1_KEY = base + (String) cfg.get("PEER_USER1_KEY");
			PEER_ADMIN_CERT = base + (String) cfg.get("PEER_ADMIN_CERT");
			PEER_ADMIN_KEY = base + (String) cfg.get("PEER_ADMIN_KEY");
			
			CA_CERT = base + (String) cfg.get("CA_CERT");
			CA_ADMIN_NAME = (String) cfg.get("CA_ADMIN_NAME");
			CA_ADMIN_PASSWORD = (String) cfg.get("CA_ADMIN_PASSWORD");
			
			CONFIG_TX = (String) cfg.get("CONFIG_TX");
			ENDORSEMENT_POLICY = (String) cfg.get("ENDORSEMENT_POLICY");
			
			CHAINCODE_BASE = (String) cfg.get("CHAINCODE_BASE");
			CHAINCODE_PATH = (String) cfg.get("CHAINCODE_PATH");
			CHAINCODE_NAME = (String) cfg.get("CHAINCODE_NAME");
			CHAINCODE_VERSION = (String) cfg.get("CHAINCODE_VERSION");
			
			CA_NODE = (String) cfg.get("CA_NODE");
			PEER_NODES = (String) cfg.get("PEER_NODES");
			EVENT_HUBS = (String) cfg.get("EVENT_HUBS");
			ORDERER_NODES = (String) cfg.get("ORDERER_NODES");
			
			SAMPLE_STORE = (String) cfg.get("SAMPLE_STORE");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    public FileStoreDriver(String config_file_path) {
    	try {
    		getCfgFromPropertiesFile(config_file_path); //"C:/res/fsdconf.properties" or "/opt/web/conf.properties"
    		peer_org = createOrg();
	    	hf_client = createHFClient();
	    	hf_cc_id = ChaincodeID.newBuilder()
	            		.setName(CHAINCODE_NAME)
	                    .setVersion(CHAINCODE_VERSION)
	                    .setPath(CHAINCODE_PATH).build();
	    	if(CREATE_NEW_CHANNEL) {
	    		hf_client.setUserContext(peer_org.getPeerAdmin());
	    		hf_channel = constructChannel(hf_client, peer_org, CHANNEL_NAME, CONFIG_TX);
	            installChainCode(hf_client, peer_org.getPeers(), hf_cc_id, true);
	            sendInstantiationProposal(hf_client, hf_channel, hf_cc_id, new String[] {});
	            sendGeneralTransaction(hf_client, hf_channel, peer_org);
	    	} else {
	    		hf_client.setUserContext(peer_org.getUser(PEER_USER1_NAME));
	    		hf_channel = constructChannel(hf_client, peer_org, CHANNEL_NAME, null);
	    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
	
    public SampleOrg createOrg() throws Exception {
    	store = getSampleStore(true, SAMPLE_STORE);
    	SampleOrg org = new SampleOrg(ORG_NAME, MSPID);
		org.setDomainName(PEER_ORG_DN);
        org.setCALocation(httpTLSify(CA_NODE));
        for (String eachone : PEER_NODES.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org.addPeerLocation(nl[0], grpcTLSify(nl[1]));
        }
        for (String eachone : EVENT_HUBS.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
        }
        for (String eachone : ORDERER_NODES.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
        }
        if(USE_CA) {
        	setOrgCAClient(org, CA_CERT);
        	setOrgCAAdmin(CA_ADMIN_NAME, CA_ADMIN_PASSWORD, org, store);
        } else {
        	org.setCAClient(null);
        	org.setAdmin(null);
        }
    	setOrgPeerAdmin(org, store, PEER_ADMIN_CERT, PEER_ADMIN_KEY);
    	addOrgUserFromFS(PEER_USER1_NAME, org, store, PEER_USER1_CERT, PEER_USER1_KEY);
    	return org;
    }
    
    
    //Get user certificate, key from file system, not from Fabric-CA
    private void addOrgUserFromFS(String userName, SampleOrg org, SampleStore store, String certPath, String keyPath) {
  		SampleUser user = store.getMember(userName, org.getName());
  		
  		File certFile = new File(certPath);
  		File keyFile = new File(keyPath);
  		
  		if (!user.isEnrolled()) {
  			EnrollmentFromFS fileEnrollment = null;
			try {
				fileEnrollment = EnrollmentFromFS.getEnrollmentFromFS(keyFile, certFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
      		user.setEnrollment(fileEnrollment);
      		user.setMspId(org.getMSPID());
  		}
  		org.addUser(user); //Remember user belongs to this Org
  	}
    
   
  	private void setOrgPeerAdmin(SampleOrg org, SampleStore store, String certPath, String keyPath) {
  		final String orgName = org.getName();
	    File certFile = new File(certPath);
	    File keyFile = new File(keyPath);
	    
	    logger.debug("\ncertFile: " + certFile.getAbsolutePath() + "\nkeyFile: " + keyFile.getAbsolutePath());
	   
	    SampleUser peerOrgAdmin = null;
		try {
			peerOrgAdmin = store.getMember(
				orgName + "Admin", 
				orgName, 
				org.getMSPID(),
				keyFile,
			    certFile
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    org.setPeerAdmin(peerOrgAdmin); 
  	}
  

  	private void setOrgCAAdmin(String userName, String password, SampleOrg org, SampleStore store) {
		HFCAClient ca = org.getCAClient();
        final String orgName = org.getName();
        //MyDebug.print(orgName);
        final String mspid = org.getMSPID();
        //MyDebug.print(mspid);
        SampleUser admin = store.getMember(userName, orgName);
        logger.debug("setOrgAdmin, admin.isEnrolled(): " + admin.isEnrolled());
        if (!admin.isEnrolled()) {  //Pre-registered admin only needs to be enrolled with Fabric caClient.
            try {
				admin.setEnrollment(ca.enroll(admin.getName(), password));
			} catch (EnrollmentException e) {
				e.printStackTrace();
			} catch (InvalidArgumentException e) {
				e.printStackTrace();
			}
            admin.setMspId(mspid);
        }
        org.setAdmin(admin); //The admin of this org
	}
    

    private void setOrgCAClient(SampleOrg org, String tlsCert) {
		Properties properties = getCAProperties(tlsCert);
		HFCAClient ca = null;
		try {
			ca = HFCAClient.createNewInstance(org.getCALocation(), properties);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if(ca != null) {
			ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite()); //new CryptoPrimitives()
			org.setCAClient(ca);
			org.setCAProperties(properties);
		}
	}
    
   
    private Properties getCAProperties(String tlsCert) {
		Properties properties = null;
		if (runningFabricCATLS) {
            File cf = new File(tlsCert);
            if (!cf.exists() || !cf.isFile()) {
                throw new RuntimeException("Missing cert file " + cf.getAbsolutePath());
            }
            properties = new Properties();
            
            properties.setProperty("pemFile", cf.getAbsolutePath());
            properties.setProperty("allowAllHostNames", "true");
        }		
		return properties;
	}
	
   
	private SampleStore getSampleStore(boolean delete, String storePath) {
		File sampleStoreFile = new File(storePath);
        if (sampleStoreFile.exists() && delete) {
            sampleStoreFile.delete();
            logger.debug("Deleted sampleStoreFile: " + sampleStoreFile.getAbsolutePath() + "\n");
        }
        SampleStore sampleStore = new SampleStore(sampleStoreFile);
        return sampleStore;
	}
	

	private HFClient createHFClient() {
		HFClient hf_client = HFClient.createNewInstance();
		try {
			hf_client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hf_client;
	}
	
	
	private BlockInfo returnedBlock = null;
	public byte[] getPayloadByTxID(String txID) throws Exception {
		//use latest testTxID as default
		if(txID == null) {
			txID = testTxID;
		}
		BlockInfo.EnvelopeInfo envelopeInfo = null;
		//look up in old returnedBlock
		if(returnedBlock != null) {
			for (BlockInfo.EnvelopeInfo info : returnedBlock.getEnvelopeInfos()) {
				if(info.getTransactionID().equals(txID)) {
					envelopeInfo = info;
					break;
				}
			}
		} 
		//TX is not in the old returnedBlock, query again to get new returnedBlock
		if(envelopeInfo == null) {
			returnedBlock = hf_channel.queryBlockByTransactionID(txID);
			for (BlockInfo.EnvelopeInfo info : returnedBlock.getEnvelopeInfos()) {
				if(info.getTransactionID().equals(txID)) {
					envelopeInfo = info;
					break;
				}
			}
		}
		byte[] payload = null;
		if(envelopeInfo != null && envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
			BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
			for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
				payload = transactionActionInfo.getProposalResponsePayload();
				//.println("&&&&&&&&&&&&&: " + new String(payload));
				return payload;
			}
		}
		return null;
	}

	
	public String getLateastTransactionID() {
		return testTxID;
	}
	
	
	private Object sendGeneralTransaction(HFClient client, Channel chain, SampleOrg sampleOrg) {
		Object object = null;
		try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            Collection<Orderer> orderers = chain.getOrderers();
            object = chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
               //waitOnFabric(0);
            	testTxID = transactionEvent.getTransactionID();
            	assertTrue(transactionEvent.isValid()); // must be valid to be here.
            	logger.debug("Finished instantiate transaction with id" + transactionEvent.getTransactionID());
            	return null;
            }).exceptionally(e -> {
                if (e instanceof TransactionEventException) {
                    BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
                    if (te != null) {
                        fail(format("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage()));
                    }
                }
                fail(format("Test failed with %s exception %s", e.getClass().getName(), e.getMessage()));
                return null;
            }).get(Integer.parseInt("100000"), TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with error : " + e.getMessage());
        }
		return object;
    }
	
	
	private void sendInstantiationProposal(HFClient client, Channel chain, ChaincodeID chainCodeID, String[] initData) throws Exception {
		InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(Integer.parseInt("120000"));
        instantiateProposalRequest.setChaincodeID(chainCodeID);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(initData);
        Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiateProposalRequest.setTransientMap(tm);
        //policy OR(Org1MSP.member, Org2MSP.member) meaning 1 signature from someone in either Org1 or Org2
        //See README.md Channelcode endorsement policies section for more details.
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File(ENDORSEMENT_POLICY));
        instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        responses = chain.sendInstantiationProposal(instantiateProposalRequest, chain.getPeers());
        distinguishSucessfulAndFailed(true, 0, "sendInstantiationProposal");
        //Got endorsed responses in a collection named successful here
	}
	
	
	public void runTransactionWithBytes(HFClient client, Channel chain, ChaincodeID chainCodeID, ArrayList<byte[]> txData) throws Exception  {
		//client.setUserContext(org.getUser(TEST_USER1_NAME));
		//Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chainCodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgBytes(txData);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
        transactionProposalRequest.setTransientMap(tm2);
        //Channelcode endorsement policies, some default ????????????????????????????????????????????
        responses = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
        distinguishSucessfulAndFailed(true, 0, "runTransactionWithBytes");
        sendGeneralTransaction(hf_client, hf_channel, peer_org);
	}
	

	public void runTransaction(HFClient client, Channel chain, ChaincodeID chainCodeID, String[] transData) throws Exception  {
		//client.setUserContext(org.getUser(TEST_USER1_NAME));
		//Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chainCodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(transData);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
        transactionProposalRequest.setTransientMap(tm2);
        //Channelcode endorsement policies, some default ????????????????????????????????????????????
        responses = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
        distinguishSucessfulAndFailed(true, 0, "runTransaction");
        sendGeneralTransaction(hf_client, hf_channel, peer_org);
	}
	
	
	private String endorsingPeerName = null;
	public Collection<Peer> getEndorsingPeers() {
		Collection<Peer> peerSet = new LinkedList<Peer>();
		Peer peer = null;
		//Peer peer = endorsingPeer;
		if(endorsingPeerName == null) {
			peer = hf_channel.getPeers().iterator().next(); //Just choose one randomly.
		} else {
			for(Peer p : hf_channel.getPeers()) {
				if(p.getName().equals(endorsingPeerName)) {
					peer = p;
					break; //Just choose the one with the name of endorsingPeerName
				}
			}
		}
		//System.out.println("Endorsing Peer Name :" + peer.getName());
		peerSet.add(peer);
		return peerSet;
	}
	
	/**
	 * 将文件写到Block
	 * @param txArgs
	 * @param fileBlock
	 * @return
	 * @throws Exception
	 */
    public Object runTransactionWithBytes(String[] txArgs, byte[] fileBlock) throws Exception  {
        TransactionProposalRequest transactionProposalRequest = hf_client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(hf_cc_id);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(txArgs);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("fileblock", fileBlock);  //This should be returned in payload filed.
        transactionProposalRequest.setTransientMap(tm2);
        //endorsement policies, some default ???
        responses = hf_channel.sendTransactionProposal(transactionProposalRequest, getEndorsingPeers());
        distinguishSucessfulAndFailed(true, 0, "runTransactionWithBytes");
        Object object = sendGeneralTransaction(hf_client, hf_channel, peer_org);
        return object;
	}
	
    
    public Object runTransaction(String[] transData) throws Exception  {
    	logger.info("transaction.....");
		//hf_client.setUserContext(org1.getUser(TEST_USER1_NAME));
		//Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = hf_client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(hf_cc_id);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(transData);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
        transactionProposalRequest.setTransientMap(tm2);
        //Channelcode endorsement policies, some default ????????????????????????????????????????????
        responses = hf_channel.sendTransactionProposal(transactionProposalRequest, getEndorsingPeers());
        distinguishSucessfulAndFailed(true, 0, "runTransaction");
        Object object = sendGeneralTransaction(hf_client, hf_channel, peer_org);
        logger.info("transaction completed.....");
        return object;
	}
    
    
    private int len = 0;
    public void queryByChainCode(String key) {
    	queryByChainCode(hf_client, hf_channel, hf_cc_id,  key);
    	//System.out.println("payload len: " + len);
    }
    
    
    private void queryByChainCode(HFClient client, Channel chain, ChaincodeID chainCodeID, String key) {
		try {
            //System.out.println("Now query chain code for the value of key: " + key);
            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            request.setFcn("invoke");
            request.setArgs(new String[] {"query", key});
            request.setChaincodeID(chainCodeID);
            //Send Query Proposal to all peers
            Collection<ProposalResponse> responses = chain.queryByChaincode(request, chain.getPeers());
            for (ProposalResponse response : responses) {
                if (!response.isVerified() || response.getStatus() != ProposalResponse.Status.SUCCESS) {
                    fail(
                    	"Failed query proposal from peer " + response.getPeer().getName() + 
                    	" status: " + response.getStatus() +
                    	". Messages: " + response.getMessage() + 
                    	". Was verified : " + response.isVerified()
                    );
                } else {
                    String payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    //System.out.println("payload len: " + payload.length());
                    len = payload.length();
                    //System.out.println(
//                        format(
//                        	"Query payload of key %s from peer %s returned %s", 
//                        	key,
//                        	response.getPeer().getName(), 
//                        	payload
//                        )
//                    );
                }
            }
        } catch (Exception e) {
        	//System.out.println("Caught exception while running query");
            e.printStackTrace();
            fail("Failed during chaincode query with error : " + e.getMessage());
        }	
	}
    
    
	private void distinguishSucessfulAndFailed(boolean verifyResponse, int acceptableFailedSize, String callerInfo) throws Exception{
		//System.out.println("distinguishSucessfulAndFailed, callerInfo: " + callerInfo);
		successful.clear();
		failed.clear();
		for (ProposalResponse response : responses) {
			boolean condition = (response.getStatus() == ProposalResponse.Status.SUCCESS);
			if (verifyResponse) {
				condition &= response.isVerified();
			}
			if (condition) {
				successful.add(response);
				logger.debug(format("Succesful proposal response Txid: %s from peer %s",
						response.getTransactionID(), response.getPeer().getName())
				);
			} else {
				failed.add(response);
			}
			
	        byte[] resultBytes = response.getChaincodeActionResponsePayload(); //This is the data returned by the chaincode.
	        //System.out.println("########resultBytes.length: " + resultBytes.length);
	        String resultAsString = null;
	        if (resultBytes != null) {
	            resultAsString = new String(resultBytes, "UTF-8");
	        }
	        //System.out.println("########resultAsString: " + resultAsString);
	        //assertEquals(200, response.getChaincodeActionResponseStatus()); //Channelcode's status.
	        //TxReadWriteSetInfo readWriteSetInfo = response.getChaincodeActionResponseReadWriteSetInfo();
	        //assertNotNull(readWriteSetInfo);
	        //assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);
	        //ChaincodeID cid = response.getChaincodeID();
	        //assertNotNull(cid);
	        //assertEquals(CHAIN_CODE_PATH, cid.getPath());
	        //assertEquals(CHAIN_CODE_NAME, cid.getName());
	        //assertEquals(CHAIN_CODE_VERSION, cid.getVersion());    
		}
		logger.debug(
			format(
				"Received %d proposal responses. Successful&Verified: %d . Failed: %d", 
				responses.size(), successful.size(), failed.size()
			)
		);
		if (failed.size() > acceptableFailedSize) {
			ProposalResponse first = failed.iterator().next();
			fail("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with "
					+ first.getMessage() + ". Was verified:" + first.isVerified());
		}
	}
	
	
	private void installChainCode(HFClient client, Set<Peer> peers, ChaincodeID chainCodeID, boolean directory) throws Exception {
		if(chainCodeID == null) {
			chainCodeID = ChaincodeID.newBuilder().
					setName(CHAINCODE_NAME).
					setVersion(CHAINCODE_VERSION).
					setPath(CHAINCODE_PATH).build();
		}
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chainCodeID);
        if (directory) {//install from file system directory.
        	installProposalRequest.setChaincodeSourceLocation(new File(CHAINCODE_BASE));
        } else {
            throw new Exception("Current version only support installing ChainCode from a directory");
        }
        installProposalRequest.setChaincodeVersion(CHAINCODE_VERSION);
        // only a client from the same org as the peer can issue an install request
        responses = client.sendInstallProposal(installProposalRequest, peers);
        distinguishSucessfulAndFailed(false, 0, "installChainCode"); //It seems must be false, or it will be failed 
	}
	

	private void addOrderersToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg, String exclude) throws Exception{
		for (String orderName : sampleOrg.getOrdererNames()) {
			if(!orderName.equals(exclude)) {
				Orderer anOrderer = client.newOrderer(
		        		orderName, 
		        		sampleOrg.getOrdererLocation(orderName),
		        		getEndPointProperties(
		                    ORDERER_ORG_BASE + ORDERER_ORG_DN + "/orderers/" + orderName + "/tls/server.crt",
		                    orderName,
		                    false
		                )
				);
				newChannel.addOrderer(anOrderer);
			}
        }
	}
	

	private void addEventHubsToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg) throws Exception{
		for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(
            	eventHubName, 
            	sampleOrg.getEventHubLocation(eventHubName),
            	getEndPointProperties(
                	PEER_ORG_BASE + PEER_ORG_DN + "/peers/" + eventHubName + "/tls/server.crt",
                	eventHubName,
                	false
                )
            );
            newChannel.addEventHub(eventHub);
        }
	}
	

	private void addPeersToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg, boolean join) throws Exception{
		for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Properties peerProperties = getEndPointProperties(
            	PEER_ORG_BASE + PEER_ORG_DN + "/peers/" + peerName + "/tls/server.crt",
            	peerName,
            	false
            );
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            //Example of setting specific options on grpc's ManagedChannelBuilder
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            
            if(join) {
            	newChannel.joinPeer(peer);
            } else {
            	newChannel.addPeer(peer);
            }
            sampleOrg.addPeer(peer);
            //MyDebug.printProperties("Peer propeties", peer.getProperties());
            logger.debug("Peer propeties:" + peer.getProperties());
        }
	}
	

    private Properties getEndPointProperties(String certPath, String fullDomainName, boolean trust) {
    	File cert = new File(certPath);
        if(!cert.exists()){
            throw new RuntimeException("Missing cert file for: " + certPath);
        }
        Properties ret = new Properties();
        ret.setProperty("pemFile", cert.getAbsolutePath());
        if(trust) { //testing environment only NOT FOR PRODUCTION!
        	 ret.setProperty("trustServerCertificate", "true"); 
        }
        ret.setProperty("hostnameOverride", fullDomainName);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");
        return ret;
    }
    
	
	private Channel constructChannel(HFClient client, SampleOrg sampleOrg, String channelName, String configTx) throws Exception {
		Channel newChannel = null;
		String orderName = null;
		boolean join = false;
		if(configTx != null) {////////Create a new channel//////////////////////////////////////////////
			join = true;
			orderName = sampleOrg.getOrdererNames().iterator().next();
			ChannelConfiguration chainConfiguration = new ChannelConfiguration(new File(configTx));
	        Orderer anOrderer =  client.newOrderer(
	        		orderName, 
	        		sampleOrg.getOrdererLocation(orderName),
	        		getEndPointProperties(
	        			ORDERER_ORG_BASE + ORDERER_ORG_DN + "/orderers/" + orderName + "/tls/server.crt",
	        			orderName,
	        			false
	        		)
	        );
	        logger.debug("orderer propeties:" + anOrderer.getProperties());
	        newChannel = client.newChannel(
	        	channelName, 
	        	anOrderer, 
	        	chainConfiguration, 
	        	client.getChannelConfigurationSignature(
	        		chainConfiguration, 
	        		sampleOrg.getPeerAdmin()
	        	)
	        );
		} else {////////Construct an channel object for existed channel/////////////////////////////////
			newChannel = client.newChannel(channelName);
		}
	    addPeersToChannel(client, newChannel, sampleOrg, join);
	    addEventHubsToChannel(client, newChannel, sampleOrg);
	    addOrderersToChannel(client, newChannel, sampleOrg, orderName);
	    newChannel.initialize();
		
        return newChannel;
    }
	
	
    private String httpTLSify(String location) {
        location = location.trim();
        return runningFabricCATLS ? location.replaceFirst("^http://", "https://") : location;
    }
    
   
	private String grpcTLSify(String location) {
        location = location.trim();
        Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }
        return runningFabricTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;
    }
	
	
	
}
	









