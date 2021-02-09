package com.agsft.sftpfile;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

/**
 * @author bpawar
 * @since 19-Sep-2018
 */
@SpringBootApplication
@IntegrationComponentScan
@EnableIntegration
public class SFTPFileTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(SFTPFileTransferApplication.class, args);
	}
}
