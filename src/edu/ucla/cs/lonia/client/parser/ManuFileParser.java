import java.io.IOException;

public class ManuFileParser {
	public static void main(String[] args) {
		StringBuffer in = new StringBuffer();
		
		try {
			FileUtils.readToBuffer(in, "/usr/share/man/man1/grep.1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
 	} 
}

