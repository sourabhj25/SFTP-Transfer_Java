package com.agsft.sftpfile.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.file.remote.session.SessionFactory;
import org.springframework.integration.sftp.filters.SftpSimplePatternFileListFilter;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizer;
import org.springframework.integration.sftp.inbound.SftpInboundFileSynchronizingMessageSource;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * @author bpawar
 * @since 19-Sep-2018
 */
@Configuration
public class SFTPConfiguration {

	@Value("${sftp.remote.host}")
	private String sftpRemoteHostAddress;

	@Value("${sftp.remote.port}")
	private int sftpRemotePort;

	@Value("${sftp.remote.user}")
	private String sftpRemoteUser;

	@Value("${sftp.remote.privateKey}")
	private String sftpRemotePrivateKey;

	@Value("${sftp.remote.privateKeyPassphrase}")
	private String sftpRemotePrivateKeyPassphrase;

	@Value("${sftp.remote.directory.download}")
	private String sftpRemoteDirectoryDownload;

	@Value("${sftp.remote.directory.download.filter:*.*}")
	private String sftpRemoteDirectoryDownloadFilter;

	@Value("${sftp.local.directory.download}")
	private String sftpLocalDirectoryDownload;

	/*@Value("${sftp.remote.directory.download.all}")
	private String sftpRemoteDirectoryDownloadall;*/
	
	@Bean
	public SessionFactory<LsEntry> sftpSessionFactory() {

		ClassPathResource sshResource = new ClassPathResource(sftpRemotePrivateKey);
		DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory(true);
		factory.setHost(sftpRemoteHostAddress);
		factory.setPort(sftpRemotePort);
		factory.setUser(sftpRemoteUser);
		factory.setPrivateKey(sshResource);
		factory.setPrivateKeyPassphrase(sftpRemotePrivateKeyPassphrase);
		factory.setAllowUnknownKeys(true);

		factory.setKnownHosts("~/.ssh/known_hosts");
		return new CachingSessionFactory<LsEntry>(factory);

	}

	@Bean
	public SftpInboundFileSynchronizer sftpInboundFileSynchronizer() {

		SftpInboundFileSynchronizer fileSynchronizer = new SftpInboundFileSynchronizer(sftpSessionFactory());
		fileSynchronizer.setDeleteRemoteFiles(false); // Restrict moving file
		fileSynchronizer.setPreserveTimestamp(true); // Transfer file if modified
		fileSynchronizer.setRemoteDirectory(sftpRemoteDirectoryDownload);
		fileSynchronizer.setFilter(new SftpSimplePatternFileListFilter(sftpRemoteDirectoryDownloadFilter));
		
		File file = new File(sftpRemoteDirectoryDownload);
		if(file.isDirectory()){
			File[] fileList = file.listFiles();
			for (File fileNew : fileList) {
				if(fileNew.isDirectory()){
					Path path = Paths.get(sftpLocalDirectoryDownload+fileNew.getName());
					 try {
						Files.createDirectories(path);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					fileSynchronizer.setRemoteDirectory(sftpRemoteDirectoryDownload+fileNew.getName()+"/");
				}
			}
		}
//		fileSynchronizer.synchronizeToLocalDirectory(new File(sftpLocalDirectoryDownload));
		return fileSynchronizer;
	}

	@Bean
	@InboundChannelAdapter(channel = "fromSftpChannel", poller = @Poller(cron = "0/5 * * * * *"))
	public MessageSource<File> sftpMessageSource() {

		SftpInboundFileSynchronizingMessageSource source = new SftpInboundFileSynchronizingMessageSource(
				sftpInboundFileSynchronizer());
		source.setLocalDirectory(new File(sftpLocalDirectoryDownload));
		source.setAutoCreateLocalDirectory(true);
		source.setLocalFilter(new AcceptOnceFileListFilter<File>());

		return source;
	}

	@Bean
	@ServiceActivator(inputChannel = "fromSftpChannel")
	public MessageHandler resultFileHandler() {
		return new MessageHandler() {
			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
//				System.err.println(message.getPayload());
			}
		};
	}

}
