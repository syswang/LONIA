
public class ResultRow{
	private String name; 
	private String description;
	
	public void setName(String str){
//		String[] names;
//		names = str.split(" ");
		this.name = str.replaceFirst("(.)*.It", "");
	}
	
	public void setDescription(String des){
		this.description = des.replaceAll(" +", " ");
//		System.out.println(cmdDes);        			
	}
	
	String getName(){
		return name;
	}
	
	String getDescription(){
		return description;
	}
}
