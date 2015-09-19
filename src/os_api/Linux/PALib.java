package os_api.Linux;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface PALib extends Library {

	PALib INSTANCE = (PALib) Native.loadLibrary(Platform.is64Bit() ? "PA64" : "PA32", PALib.class);
	String setAffinity(int pid, int[] affinity);
	String setPriority(int pid, int type, int priority);
	int getProcessorCount();
}
