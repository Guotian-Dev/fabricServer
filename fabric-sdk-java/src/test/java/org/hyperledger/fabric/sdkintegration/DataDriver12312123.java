package org.hyperledger.fabric.sdkintegration;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.BlockInfo.EnvelopeType.TRANSACTION_ENVELOPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.protos.ledger.rwset.kvrwset.KvRwset;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
import org.hyperledger.fabric.sdk.BlockInfo.EnvelopeInfo;
import org.hyperledger.fabric.sdk.BlockchainInfo;
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
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.InvalidProtocolBufferRuntimeException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;

/**
 * 
 * @author PC
 * DataDriver
 */
public class DataDriver12312123 {
	//日志打印
	private static final Log logger = LogFactory.getLog(DataDriver12312123.class);
	
	private static String testTxID = null;
	private static Collection<ProposalResponse> responses = null;
	private static Collection<ProposalResponse> successful = new LinkedList<>();
	private static Collection<ProposalResponse> failed = new LinkedList<>();
	
	private static Properties pro = new Properties();
	//SampleOrg new SampleOrg("peerOrg1", "Org1MSP")
	private static SampleOrg org1;
	//= getSampleStore(true, SAMPLE_STORE);
	private static SampleStore store;
	
	private static String NAME;
	private static String MSPID;
	
	//orgInit初始化需要的一些属性
	private static final String FOO_CHAIN_NAME;
	
	private static final String TEST_ADMIN_NAME;
    private static final String TEST_USER1_NAME;
    private static final String TEST_ADMIN_PASSWORD;
    
    private static final String ORDERER_DN;
    private static final String PEER_ORG1_DN;

    private static final String CHANNEL_BASE;
    private static final String CRYPTO_BASE;
    private static final String PEER_ORG_BASE;
    private static final String ORDERER_ORG_BASE;
    
    private static final String FOO_CONFIGTX;
    private static final String ENDORSEMENT_POLICY;
    
    private static final String CHAIN_CODE_NAME;
    private static final String CHAIN_CODE_PATH; 
    private static final String CHAIN_CODE_VERSION;
    
    private static final String SAMPLE_STORE;
    
    private static final String PEER_ORG1_USER_ADMIN_CERT;
    private static final String PEER_ORG1_ADMIN_KEY;	
    private static final String PEER_ORG1_CA_CERT ;
    
    private static String peers;
    private static String eventHubs;
    private static String orderers;
    private static String location;
    
    private static HFClient client;
    private static Channel chain;
    
    private static String CHAINCODE_SOURCE_LOCATION;
    private static String TARGZ_INPUT_STREAM_PATH1;
    private static String TARGZ_INPUT_STREAM_PATH2;
    
    private static final boolean runningTLS = false;
    private static final boolean runningFabricCATLS = runningTLS;
    private static final boolean runningFabricTLS = runningTLS;
    
    private ChaincodeID initChaincodeID;
    
    // private static HFClient hf_client = null;
    
    //从properties文件中加载属性值
    static{
    	//资源放到了src/test/resources中
//        InputStream in = DataDriver.class.getResourceAsStream("/dataconf.properties");
    	
    	//从磁盘读取配置文件
    	InputStream in = null;
		try {
//			in = new BufferedInputStream (new FileInputStream("C:/res/conf.properties"));
			in = new BufferedInputStream (new FileInputStream("/opt/web/conf.properties"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
    	
        try{
            pro.load(in);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        FOO_CHAIN_NAME = (String) pro.get("FOO_CHAIN_NAME");
        
        TEST_ADMIN_NAME = (String) pro.get("TEST_ADMIN_NAME");
        TEST_USER1_NAME = (String) pro.get("TEST_USER1_NAME");
        TEST_ADMIN_PASSWORD = (String) pro.get("TEST_ADMIN_PASSWORD");
        ORDERER_DN = (String) pro.get("ORDERER_DN");
        PEER_ORG1_DN = (String) pro.get("PEER_ORG1_DN");
        CHANNEL_BASE = (String) pro.get("CHANNEL_BASE");
        CRYPTO_BASE = (String) pro.get("CRYPTO_BASE");
        PEER_ORG_BASE = (String) pro.get("PEER_ORG_BASE");
        ORDERER_ORG_BASE = (String) pro.get("ORDERER_ORG_BASE");
        FOO_CONFIGTX = (String) pro.get("FOO_CONFIGTX");
        ENDORSEMENT_POLICY = (String) pro.get("ENDORSEMENT_POLICY");
        CHAIN_CODE_NAME = (String) pro.get("CHAIN_CODE_NAME");
        CHAIN_CODE_PATH = (String) pro.get("CHAIN_CODE_PATH");
        CHAIN_CODE_VERSION = (String) pro.get("CHAIN_CODE_VERSION");
        SAMPLE_STORE = (String) pro.get("SAMPLE_STORE");
        PEER_ORG1_USER_ADMIN_CERT = (String) pro.get("PEER_ORG1_USER_ADMIN_CERT");
        PEER_ORG1_ADMIN_KEY = (String) pro.get("PEER_ORG1_ADMIN_KEY");
        PEER_ORG1_CA_CERT = (String) pro.get("PEER_ORG1_CA_CERT");
        //2017年7月27日15:08:58
        CHAINCODE_SOURCE_LOCATION = (String) pro.get("CHAINCODE_SOURCE_LOCATION");
        TARGZ_INPUT_STREAM_PATH1 = (String) pro.get("TARGZ_INPUT_STREAM_PATH1");
        TARGZ_INPUT_STREAM_PATH2 = (String) pro.get("TARGZ_INPUT_STREAM_PATH2");
        //2017年7月27日15:09:04
        
        NAME = pro.getProperty("NAME");
        MSPID = pro.getProperty("MSPID");
        
        peers = (String) pro.get("peers");
        eventHubs = (String) pro.get("eventHubs");
        orderers = (String) pro.get("orderers");
        location = (String) pro.get("location");
    }
    
    public DataDriver12312123(boolean newChannel) {
    	//初始化org1和store
    	org1 = new SampleOrg(NAME, MSPID);
    	store = getSampleStore(true, SAMPLE_STORE);
    	
    	initOrg1();
    	setOrgCAClient(org1,PEER_ORG1_CA_CERT);
//    	setOrgAdmin(TEST_ADMIN_NAME, TEST_ADMIN_PASSWORD, org1, store);
    	addOrgUser2(TEST_ADMIN_NAME,org1, store,PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
    	
    	setOrgPeerAdmin(org1, store, PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
    	//String userName, SampleOrg org, SampleStore store, String certPath, String keyPath
    	addOrgUser2(TEST_USER1_NAME,org1, store,PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
    	
//		Map<String, String> map = new HashMap<String, String>();
//		map.put("a", "100");
//		map.put("b", "200");
    	
    	//chainCode初始化
    	client = DataDriver12312123.getHFClient();
    	if(newChannel) {
    		//执行newChannel
    		chain = DataDriver12312123.getChannel(newChannel, client);
    		//init chaincodeId
    		initChaincodeID = DataDriver12312123.initChaincodeID(org1, chain, client);
    	} else {
    		chain = DataDriver12312123.getChannel(newChannel, client);
    	}
    	
    	logger.info("init completed");
    	
	}
    
    public Object runTransaction(String[] transData) throws Exception  {
        
    	logger.info("transaction.....");
    	
		client.setUserContext(org1.getUser(TEST_USER1_NAME));
		
		//Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(initChaincodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(transData);
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
        transactionProposalRequest.setTransientMap(tm2);
        //Channelcode endorsement policies, some default ????????????????????????????????????????????
        responses = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
        
        collectResponses(true);
        Collection<Orderer> orderers = chain.getOrderers();
        
//        ProposalResponse resp = responses.iterator().next();
//        byte[] resultBytes = resp.getChaincodeActionResponsePayload(); //This is the data returned by the chaincode.
//        String resultAsString = null;
//        if (resultBytes != null) {
//            resultAsString = new String(resultBytes, "UTF-8");
//        }
//        assertEquals(":)", resultAsString);
//        assertEquals(200, resp.getChaincodeActionResponseStatus()); //Channelcode's status.
//        TxReadWriteSetInfo readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
//        //See blockwaler below how to transverse this
//        assertNotNull(readWriteSetInfo);
//        assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);
//        ChaincodeID cid = resp.getChaincodeID();
//        assertNotNull(cid);
//        assertEquals(CHAIN_CODE_PATH, cid.getPath());
//        assertEquals(CHAIN_CODE_NAME, cid.getName());
//        assertEquals(CHAIN_CODE_VERSION, cid.getVersion());    
        
        //交易发送
        Object object = chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            //waitOnFabric(0);
            assertTrue(transactionEvent.isValid()); // must be valid to be here.
            logger.debug("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
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
        
        logger.info("transaction completed.....");
        return object;
	}
    
	 public void runTransaction1(String[] transData) throws Exception  {
	        
	    	logger.info("transaction.....");
	    	
			client.setUserContext(org1.getUser(TEST_USER1_NAME));
			
			//Send transaction proposal to all peers
	        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
	        transactionProposalRequest.setChaincodeID(initChaincodeID);
	        transactionProposalRequest.setFcn("invoke");
	        transactionProposalRequest.setArgs(transData);
	        Map<String, byte[]> tm2 = new HashMap<>();
	        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
	        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
	        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
	        transactionProposalRequest.setTransientMap(tm2);
	        //Channelcode endorsement policies, some default ????????????????????????????????????????????
	        responses = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
	        
	        collectResponses(true);
	        Collection<Orderer> orderers = chain.getOrderers();
	        
	//        ProposalResponse resp = responses.iterator().next();
	//        byte[] resultBytes = resp.getChaincodeActionResponsePayload(); //This is the data returned by the chaincode.
	//        String resultAsString = null;
	//        if (resultBytes != null) {
	//            resultAsString = new String(resultBytes, "UTF-8");
	//        }
	//        assertEquals(":)", resultAsString);
	//        assertEquals(200, resp.getChaincodeActionResponseStatus()); //Channelcode's status.
	//        TxReadWriteSetInfo readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
	//        //See blockwaler below how to transverse this
	//        assertNotNull(readWriteSetInfo);
	//        assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);
	//        ChaincodeID cid = resp.getChaincodeID();
	//        assertNotNull(cid);
	//        assertEquals(CHAIN_CODE_PATH, cid.getPath());
	//        assertEquals(CHAIN_CODE_NAME, cid.getName());
	//        assertEquals(CHAIN_CODE_VERSION, cid.getVersion());    
	        
	        //交易发送
	        chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
	            //waitOnFabric(0);
	            assertTrue(transactionEvent.isValid()); // must be valid to be here.
	            logger.debug("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
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
	        
	        logger.info("transaction completed.....");
		}
    

	/**
     * 获取一个初始化好的Org
     * @param name
     * @param mspid
     * @return
     * @throws Exception
     */
    public static  SampleOrg getOrgInstance(String name, String mspid) throws Exception {
    	//对象创建  name mspid
    	org1 = new SampleOrg(name, mspid);
    	store = getSampleStore(true, SAMPLE_STORE);
    	
    	initOrg1();
    	setOrgCAClient(org1,PEER_ORG1_CA_CERT);
    	setOrgAdmin(TEST_ADMIN_NAME, TEST_ADMIN_PASSWORD, org1, store);
    	
    	setOrgPeerAdmin(org1, store, PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
    	//String userName, SampleOrg org, SampleStore store, String certPath, String keyPath
    	addOrgUser2(TEST_USER1_NAME,org1, store,PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
//    	从外界传递HFClient
//    	setOrgHFClient(org1);
    	return org1;
    }
    
    /**
     * 通过File创建orgUser2
     * @param userName
     * @param org
     * @param store
     * @param certPath
     * @param keyPath
     * @throws Exception
     */
  //getEnrollmentFromFile
  	public static void addOrgUser2(String userName, SampleOrg org, SampleStore store, String certPath, String keyPath) {
  		SampleUser user = store.getMember(userName, org.getName());
  		
  		File certFile = new File(certPath);
          File keyFile = new File(keyPath);
  		
          if (!user.isEnrolled()) {
          	DataFileEnrollment fileEnrollment = null;
			try {
				fileEnrollment = DataFileEnrollment.getEnrollmentFromFile(keyFile, certFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
      		user.setEnrollment(fileEnrollment);
      		//set mpsid enrollment
      		user.setMspId(org.getMSPID());
          }
          org.addUser(user); //Remember user belongs to this Org
  	}
  	//getEnrollmentFromFile
    
    /**
     * 设置节点管理员
     * @param org
     * @param store
     * @param certPath
     * @param keyPath
     * @throws Exception
     */
  	public static void setOrgPeerAdmin(SampleOrg org, SampleStore store, String certPath, String keyPath) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    org.setPeerAdmin(peerOrgAdmin); 
  	}
    
    /**
     * 设置orgAdmin
     * @param userName
     * @param password
     * @param org
     * @param store
     * @throws Exception
     */
    public static void setOrgAdmin(String userName, String password, SampleOrg org, SampleStore store) {
		HFCAClient ca = org.getCAClient();
        final String orgName = org.getName();
//        MyDebug.print(orgName);
        final String mspid = org.getMSPID();
//        MyDebug.print(mspid);
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
        org.setAdmin(admin); //The admin of this org --
	}
    
    /**
     * 设置org的CA客户端
     * @param org
     * @param tlsCert
     * @throws Exception
     */
    public static void setOrgCAClient(SampleOrg org, String tlsCert){
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
    
    /**
     * 根据文件路径获取CA properties
     * @param tlsCert
     * @return
     */
	public static Properties getCAProperties(String tlsCert) {
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
    
    
    /**
     * org1的Name peers eventHubs orderers设置
     */
	public static void initOrg1() {
		//org1.example.com
		org1.setDomainName(PEER_ORG1_DN);
        org1.setCALocation(httpTLSify(location));
        for (String eachone : peers.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org1.addPeerLocation(nl[0], grpcTLSify(nl[1]));
        }
        for (String eachone : eventHubs.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org1.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
        }
        for (String eachone : orderers.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            org1.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
        }
	}
	
    /**
     * 创建一个简单的sampleStore
     * @param fresh
     * @param storePath
     * @return
     */
	public static SampleStore getSampleStore(boolean fresh, String storePath) {
		File sampleStoreFile = new File(storePath);
        if (sampleStoreFile.exists() && fresh) { //For testing start fresh
            sampleStoreFile.delete();
            logger.debug(("----getSampleStore: Deleted old sampleStoreFile: \n" + sampleStoreFile.getAbsolutePath()));
        }
        SampleStore sampleStore = new SampleStore(sampleStoreFile);
        return sampleStore;
	}
	
	/**
	 * org 设置HFClient 通信所用的
	 * @param org
	 * @throws Exception
	 */
	public static HFClient getHFClient() {
		HFClient hf_client = HFClient.createNewInstance();
		try {
			hf_client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hf_client;
	}
	
	
	/**
	 * new Channel 需要初始化chainCodeID
	 * 初始化chainCodeId
	 * @param sampleOrg
	 * @param userName
	 * @param chain
	 */
	public static ChaincodeID initChaincodeID(SampleOrg sampleOrg, Channel chain, HFClient client) {
		ChaincodeID chainCodeID = null;
		try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            Set<Peer> peers = sampleOrg.getPeers();
            Collection<Orderer> orderers = chain.getOrderers();
            
            chainCodeID = ChaincodeID.newBuilder()
            		.setName(CHAIN_CODE_NAME)
                    .setVersion(CHAIN_CODE_VERSION)
                    .setPath(CHAIN_CODE_PATH).build();
            
            client.setUserContext(sampleOrg.getPeerAdmin());
            
            installChainCode(client, peers, chainCodeID, true);
            //这里init操作不做任何操作，参数为空，不能为null，若为null会发生空指针
            sendInstantiationProposal(client, chain, chainCodeID, new String[] {});
            //Send instantiate transaction to orderer
            
            //通道执行
           chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
               //waitOnFabric(0);
               assertTrue(transactionEvent.isValid()); // must be valid to be here.
//               logger.debug("Finished instantiate transaction with id" + transactionEvent.getTransactionID());
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
//        	logger.debug("Caught an exception running chain: " + chain.getName());
            e.printStackTrace();
            fail("Test failed with error : " + e.getMessage());
        }
		return chainCodeID;
    }
	
	/**
	 * 真正的将chainCode配置上
	 * @param client
	 * @param chain
	 * @param chainCodeID
	 * @throws Exception
	 */
	public static void sendInstantiationProposal(HFClient client, Channel chain, ChaincodeID chainCodeID, String[] initData) throws Exception {
        
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
        
        collectResponses(true);
        //Got endorsed responses in a collection named successful here
	}
	
	
	/**
	 * 执行交易
	 * @param client
	 * @param chain
	 * @param chainCodeID
	 * @throws Exception
	 */
	public static void runTransaction(SampleOrg org, HFClient client, Channel chain, ChaincodeID chainCodeID, String[] transData) throws Exception  {
        
		client.setUserContext(org.getUser(TEST_USER1_NAME));
		
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
        
        collectResponses(true);
        Collection<Orderer> orderers = chain.getOrderers();
        
//        ProposalResponse resp = responses.iterator().next();
//        byte[] resultBytes = resp.getChaincodeActionResponsePayload(); //This is the data returned by the chaincode.
//        String resultAsString = null;
//        if (resultBytes != null) {
//            resultAsString = new String(resultBytes, "UTF-8");
//        }
//        assertEquals(":)", resultAsString);
//        assertEquals(200, resp.getChaincodeActionResponseStatus()); //Channelcode's status.
//        TxReadWriteSetInfo readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
//        //See blockwaler below how to transverse this
//        assertNotNull(readWriteSetInfo);
//        assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);
//        ChaincodeID cid = resp.getChaincodeID();
//        assertNotNull(cid);
//        assertEquals(CHAIN_CODE_PATH, cid.getPath());
//        assertEquals(CHAIN_CODE_NAME, cid.getName());
//        assertEquals(CHAIN_CODE_VERSION, cid.getVersion());    
        
        //交易发送
        chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            //waitOnFabric(0);
            assertTrue(transactionEvent.isValid()); // must be valid to be here.
            logger.debug("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
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
	}
	
	
	
	/**
	 * 将response放到successful中
	 * @param verifyResponse
	 */
	public static void collectResponses(boolean verifyResponse) {
		successful.clear();
		failed.clear();
		boolean condition = false;
		for (ProposalResponse response : responses) {
			condition = (response.getStatus() == ProposalResponse.Status.SUCCESS);
			if (verifyResponse) {
				condition &= response.isVerified();
			}
			if (condition) {
				successful.add(response);
				logger.debug(format("Succesful proposal response Txid: %s from peer %s",
						response.getTransactionID(), response.getPeer().getName()));
			} else {
				failed.add(response);
			}
		}
		logger.debug(format("Received %d proposal responses. Successful&Verified: %d . Failed: %d",
				responses.size(), successful.size(), failed.size()));
		if (failed.size() > 0) {
			ProposalResponse first = failed.iterator().next();
			fail("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with "
					+ first.getMessage() + ". Was verified:" + first.isVerified());
		}
	}
	
	/**
	 * 根据ChainCode查询信息
	 * @param client
	 * @param chain
	 * @param chainCodeID
	 * @param key
	 */
	public static void queryByChainCode(HFClient client, Channel chain, ChaincodeID chainCodeID, String key) {
		try {
            logger.debug("Now query chain code for the value of key: " + key);
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
                    logger.debug(
                        format(
                        	"Query payload of key %s from peer %s returned %s", 
                        	key,
                        	response.getPeer().getName(), 
                        	payload
                        )
                    );
                }
            }
        } catch (Exception e) {
        	logger.debug("Caught exception while running query");
            e.printStackTrace();
            fail("Failed during chaincode query with error : " + e.getMessage());
        }	
	}
	
	/**
	 * 根据key查询value，返回String, peerName 可以在这里传如
	 * @param key
	 * @return
	 */
	public String queryByChainCode(String key, String peerName) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		
		Set<Peer> peerSet = org1.getPeers();
		
		try {
            logger.debug("Now query chain code for the value of key: " + key);
            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            request.setFcn("invoke");
            request.setArgs(new String[] {"query", key});
            request.setChaincodeID(initChaincodeID);
            //Send Query Proposal to all peers
            Collection<ProposalResponse> responses = chain.queryByChaincode(request, chain.getPeers());
            
            for (ProposalResponse response : responses) {
                if (!response.isVerified() || response.getStatus() != ProposalResponse.Status.SUCCESS) {
//                    fail(
//                    	"Failed query proposal from peer " + response.getPeer().getName() + 
//                    	" status: " + response.getStatus() +
//                    	". Messages: " + response.getMessage() + 
//                    	". Was verified : " + response.isVerified()
//                    );
                } else {
                	if(peerName.equals(response.getPeer().getName())) {
                		String payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                		String txId = response.getTransactionID();
                		
//                		System.out.println(txId);
                		
                		
//                		Iterator<Peer> it = peerSet.iterator();
//                		while(it.hasNext()){
//                			Peer peer = it.next();
//                			if(peerName.equals(peer.getName())) {
//                				BlockInfo blockInfo = chain.queryBlockByTransactionID(peer, txId);
//                				
//                				map.put("blockNumber", String.valueOf(blockInfo.getBlockNumber()));
//                	            map.put("dataHash", Hex.encodeHexString(blockInfo.getDataHash()));
//                	            map.put("previousHash", Hex.encodeHexString(blockInfo.getDataHash()));
//                	            map.put("envelopeCount", String.valueOf(blockInfo.getEnvelopCount()));
//                	            
//                	            int i = 0;
//                	            
//                	            List<Map<String, Object>> eveInfoList = new ArrayList<Map<String, Object>>();
//                	            //交易信息
//                	            for (BlockInfo.EnvelopeInfo envelopeInfo : blockInfo.getEnvelopeInfos()) {
//                                	
//                                	Map<String, Object> eveInfo = new HashMap<String, Object>();
//                                    ++i;
//                                    final String channelId = envelopeInfo.getChannelId();
//                                    
//                                    eveInfo.put("channelId", channelId);
//                                    eveInfo.put("epoch", String.valueOf(envelopeInfo.getEpoch()));
//                                    
//                                    //时间格式化
//                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                    
//                                    eveInfo.put("transactionTime", sdf.format(envelopeInfo.getTimestamp()));
//                                    
//                                    eveInfoList.add(eveInfo);
//                	            }
//                	            map.put("eveInfo", eveInfoList);
//                			}
//                		}
                		
                		logger.debug(
                				format(
                						"Query payload of key %s from peer %s returned %s", 
                						key,
                						response.getPeer().getName(), 
                						payload
                						)
                				);
                		map.put("payload", payload);
                		map.put("txId", txId);
                	}
                }
            }
        } catch (Exception e) {
        	logger.debug("Caught exception while running query");
            e.printStackTrace();
//            fail("Failed during chaincode query with error : " + e.getMessage());
            return "failed";
        }
		return DataJsonUtils.toJson(map);
	}
	
	public String queryByChainCode(String key) {
			
			String payload = null;
			try {
	            logger.debug("Now query chain code for the value of key: " + key);
	            QueryByChaincodeRequest request = client.newQueryProposalRequest();
	            request.setFcn("invoke");
	            request.setArgs(new String[] {"query", key});
	            request.setChaincodeID(initChaincodeID);
	            //Send Query Proposal to all peers
	            Collection<ProposalResponse> responses = chain.queryByChaincode(request, chain.getPeers());
	            
	            for (ProposalResponse response : responses) {
	                if (!response.isVerified() || response.getStatus() != ProposalResponse.Status.SUCCESS) {
	//                    fail(
	//                    	"Failed query proposal from peer " + response.getPeer().getName() + 
	//                    	" status: " + response.getStatus() +
	//                    	". Messages: " + response.getMessage() + 
	//                    	". Was verified : " + response.isVerified()
	//                    );
	                } else {
	                    payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
	                    logger.debug(
	                        format(
	                        	"Query payload of key %s from peer %s returned %s", 
	                        	key,
	                        	response.getPeer().getName(), 
	                        	payload
	                        )
	                    );
	                }
	            }
	        } catch (Exception e) {
	        	logger.debug("Caught exception while running query");
	            e.printStackTrace();
	//            fail("Failed during chaincode query with error : " + e.getMessage());
	            return "failed";
	        }
			return payload;
		}
	
	
	/**
	 * 通道信息查询
	 * @param chain
	 * @param sampleOrg
	 * @throws Exception
	 */
	public String channelQuerry(String peerName) throws Exception {
        //Only send channel queries to peers that are in the same org as the SDK user context
        //Get the peers from the current org being used, and pick one randomly to query.
        Set<Peer> peerSet = org1.getPeers();
        
        Map<String, String> channelMap = new HashMap<String, String>();
        Iterator<Peer> it = peerSet.iterator();
        while(it.hasNext()) {
        	Peer peer = it.next();
        	if(peerName.equals(peer.getName())){
        		channelMap.put("peerName", peer.getName());
        		BlockchainInfo channelInfo = chain.queryBlockchainInfo(peer);
        		channelMap.put("channelName", chain.getName());
        		channelMap.put("channelHeight", String.valueOf(channelInfo.getHeight()));
        		channelMap.put("currentBlockHash", Hex.encodeHexString(channelInfo.getCurrentBlockHash()));
        	}
        }
        
        String rtJson = DataJsonUtils.toJson(channelMap);
        return rtJson;
	}
	
	/**
	 * 通道信息查询
	 * @param chain
	 * @param sampleOrg
	 * @throws Exception
	 */
	public String channelQuerry2(String peerName) throws Exception {
        //Only send channel queries to peers that are in the same org as the SDK user context
        //Get the peers from the current org being used, and pick one randomly to query.
        Set<Peer> peerSet = org1.getPeers();
//        Peer queryPeer = peerSet.iterator().next();
        
//        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, List<Map<String, String>>> map = new HashMap<String, List<Map<String, String>>>();
        
        Iterator<Peer> it = peerSet.iterator();
        List<Map<String, String>> channelList = new ArrayList<Map<String, String>>();
        while(it.hasNext()) {
        	Map<String, String> channelMap = new HashMap<String, String>();
        	Peer peer = it.next();
        	if(peerName.equals(peer.getName())){
        		channelMap.put("peerName", peer.getName());
        		BlockchainInfo channelInfo = chain.queryBlockchainInfo(peer);
        		channelMap.put("channelName", chain.getName());
        		channelMap.put("channelHeight", String.valueOf(channelInfo.getHeight()));
        		channelMap.put("currentBlockHash", Hex.encodeHexString(channelInfo.getCurrentBlockHash()));
//        		map.put("previousBlockHash", Hex.encodeHexString(channelInfo.getPreviousBlockHash()));
        		
        		channelList.add(channelMap);
        		
        		map.put("channel", channelList);
        		
        		//块信息
        		long count = channelInfo.getHeight() - 1;
        		List<Map<String, String>> blockList = new ArrayList<Map<String, String>>();
        		//遍历当前高度之前的块
        		while(count >= 0) {
        			Map<String, String> blockMap = new HashMap<String,String>();
        			BlockInfo block = chain.queryBlockByNumber(peer, count);
        			blockMap.put("blockHeight", String.valueOf(block.getBlockNumber()));
        			blockMap.put("blockHash", Hex.encodeHexString(block.getDataHash()));
        			blockMap.put("preBlockHash", Hex.encodeHexString(block.getPreviousHash()));
        			
//        			System.out.println("#####################################");
//        			EnvelopeInfo eInfo = block.getEnvelopeInfo(0);
//        			String transactionID = eInfo.getTransactionID();
//        			System.out.println(":" + transactionID);
//        			System.out.println(":" + eInfo.getChannelId());
//        			System.out.println(":" + eInfo.getEpoch());
//        			System.out.println(":" + eInfo.getValidationCode());
//        			System.out.println(":" + eInfo.getTimestamp());
//        			System.out.println(":" + eInfo.getType());
//        			System.out.println("====================================");
        			blockList.add(blockMap);
            		count--;
        		}
        		map.put("blockMap", blockList);
        	}
        }
        
        String rtJson = DataJsonUtils.toJson(map);
        
        System.out.println(rtJson);
        
        
        return rtJson;
        
        
        
        //Query by block number. Should return latest block, i.e. block number 2
//        BlockInfo returnedBlock = chain.queryBlockByNumber(queryPeer, channelInfo.getHeight() - 1);
//        String previousHash = Hex.encodeHexString(returnedBlock.getPreviousHash());
//        System.out.println(
//        	"queryBlockByNumber returned correct block with blockNumber " + 
//        	returnedBlock.getBlockNumber() + " \n previous_hash " + previousHash
//        );
//        assertEquals(channelInfo.getHeight() - 1, returnedBlock.getBlockNumber());
//        assertEquals(chainPreviousHash, previousHash);
//        //Query by block hash. Using latest block's previous hash so should return block number 1
//        byte[] hashQuery = returnedBlock.getPreviousHash();
//        returnedBlock = chain.queryBlockByHash(queryPeer, hashQuery);
//        System.out.println("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());
//        assertEquals(channelInfo.getHeight() - 2, returnedBlock.getBlockNumber());
//        //Query block by TxID. Since it's the last TxID, should be block 2
//        returnedBlock = chain.queryBlockByTransactionID(queryPeer, testTxID);
//        System.out.println("queryBlockByTxID returned block with blockNumber " + returnedBlock.getBlockNumber());
//        assertEquals(channelInfo.getHeight() - 1, returnedBlock.getBlockNumber());
//        //Query transaction by ID
//        TransactionInfo txInfo = chain.queryTransactionByID(queryPeer, testTxID);
//        System.out.println(
//        	"queryTransactionByID returned TransactionInfo: txID " + txInfo.getTransactionID() + 
//        	"\nvalidation code " + txInfo.getValidationCode().getNumber()
//        );
//        System.out.println("Done querry Channel: " + chain.getName());
	}
	
	public String channelQuerry1(String peerName) throws Exception {
		Set<Peer> peerSet = org1.getPeers();
		Map<String, List<Map<String, String>>> map = new HashMap<String, List<Map<String, String>>>();
		
		Iterator<Peer> it = peerSet.iterator();
		Peer peer = it.next();
				
		BlockInfo block = chain.queryBlockByNumber(peer, 1);
		
		System.out.print("----------------------------------------------");
		System.out.println(block.getTransActionsMetaData());
		System.out.println(block.getDataHash());
		System.out.println(block.getPreviousHash());
		System.out.println(block.getChannelId());
		System.out.println(block.getEnvelopCount());
		System.out.println(block.getTransActionsMetaData());
		System.out.println(block.getBlockNumber());
		System.out.print("----------------------------------------------");
		
		int count = 10;
		while(count > 0) {
			System.out.println("#####################################");
			EnvelopeInfo eInfo = block.getEnvelopeInfo(0);
			String transactionID = eInfo.getTransactionID();
			System.out.println(":" + transactionID);
			System.out.println(":" + eInfo.getChannelId());
			System.out.println(":" + eInfo.getEpoch());
			System.out.println(":" + eInfo.getValidationCode());
			System.out.println(":" + eInfo.getTimestamp());
			System.out.println(":" + eInfo.getType());
			System.out.println("====================================");
			count--;
		}
		
		String rtJson = DataJsonUtils.toJson(map);
		
		return rtJson;
	
	}
	
	private static final Map<String, String> TX_EXPECTED;

    static {
        TX_EXPECTED = new HashMap<>();
        TX_EXPECTED.put("readset1", "Missing readset for channel bar block 1");
        TX_EXPECTED.put("writeset1", "Missing writeset for channel bar block 1");
    }
	
	public String blockWalker() throws Exception {
		
//		Map<String, List<Map<String, String>>> map = new HashMap<String, List<Map<String, String>>>();
		
		Map<String, Object> chainInfoMap = new HashMap<String, Object>();
        try {
        	
            BlockchainInfo channelInfo = chain.queryBlockchainInfo();
            
            chainInfoMap.put("height", channelInfo.getHeight());
            
            
            List<Map<String, Object>> blockListInfo = new ArrayList<Map<String, Object>>();
            
            for (long current = channelInfo.getHeight() - 1; current > -1; --current) {
                //map
            	Map<String, Object> blockInfo = new HashMap<String, Object>(); 
            	
            	BlockInfo returnedBlock = chain.queryBlockByNumber(current);
                final long blockNumber = returnedBlock.getBlockNumber();
                
//                out("current block number %d has data hash: %s", blockNumber, Hex.encodeHexString(returnedBlock.getDataHash()));
//                out("current block number %d has previous hash id: %s", blockNumber, Hex.encodeHexString(returnedBlock.getPreviousHash()));
//                out("current block number %d has calculated block hash is %s", blockNumber, Hex.encodeHexString(SDKUtils.calculateBlockHash(blockNumber, returnedBlock.getPreviousHash(), returnedBlock.getDataHash())));
//
//                final int envelopCount = returnedBlock.getEnvelopCount();
//                
//                assertEquals(1, envelopCount);
//                out("current block number %d has %d envelope count:", blockNumber, returnedBlock.getEnvelopCount());
                
                blockInfo.put("blockNumber", String.valueOf(blockNumber));
                blockInfo.put("dataHash", Hex.encodeHexString(returnedBlock.getDataHash()));
                blockInfo.put("previousHash", Hex.encodeHexString(returnedBlock.getPreviousHash()));
                blockInfo.put("envelopeCount", String.valueOf(returnedBlock.getEnvelopCount()));
                
                //用于保存eveInfo
                List<Map<String, Object>> eveInfoList = new ArrayList<Map<String, Object>>();
                int i = 0;
                for (BlockInfo.EnvelopeInfo envelopeInfo : returnedBlock.getEnvelopeInfos()) {
                	
                	Map<String, Object> eveInfo = new HashMap<String, Object>();
                	
                    ++i;
//                    out("  Transaction number %d has transaction id: %s", i, envelopeInfo.getTransactionID());
                    final String channelId = envelopeInfo.getChannelId();
//                    assertTrue("foo".equals(channelId) || "bar".equals(channelId));
//                    out("  Transaction number %d has channel id: %s", i, channelId);
//                    out("  Transaction number %d has epoch: %d", i, envelopeInfo.getEpoch());
//                    out("  Transaction number %d has transaction timestamp: %tB %<te,  %<tY  %<tT %<Tp", i, envelopeInfo.getTimestamp());
//                    out("  Transaction number %d has type id: %s", i, "" + envelopeInfo.getType());
                    
                    eveInfo.put("channelId", channelId);
                    eveInfo.put("epoch", String.valueOf(envelopeInfo.getEpoch()));
                    
                    //时间格式化
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    
                    eveInfo.put("transactionTime", sdf.format(envelopeInfo.getTimestamp()));
                    eveInfo.put("typeId", String.valueOf(envelopeInfo.getType()));
                    
                    if (envelopeInfo.getType() == TRANSACTION_ENVELOPE) {
                        BlockInfo.TransactionEnvelopeInfo transactionEnvelopeInfo = (BlockInfo.TransactionEnvelopeInfo) envelopeInfo;
                        
                        eveInfo.put("actionCount", String.valueOf(transactionEnvelopeInfo.getTransactionActionInfoCount()));
                        eveInfo.put("isValid", String.valueOf(transactionEnvelopeInfo.isValid()));
                        eveInfo.put("validationCode", String.valueOf(transactionEnvelopeInfo.getValidationCode()));
                        
//                        out("  Transaction number %d has %d actions", i, transactionEnvelopeInfo.getTransactionActionInfoCount());
//                        assertEquals(1, transactionEnvelopeInfo.getTransactionActionInfoCount()); // for now there is only 1 action per transaction.
//                        out("  Transaction number %d isValid %b", i, transactionEnvelopeInfo.isValid());
//                        assertEquals(transactionEnvelopeInfo.isValid(), true);
//                        out("  Transaction number %d validation code %d", i, transactionEnvelopeInfo.getValidationCode());
//                        assertEquals(0, transactionEnvelopeInfo.getValidationCode());
                        
                        List<Map<String, Object>> actionInfoList = new ArrayList<Map<String, Object>>();
                        int j = 0;
                        for (BlockInfo.TransactionEnvelopeInfo.TransactionActionInfo transactionActionInfo : transactionEnvelopeInfo.getTransactionActionInfos()) {
                            ++j;
                            
                            Map<String, Object> actionInfo = new HashMap<String, Object>();
                            
                            actionInfo.put("responseStatus", String.valueOf(transactionActionInfo.getResponseStatus()));
                            actionInfo.put("responseMessage", printableString(new String(transactionActionInfo.getResponseMessageBytes(), "UTF-8")));
                            actionInfo.put("endorsementCount", String.valueOf(transactionActionInfo.getEndorsementsCount()));
                            actionInfo.put("inputArgsCount", String.valueOf(transactionActionInfo.getChaincodeInputArgsCount()));
                            
//                            out("   Transaction action %d has response status %d", j, transactionActionInfo.getResponseStatus());
//                            assertEquals(200, transactionActionInfo.getResponseStatus());
//                            out("   Transaction action %d has response message bytes as string: %s", j,
//                                    printableString(new String(transactionActionInfo.getResponseMessageBytes(), "UTF-8")));
//                            out("   Transaction action %d has %d endorsements", j, transactionActionInfo.getEndorsementsCount());
//                            assertEquals(2, transactionActionInfo.getEndorsementsCount());
                            
                            List<Map<String, Object>> endorsmentsInfo = new ArrayList<Map<String, Object>>();
                            
                            for (int n = 0; n < transactionActionInfo.getEndorsementsCount(); ++n) {
                            	//map
                            	Map<String, Object> endorsInfo = new HashMap<String, Object>();
                            	
                                BlockInfo.EndorserInfo endorserInfo = transactionActionInfo.getEndorsementInfo(n);
                                
                                endorsInfo.put("signature", Hex.encodeHexString(endorserInfo.getSignature()));
                                endorsInfo.put("endorser", new String(endorserInfo.getEndorser(), "UTF-8"));
                                
//                                out("Endorser %d signature: %s", n, Hex.encodeHexString(endorserInfo.getSignature()));
//                                out("Endorser %d endorser: %s", n, new String(endorserInfo.getEndorser(), "UTF-8"));
                                //add
                                endorsmentsInfo.add(endorsInfo);
                            }
                            //put
                            actionInfo.put("endorsmentsInfo", endorsmentsInfo);
                            
                            
//                            out("   Transaction action %d has %d chaincode input arguments", j, transactionActionInfo.getChaincodeInputArgsCount());
                            
                            //list
                            List<String> argsList = new ArrayList<String>();
                            for (int z = 0; z < transactionActionInfo.getChaincodeInputArgsCount(); ++z) {
                            	argsList.add(printableString(new String(transactionActionInfo.getChaincodeInputArgs(z), "UTF-8")));
//                                out("     Transaction action %d has chaincode input argument %d is: %s", j, z,
//                                        printableString(new String(transactionActionInfo.getChaincodeInputArgs(z), "UTF-8")));
                            }
                            
//                            String argsJson = DataJsonUtils.toJson(argsList);
                            actionInfo.put("argsInfo", argsList);
                            actionInfo.put("payload", printableString(new String(transactionActionInfo.getProposalResponsePayload())));
                            
                            
//                            out("   Transaction action %d proposal response status: %d", j,
//                                    transactionActionInfo.getProposalResponseStatus());
//                            out("   Transaction action %d proposal response payload: %s", j,
//                                    printableString(new String(transactionActionInfo.getProposalResponsePayload())));

                            TxReadWriteSetInfo rwsetInfo = transactionActionInfo.getTxReadWriteSet();
                            
                            if (null != rwsetInfo) {
                            	
//                            	Map<String, String> rsInfo = new HashMap<String, String>();
                            	
                            	actionInfo.put("rwNameSpaceCount", String.valueOf(rwsetInfo.getNsRwsetCount()));
                            	
//                            	rsInfo.put("rwNameSpaceCount", String.valueOf(rwsetInfo.getNsRwsetCount()));
                            	
//                                out("   Transaction action %d has %d name space read write sets", j, rwsetInfo.getNsRwsetCount());
                                
                                List<Map<String, Object>> nsRwInfoList = new ArrayList<Map<String, Object>>();
                                for (TxReadWriteSetInfo.NsRwsetInfo nsRwsetInfo : rwsetInfo.getNsRwsetInfos()) {
                                	
                                    final String namespace = nsRwsetInfo.getNaamespace();
                                    KvRwset.KVRWSet rws = nsRwsetInfo.getRwset();
                                    
                                    Map<String, Object> nsRwInfo = new HashMap<String, Object>();
                                    
                                    //读list
                                    List<Map<String, Object>> readInfoList = new ArrayList<Map<String, Object>>();
                                    //
                                    int rs = -1;
                                    for (KvRwset.KVRead readList : rws.getReadsList()) {
                                        rs++;
                                        //map
                                        Map<String, Object> readInfo = new HashMap<String, Object>();
                                        readInfo.put("namespace", namespace);
                                        readInfo.put("key", readList.getKey());
                                        readInfo.put("version", String.valueOf(readList.getVersion().getBlockNum()) + ":" + String.valueOf(readList.getVersion().getTxNum()));
                                        
//                                        out("     Namespace %s read set %d key %s  version [%d:%d]", namespace, rs, readList.getKey(),
//                                                readList.getVersion().getBlockNum(), readList.getVersion().getTxNum());

                                        if ("bar".equals(channelId) && blockNumber == 2) {
                                            if ("example_cc_go".equals(namespace)) {
                                                if (rs == 0) {
//                                                    assertEquals("a", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
                                                } else if (rs == 1) {
//                                                    assertEquals("b", readList.getKey());
//                                                    assertEquals(1, readList.getVersion().getBlockNum());
//                                                    assertEquals(0, readList.getVersion().getTxNum());
                                                } else {
                                                    fail(format("unexpected readset %d", rs));
                                                }

                                                TX_EXPECTED.remove("readset1");
                                            }
                                        }
                                        
                                        //add
                                        readInfoList.add(readInfo);
                                    }
                                    
                                    //put
                                    nsRwInfo.put("readInfoList", readInfoList);
                                    
                                    //写list
                                    List<Map<String, Object>> writeInfoList = new ArrayList<Map<String, Object>>();
                                    //
                                    
                                    rs = -1;
                                    for (KvRwset.KVWrite writeList : rws.getWritesList()) {
                                        rs++;
                                        
                                        //map
                                        Map<String, Object> writeInfo = new HashMap<String, Object>();
                                        writeInfo.put("valAsString", printableString(new String(writeList.getValue().toByteArray(), "UTF-8")));
                                        writeInfo.put("namespace", namespace);
                                        writeInfo.put("key", writeList.getKey());
                                        
                                        String valAsString = printableString(new String(writeList.getValue().toByteArray(), "UTF-8"));
                                        
//                                        out("     Namespace %s write set %d key %s has value '%s' ", namespace, rs,
//                                                writeList.getKey(),
//                                                valAsString);

                                        if ("bar".equals(channelId) && blockNumber == 2) {
                                            if (rs == 0) {
//                                                assertEquals("a", writeList.getKey());
//                                                assertEquals("400", valAsString);
                                            } else if (rs == 1) {
//                                                assertEquals("b", writeList.getKey());
//                                                assertEquals("400", valAsString);
                                            } else {
                                                fail(format("unexpected writeset %d", rs));
                                            }

                                            TX_EXPECTED.remove("writeset1");
                                        }
                                        
                                        //add
                                        writeInfoList.add(writeInfo);
                                    }
                                    //put
                                    nsRwInfo.put("writeInfoList", writeInfoList);
                                    //add
                                    nsRwInfoList.add(nsRwInfo);
                                }
                                
                                //put
                                actionInfo.put("nsRwInfoList", nsRwInfoList);
                                //add
                                actionInfoList.add(actionInfo);
                            }
                            
                        }
                        eveInfo.put("actionInfoList", actionInfoList);
                    }
                    //add
                    eveInfoList.add(eveInfo);
                }
                blockInfo.put("eveInfoList",eveInfoList);
                //add
                blockListInfo.add(blockInfo);
            }
            
            chainInfoMap.put("blockListInfo", blockListInfo);
            
            if (!TX_EXPECTED.isEmpty()) {
//                fail(TX_EXPECTED.get(0));
            }
        } catch (InvalidProtocolBufferRuntimeException e) {
            throw e.getCause();
        }
        
        return DataJsonUtils.toJson(chainInfoMap);
        
    }
	
    static String printableString(final String string) {
        int maxLogStringLength = 64;
        if (string == null || string.length() == 0) {
            return string;
        }

        String ret = string.replaceAll("[^\\p{Print}]", "?");

        ret = ret.substring(0, Math.min(ret.length(), maxLogStringLength)) + (ret.length() > maxLogStringLength ? "..." : "");

        return ret;

    }
	
	static void out(String format, Object... args) {

        System.err.flush();
        System.out.flush();

        System.out.println(format(format, args));
        System.err.flush();
        System.out.flush();

    }
	
	public void queryByChainCode1(String key) {
		try {
            logger.debug("Now query chain code for the value of key: " + key);
            QueryByChaincodeRequest request = client.newQueryProposalRequest();
            request.setFcn("invoke");
            request.setArgs(new String[] {"query", key});
            request.setChaincodeID(initChaincodeID);
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
                    logger.debug(
                        format(
                        	"Query payload of key %s from peer %s returned %s", 
                        	key,
                        	response.getPeer().getName(), 
                        	payload
                        )
                    );
                }
            }
        } catch (Exception e) {
        	logger.debug("Caught exception while running query");
            e.printStackTrace();
            fail("Failed during chaincode query with error : " + e.getMessage());
        }	
	}
	
	/**
	 * 配置chainCode
	 * @param client
	 * @param peers
	 * @param chainCodeID
	 * @param directory
	 * @throws Exception
	 */
	//chainCodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH).build();
	public static void installChainCode(HFClient client, Set<Peer> peers, ChaincodeID chainCodeID, boolean directory) throws Exception {
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chainCodeID);
        if (directory) {//install from file system directory.
        	
        	installProposalRequest.setChaincodeSourceLocation(new File(CHAINCODE_SOURCE_LOCATION));
        	
//            installProposalRequest.setChaincodeSourceLocation(new File("src/smartcc"));
///////////////////////////////////////////////////////////////////////////////////////////////////////
//            installProposalRequest.setChaincodeSourceLocation(new File("/opt/web/smartcc"));
        } else {//install from an input stream. There are some errors here, remember to fix it in the future.!!!!!!!!!!!!!!!!!
            installProposalRequest.setChaincodeInputStream(
        		generateTarGzInputStream(
            		Paths.get(TARGZ_INPUT_STREAM_PATH1, CHAIN_CODE_PATH).toFile(),
            		//Paths.get("src", "sample2", "src", CHAIN_CODE_PATH).toFile(),
            		Paths.get(TARGZ_INPUT_STREAM_PATH2, CHAIN_CODE_PATH).toString()
            		
            		//            		generateTarGzInputStream(
//            			Paths.get("src/test/fixture/sdkintegration/gocc/sample1/src/github.com", CHAIN_CODE_PATH).toFile(),
//            		//Paths.get("src", "sample2", "src", CHAIN_CODE_PATH).toFile(),
//            		Paths.get("src/github.com", CHAIN_CODE_PATH).toString()
/////////////////////////////////////////////////////////////////////////////////////////////////
//            		generateTarGzInputStream(
//                			Paths.get("/opt/web/test/fixture/sdkintegration/gocc/sample1/src/github.com", CHAIN_CODE_PATH).toFile(),
//                		//Paths.get("src", "sample2", "src", CHAIN_CODE_PATH).toFile(),
//                		Paths.get("/opt/web/github.com", CHAIN_CODE_PATH).toString()
            	)
            );
        }
        installProposalRequest.setChaincodeVersion(CHAIN_CODE_VERSION);
        // only a client from the same org as the peer can issue an install request
        responses = client.sendInstallProposal(installProposalRequest, peers);
        collectResponses(false); //It seems must be false, or it will be failed 
	}
	
	/**
	 * 配置一个通道new or Exist
	 * @return Channel
	 * @throws Exception 
	 */
	public static Channel getChannel(Boolean newChannel, HFClient client){
		if(newChannel) {
			//客户端，org， 链名字，
			try {
				return constructNewChannel(client, org1, FOO_CHAIN_NAME, FOO_CONFIGTX);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				return constructExistedChannel(client, org1, TEST_USER1_NAME, FOO_CHAIN_NAME);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 配置已存在的通道
	 * @param sampleOrg
	 * @param userName
	 * @param channelName
	 * @return
	 * @throws Exception
	 */
	public static Channel constructExistedChannel(HFClient client, SampleOrg sampleOrg, String userName, String channelName) throws Exception {
        client.setUserContext(sampleOrg.getUser(userName));
        Channel newChannel = client.newChannel(channelName);
        addPeersToChannel(client, newChannel, sampleOrg);
        addEventHubsToChannel(client, newChannel, sampleOrg);
        addOrderersToChannel(client, newChannel, sampleOrg);
        newChannel.initialize();        
        return newChannel;
    }
	
	/**
	 * 添加Orders到通道
	 * @param newChannel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public static void addOrderersToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg) throws Exception{
		for (String orderName : sampleOrg.getOrdererNames()) {
        	Orderer anOrderer = client.newOrderer(
        		orderName, 
        		sampleOrg.getOrdererLocation(orderName),
        		getEndPointProperties(
                    ORDERER_ORG_BASE + ORDERER_DN + "/orderers/" + orderName + "/tls/server.crt",
                    orderName,
                    false
                )
        	);
        	newChannel.addOrderer(anOrderer);
        }
	}
	
	/**
	 * 添加Event到通道
	 * @param newChannel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public static void addEventHubsToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg) throws Exception{
		for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(
            	eventHubName, 
            	sampleOrg.getEventHubLocation(eventHubName),
            	getEndPointProperties(
                	PEER_ORG_BASE + PEER_ORG1_DN + "/peers/" + eventHubName + "/tls/server.crt",
                	eventHubName,
                	false
                )
            );
            newChannel.addEventHub(eventHub);
        }
	}
	
	/**
	 * 添加节点到通道
	 * @param newChannel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public static void addPeersToChannel(HFClient client, Channel newChannel, SampleOrg sampleOrg) throws Exception{
		for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Properties peerProperties = getEndPointProperties(
            	PEER_ORG_BASE + PEER_ORG1_DN + "/peers/" + peerName + "/tls/server.crt",
            	peerName,
            	false
            );
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            //Example of setting specific options on grpc's ManagedChannelBuilder
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            newChannel.addPeer(peer);
            sampleOrg.addPeer(peer);
//            MyDebug.printProperties("Peer propeties", peer.getProperties());
            logger.debug("Peer propeties:" + peer.getProperties());
        }
	}
	
	/**
	 * 
	 * @param certPath
	 * @param fullDomainName
	 * @param trust
	 * @return
	 */
    public static Properties getEndPointProperties(String certPath, String fullDomainName, boolean trust) {
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
	
	
	/**
	 * 创建一个新的通道(channel peer orderers )
	 * @param sampleOrg
	 * @param channelName
	 * @param configTx
	 * @return
	 * @throws Exception
	 */
	public static Channel constructNewChannel(HFClient client, SampleOrg sampleOrg, String channelName, String configTx) throws Exception {
        
        Collection<Orderer> orderers = new LinkedList<>();
        client.setUserContext(sampleOrg.getPeerAdmin());
        for (String orderName : sampleOrg.getOrdererNames()) {
            orderers.add(
            	client.newOrderer(
            		orderName, 
            		sampleOrg.getOrdererLocation(orderName),
            		getEndPointProperties(
            			ORDERER_ORG_BASE + ORDERER_DN + "/orderers/" + orderName + "/tls/server.crt",
            			orderName,
            			false
            		)
            	)
            );
        }
        
        
        //Just pick the first orderer in the list to create the chain.
        Orderer anOrderer = orderers.iterator().next();
        
        orderers.remove(anOrderer);
//        MyDebug.printProperties("orderer propeties", anOrderer.getProperties());
        logger.debug("orderer propeties:" + anOrderer.getProperties());
        File txFile = new File(configTx);
        ChannelConfiguration chainConfiguration = new ChannelConfiguration(txFile);
        
//        client.setUserContext(sampleOrg.getPeerAdmin());
        //Only peer Admin org
        //Create chain that has only one signer that is this org's peer admin. 
        //If chain creation policy needed more signature they would need to be added too.
        Channel newChannel = client.newChannel(
        	channelName, 
        	anOrderer, 
        	chainConfiguration, 
        	client.getChannelConfigurationSignature(
        		chainConfiguration, 
        		sampleOrg.getPeerAdmin()
        	)
        );
        
        for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Properties peerProperties = getEndPointProperties(
                PEER_ORG_BASE + PEER_ORG1_DN + "/peers/" + peerName + "/tls/server.crt",
                peerName,
                false
            );
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            //Example of setting specific options on grpc's ManagedChannelBuilder
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            newChannel.joinPeer(peer);
            logger.debug(format("Peer %s joined chain %s", peerName, channelName));
            sampleOrg.addPeer(peer);
//            MyDebug.printProperties("Peer propeties", peer.getProperties());
            logger.debug("Peer propeties:" + peer.getProperties());
        }
        for (Orderer orderer : orderers) { //add remaining orderers if any.
            newChannel.addOrderer(orderer);
        }
        for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(
            	eventHubName, 
            	sampleOrg.getEventHubLocation(eventHubName),
            	getEndPointProperties(
                        PEER_ORG_BASE + PEER_ORG1_DN + "/peers/" + eventHubName + "/tls/server.crt",
                        eventHubName,
                        false
               )
            );
            newChannel.addEventHub(eventHub);
        }
        newChannel.initialize();
        return newChannel;
    }
	
	
	public static InputStream generateTarGzInputStream(File src, String pathPrefix) throws IOException {
        File sourceDirectory = src;
        //File destinationArchive = new File(target);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(500000);

        String sourcePath = sourceDirectory.getAbsolutePath();
        //	FileOutputStream destinationOutputStream = new FileOutputStream(destinationArchive);

        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(bos)));
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        try {
            Collection<File> childrenFiles = org.apache.commons.io.FileUtils.listFiles(sourceDirectory, null, true);
            //		childrenFiles.remove(destinationArchive);

            ArchiveEntry archiveEntry;
            FileInputStream fileInputStream;
            for (File childFile : childrenFiles) {
                String childPath = childFile.getAbsolutePath();
                String relativePath = childPath.substring((sourcePath.length() + 1), childPath.length());

                if (pathPrefix != null) {
                    relativePath = Utils.combinePaths(pathPrefix, relativePath);
                }

                relativePath = FilenameUtils.separatorsToUnix(relativePath);

                archiveEntry = new TarArchiveEntry(childFile, relativePath);
                fileInputStream = new FileInputStream(childFile);
                archiveOutputStream.putArchiveEntry(archiveEntry);

                try {
                    IOUtils.copy(fileInputStream, archiveOutputStream);
                } finally {
                    IOUtils.closeQuietly(fileInputStream);
                    archiveOutputStream.closeArchiveEntry();
                }
            }
        } finally {
            IOUtils.closeQuietly(archiveOutputStream);
        }

        return new ByteArrayInputStream(bos.toByteArray());
    }
	
	/**
	 * http - https 转换
	 * @param location
	 * @return
	 */
    public static String httpTLSify(String location) {
        location = location.trim();
        return runningFabricCATLS ? location.replaceFirst("^http://", "https://") : location;
    }
    
    /**
     * grpc - grpcs 转换
     * @param location
     * @return
     */
	private static String grpcTLSify(String location) {
        location = location.trim();
        Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }
        return runningFabricTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;
    }
}
