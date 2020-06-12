package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bean.Network;

public class Save{
	public static void saveNetwork(Network network, String fileName) {
		try {
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(network);
			oos.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Network readNetwork(String fileName) {
		Network result = null;
		try {
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(fileName));
			result = (Network)ois.readObject();
			ois.close();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}
}