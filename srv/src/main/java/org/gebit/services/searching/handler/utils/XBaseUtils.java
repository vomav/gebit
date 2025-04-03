package org.gebit.services.searching.handler.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.sap.cds.services.ServiceException;

@Component
public class XBaseUtils {

	public String resolveExtensionByPropic(String payload) {
		String input = payload.split(",")[0];
        // Regular expression to extract the file extension
        String regex = "data:image/([a-zA-Z0-9]+);base64";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new ServiceException("Image is not valid");
        }
	}
	
	
	public String resolveXbasePayload(String payload) {
		return payload.split(",")[1];
	}
}
