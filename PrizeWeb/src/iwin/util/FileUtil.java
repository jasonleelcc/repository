package iwin.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

import org.slf4j.Logger;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class FileUtil {
	protected final Logger log;

	public FileUtil(Logger log) {
		this.log = log;
	}

	public boolean canLockFile(File xmlFile, FileOutputStream outFile) {
		FileLock lock = null;
		FileChannel channel = null;
		try {
			channel = outFile.getChannel();
			// lock = channel.lock()
			for (int i = 0; i < 100; i++) {
				lock = channel.tryLock();
				if (lock != null) {
					break;
				}
				Thread.sleep(20);
			}
			return lock != null;
		} catch (OverlappingFileLockException e) {
			log.warn("xmlFile=" + xmlFile, e);
			return false;
		} catch (Exception e) {
			log.warn("xmlFile=" + xmlFile, e);
			return false;
		} finally {
			if (lock != null) {
				try {
					lock.release(); // Remember to release the lock
				} catch (IOException e) {
					log.warn("xmlFile=" + xmlFile + ", " + e.toString());
				}
			}
			// if (channel != null) {
			// // Close the file
			// try {
			// channel.close();
			// } catch (IOException e) {
			// log.warn("xmlFile=" + xmlFile + ", " + e.toString());
			// }
			// }
		}
	}

}
