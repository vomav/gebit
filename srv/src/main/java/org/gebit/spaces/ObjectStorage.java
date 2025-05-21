package org.gebit.spaces;

import java.io.IOException;

public interface ObjectStorage {

	String save(byte[] bytesArray, String filePath, String contentType) throws IOException;

	void deleteFile(String filePath);

	void deleteDirectory(String prefix);

}