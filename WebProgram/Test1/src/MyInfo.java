import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
public class MyInfo {

	public static void main(String[] args) throws SocketException {
		// TODO Auto-generated method stub
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String dottedQuad=addr.getHostAddress(); 
			System.out.println("My Localhost Address is "+dottedQuad);
			Enumeration<NetworkInterface> interfacesEnumeration = NetworkInterface.getNetworkInterfaces();
			while (interfacesEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) interfacesEnumeration.nextElement();
				System.out.println(networkInterface);
			}
		}
		catch(UnknownHostException ex){
			System.err.println(ex.toString());
		}
	}

}
