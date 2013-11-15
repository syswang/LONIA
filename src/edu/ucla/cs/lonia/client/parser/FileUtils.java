import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileUtils {
    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ArrayList<ResultRow> result = new ArrayList<ResultRow>();
        line = reader.readLine(); // 读取第一行
        int k=0;
        while(line != null) {
        	ResultRow row = new ResultRow();
        	if(line.startsWith(".It Fl")) {
	        	row.setName((replaceSign(line)));	        	
        		line = reader.readLine();
        		StringBuilder lineBuilder = new StringBuilder();        			        	
        		do{
        			line = replaceSign(line);        			
        			lineBuilder = lineBuilder.append(" "+line);
        			line = reader.readLine();        		
        		} while( !line.startsWith(".It") && !line.startsWith(".El") );  
        		row.setDescription(lineBuilder.toString());
        		result.add(row);
        		System.out.println(row.getName());        		
        		System.out.println(row.getDescription());  
        	} else { line = reader.readLine(); } 
        }    
        reader.close();
        is.close();
    }

	public static String readName(String str) {
		String[] names;
		names = str.split(" ");
		return "-"+names[2];
	}
	
	public static String replaceSign(String str){
		str = str.replaceAll(".Nm", "");
		str = str.replaceAll(".Em ", "");
		str = str.replaceAll(".?Fl ", "-");
		str = str.replaceAll(".?Ar ", " ");
		str = str.replaceAll(".Ev ", " ");
		
		if (str.startsWith(".Sm")) { return ""; }
		
		str = replaceWordQuote(str, ".Sq", "'", "'");
		if (str.contains(".Sq")) { str = replaceWordQuote(str, ".Sq", "'", "'"); }
		if (str.contains("Sq")) { str = replaceWordQuote(str, "Sq", "'", "'"); }
		
		if (str.contains(".Dq")) { str = replaceSentenceBracket(str, ".Dq", "''", "''"); }		
		if (str.contains("Op")) { str = replaceSentenceBracket(str, "Op", "[", "]"); }
		if (str.contains(".Pq")) { str = replaceSentenceBracket(str, ".Pq", "(", ")"); }
		
		if (str.contains(".Xr")) { str = replaceFunctionParen(str, ".Xr", "(", ")"); }
		
		str = str.replaceAll(" Ns = Ns ", "=");
		str = str.replaceAll(" +", " ");
		str = str.replaceAll(" , ?", ", ");
		str = str.replaceAll(" /.", ". ");
		return str;			
	}
	
	public static String replaceWordQuote(String str, String qut, String leftsign, String rightsign) {
//		if (str.contains(qut)) {
			String result = new String();
			String[] strArr = str.split(" ");
			for (int i=0; i<strArr.length; i++){
				if (strArr[i] == null) continue;
				if (strArr[i].equals(qut)){
					strArr[i] = leftsign+strArr[i+1]+rightsign;
					strArr[i+1] = null;
				}
				result += strArr[i]+" ";
			}
			return result; 
//		} else return str;
	}

	public static String replaceSentenceBracket(String str, String brk, String leftsign, String rightsign) {
//		if (str.contains(brk)) {
			String[] strArr = str.split(" ");
			String resultStr = new String();
			int bracketCnt = 0;
			for (int i=0; i<strArr.length; i++) {
				if (strArr[i] == null) continue;
				if (strArr[i].equals(brk)) {
					strArr[i] = leftsign+strArr[i+1];
					++bracketCnt;					
					strArr[i+1] = null;
					resultStr += " "+strArr[i];
				} else{
					resultStr += " "+strArr[i];
				}	
			}
			for (int i=0; i<bracketCnt; i++) {
				resultStr += rightsign;
			}
			return resultStr;
	}
	
	public static String replaceFunctionParen(String str, String par, String leftsign, String rightsign) {
		String result = new String();
		String[] strArr = str.split(" ");
		for (int i=0; i<strArr.length; i++){
			if (strArr[i] == null) continue;
			if (strArr[i].equals(par)){
				strArr[i] = strArr[i+1]+leftsign+strArr[i+2]+rightsign;
				strArr[i+1] = null;
				strArr[i+2] = null;
			}
			result += strArr[i]+" ";
		}
		return result; 
	}
}	