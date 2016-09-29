package com.rw.fsutil.cacheDao.attachment;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.rw.fsutil.dao.attachment.NewAttachmentEntry;

public class PlayerExtPropertyUtil {

	public static <T extends PlayerExtProperty>  NewAttachmentEntry convert(ObjectMapper mapper, String searchId, short type, T t) throws JsonGenerationException, JsonMappingException, IOException {
		String extension = mapper.writeValueAsString(t);
		NewAttachmentEntry entry = new NewAttachmentEntry(searchId, type, t.getId(), extension);
		return entry;
	}
	
}
