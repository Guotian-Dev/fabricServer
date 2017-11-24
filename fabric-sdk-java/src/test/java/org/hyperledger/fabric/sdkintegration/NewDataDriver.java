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
public class NewDataDriver {
	//日志打印
	private static final Log logger = LogFactory.getLog(NewDataDriver.class);
	
	
	private static String testTxID = null;
	private static Collection<ProposalResponse> responses = null;
	private static Collection<ProposalResponse> successful = new LinkedList<>();
	private static Collection<ProposalResponse> failed = new LinkedList<>();
	
	private static Properties pro = new Properties();
	//SampleOrg new SampleOrg("peerOrg1", "Org1MSP")
	private static SampleOrg org1;
	//= getSampleStore(true, SAMPLE_STORE);
	private static SampleStore store;
	
	private static String NAME1;
	private static String MSPID1;
	
	/////////////////////org2///////////////
	
	private static String NAME2;
	private static String MSPID2;
	
	private static SampleOrg org2;
	private static final String PEER_ORG2_DN;
    private static String peers2;
    private static String eventHubs2;
    
    private static String PEER_ORG2_USER_ADMIN_CERT;
    private static String PEER_ORG2_ADMIN_KEY;
    
    private static Channel chain2;
    private static HFClient client2;
    
	////////////////////org2////////////////
	
	
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
    
    private static ChaincodeID initChaincodeID;
    
    //没用？
    private ChaincodeID initChaincodeID2;
    
    // private static HFClient hf_client = null;
    
    //从properties文件中加载属性值
    static{
    	//资源放到了src/test/resources中
//        InputStream in = DataDriver.class.getResourceAsStream("/dataconf.properties");
    	
    	//从磁盘读取配置文件
    	InputStream in = null;
		try {
			in = new BufferedInputStream (new FileInputStream("C:/res/conf.properties"));
//			in = new BufferedInputStream (new FileInputStream("/opt/web/conf.properties"));
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
        
        ///////////////////////org2///////////////////
        NAME2 = pro.getProperty("NAME2");
        MSPID2 = pro.getProperty("MSPID2");
        
        PEER_ORG2_DN=(String) pro.get("PEER_ORG2_DN");
        peers2 = (String) pro.get("peers2");
        eventHubs2 = (String) pro.get("eventHubs2");
        PEER_ORG2_USER_ADMIN_CERT = (String)pro.get("PEER_ORG2_USER_ADMIN_CERT");
        PEER_ORG2_ADMIN_KEY = (String)pro.get("PEER_ORG2_ADMIN_KEY");
        ///////////////////////org2///////////////////
        
        NAME1 = pro.getProperty("NAME1");
        MSPID1 = pro.getProperty("MSPID1");
        
        peers = (String) pro.get("peers");
        eventHubs = (String) pro.get("eventHubs");
        
        orderers = (String) pro.get("orderers");
        location = (String) pro.get("location");
    }

    //构造Driver
	public NewDataDriver() {
		
		Channel initChannel = initChannel();
		initChaincodeID = initChaincodeID(org1, initChannel);
		
	}
	
	/**
	 * initChaincodeID
	 * @param sampleOrg
	 * @param chain
	 * @param client
	 * @return
	 */
	public ChaincodeID initChaincodeID(SampleOrg sampleOrg, Channel chain) {
		ChaincodeID chainCodeID = null;
		HFClient client = getHFClient(); 
		try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            Set<Peer> peers = (Set<Peer>) chain.getPeers();
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
	 * 配置chainCode
	 * @param client
	 * @param peers
	 * @param chainCodeID
	 * @param directory
	 * @throws Exception
	 */
	//chainCodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH).build();
	public void installChainCode(HFClient client, Set<Peer> peers, ChaincodeID chainCodeID, boolean directory) throws Exception {
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
	 * 初始化channel
	 * @return
	 */
	public Channel initChannel() {
		SampleStore store = getSampleStore(true, SAMPLE_STORE);
		SampleOrg org1 = new SampleOrg(NAME1, MSPID1);
		org1 = initOrg(org1, peers, eventHubs, orderers, store, PEER_ORG1_USER_ADMIN_CERT, PEER_ORG1_ADMIN_KEY);
		
		SampleOrg org2 = new SampleOrg(NAME2, MSPID2);
		org2 = initOrg(org2, peers2, eventHubs2, orderers, store, PEER_ORG2_USER_ADMIN_CERT, PEER_ORG2_ADMIN_KEY);
		
		try {
			Channel channel = newChannel(org1, FOO_CHAIN_NAME, FOO_CONFIGTX);
			initChannel(org1, channel, TEST_USER1_NAME);
			initChannel(org2, channel, TEST_USER1_NAME);
			channel.initialize();
			
//			Iterator<Peer> iterator = channel.getPeers().iterator();
//			while(iterator.hasNext()) {
//				logger.debug("peer->" + iterator.next().getName());
//			}
			
			return channel;
			
		} catch (Exception e) {
		}
		return null;
	}
	
	
	/**
	 * 初始化通道，将orderer/peer/evehub添加到通道
	 * @param sampleOrg
	 * @param channel
	 * @return
	 */
	public Channel initChannel(SampleOrg sampleOrg, Channel channel, String userName) {
		
		String POD = PEER_ORG1_DN;
		
		if("peerOrg2".equals(sampleOrg.getName())) {
			POD = PEER_ORG2_DN;
		}
		
		HFClient client = getHFClient(); 
		try {
			client.setUserContext(sampleOrg.getUser(userName));
			addPeersToChannel(client, channel, sampleOrg, POD);
			addOrderersToChannel(client, channel, sampleOrg);
			addEventHubsToChannel(client, channel, sampleOrg, POD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	/**
	 * 创建一个新的通道(channel 未init)
	 * @param sampleOrg
	 * @param channelName
	 * @param configTx
	 * @return Channel
	 * @throws Exception
	 */
	public Channel newChannel(SampleOrg sampleOrg, String channelName, String configTx) throws Exception {
        
        Collection<Orderer> orderers = new LinkedList<>();
        HFClient client = getHFClient(); 
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
        logger.debug("orderer propeties:" + anOrderer.getProperties());
        File txFile = new File(configTx);
        ChannelConfiguration chainConfiguration = new ChannelConfiguration(txFile);
        
        Channel newChannel = client.newChannel(
        	channelName, 
        	anOrderer, 
        	chainConfiguration, 
        	client.getChannelConfigurationSignature(
        		chainConfiguration, 
        		sampleOrg.getPeerAdmin()
        	)
        );
        
        return newChannel;
    }
	
	/**
	 * 添加节点到通道
	 * @param newChannel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public void addPeersToChannel(HFClient client, Channel channel, SampleOrg sampleOrg, String POD) throws Exception{
		for (String peerName : sampleOrg.getPeerNames()) {
            String peerLocation = sampleOrg.getPeerLocation(peerName);
            Properties peerProperties = getEndPointProperties(
            	PEER_ORG_BASE + POD + "/peers/" + peerName + "/tls/server.crt",
            	peerName,
            	false
            );
            if (peerProperties == null) {
                peerProperties = new Properties();
            }
            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);
            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
            channel.addPeer(peer);
            sampleOrg.addPeer(peer);
            logger.debug("Peer propeties:" + peer.getProperties());
        }
	}
	
	/**
	 * 添加orderers到通道中
	 * @param client
	 * @param channel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public void addOrderersToChannel(HFClient client, Channel channel, SampleOrg sampleOrg) throws Exception{
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
        	channel.addOrderer(anOrderer);
        }
	}
	
	/**
	 * 添加Event到通道
	 * @param newChannel
	 * @param sampleOrg
	 * @throws Exception
	 */
	public void addEventHubsToChannel(HFClient client, Channel channel, SampleOrg sampleOrg, String POD) throws Exception{
		for (String eventHubName : sampleOrg.getEventHubNames()) {
            EventHub eventHub = client.newEventHub(
            	eventHubName, 
            	sampleOrg.getEventHubLocation(eventHubName),
            	getEndPointProperties(
                	PEER_ORG_BASE + POD + "/peers/" + eventHubName + "/tls/server.crt",
                	eventHubName,
                	false
                )
            );
            channel.addEventHub(eventHub);
        }
	}
	
	/**
	 * 初始化SampleOrg
	 * @param sampleOrg
	 * @param peersInfo
	 * @param eventHubsInfo
	 * @param orderersInfo
	 * @param sampleStore
	 * @param certPath
	 * @param keyPath
	 * @return
	 */
	public SampleOrg initOrg(SampleOrg sampleOrg, String peersInfo, String eventHubsInfo, String orderersInfo, SampleStore sampleStore, String certPath, String keyPath) {
		//初始化org
		sampleOrg = initPEOinfo(sampleOrg, peersInfo, eventHubsInfo, orderersInfo);
		sampleOrg.setAdmin(getSampleUserEnroll(TEST_ADMIN_NAME,sampleOrg, sampleStore, certPath, keyPath));
		//设置节点管理员
		setOrgPeerAdmin(sampleOrg, sampleStore, certPath, keyPath);
		sampleOrg.addUser(getSampleUserEnroll(TEST_USER1_NAME,sampleOrg, sampleStore,certPath, keyPath));
		
		return sampleOrg;
	}
	
    /**
     * 设置节点管理员
     * @param org
     * @param store
     * @param certPath
     * @param keyPath
     * @throws Exception
     */
  	public void setOrgPeerAdmin(SampleOrg org, SampleStore store, String certPath, String keyPath) {
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
	
	
	/**
	 * 获取一个SampleUser
	 * @param userName
	 * @param org
	 * @param store
	 * @param certPath
	 * @param keyPath
	 * @return
	 */
	public SampleUser getSampleUserEnroll(String userName, SampleOrg org, SampleStore store, String certPath, String keyPath) {
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
			// set mpsid enrollment
			user.setMspId(org.getMSPID());
		}
		return user;
	}
	
	/**
	 * 初始化Org
	 * @param sampleOrg
	 * @param peerInfo
	 * @param eventHubsInfo
	 * @param orderersInfo
	 * @return
	 */
	public SampleOrg initPEOinfo(SampleOrg sampleOrg, String peerInfo, String eventHubsInfo, String orderersInfo) {
		
		sampleOrg.setDomainName(PEER_ORG1_DN);
		//CA干掉
		sampleOrg.setCALocation(null);
		
		//org添加peers
        for (String eachone : peerInfo.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            sampleOrg.addPeerLocation(nl[0], grpcTLSify(nl[1]));
        }
        
        //org添加eventHubs
        for (String eachone : eventHubsInfo.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            sampleOrg.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
        }
        
        //org添加orderers
        for (String eachone : orderersInfo.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            sampleOrg.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
        }
        
        return sampleOrg;
	}
	
	
    /**
     * 创建一个简单的sampleStore
     * @param fresh
     * @param storePath
     * @return
     */
	public SampleStore getSampleStore(boolean fresh, String storePath) {
		File sampleStoreFile = new File(storePath);
        if (sampleStoreFile.exists() && fresh) { //For testing start fresh
            sampleStoreFile.delete();
            logger.debug(("----getSampleStore: Deleted old sampleStoreFile: \n" + sampleStoreFile.getAbsolutePath()));
        }
        SampleStore sampleStore = new SampleStore(sampleStoreFile);
        return sampleStore;
	}
	
	
	/**
	 * 真正的将chainCode配置上
	 * @param client
	 * @param chain
	 * @param chainCodeID
	 * @throws Exception
	 */
	public void sendInstantiationProposal(HFClient client, Channel chain, ChaincodeID chainCodeID, String[] initData) throws Exception {
        
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
	
	public InputStream generateTarGzInputStream(File src, String pathPrefix) throws IOException {
        File sourceDirectory = src;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(500000);

        String sourcePath = sourceDirectory.getAbsolutePath();

        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new GzipCompressorOutputStream(new BufferedOutputStream(bos)));
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);

        try {
            Collection<File> childrenFiles = org.apache.commons.io.FileUtils.listFiles(sourceDirectory, null, true);

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
	 * org 设置HFClient 通信所用的
	 * @param org
	 * @throws Exception
	 */
	public HFClient getHFClient() {
		HFClient hf_client = HFClient.createNewInstance();
		try {
			hf_client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return hf_client;
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
 