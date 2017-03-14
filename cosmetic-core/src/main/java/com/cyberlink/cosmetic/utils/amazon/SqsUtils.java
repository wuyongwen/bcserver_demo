package com.cyberlink.cosmetic.utils.amazon;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SqsUtils {

    public enum NotificationType {
    	BOUNCE, COMPLAIN;
    }
	
	final Logger logger = LoggerFactory.getLogger(getClass()); 
	private static String ses_bounce_queue = "https://sqs.us-west-2.amazonaws.com/073012653937/SES_Bounce_Queue";
	private static String ses_complain_queue = "https://sqs.us-west-2.amazonaws.com/073012653937/SES_Complain_Queue";

	private static AWSCredentials credentials = null;
	
	
	private static AmazonSQS sqs = null;
	private String queueUrl = null;
	
	private void InitAWSCredentials(){
	}
	
	private Boolean initAmazonSQS(){
        try {
        	String path =  SqsUtils.class.getClassLoader().getResource("aws_credentials").getPath();
            credentials = new ProfileCredentialsProvider(path,"default").getCredentials();
        } catch (Exception e) {
        	logger.info("Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct ");
            return Boolean.FALSE;
        }
		try {
			if(credentials == null){
				InitAWSCredentials();
			}
			sqs = new AmazonSQSClient(credentials);
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			sqs.setRegion(usWest2);
		} catch (Exception e){
			logger.info("Cannot init the AmazonSQS from the InitAmazonSQS function.");
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}
	
	public Boolean initQueueUrl(NotificationType handleType){
		switch(handleType){
			case BOUNCE:
				queueUrl = ses_bounce_queue;
				return Boolean.TRUE;
			case COMPLAIN:
				queueUrl = ses_complain_queue;
				return Boolean.TRUE;
			default:
				logger.info("Type have to be 'bounce' or 'complain'.");
				return Boolean.FALSE;
		}
	}
	
	public void sendMessageToSQS(NotificationType handleType, String message){
		if(!initAmazonSQS() & !initQueueUrl(handleType)){
			return;
		}
		sqs.sendMessage(new SendMessageRequest(queueUrl, message));
	}
	
	
	public List<Message> receiveMessageFromSQS(NotificationType handleType){
		List<Message> messages = new LinkedList<Message>();
		
		if(!initAmazonSQS() & !initQueueUrl(handleType)){
			return messages;
		}
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
		receiveMessageRequest.setWaitTimeSeconds(10);
		receiveMessageRequest.setMaxNumberOfMessages(10);
		receiveMessageRequest.setVisibilityTimeout(60);
        messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
	}
	
	public Map<String,String> getMailsFromMessages(List<Message> messages, NotificationType Type){
		Map<String,String> emailMap = new HashMap<String,String>();
		for(Message message : messages){
			String json = message.getBody();
			ObjectMapper mapper = new ObjectMapper();
			try {
				AmazonSqsNotification amazonSqsNotification = mapper.readValue(json, AmazonSqsNotification.class);
				String subNessage = amazonSqsNotification.getMessage();
				if(Type.equals(NotificationType.BOUNCE)){
					try{
						AmazonSesBounceNotification amazonSesBounceNotification = mapper.readValue(subNessage, AmazonSesBounceNotification.class);
						List<AmazonSesBouncedRecipient> amazonSesBouncedRecipients = amazonSesBounceNotification.getBounce().getBouncedRecipients();
						for(AmazonSesBouncedRecipient amazonSesBouncedRecipient :amazonSesBouncedRecipients){
							String email = amazonSesBouncedRecipient.getEmailAddress();
							emailMap.put(message.getMessageId(), email);
						}
					}catch(Exception e){
						emailMap.put(message.getMessageId(), "");
					}
				}else if(Type.equals(NotificationType.COMPLAIN)){
					try{
						AmazonSesComplaintNotification amazonSesComplaintNotification = mapper.readValue(subNessage, AmazonSesComplaintNotification.class);
						List<AmazonSesComplainedRecipient> amazonSesBouncedRecipients = amazonSesComplaintNotification.getComplaint().getComplainedRecipients();
						for(AmazonSesComplainedRecipient amazonSesBouncedRecipient :amazonSesBouncedRecipients){
							String email = amazonSesBouncedRecipient.getEmailAddress();
							emailMap.put(message.getMessageId(), email);
						}
					}catch(Exception e){
						emailMap.put(message.getMessageId(), "");
					}
				}
			} catch (JsonParseException e) {
				logger.info("SqsUtils getMailsFromMessage JsonParseException message" + e.getMessage());
				return emailMap;
			} catch (JsonMappingException e) {
				logger.info("SqsUtils getMailsFromMessage JsonMappingException message" + e.getMessage());
				return emailMap;
			} catch (IOException e) {
				logger.info("SqsUtils getMailsFromMessage IOException message" + e.getMessage());
				return emailMap;
			}
		}
		return emailMap;
	}
	
	public Set<String> getMessageIdFromMessages(List<Message> messages){
		Set<String> messageSet = new HashSet<String>();
		if(messages.isEmpty())
			return messageSet;
		for(Message message : messages){
			messageSet.add(message.getMessageId());
		}
		return messageSet;
	}
	
	
	public void deleteMessageToSQS(NotificationType handleType, Message message){
		if(!initAmazonSQS() & !initQueueUrl(handleType)){
			return;
		}
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, message.getReceiptHandle()));
	}
	
	public void batchDeleteMessagesToSQS(NotificationType handleType, List<Message> messages){
		if((!initAmazonSQS() & !initQueueUrl(handleType)) && messages != null && messages.size() == 0){
			return;
		}
		List<DeleteMessageBatchRequestEntry> deleteMessageEntries = new LinkedList<DeleteMessageBatchRequestEntry>();
		for(Message message : messages){
			deleteMessageEntries.add(new DeleteMessageBatchRequestEntry(message.getMessageId(),message.getReceiptHandle()));
		}
		sqs.deleteMessageBatch(queueUrl, deleteMessageEntries);
		return;
	}
	
	
	/// <summary>Represents the bounce or complaint notification stored in Amazon SQS.</summary>
	public static class AmazonSqsNotification
	{
	    public String Type;
	    public String MessageId;
	    public String TopicArn;
	    public String Message;
	    public Timestamp Timestamp;
	    public String SignatureVersion;
	    public String Signature;
	    public String SigningCertURL;
	    public String UnsubscribeURL;
	    
	    public AmazonSqsNotification(){
	    }
		
		public String getType() {
			return Type;
		}
		
		public void setType(String type) {
			Type = type;
		}
		
		public String getMessage() {
			return Message;
		}
		
		public void setMessage(String message) {
			Message = message;
		}

		public String getMessageId() {
			return MessageId;
		}

		public void setMessageId(String messageId) {
			MessageId = messageId;
		}

		public String getTopicArn() {
			return TopicArn;
		}

		public void setTopicArn(String topicArn) {
			TopicArn = topicArn;
		}

		public Timestamp getTimestamp() {
			return Timestamp;
		}

		public void setTimestamp(Timestamp timestamp) {
			Timestamp = timestamp;
		}

		public String getSignatureVersion() {
			return SignatureVersion;
		}

		public void setSignatureVersion(String signatureVersion) {
			SignatureVersion = signatureVersion;
		}

		public String getSignature() {
			return Signature;
		}

		public void setSignature(String signature) {
			Signature = signature;
		}

		public String getSigningCertURL() {
			return SigningCertURL;
		}

		public void setSigningCertURL(String signingCertURL) {
			SigningCertURL = signingCertURL;
		}

		public String getUnsubscribeURL() {
			return UnsubscribeURL;
		}

		public void setUnsubscribeURL(String unsubscribeURL) {
			UnsubscribeURL = unsubscribeURL;
		}
	}

	/// <summary>Represents an Amazon SES bounce notification.</summary>
	public static class AmazonSesBounceNotification
	{
	    public String NotificationType;
	    public AmazonSesBounce Bounce;
	    public AmazonSesMail mail;
	    
	    public AmazonSesBounceNotification(){
	    }

		public String getNotificationType() {
			return NotificationType;
		}

		public void setNotificationType(String notificationType) {
			NotificationType = notificationType;
		}

		public AmazonSesBounce getBounce() {
			return Bounce;
		}

		public void setBounce(AmazonSesBounce bounce) {
			Bounce = bounce;
		}

		public AmazonSesMail getMail() {
			return mail;
		}

		public void setMail(AmazonSesMail mail) {
			this.mail = mail;
		}
	}
	
	/// <summary>Represents meta data for the mail notification from Amazon SES.</summary>
	public static class AmazonSesMail
	{
	    public String sendingAccountId;
	    public Timestamp timestamp;
	    public String source;
	    public String messageId;
	    public List<String> destination;
	    public String sourceArn;
	    
	    public AmazonSesMail(){
	    }

		public String getSendingAccountId() {
			return sendingAccountId;
		}

		public void setSendingAccountId(String sendingAccountId) {
			this.sendingAccountId = sendingAccountId;
		}

		public Timestamp getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public String getMessageId() {
			return messageId;
		}

		public void setMessageId(String messageId) {
			this.messageId = messageId;
		}

		public List<String> getDestination() {
			return destination;
		}

		public void setDestination(List<String> destination) {
			this.destination = destination;
		}

		public String getSourceArn() {
			return sourceArn;
		}

		public void setSourceArn(String sourceArn) {
			this.sourceArn = sourceArn;
		}
	}
	
	/// <summary>Represents meta data for the bounce notification from Amazon SES.</summary>
	public static class AmazonSesBounce
	{
	    public String bounceType;
	    public String bounceSubType;
	    public String reportingMTA;
	    public Timestamp timestamp;
	    public String feedbackId;
	    public List<AmazonSesBouncedRecipient> bouncedRecipients;
	    
	    public AmazonSesBounce(){
	    }
	    
		public String getBounceType() {
			return bounceType;
		}
		public void setBounceType(String bounceType) {
			this.bounceType = bounceType;
		}
		public String getBounceSubType() {
			return bounceSubType;
		}
		public void setBounceSubType(String bounceSubType) {
			this.bounceSubType = bounceSubType;
		}
		public Timestamp getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		public List<AmazonSesBouncedRecipient> getBouncedRecipients() {
			return bouncedRecipients;
		}
		public void setBouncedRecipients(List<AmazonSesBouncedRecipient> bouncedRecipients) {
			this.bouncedRecipients = bouncedRecipients;
		}

		public String getReportingMTA() {
			return reportingMTA;
		}

		public void setReportingMTA(String reportingMTA) {
			this.reportingMTA = reportingMTA;
		}

		public String getFeedbackId() {
			return feedbackId;
		}

		public void setFeedbackId(String feedbackId) {
			this.feedbackId = feedbackId;
		}
	}
	/// <summary>Represents the email address of recipients that bounced
	/// when sending from Amazon SES.</summary>
	public static class AmazonSesBouncedRecipient
	{
	    public String emailAddress;
	    public String status;
	    public String diagnosticCode;
	    public String action;
	    
	    public AmazonSesBouncedRecipient(){
	    }

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDiagnosticCode() {
			return diagnosticCode;
		}

		public void setDiagnosticCode(String diagnosticCode) {
			this.diagnosticCode = diagnosticCode;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}
	    
	}
	
	/// <summary>Represents an Amazon SES complaint notification.</summary>
	public static class AmazonSesComplaintNotification{
		
	    public String notificationType;
	    public AmazonSesComplaint complaint;
	    public AmazonSesMail mail;
	    
		public String getNotificationType() {
			return notificationType;
		}
		
		public void setNotificationType(String notificationType) {
			this.notificationType = notificationType;
		}
		
		public AmazonSesComplaint getComplaint() {
			return complaint;
		}
		
		public void setComplaint(AmazonSesComplaint complaint) {
			this.complaint = complaint;
		}
		
		public AmazonSesMail getMail() {
			return mail;
		}
		
		public void setMail(AmazonSesMail mail) {
			this.mail = mail;
		}
	}
	/// <summary>Represents the email address of individual recipients that complained 
	/// to Amazon SES.</summary>
	public static class AmazonSesComplainedRecipient
	{
	    public String emailAddress;
	    
	    public AmazonSesComplainedRecipient(){
	    }

		public String getEmailAddress() {
			return emailAddress;
		}

		public void setEmailAddress(String emailAddress) {
			this.emailAddress = emailAddress;
		}
	}
	
	/// <summary>Represents meta data for the complaint notification from Amazon SES.</summary>
	public static class AmazonSesComplaint{
		
	    public List<AmazonSesComplainedRecipient> ComplainedRecipients;
	    public Timestamp timestamp;
	    public String complaintFeedbackType;
	    public String userAgent;
	    public String feedbackId;
	    
	    public AmazonSesComplaint(){
	    }
	    
		public List<AmazonSesComplainedRecipient> getComplainedRecipients() {
			return ComplainedRecipients;
		}
		public void setComplainedRecipients(List<AmazonSesComplainedRecipient> complainedRecipients) {
			ComplainedRecipients = complainedRecipients;
		}
		public Timestamp getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		public String getComplaintFeedbackType() {
			return complaintFeedbackType;
		}
		public void setComplaintFeedbackType(String complaintFeedbackType) {
			this.complaintFeedbackType = complaintFeedbackType;
		}
		public String getUserAgent() {
			return userAgent;
		}
		public void setUserAgent(String userAgent) {
			this.userAgent = userAgent;
		}

		public String getFeedbackId() {
			return feedbackId;
		}

		public void setFeedbackId(String feedbackId) {
			this.feedbackId = feedbackId;
		}
		
	}
	
}
