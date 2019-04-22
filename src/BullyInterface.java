package src;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BullyInterface extends Remote {
	String startElect(String NodeID) throws RemoteException;
	void sendOK(String NodeID) throws RemoteException;
	void sendCoordinator(String NodeID) throws RemoteException;
}