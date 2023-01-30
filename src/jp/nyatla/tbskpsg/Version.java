package jp.nyatla.tbskpsg;


public class Version {
	public final static String MODULE = "TBSKmodemForProcessing";
	public final static int MAJOR = 0;
	public final static int MINER = 1;
	public final static int PATCH = 0;
	public final static String STRING=String.format("%s/%d.%d.%d;%s",Version.MODULE,Version.MAJOR,Version.MINER,Version.PATCH,jp.nyatla.tbskmodem.Version.STRING);
}
