package com.ik.remotedic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping(value = "ik")
public class DictionaryController {
 
	private static Logger LOGGER = LoggerFactory.getLogger(DictionaryController.class);
 
	// 最新更新间隔5分钟
	private static final long MIN_UPDATE_INTERVAL = 300;
	private static final String CHARSET = "UTF-8";
	// 请求头
	private static final String REQUEST_MODIFIED_KEY = "If-Modified-Since";
	private static final String RESPONSE_MODIFIED_KEY = "Last-Modified";
	// 响应头
	private static final String REQUEST_ETAG_KEY = "If-None-Match";
	private static final String RESPONSE_ETAG_KEY = "ETag";
	
	@Autowired
	DictionaryService dictionaryService;
	
	private long globeLastModified; 
 
	@RequestMapping(value = "/dict", method = RequestMethod.HEAD)
	public void needUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		long current = System.currentTimeMillis() / 1000;
		String lastModifiedStr = request.getHeader(REQUEST_MODIFIED_KEY);
		String eTag = request.getHeader(REQUEST_ETAG_KEY);
		if (StringUtils.isEmpty(lastModifiedStr)) {
			// 首次加载
			response.setStatus(HttpStatus.OK.value());
			response.setHeader(RESPONSE_ETAG_KEY, eTag);
			response.setHeader(RESPONSE_MODIFIED_KEY, String.valueOf(current));
			return;
		}
		long lastModified;
		try {
			lastModified = Long.parseLong(lastModifiedStr);
			globeLastModified = lastModified;
		} catch (NumberFormatException e) {
			LOGGER.error("invalid header info {}", lastModifiedStr, e);
			response.sendError(HttpStatus.BAD_REQUEST.value(), "invalid header info");
			return;
		}
		// 上次更新时间不会大于当前时间
		if (lastModified >= current) {
			LOGGER.error("illegal header info {}", lastModifiedStr);
			response.sendError(HttpStatus.BAD_REQUEST.value(), "illegal header info");
			return;
		}
		// 防止频繁更新
		if (current <= lastModified + MIN_UPDATE_INTERVAL) {
			response.setStatus(HttpStatus.NOT_MODIFIED.value());
			return;
		}
		long lastDictionaryUpdateTime = dictionaryService.getLastUpdateTime() / 1000;
		// 上次更新后如果数据库没有更新则不进行同步
		if (lastModified >= lastDictionaryUpdateTime) {
			response.setStatus(HttpStatus.NOT_MODIFIED.value());
		} else {
			response.setStatus(HttpStatus.OK.value());
			response.setHeader(RESPONSE_ETAG_KEY, eTag);
			response.setHeader(RESPONSE_MODIFIED_KEY, String.valueOf(current));
		}
	}
 
	@RequestMapping(value = "/dict", method = RequestMethod.GET)
	public void sendDict(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.OK.value());
		response.setContentType("text/plain; charset=" + CHARSET);		
		
		List<String> words = dictionaryService.getWords(globeLastModified * 1000);
		if (words != null) {
			try (OutputStream out = response.getOutputStream()) {
				for (String word : words) {
					out.write((word + "\n").getBytes(CHARSET));
				}
			} catch (IOException e) {
				LOGGER.error("dict update faild!", e);
			}
		}
	}	
}