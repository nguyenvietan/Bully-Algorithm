package src;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class Node1 implements BullyInterface {

	static final String Node0_IP = "143.248.146.153";
	static final String Node1_IP = "143.248.146.155";
	static final String Node2_IP = "143.248.146.157";
	static List<String> nodes = new ArrayList<>();

	static final String thisNode = Node1_IP;
	static final String initNode = Node0_IP;

	public Node1() {}

	public String startElect(String nodeID) {
		if (nodeID.equals(thisNode)){
			for (int i = nodes.indexOf(nodeID) + 1; i < nodes.size(); i++){
                                try {
                                        Registry registry = LocateRegistry.getRegistry(nodes.get(i));
                                        BullyInterface stub = (BullyInterface) registry.lookup("BullyInterface");
                                        stub.startElect(thisNode);
                                } catch (Exception e){
                                        System.err.println("Node " + nodes.get(i) + " exception: " + e.toString());
                                        e.printStackTrace();
                                }
                        }
		} else{
			System.out.println("Receive ELECTION massage from " + nodeID);

			boolean IS_EXIST_HIGHER = false;
			try {
				Registry registry = LocateRegistry.getRegistry(nodeID);
				BullyInterface stub = (BullyInterface) registry.lookup("BullyInterface");
				stub.sendOK(thisNode);
				for (int i = nodes.indexOf(thisNode) + 1; i < nodes.size(); i++){
                                        try {
                                                Registry registry2 = LocateRegistry.getRegistry(nodes.get(i));
                                                BullyInterface stub2 = (BullyInterface) registry2.lookup("BullyInterface");
                                                stub2.startElect(thisNode);
                                                IS_EXIST_HIGHER = true;
                                        } catch (Exception e){
                                                System.err.println("Node " + nodes.get(i) + " exception: " + e.toString());
                                                e.printStackTrace();
                                        }
                                }
                                if(!IS_EXIST_HIGHER){
                                        System.out.println("You're chosen to be the COORDINATOR.");
                                        for (int i = nodes.indexOf(thisNode) - 1; i >= 0; i--){
                                                try {
                                                        Registry registry3 = LocateRegistry.getRegistry(nodes.get(i));
                                                        BullyInterface stub3 = (BullyInterface) registry3.lookup("BullyInterface");
                                                        stub3.sendCoordinator(thisNode);
                                                        IS_EXIST_HIGHER = true;
                                                } catch (Exception e){
                                                        System.err.println("Node " + nodes.get(i) + " exception: " + e.toString());
                                                        e.printStackTrace();
                                                }
                                        }
                                }
			} catch (Exception e) {
				System.err.println("Node" + thisNode + " exception: " + e.toString());
				e.printStackTrace();
			}
		}
		return "";
	}

	public void sendOK(String NodeID){
		System.out.println("Receive OK message from " + NodeID);
	}

	public void sendCoordinator(String nodeID) {
		System.out.println("The newly elected COORDINATOR: " + nodeID);
	}

	public static void initialize(){
		nodes.add(Node0_IP);
		nodes.add(Node1_IP);
		nodes.add(Node2_IP);
	}


	public static void main (String[] args) {
		initialize();
		try {
                        Node1 obj = new Node1();
                        BullyInterface stub = (BullyInterface) UnicastRemoteObject.exportObject(obj, 0);
                        Registry registry = LocateRegistry.getRegistry();
                        registry.bind("BullyInterface", stub);
                        System.err.println("Node1 ready");

			if (thisNode.equals(initNode)){
                        	obj.startElect(thisNode);
			}

                } catch (Exception e) {
                        System.err.println("Node1 exception: " + e.toString());
                        e.printStackTrace();
                }

	}
}