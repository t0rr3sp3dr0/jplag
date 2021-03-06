/*
 * @Author  Emeric Kwemou on 10.02.2005
 *
 * 
 */
package swingClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Changed by Emeric Kwemou 10.02.2005
 * 
 */
public class ZipUtil {
	/**
	 * 
	 * @param file
	 * @param dest
	 *            Where the zipped file will be stored
	 * @return zipped file
	 */

	public static File zip(File file, String dest) {
		File zippedFile = new File(dest + "/" + file.getName() + ".zip");
		try {
			FileOutputStream ops = new FileOutputStream(zippedFile);
			ZipOutputStream zos = new ZipOutputStream(ops);
			zip(file, zos, "");
			zos.close();
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return zippedFile;
	}

	public static File zipTo(File file, File zippedFile) {
		try {
			FileOutputStream ops = new FileOutputStream(zippedFile);
			ZipOutputStream zos = new ZipOutputStream(ops);
			zip(file, zos, "");
			zos.close();
		} catch (FileNotFoundException fnfex) {
			fnfex.printStackTrace();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return zippedFile;
	}

	public static ZipOutputStream zip(File file, ZipOutputStream zos, String prefix) {
		File[] entries = file.listFiles();
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].isDirectory()) {
				// generate directory entry
				ZipEntry zi = new ZipEntry(prefix + entries[i].getName() + "/");
				try {
					zos.putNextEntry(zi);
					zos.closeEntry();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
				zip(entries[i], zos, prefix + entries[i].getName() + "/");
			} else {
				try {
					FileInputStream fis = new FileInputStream(entries[i]);
					ZipEntry zi = new ZipEntry(prefix + entries[i].getName());
					zos.putNextEntry(zi);
					copystream(fis, zos);
					zos.closeEntry();
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ioex) {
					ioex.printStackTrace();
				}
			}
		}
		return zos;
	}

	public static void copystream(InputStream in, OutputStream out) throws IOException {

		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		// out.close();
	}

	/**
	 * @param container
	 *            all extracted file will be stored in container, which will be
	 *            created in the destination
	 * @param file
	 * @param destination
	 *            where the file will be stored
	 * @return unzipped file
	 * 
	 */

	public static File unzip(File file, String destination, String container) {
		File result = new File(destination + "/" + container);
		result.mkdir();
		destination = destination + "/" + container + "/";
		try {
			ZipFile zipFile = new ZipFile(file);
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				ZipEntry ze = entries.nextElement();
				if (ze.isDirectory())
					(new File(destination + ze.getName())).mkdir();
				else {
					// make sure directories exist in case the client
					// didn't provide directory entries!

					File f = new File(destination + ze.getName());
					(new File(f.getParent())).mkdirs();

					FileOutputStream fos = new FileOutputStream(f);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					InputStream in = zipFile.getInputStream(ze);
					copystream(in, bos);
					bos.close();
				}
			}
			zipFile.close();
		} catch (IOException ioex) {
			ioex.printStackTrace();
		}
		return result;
	}
}
