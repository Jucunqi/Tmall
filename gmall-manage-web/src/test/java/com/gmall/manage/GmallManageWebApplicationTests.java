package com.gmall.manage;

import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallManageWebApplicationTests {

	@Autowired
	private FastFileStorageClient storageClient;
	@Test
	void contextLoads() {


	}

}
