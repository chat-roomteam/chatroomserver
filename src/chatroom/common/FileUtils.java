package chatroom.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {

	public static String  readJsonFile(String fileName) throws IOException {
		String encoding = "UTF-8";

		File file = new File(fileName);
		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];

		FileInputStream in = new FileInputStream(file);
		in.read(filecontent);
		in.close();
		return new String(filecontent, encoding);
	}

}
