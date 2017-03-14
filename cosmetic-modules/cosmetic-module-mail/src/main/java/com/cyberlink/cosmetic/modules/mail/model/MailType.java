package com.cyberlink.cosmetic.modules.mail.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum MailType {
    UPLOAD_VIDEO("email.upload.creation.subject", "templates/upload_video/upload_video.ftl"), 
    UPLOAD_EFFECT("email.upload.creation.subject", "templates/upload_effect/upload_effect.ftl"), 
    UPLOAD_PHOTO("email.upload.creation.subject", "templates/upload_photo/upload_photo.ftl"),
    CONTACT_US("", "templates/contact_us/contact_us.ftl"), 
    MEMBER_ACTIVATION("activation.emailsubject", "templates/member/activation/activation.ftl"), 
    MEMBER_ACTIVATION_BCW("activation.emailsubject", "templates/member/activation/activation_bcw.ftl"),
    MEMBER_CREATE_SUCCESSFULLY("signup.emailsubject", "templates/member/signup/sign_up.ftl"),
    MEMBER_CREATE_SUCCESSFULLY_BCW("signup.emailsubject", "templates/member/signup/sign_up_bcw.ftl"),
    MEMBER_RESET_PASSWORD("forgetpasswordlink.subject", "templates/member/resetpassword/reset_password.ftl"),
    MEMBER_RESET_PASSWORD_BCW("forgetpasswordlink.subject", "templates/member/resetpassword/reset_password_bcw.ftl"), 
    MEMBER_CHANGE_PASSWORD("changepwd.emailsubject", "templates/member/changepassword/change_password.ftl"),
    LENS_PROFILE_UPLOAD("lensprofile.upload.emailsubject", "templates/lensprofile/upload/upload.ftl"),
    LENS_PROFILE_APPROVE("lensprofile.approve.emailsubject", "templates/lensprofile/approve/approve.ftl"), 
    LENS_PROFILE_NOTIFY("lensprofile.notify.emailsubject", "templates/lensprofile/notify/notify.ftl"),
    REPORTBAD_NOTIFY_CREATOR("notifyreport.emailsubject", "templates/reportbad/reportbadnotifycreator/reportbad_notify_creator.ftl"),
    REPORTBAD_CREATION_REPORTER("reportcreation.emailsubject", "templates/reportbad/reportbadcreationreporter/reportbad_creation_reporter.ftl"),
    REPORTBAD_COMMENT_REPORTER("reportcomment.emailsubject", "templates/reportbad/reportbadcommentreporter/reportbad_comment_reporter.ftl"),
    REPORTBAD_POST("reportpost.emailsubject", "templates/reportbad/reportbadpost/reportbad_post.ftl"),
    SYNC_REPORT("", "templates/contact_us/sync_report.ftl"),
    JOIN_EVENT_HOME("joineventhome.emailsubject","templates/event/home/event_home.ftl"),
    JOIN_EVENT_STORE("joineventstore.emailsubject","templates/event/store/event_store.ftl"),
    JOIN_EVENT_COUPON("joineventstore.emailsubject","templates/event/coupon/event_coupon.ftl"),
    JOIN_EVENT_CONSULTATION("joineventconsultation.emailsubject","templates/event/consultation/consultation.ftl"),
    FREE_SAMPLE_CUSTOMER("freesamplecustomer.emailsubject", "templates/event/customer/free_sample.ftl"),
    SUSPENSION("suspension.emailsubject", "templates/reportuser/suspension/suspension.ftl"),
    IMPERSONATION_SUSPENSION("impersonationsuspension.emailsubject", "templates/reportuser/impersonationsuspension/impersonation_suspension.ftl"),
    IMPERSONATION_INVESTIGATION("impersonationinvestigation.emailsubject", "templates/reportuser/impersonationinvestigation/impersonation_investigation.ftl");
    

    private String subjectMessageKey;
    private String templatePath;

    private MailType(String subjectMessageKey, String templatePath) {
        this.subjectMessageKey = subjectMessageKey;
        this.templatePath = templatePath;
    }

    public String getSubjectMessageKey() {
        return subjectMessageKey;
    }

    public String getTemplatePath() {
        return templatePath;
    }
}
