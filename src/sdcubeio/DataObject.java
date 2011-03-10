package sdcubeio;

public interface DataObject {
	public int getHDFType();

	public long[] getDimensions();

	public String getDataType();

	public String getName();
	
	public int getRank();
}
