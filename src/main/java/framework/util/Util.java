package framework.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Util {
	
	public static void toFile(String fileName, String content) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			out.write(content);
			out.close();
		}
		catch (IOException e) {
			System.err.println(e.getMessage());		
		}
	}

}
