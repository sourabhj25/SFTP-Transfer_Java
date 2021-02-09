package fileParse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.ulink.common.constant.FileProcessStatus;
import com.ulink.common.domain.FileProcess;

public class parseFileandCheckFordata {

	
public void parseFileAndFindVersions() throws FileNotFoundException {
		
		List<FileProcess> syncGrabFiles= fileProcessRepo.findByProcessStatus(FileProcessStatus.FILE_PARSE_DONE);
		
		for (FileProcess filePath : syncGrabFiles) {
			File file = new File(filePath.getFilePath());
			InputStream targetStream = new FileInputStream(file);
			List<String> fileInListFormat=inputStreamToFileLine(targetStream);
			
			String testSuiteName = fileInListFormat.get(0);
			
			String suiteName = testSuiteName.split(",")[0].trim();
			
			String testsuiteVersion = testSuiteName
					.substring(testSuiteName.indexOf("Script"), testSuiteName.indexOf("(")).trim();
			
			System.out.println(suiteName +" ::::: "+ testsuiteVersion);
		}
		
	}
	
	public List<String> inputStreamToFileLine(InputStream inputStream)
	{
		 BufferedReader templateBR =new BufferedReader(new InputStreamReader(inputStream));
		 List<String> templateFileLines= templateBR.lines().collect(Collectors.toList());
		 return templateFileLines;
	}
}
