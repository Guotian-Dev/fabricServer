package org.hyperledger.fabric.sdkintegration;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.BlockInfo;
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
import org.hyperledger.fabric.sdk.TransactionInfo;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TxReadWriteSetInfo;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.hyperledger.fabric.sdk.helper.Config;
import org.hyperledger.fabric.sdk.helper.Utils;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAEnrollment;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

public class MyDriver {
	//private static final String BAR_CHAIN_NAME = "bar";
	//private static final String PEER_ORG2_DN = "org2.example.com";
	//private static final String BAR_CONFIGTX = "src/test/fixture/sdkintegration/e2e-2Orgs/channel/bar.tx";
	//private static SampleOrg org2 = new SampleOrg("peerOrg2", "Org2MSP");
	
	private static final String FOO_CHAIN_NAME = "foo";
    
	
	private static final String TEST_ADMIN_NAME = "admin";
    private static final String TEST_USER1_NAME = "user1";
    private static final String TEST_ADMIN_PASSWORD = "adminpw";
    
    private static final String ORDERER_DN = "example.com";
    private static final String PEER_ORG1_DN = "org1.example.com";

    private static final String CHANNEL_BASE = "src/test/fixture/sdkintegration/e2e-2Orgs/channel/";
    private static final String CRYPTO_BASE = CHANNEL_BASE + "crypto-config/";
    private static final String PEER_ORG_BASE = CRYPTO_BASE + "peerOrganizations/";
    private static final String ORDERER_ORG_BASE = CRYPTO_BASE + "ordererOrganizations/";
    
    private static final String FOO_CONFIGTX = CHANNEL_BASE + "foo.tx";
    private static final String ENDORSEMENT_POLICY = "src/test/fixture/sdkintegration/chaincodeendorsementpolicy.yaml";
    
    //private static final String CHAIN_CODE_NAME = "example_cc_go";
    private static final String CHAIN_CODE_NAME = "lvqinghao888_go";
    //private static final String CHAIN_CODE_PATH = "github.com/example_cc";
    private static final String CHAIN_CODE_PATH = "example_cc"; 
    //src/example_cc, the src prefix is automatically added by sdk
    private static final String CHAIN_CODE_VERSION = "1";
    
    
    //ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_TLS=true
    //ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_CA_TLS=--tls.enabled
    //ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_TLS=false
    //ORG_HYPERLEDGER_FABRIC_SDKTEST_INTEGRATIONTESTS_CA_TLS=
    //Do remember to modify these ENVs in "src/test/fixture/sdkintegration/.env" file  
    //before restart Docker-compose Fabric network after you modified these boolean flags.
    private final boolean runningTLS = false;
    private final boolean runningFabricCATLS = runningTLS;
    private final boolean runningFabricTLS = runningTLS;
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //Set TLS flag to true, it is OK to communicate with peer, install chiancode etc.
    //But peer can't start docker container(VM) to initiate chaincode. 
    //perhaps peer can't use TLS to communicate with chaincode docker container(VM)
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    
    
    private static final String SAMPLE_STORE = "/tmp/fabric-sample-store";
    private static final Config config = Config.getConfig();
    private static final SampleOrg org1 = new SampleOrg("peerOrg1", "Org1MSP");
    private static final SampleStore store = MyDriver.getSampleStore(true, SAMPLE_STORE);
    
    
	public static SampleStore getSampleStore(boolean fresh, String storePath) {
		File sampleStoreFile = new File(storePath);
        if (sampleStoreFile.exists() && fresh) { //For testing start fresh
            sampleStoreFile.delete();
            System.out.println("----getSampleStore: Deleted old sampleStoreFile: \n" + sampleStoreFile.getAbsolutePath());
        }
        SampleStore sampleStore = new SampleStore(sampleStoreFile);
        return sampleStore;
	}
    
	
	
	public static void main2(String args[]) {
        End2endIT test = new End2endIT();
        try {
            test.checkConfig();
            
            //test.printInfo();
            
            test.setup();
            //test.clearConfig();
            
            //test.printInfo();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
	
	
	
	
    
	public static void main3(String[] args) throws Exception {
		//MyDriver.config.printInfo();
		//System.exit(0);
		MyDriver.e2eInit();
		final ChaincodeID chainCodeID = ChaincodeID.newBuilder()
        		.setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION)
                .setPath(CHAIN_CODE_PATH).build();
		//System.exit(0);
		Channel chain;
		boolean newChannel = true;
		if(newChannel) {
			chain = MyDriver.constructNewChannel(MyDriver.org1, FOO_CHAIN_NAME, FOO_CONFIGTX);
			MyDriver.runNewChannel(MyDriver.org1, TEST_USER1_NAME, chain);
		} else {
			chain = MyDriver.constructExistedChannel(MyDriver.org1, TEST_USER1_NAME, FOO_CHAIN_NAME);
	        MyDriver.runExistedChannel(MyDriver.org1, TEST_USER1_NAME, chain, chainCodeID, false);
		}
		//DigitalAsset.channelQuerry(chain, DigitalAsset.org1);
		chain.shutdown(true);		
        //MyDriver.org1.printInfo();
	}
	
	public static void main(String[] args) throws Exception {
		//MyDriver.config.printInfo();
		//System.exit(0);
		MyDriver.e2eInit1();
		
		final ChaincodeID chainCodeID = ChaincodeID.newBuilder()
        		.setName(CHAIN_CODE_NAME)
                .setVersion(CHAIN_CODE_VERSION)
                .setPath(CHAIN_CODE_PATH).build();

		//System.exit(0);
		Channel chain;
		boolean newChannel = false;
		if(newChannel) {
			chain = MyDriver.constructNewChannel(MyDriver.org1, FOO_CHAIN_NAME, FOO_CONFIGTX);
			MyDriver.runNewChannel1(MyDriver.org1, TEST_USER1_NAME, chain);
		} else {
			chain = MyDriver.constructExistedChannel(MyDriver.org1, TEST_USER1_NAME, FOO_CHAIN_NAME);
	        MyDriver.runExistedChannel(MyDriver.org1, TEST_USER1_NAME, chain, chainCodeID, false);
		}
		//DigitalAsset.channelQuerry(chain, DigitalAsset.org1);
		chain.shutdown(true);		
        //MyDriver.org1.printInfo();
		
	}
	
	
    private static final String PEER_ORG1_USER_ADMIN_CERT = PEER_ORG_BASE + PEER_ORG1_DN + 
    	"/users/Admin@org1.example.com/msp/signcerts/Admin@org1.example.com-cert.pem";
    private static final String PEER_ORG1_ADMIN_KEY = PEER_ORG_BASE + PEER_ORG1_DN + 
    	"/users/Admin@org1.example.com/msp/keystore/6b32e59640c594cf633ad8c64b5958ef7e5ba2a205cfeefd44a9e982ce624d93_sk";	
//    "/users/Admin@org1.example.com/msp/keystore/f1022dfda62d66248343d3af08e7bb94270cda5162eae5ad587d36196054265f_sk";	
    private static final String PEER_ORG1_CA_CERT = PEER_ORG_BASE + PEER_ORG1_DN + "/ca/ca.org1.example.com-cert.pem";

    private static void e2eInit() throws Exception {
		MyDriver e2e = new MyDriver();
		e2e.initOrg1();
		e2e.setOrgCAClient(
			MyDriver.org1, 
			PEER_ORG1_CA_CERT
		);
		e2e.setOrgAdmin(
			TEST_ADMIN_NAME, 
			TEST_ADMIN_PASSWORD, 
			MyDriver.org1, 
			MyDriver.store
		);
		e2e.setOrgPeerAdmin(
			MyDriver.org1, 
			MyDriver.store, 
			PEER_ORG1_USER_ADMIN_CERT, 
			PEER_ORG1_ADMIN_KEY
		);
		e2e.addOrgUser(
			TEST_USER1_NAME, 
			"org1.department1", 
			MyDriver.org1, 
			MyDriver.store
		);
		e2e.setOrgHFClient(MyDriver.org1);
		//MyDriver.org1.printInfo();
	}
    
    private static void e2eInit1() throws Exception {
		MyDriver e2e = new MyDriver();
		e2e.initOrg1();
		e2e.setOrgCAClient(
				MyDriver.org1, 
				PEER_ORG1_CA_CERT
		);
		e2e.setOrgAdmin(
				TEST_ADMIN_NAME, 
				TEST_ADMIN_PASSWORD, 
				MyDriver.org1, 
				MyDriver.store
		);
		e2e.setOrgPeerAdmin(
			MyDriver.org1, 
			MyDriver.store, 
			PEER_ORG1_USER_ADMIN_CERT, 
			PEER_ORG1_ADMIN_KEY
		);
		//String userName, SampleOrg org, SampleStore store, String certPath, String keyPath
		e2e.addOrgUser2(
			TEST_USER1_NAME,
			MyDriver.org1, 
			MyDriver.store,
			PEER_ORG1_USER_ADMIN_CERT, 
			PEER_ORG1_ADMIN_KEY
		);
		e2e.setOrgHFClient(MyDriver.org1);
		//MyDriver.org1.printInfo();
	}
	
    
	private String grpcTLSify(String location) {
        location = location.trim();
        Exception e = Utils.checkGrpcUrl(location);
        if (e != null) {
            throw new RuntimeException(String.format("Bad TEST parameters for grpc url %s", location), e);
        }
        return runningFabricTLS ? location.replaceFirst("^grpc://", "grpcs://") : location;
    }
	

    private String httpTLSify(String location) {
        location = location.trim();
        return runningFabricCATLS ? location.replaceFirst("^http://", "https://") : location;
    }
	
    
	private void initOrg1() {
		MyDriver.org1.setDomainName("org1.example.com");
        MyDriver.org1.setCALocation(httpTLSify("http://192.168.5.236:7054"));
		String peers = "peer0.org1.example.com@grpc://192.168.5.236:7051, peer1.org1.example.com@grpc://192.168.5.236:7056";
        for (String eachone : peers.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            MyDriver.org1.addPeerLocation(nl[0], grpcTLSify(nl[1]));
        }
        String eventHubs = "peer0.org1.example.com@grpc://192.168.5.236:7053, peer1.org1.example.com@grpc://192.168.5.236:7058";
        for (String eachone : eventHubs.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            MyDriver.org1.addEventHubLocation(nl[0], grpcTLSify(nl[1]));
        }
        String orderers = "orderer.example.com@grpc://192.168.5.236:7050";
        for (String eachone : orderers.split("[ \t]*,[ \t]*")) {
            String[] nl = eachone.split("[ \t]*@[ \t]*");
            MyDriver.org1.addOrdererLocation(nl[0], grpcTLSify(nl[1]));
        }
	}
	
	
	public Properties getCAProperties(String tlsCert) {
		Properties properties = null;
		if (runningFabricCATLS) {
            File cf = new File(tlsCert);
            if (!cf.exists() || !cf.isFile()) {
                throw new RuntimeException("Missing cert file " + cf.getAbsolutePath());
            }
            properties = new Properties();
            
            properties.setProperty("pemFile", cf.getAbsolutePath());
            properties.setProperty("allowAllHostNames", "true");
            //!!!!!!testing environment only NOT FOR PRODUCTION!
            //to avoid/override hostname verifier for SSL connections
            
            //if(trust) { //testing environment only NOT FOR PRODUCTION!
            //properties.setProperty("trustServerCertificate", "true"); 
            //}
            //properties.setProperty("hostnameOverride", "ca.org1.example.com");
            //properties.setProperty("sslProvider", "openSSL");
            //properties.setProperty("negotiationType", "TLS");
        }		
		return properties;
	}
	
	
	public void setOrgCAClient(SampleOrg org, String tlsCert) throws Exception {
		
		Properties properties = getCAProperties(tlsCert);
		HFCAClient ca = HFCAClient.createNewInstance(org.getCALocation(), properties);
    	ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite()); //new CryptoPrimitives()
    	org.setCAClient(ca);
    	org.setCAProperties(properties);
	}
	
	
	private static HFClient hf_client = null;
	public void setOrgHFClient(SampleOrg org) throws Exception {
		hf_client = HFClient.createNewInstance();
		hf_client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite()); //new CryptoPrimitives()
	}
	
	
	//TEST_ADMIN_NAME, "adminpw"
	public void setOrgAdmin(String userName, String password, SampleOrg org, SampleStore store) throws Exception {
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
	
	
	//A special user that can crate channels, join peers, install chain code, jump tall blockchains in a single leap!
	public void setOrgPeerAdmin(SampleOrg org, SampleStore store, String certPath, String keyPath) throws Exception {
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
	
	
	//TEST_USER1_NAME, "org1.department1"
	public void addOrgUser(String userName, String affiliation, SampleOrg org, SampleStore store) throws Exception {
		SampleUser user = store.getMember(userName, org.getName());
		HFCAClient caClient = org.getCAClient();
        if (!user.isRegistered()) {  // users need to be registered AND enrolled
            RegistrationRequest rr = new RegistrationRequest(user.getName(), affiliation);
            user.setEnrollmentSecret(caClient.register(rr, org.getAdmin()));
        }
        if (!user.isEnrolled()) {
            user.setEnrollment(caClient.enroll(user.getName(), user.getEnrollmentSecret()));
            user.setMspId(org.getMSPID());
        }
        org.addUser(user); //Remember user belongs to this Org
	}
	//getEnrollmentFromFile
	public void addOrgUser2(String userName, SampleOrg org, SampleStore store, String certPath, String keyPath) throws Exception {
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
	
	public void addOrgUser1(String userName, String affiliation, SampleOrg org, SampleStore store) throws Exception {
		SampleUser user = store.getMember(userName, org.getName());
		
//		System.out.println("******************");
//		System.out.println("user:" + user.getName());
//		System.out.println("******************");
		
		HFCAClient caClient = org.getCAClient();
		
		HFCAEnrollment hFCAEnrollment = null;
		
		//readFile
		String hFCAEnrollmentFileName = user.getName() + "hFCAEnrollmentFile.dat";
		File hFCAEnrollmentFile = new File("src/test/resources/" + hFCAEnrollmentFileName);
		if(hFCAEnrollmentFile.exists()) {
			hFCAEnrollment = readEnrollmentFromFile(hFCAEnrollmentFile);
		}
		
		//generate hfCAEnrollment and write
		if(hFCAEnrollment == null) {
			
			if (!user.isRegistered()) { // users need to be registered AND enrolled
				RegistrationRequest rr = new RegistrationRequest(user.getName(), affiliation);
				user.setEnrollmentSecret(caClient.register(rr, org.getAdmin()));
			}
			if (!user.isEnrolled()) {
				hFCAEnrollment = (HFCAEnrollment) caClient.enroll(user.getName(), user.getEnrollmentSecret());
			}
			
			//bao cun hFCAEnrollment
			writeEnrollmentToFile(hFCAEnrollmentFile, hFCAEnrollment);
		}
		
		//set mpsid enrollment
		user.setMspId(org.getMSPID());
		user.setEnrollment(hFCAEnrollment);
		
        org.addUser(user); //Remember user belongs to this Org
	}
	
	public static void writeEnrollmentToFile(File file, HFCAEnrollment hFCAEnrollment) {
		
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileOutputStream out = null;
		ObjectOutputStream objOut = null;
		
		try{
			out = new FileOutputStream(file);
			objOut = new ObjectOutputStream(out);
			objOut.writeObject(hFCAEnrollment);
			objOut.flush();
			System.out.println("write object success!");
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(objOut != null) {
				try {
					objOut.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static HFCAEnrollment readEnrollmentFromFile(File file){
		
		FileInputStream in = null;
		ObjectInputStream objIn = null;
		HFCAEnrollment hFCAEnrollment = null;
		
		try {
			in = new FileInputStream(file);
			objIn = new ObjectInputStream(in);
			hFCAEnrollment = (HFCAEnrollment) objIn.readObject();
//			System.out.println("*************************");
//			System.out.println("write object success!");
//			System.out.println("HFCAEnrollment" + hFCAEnrollment.getCert() + hFCAEnrollment.getKey());
//			System.out.println("*************************");
		} catch (Exception e) {
		} finally {
			if(objIn != null) {
				try {
					objIn.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		
		return hFCAEnrollment;
	}
			
	public static void addPeersToChannel(Channel newChannel, SampleOrg sampleOrg) throws Exception{
		HFClient client = hf_client;
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
	
	
	public static void addOrderersToChannel(Channel newChannel, SampleOrg sampleOrg) throws Exception{
		HFClient client = hf_client;
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
	
	
	public static void addEventHubsToChannel(Channel newChannel, SampleOrg sampleOrg) throws Exception{
		HFClient client = hf_client;
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
    
	
	public static Channel constructExistedChannel(SampleOrg sampleOrg, String userName, String channelName) throws Exception {
        HFClient client = hf_client;
        client.setUserContext(sampleOrg.getUser(userName));
        Channel newChannel = client.newChannel(channelName);
        addPeersToChannel(newChannel, sampleOrg);
        addEventHubsToChannel(newChannel, sampleOrg);
        addOrderersToChannel(newChannel, sampleOrg);
        newChannel.initialize();        
        return newChannel;
    }
	
	
	public static Channel constructNewChannel(SampleOrg sampleOrg, String channelName, String configTx) throws Exception {
        HFClient client = hf_client;
        client.setUserContext(sampleOrg.getPeerAdmin());
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
	
	
	private static String testTxID = null;
	private static Collection<ProposalResponse> responses = null;
	private static Collection<ProposalResponse> successful = new LinkedList<>();
	private static Collection<ProposalResponse> failed = new LinkedList<>();
	
	
    public static void collectResponses(boolean verifyResponse) {
		successful.clear();
        failed.clear();
        boolean condition = false;
        for (ProposalResponse response : responses) {
        	condition = (response.getStatus() == ProposalResponse.Status.SUCCESS);
        	if(verifyResponse) {
        		condition &= response.isVerified();
        	} 
            if(condition) {
                successful.add(response);
                System.out.println(
                    format("Succesful proposal response Txid: %s from peer %s", 
                    	response.getTransactionID(), 
                    	response.getPeer().getName()
                    )
                );
            } else {
                failed.add(response);
            }
        }
        System.out.println(
        	format(
        		"Received %d proposal responses. Successful&Verified: %d . Failed: %d", 
        		responses.size(), 
        		successful.size(), 
        		failed.size()
        	)
        );
        if (failed.size() > 0) {
            ProposalResponse first = failed.iterator().next();
            fail(
            	"Not enough endorsers for instantiate :" + successful.size() + 
            	"endorser failed with " + first.getMessage() + 
            	". Was verified:" + first.isVerified()
            );
        }      
    }
	
	
	//chainCodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH).build();
	public static void installChainCode(HFClient client, Set<Peer> peers, ChaincodeID chainCodeID, boolean directory) throws Exception {
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chainCodeID);
        if (directory) {//install from file system directory.
            installProposalRequest.setChaincodeSourceLocation(new File("src/smartcc"));
        } else {//install from an input stream. There are some errors here, remember to fix it in the future.!!!!!!!!!!!!!!!!!
            installProposalRequest.setChaincodeInputStream(
            	Util.generateTarGzInputStream(
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
	
	//chainCodeID = ChaincodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION).setPath(CHAIN_CODE_PATH).build();
	public static void installChainCode1(HFClient client, Set<Peer> peers, ChaincodeID chainCodeID, boolean directory) throws Exception {
        InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
        installProposalRequest.setChaincodeID(chainCodeID);
        if (directory) {//install from file system directory.
            installProposalRequest.setChaincodeSourceLocation(new File("src/smartcc"));
        } else {//install from an input stream. There are some errors here, remember to fix it in the future.!!!!!!!!!!!!!!!!!
            installProposalRequest.setChaincodeInputStream(
            	Util.generateTarGzInputStream(
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
	
	
    public static void instantiateChainCode(HFClient client, Channel chain, ChaincodeID chainCodeID) throws Exception {
        chain.setTransactionWaitTime(Integer.parseInt("100000"));
        chain.setDeployWaitTime(Integer.parseInt("120000"));
        Collection<Orderer> orderers = chain.getOrderers();
        sendInstantiationProposal(client, chain, chainCodeID);
        //Send instantiate transaction to orderer
        chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            //waitOnFabric(0);
            assertTrue(transactionEvent.isValid()); // must be valid here.
            System.out.println(
            	format(
            		"Finished instantiate transaction with id %s", 
            		transactionEvent.getTransactionID()
            	)
            );
            testTxID = transactionEvent.getTransactionID(); // used in the channel queries later
            //query(hf_client, chain, chainCodeID, transactionEvent, "a");
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
    
    
    public static void invokeChainCode(HFClient client, Channel chain, ChaincodeID chainCodeID) throws Exception {
        chain.setTransactionWaitTime(Integer.parseInt("100000"));
        chain.setDeployWaitTime(Integer.parseInt("120000"));
        Collection<Orderer> orderers = chain.getOrderers();
        sendTransactionProposal(client, chain, chainCodeID);
        chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            //waitOnFabric(0);
            assertTrue(transactionEvent.isValid()); //must be valid here.
            System.out.println("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
            try {
                assertTrue(transactionEvent.isValid()); //must be valid here.
                System.out.println(
                	format(
                		"Finished transaction with id %s", 
                		transactionEvent.getTransactionID()
                	)
                );
                testTxID = transactionEvent.getTransactionID(); //used in the channel queries later
                //query(hf_client, chain, chainCodeID, transactionEvent, "a");
            } catch (Exception e) {
            	System.out.println("Caught an exception while invoking chaincode");
                e.printStackTrace();
                fail("Failed invoking chaincode with error : " + e.getMessage());
            } 
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
    
	
	public static void sendInstantiationProposal(HFClient client, Channel chain, ChaincodeID chainCodeID) throws Exception {
        InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
        instantiateProposalRequest.setProposalWaitTime(Integer.parseInt("120000"));
        instantiateProposalRequest.setChaincodeID(chainCodeID);
        instantiateProposalRequest.setFcn("init");
        instantiateProposalRequest.setArgs(new String[] {"a", "500", "b", "200"});
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
	
	
	public static void sendTransactionProposal(HFClient client, Channel chain, ChaincodeID chainCodeID) throws Exception  {
        //Send transaction proposal to all peers
        TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chainCodeID);
        transactionProposalRequest.setFcn("invoke");
        transactionProposalRequest.setArgs(new String[] {"move", "a", "b", "100"});
        Map<String, byte[]> tm2 = new HashMap<>();
        tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
        tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
        tm2.put("result", ":)".getBytes(UTF_8));  //This should be returned see chaincode.
        transactionProposalRequest.setTransientMap(tm2);
        //Channelcode endorsement policies, some default ????????????????????????????????????????????
        responses = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
        collectResponses(true);
        ProposalResponse resp = responses.iterator().next();
        byte[] resultBytes = resp.getChaincodeActionResponsePayload(); //This is the data returned by the chaincode.
        String resultAsString = null;
        if (resultBytes != null) {
            resultAsString = new String(resultBytes, "UTF-8");
        }
        assertEquals(":)", resultAsString);
        assertEquals(200, resp.getChaincodeActionResponseStatus()); //Channelcode's status.
        TxReadWriteSetInfo readWriteSetInfo = resp.getChaincodeActionResponseReadWriteSetInfo();
        //See blockwaler below how to transverse this
        assertNotNull(readWriteSetInfo);
        assertTrue(readWriteSetInfo.getNsRwsetCount() > 0);
        ChaincodeID cid = resp.getChaincodeID();
        assertNotNull(cid);
        assertEquals(CHAIN_CODE_PATH, cid.getPath());
        assertEquals(CHAIN_CODE_NAME, cid.getName());
        assertEquals(CHAIN_CODE_VERSION, cid.getVersion());    
	}
	
	
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
	
	
	public static void channelQuerry(Channel chain, SampleOrg sampleOrg) throws Exception {
        //Only send channel queries to peers that are in the same org as the SDK user context
        //Get the peers from the current org being used, and pick one randomly to query.
        Set<Peer> peerSet = sampleOrg.getPeers();
        Peer queryPeer = peerSet.iterator().next();
        System.out.println("Channel queries, using peer: " + queryPeer.getName());
        BlockchainInfo channelInfo = chain.queryBlockchainInfo(queryPeer);
        System.out.println("Channel info for: " + chain.getName());
        System.out.println("Channel height: " + channelInfo.getHeight());
        String chainCurrentHash = Hex.encodeHexString(channelInfo.getCurrentBlockHash());
        String chainPreviousHash = Hex.encodeHexString(channelInfo.getPreviousBlockHash());
        System.out.println("Channel current block hash: " + chainCurrentHash);
        System.out.println("Channel previous block hash: " + chainPreviousHash);
        //Query by block number. Should return latest block, i.e. block number 2
        BlockInfo returnedBlock = chain.queryBlockByNumber(queryPeer, channelInfo.getHeight() - 1);
        String previousHash = Hex.encodeHexString(returnedBlock.getPreviousHash());
        System.out.println(
        	"queryBlockByNumber returned correct block with blockNumber " + 
        	returnedBlock.getBlockNumber() + " \n previous_hash " + previousHash
        );
        assertEquals(channelInfo.getHeight() - 1, returnedBlock.getBlockNumber());
        assertEquals(chainPreviousHash, previousHash);
        //Query by block hash. Using latest block's previous hash so should return block number 1
        byte[] hashQuery = returnedBlock.getPreviousHash();
        returnedBlock = chain.queryBlockByHash(queryPeer, hashQuery);
        System.out.println("queryBlockByHash returned block with blockNumber " + returnedBlock.getBlockNumber());
        assertEquals(channelInfo.getHeight() - 2, returnedBlock.getBlockNumber());
        //Query block by TxID. Since it's the last TxID, should be block 2
        returnedBlock = chain.queryBlockByTransactionID(queryPeer, testTxID);
        System.out.println("queryBlockByTxID returned block with blockNumber " + returnedBlock.getBlockNumber());
        assertEquals(channelInfo.getHeight() - 1, returnedBlock.getBlockNumber());
        //Query transaction by ID
        TransactionInfo txInfo = chain.queryTransactionByID(queryPeer, testTxID);
        System.out.println(
        	"queryTransactionByID returned TransactionInfo: txID " + txInfo.getTransactionID() + 
        	"\nvalidation code " + txInfo.getValidationCode().getNumber()
        );
        System.out.println("Done querry Channel: " + chain.getName());
	}
    
	
    public static void runNewChannel(SampleOrg sampleOrg, String userName, Channel chain) {
        try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            Set<Peer> peers = sampleOrg.getPeers();
            Collection<Orderer> orderers = chain.getOrderers();
            ChaincodeID chainCodeID = ChaincodeID.newBuilder()
            		.setName(CHAIN_CODE_NAME)
                    .setVersion(CHAIN_CODE_VERSION)
                    .setPath(CHAIN_CODE_PATH).build();
            HFClient client = hf_client;
            client.setUserContext(sampleOrg.getPeerAdmin());
            installChainCode(client, peers, chainCodeID, true);
            sendInstantiationProposal(client, chain, chainCodeID);
            
            //Send instantiate transaction to orderer
            chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
                //waitOnFabric(0);
                assertTrue(transactionEvent.isValid()); // must be valid to be here.
                System.out.println("Finished instantiate transaction with id" + transactionEvent.getTransactionID());
                try {
                    client.setUserContext(sampleOrg.getUser(userName));
                    sendTransactionProposal(client, chain, chainCodeID);
                    //Send Transaction Transaction to orderer
                    return chain.sendTransaction(successful).get(Integer.parseInt("100000"), TimeUnit.SECONDS);
                } catch (Exception e) {
                	System.out.println("Caught an exception while invoking chaincode");
                    e.printStackTrace();
                    fail("Failed invoking chaincode with error : " + e.getMessage());
                } 
                return null;
            }).thenApply(transactionEvent -> {
            	//waitOnFabric(0);
                assertTrue(transactionEvent.isValid()); // must be valid here.
                System.out.println(
                	format(
                		"Finished transaction with id %s", 
                		transactionEvent.getTransactionID()
                	)
                );
                testTxID = transactionEvent.getTransactionID(); // used in the channel queries later
            	queryByChainCode(client, chain, chainCodeID, "a");
            	queryByChainCode(client, chain, chainCodeID, "b");
            	queryByChainCode(client, chain, chainCodeID, "c");
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
            //channelQuerry(chain, sampleOrg);
        } catch (Exception e) {
        	System.out.println("Caught an exception running chain: " + chain.getName());
            e.printStackTrace();
            fail("Test failed with error : " + e.getMessage());
        }
    }
    
    public static void runNewChannel1(SampleOrg sampleOrg, String userName, Channel chain) {
        try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            Set<Peer> peers = sampleOrg.getPeers();
            Collection<Orderer> orderers = chain.getOrderers();
            ChaincodeID chainCodeID = ChaincodeID.newBuilder()
            		.setName(CHAIN_CODE_NAME)
                    .setVersion(CHAIN_CODE_VERSION)
                    .setPath(CHAIN_CODE_PATH).build();
            HFClient client = hf_client;
            
            client.setUserContext(sampleOrg.getPeerAdmin());
            
            installChainCode1(client, peers, chainCodeID, true);
            sendInstantiationProposal(client, chain, chainCodeID);
            
            //Send instantiate transaction to orderer
            chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            	
//				System.out.println("************************************************************");
//				queryByChainCode(client, chain, chainCodeID, "a");
//				queryByChainCode(client, chain, chainCodeID, "b");
//				System.out.println("************************************************************");
            	
                //waitOnFabric(0);
                assertTrue(transactionEvent.isValid()); // must be valid to be here.
                System.out.println("Finished instantiate transaction with id" + transactionEvent.getTransactionID());
                try {
                    client.setUserContext(sampleOrg.getUser(userName));
                    sendTransactionProposal(client, chain, chainCodeID);
                    //Send Transaction Transaction to orderer
                    return chain.sendTransaction(successful).get(Integer.parseInt("100000"), TimeUnit.SECONDS);
                } catch (Exception e) {
                	System.out.println("Caught an exception while invoking chaincode");
                    e.printStackTrace();
                    fail("Failed invoking chaincode with error : " + e.getMessage());
                } 
                return null;
            }).thenApply(transactionEvent -> {
            	//waitOnFabric(0);
                assertTrue(transactionEvent.isValid()); // must be valid here.
                System.out.println(
                	format(
                		"Finished transaction with id %s", 
                		transactionEvent.getTransactionID()
                	)
                );
                testTxID = transactionEvent.getTransactionID(); // used in the channel queries later
            	queryByChainCode(client, chain, chainCodeID, "a");
            	queryByChainCode(client, chain, chainCodeID, "b");
            	queryByChainCode(client, chain, chainCodeID, "c");
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
            //channelQuerry(chain, sampleOrg);
        } catch (Exception e) {
        	System.out.println("Caught an exception running chain: " + chain.getName());
            e.printStackTrace();
            fail("Test failed with error : " + e.getMessage());
        }
    }
    
    
    public static void runExistedChannel(SampleOrg sampleOrg, String userName, Channel chain, ChaincodeID chainCodeID, boolean instantiateChainCode) {
        try {
            chain.setTransactionWaitTime(Integer.parseInt("100000"));
            chain.setDeployWaitTime(Integer.parseInt("120000"));
            ////Collection<Peer> channelPeers = chain.getPeers();
            Collection<Orderer> orderers = chain.getOrderers();
    		HFClient client = hf_client;
            if(instantiateChainCode) {
            	client.setUserContext(sampleOrg.getPeerAdmin());
            	instantiateChainCode(client, chain, chainCodeID);
            }
            client.setUserContext(sampleOrg.getUser(userName));
            sendTransactionProposal(client, chain, chainCodeID);
            chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
                //waitOnFabric(0);
                assertTrue(transactionEvent.isValid()); // must be valid to be here.
                System.out.println("Finished instantiate transaction with id: " + transactionEvent.getTransactionID());
                try {
                    assertTrue(transactionEvent.isValid()); // must be valid here.
                    System.out.println(
                    	format(
                    		"Finished transaction with id %s", 
                    		transactionEvent.getTransactionID()
                    	)
                    );
                    testTxID = transactionEvent.getTransactionID(); // used in the channel queries later
                	queryByChainCode(client, chain, chainCodeID, "a");
                	queryByChainCode(client, chain, chainCodeID, "b");
                	queryByChainCode(client, chain, chainCodeID, "c");
                    return null;
                } catch (Exception e) {
                	System.out.println("Caught an exception while invoking chaincode");
                    e.printStackTrace();
                    fail("Failed invoking chaincode with error : " + e.getMessage());
                } 
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
            //channelQuerry(chain, sampleOrg);
        } catch (Exception e) {
        	System.out.println("Caught an exception running chain: " + chain.getName());
            e.printStackTrace();
            fail("Test failed with error : " + e.getMessage());
        }
    }

	
}





