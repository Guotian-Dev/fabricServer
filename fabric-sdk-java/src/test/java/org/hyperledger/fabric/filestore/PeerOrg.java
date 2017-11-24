package org.hyperledger.fabric.filestore;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class PeerOrg {
    final String name;
    final String mspid;
    HFCAClient caClient;

    Map<String, User> userMap = new HashMap<>();
    Map<String, String> peerLocations = new HashMap<>();
    Map<String, String> ordererLocations = new HashMap<>();
    Map<String, String> eventHubLocations = new HashMap<>();
    Set<Peer> peers = new HashSet<>();
    private PeerUser caAdmin;
    private String caLocation;
    private Properties caProperties = null;

    private PeerUser peerAdmin;

    private String domainName;

    public PeerOrg(String name, String mspid) {
        this.name = name;
        this.mspid = mspid;
    }

    public PeerUser getCAAdmin() {
        return caAdmin;
    }

    public void setCAAdmin(PeerUser caAdmin) {
        this.caAdmin = caAdmin;
    }

    public String getMSPID() {
        return mspid;
    }

    public String getCALocation() {
        return this.caLocation;
    }

    public void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    public void addPeerLocation(String name, String location) {
        peerLocations.put(name, location);
    }

    public void addOrdererLocation(String name, String location) {
        ordererLocations.put(name, location);
    }

    public void addEventHubLocation(String name, String location) {
        eventHubLocations.put(name, location);
    }

    public String getPeerLocation(String name) {
        return peerLocations.get(name);
    }

    public String getOrdererLocation(String name) {
        return ordererLocations.get(name);
    }

    public String getEventHubLocation(String name) {
        return eventHubLocations.get(name);
    }

    public Set<String> getPeerNames() {
        return Collections.unmodifiableSet(peerLocations.keySet());
    }


    public Set<String> getOrdererNames() {
        return Collections.unmodifiableSet(ordererLocations.keySet());
    }

    public Set<String> getEventHubNames() {
        return Collections.unmodifiableSet(eventHubLocations.keySet());
    }

    public HFCAClient getCAClient() {
        return caClient;
    }

    public void setCAClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    public String getName() {
        return name;
    }

    public void addUser(PeerUser user) {
        userMap.put(user.getName(), user);
    }

    public User getUser(String name) {
        return userMap.get(name);
    }

    public Collection<String> getOrdererLocations() {
        return Collections.unmodifiableCollection(ordererLocations.values());
    }

    public Collection<String> getEventHubLocations() {
        return Collections.unmodifiableCollection(eventHubLocations.values());
    }

    public Set<Peer> getPeers() {
        return Collections.unmodifiableSet(peers);
    }

    public void addPeer(Peer peer) {
        peers.add(peer);
    }

    public void setCAProperties(Properties caProperties) {
        this.caProperties = caProperties;
    }

    public Properties getCAProperties() {
        return caProperties;
    }

    public PeerUser getPeerAdmin() {
        return peerAdmin;
    }

    public void setPeerAdmin(PeerUser peerAdmin) {
        this.peerAdmin = peerAdmin;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }
}




