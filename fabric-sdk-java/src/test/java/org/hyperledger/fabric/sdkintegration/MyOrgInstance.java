package org.hyperledger.fabric.sdkintegration;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
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
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdkintegration.MyFileEnrollment;
import org.hyperledger.fabric.sdkintegration.MyDebug;
import org.hyperledger.fabric.sdkintegration.SampleOrg;
import org.hyperledger.fabric.sdkintegration.SampleStore;
import org.hyperledger.fabric.sdkintegration.SampleUser;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

/**
 * orgInstance 具有SampleOrg的初始化方法
 * @author PC
 *
 */
public class MyOrgInstance {
	
	private static String testTxID = null;
	private static Collection<ProposalResponse> responses = null;
	private static Collection<ProposalResponse> successful = new LinkedList<>();
	private static Collection<ProposalResponse> failed = new LinkedList<>();
	
	private static Properties pro = new Properties();
	//SampleOrg new SampleOrg("peerOrg1", "Org1MSP")
	private static SampleOrg org1;
	//= getSampleStore(true, SAMPLE_STORE);
	private static SampleStore store;
	
	
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
    
    private static final boolean runningTLS = false;
    private static final boolean runningFabricCATLS = runningTLS;
    private static final boolean runningFabricTLS = runningTLS;
    
//    private static HFClient hf_client = null;
    
    //从properties文件中加载属性值
    static{
    	//资源放到了src/test/resources中
        InputStream in = MyOrgInstance.class.getResourceAsStream("/myconf.properties");
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
        
        
        peers = (String) pro.get("peers");
        eventHubs = (String) pro.get("eventHubs");
        orderers = (String) pro.get("orderers");
        location = (String) pro.get("location");
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
  	public static void addOrgUser2(String userName, SampleOrg org, SampleStore store, String certPath, String keyPath) throws Exception {
  		SampleUser user = store.getMember(userName, org.getName());
  		
  		File certFile = new File(certPath);
          File keyFile = new File(keyPath);
  		
          if (!user.isEnrolled()) {
          	MyFileEnrollment fileEnrollment =  MyFileEnrollment.getEnrollmentFromFile(keyFile, certFile);
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
  	public static void setOrgPeerAdmin(SampleOrg org, SampleStore store, String certPath, String keyPath) throws Exception {
  		final String orgName = org.getName();
	    File certFile = new File(certPath);
	    File keyFile = new File(keyPath);
	    System.out.println("\ncertFile: " + certFile.getAbsolutePath() + "\nkeyFile: " + keyFile.getAbsolutePath());
	    SampleUser peerOrgAdmin = store.getMember(
	  		orgName + "Admin", 
	  		orgName, 
	  		org.getMSPID(),
	  		keyFile,
	        certFile
	    );
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
    public static void setOrgAdmin(String userName, String password, SampleOrg org, SampleStore store) throws Exception {
		HFCAClient ca = org.getCAClient();
        final String orgName = org.getName();
        MyDebug.print(orgName);
        final String mspid = org.getMSPID();
        MyDebug.print(mspid);
        SampleUser admin = store.getMember(userName, orgName);
        
        System.out.println("setOrgAdmin, admin.isEnrolled(): " + admin.isEnrolled());
        if (!admin.isEnrolled()) {  //Pre-registered admin only needs to be enrolled with Fabric caClient.
            admin.setEnrollment(ca.enroll(admin.getName(), password));
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
    public static void setOrgCAClient(SampleOrg org, String tlsCert) throws Exception {
		Properties properties = getCAProperties(tlsCert);
		HFCAClient ca = HFCAClient.createNewInstance(org.getCALocation(), properties);
    	ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite()); //new CryptoPrimitives()
    	org.setCAClient(ca);
    	org.setCAProperties(properties);
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
            System.out.println("----getSampleStore: Deleted old sampleStoreFile: \n" + sampleStoreFile.getAbsolutePath());
        }
        SampleStore sampleStore = new SampleStore(sampleStoreFile);
        return sampleStore;
	}
	
	/**
	 * org 设置HFClient 通信所用的
	 * @param org
	 * @throws Exception
	 */
	public static HFClient getHFClient(){
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
	public static ChaincodeID initChaincodeID(SampleOrg sampleOrg, Channel chain, HFClient client, String[] initData) {
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
            
            sendInstantiationProposal(client, chain, chainCodeID, initData);
            //Send instantiate transaction to orderer
            
            //通道执行
           chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
               //waitOnFabric(0);
               assertTrue(transactionEvent.isValid()); // must be valid to be here.
               System.out.println("Finished instantiate transaction with id" + transactionEvent.getTransactionID());
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
        	System.out.println("Caught an exception running chain: " + chain.getName());
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
            System.out.println("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
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
				System.out.println(format("Succesful proposal response Txid: %s from peer %s",
						response.getTransactionID(), response.getPeer().getName()));
			} else {
				failed.add(response);
			}
		}
		System.out.println(format("Received %d proposal responses. Successful&Verified: %d . Failed: %d",
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
            System.out.println("Now query chain code for the value of key: " + key);
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
                    System.out.println(
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
        	System.out.println("Caught exception while running query");
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
            installProposalRequest.setChaincodeSourceLocation(new File("src/smartcc"));
        } else {//install from an input stream. There are some errors here, remember to fix it in the future.!!!!!!!!!!!!!!!!!
            installProposalRequest.setChaincodeInputStream(
            		generateTarGzInputStream(
            			Paths.get("src/test/fixture/sdkintegration/gocc/sample1/src/github.com", CHAIN_CODE_PATH).toFile(),
            		//Paths.get("src", "sample2", "src", CHAIN_CODE_PATH).toFile(),
            		Paths.get("src/github.com", CHAIN_CODE_PATH).toString()
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
	public static Channel getChannel(Boolean newChannel, HFClient client) throws Exception {
		if(newChannel) {
			//客户端，org， 链名字，
			return constructNewChannel(client, org1, FOO_CHAIN_NAME, FOO_CONFIGTX);
		} else {
			return constructExistedChannel(client, org1, TEST_USER1_NAME, FOO_CHAIN_NAME);
		}
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
            MyDebug.printProperties("Peer propeties", peer.getProperties());
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
        MyDebug.printProperties("orderer propeties", anOrderer.getProperties());
        File txFile = new File(configTx);
        ChannelConfiguration chainConfiguration = new ChannelConfiguration(txFile);
        
        client.setUserContext(sampleOrg.getPeerAdmin());
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
            System.out.println(format("Peer %s joined chain %s", peerName, channelName));
            sampleOrg.addPeer(peer);
            MyDebug.printProperties("Peer propeties", peer.getProperties());
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
