package fileTransfer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.web.multipart.MultipartFile;

public class extractSpecificDataFromFile {

	public static void main(String[] args) throws FileNotFoundException {

		File inputF = new File(
				"/home/saurabh/Downloads/MasterTemplateLogFiles/TCG_I1667-6.5/Csv/Ptc_TCG_KXG5AZNV256GTOSHIBASAMPLE_37DF2013FU0U_01.csv");
		InputStream inputFS = new FileInputStream(inputF);
		String data = "";

		List<String> masterInfoList = inputStreamToFileLines(inputFS);

		for (String str : masterInfoList) {
			data = data + str + "\n";
		}
		fileSeperation(data);
		/*List<String> regexptrns = new ArrayList<>();
		String regex1 = ".*\\,.*,.*";
		String regex2 = "^<+<+.*";
		String regex3 = "\n{3,}[\r]?";
		
		regexptrns.add(regex1);
		regexptrns.add(regex2);
		regexptrns.add(regex3);

		String resultData = replaceWithPattern(regexptrns, data);*/

//		System.out.println(resultData.trim());
	}

	private static String replaceWithPattern(List<String> regexptrns, String data) {
		for (String regex : regexptrns) {
			Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(data);
			while (matcher.find()) {

				data = matcher.replaceFirst("");
				matcher = pattern.matcher(data);
			}
		}
		return data;
	}

	public static String removeWhiteSpace(String data) {
		Pattern pat = Pattern.compile("^(\\s*\\r\\n){3,}", Pattern.MULTILINE);
		Matcher mat = pat.matcher(data);
		return mat.replaceAll("\n");
	}

	public static List<String> inputStreamToFileLines(InputStream inputFS) {
		BufferedReader templateBR = new BufferedReader(new InputStreamReader(inputFS));
		List<String> templateFileLines = templateBR.lines().collect(Collectors.toList());
		return templateFileLines;
	}

	public File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {

		File convFile = new File(multipart.getOriginalFilename());
		multipart.transferTo(convFile);
		return convFile;
	}
	
	public static String fileSeperation(String data){
		/*System.out.println(data.indexOf("^^^"));
		System.out.println(data.indexOf("# Tested"));*/
		
		
		return data;
	}
}
