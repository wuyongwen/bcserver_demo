package com.cyberlink.cosmetic.error;

import com.cyberlink.cosmetic.Constants;

public enum ErrorDef {
	BadRequest(400, "Bad request"),
	Forbidden(400, "Forbidden"),
	InvalidSignature(410, "Invalid Signature"),
	DuplicatedUniqueId(417, "Duplicated UniqueId"),
	ExpiredToken(418, "Expired Token"),
	InvalidPassword(419, "Invalid password"), 
	InvalidToken(420, Constants.getNotifyRegion()), 
	InvalidAccount(421, "Invalid account"),
	InvalidLocale(422, "Invalid locale"), 
	InvalidAccountToken(423, "Invalid account token"),
	WaitingValidate(424, "Waiting Validate"),
	InvalidAccountSource(425, "Invalid account source"),
	InvalidUserId(426, "Invalid userId"),
	InvalidProductId(427, "Invalid productId"),
	InvalidTargetList(428, "Invalid targetList"),
	InvalidBrandName(429, "Invalid brandName"),
	InvalidBrandIndexName(430, "Invalid brandIndexName"),
	InvalidProdComment(431, "Invalid Product Comment"),
	InvalidProdTypeName(432, "Invalid Product Type Name"),
	InvalidBrandId(433, "Invalid brandId"),
	InvalidTypeId(434, "Invalid typeId"),
	InvalidBarcode(435, "Invalid barcode"),
	InvalidCommentId(436, "Invalid commentId"),
	AccountEmailDeleted(437, "Email of this account is been deleted"),
	InvalidGroupId(438, "Invalid groupId"),
	InvalidFileType(439, "Invalid fileType"),
	InvalidFile(440, "Invalid file"),
	InvalidMetadata(441, "Invalid metadata"),
	InvalidFileId(442, "Invalid fileId"),
	InvalidFileItemId(443, "Invalid fileItemId"),
	InvalidIPAddress(444, "Invalid IP Address"),
	InvalidName(445, "Invalid Display Name"),
	InvalidProdCommentId(446, "Invalid Product Comment Id"),
	ReportSelfProductReview(447, "You can't report your own review"),
	ReportCLAccount(448, "You can't report a CyberLink authorized user"),
	DuplicatedReportProductReview(449, "You already reported this review"),
	InvalidReportReason(450, "Invalid report reason"),
	InvalidNotifyId(451, "Invalid notifyId"),
	DeviceBlocked(452, "This device is been blocked"),
	UserBlocked(453, "This User is been blocked"),
	InvalidPostAttachmentFormat(460, "Invalid post attachment Json format"),
	InvalidPostJsonFormat(461,"Invalid post Json format"),
    InvalidPostTitle(462, "Invalid post title"),
    InvalidPostContent(463, "Invalid post content"),
    InvalidPostComment(464, "Invalid post comment"),
    InvalidPostTargetId(465,"Invalid target Id"),
    InvalidPostTargetType(466, "Invalid target type"),
    InvalidPostNotAuth(467, "Not authorized to this post operation"),
    InvalidPostReportReason(468, "Invalid report post reason"),
    UnknownPostError(469, "Unknown post error"),
    InvalidCircleTypeId(470, "Invalid circleType Id"),
    InvalidCircleId(471, "Invalid circle Id"),
    InvalidCircleNotAuth(472, "Not authorized to this circle operation"),
    InvalidCircleDefaultType(473, "Invalid circle default type"),
    UnknownCircleError(479, "Unknown circle error"),
	InvalidOffset(480, "Invalid offset"),
	InvalidLimit(490, "Invalid limit"),
	InvalidPriceRange(491, "Invalid price range"),
	ServerUnavailable(503, "Service Unavailable"),
	InvalidBrandEventId(510, "Invalid brandEvent Id"),
	ServerBusy(511, "Server Busy"),
	OutOfStock(512, "Out of Stock"),
	InvalidJoinUser(513, "User is not qualified"),
	DuplicatedJoinEvent(514, "User already joined this event"),
	InvalidUserTargetId(520, "Invalid target User Id"),
	InvalidUserReportReason(521,"Invalid report user reason"),
	UnknownUserError(522, "Unknown user error"),
	BlockedTheUser(523, "You blocked the user"),
	BlockedByUser(524, "The user blocked you"),
	BlockCLAccount(525, "You can't block a CyberLink authorized user"),
	UnknownLookError(530, "Unknown look error"),
	InvalidImgUrl(536, "Invalid imgUrl"),
	InvalidRedirectUrl(537, "Invalid redirectUrl"),
	InvalidDescription(538, "Invalid description"),
	InvalidId(539, "Invalid Id"),
	InvalidJsonFormat(540, "Invalid Json Format");
	
	private final int errorCode;
	private final String errorMessage;
    
	private ErrorDef(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int code() {
        return errorCode;
    }
    
    public String message() {
    	return errorMessage;
    }

}
